package no.lenes.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {

    public static final float FRUSTUM_WIDTH = 9;
    public static final float FRUSTUM_HEIGHT = 16;

    private World world;
    private OrthographicCamera cam;
    private SpriteBatch batch;

    public WorldRenderer(SpriteBatch batch, World world) {
        this.world = world;
        this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT); // Rendering is done by game coordinates instead of pixels
        this.cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
        this.batch = batch;
    }

    public void render() {

        // Camera should follow the player upwards when the player goes above the middle of the screen
        if (world.alien.position.y > cam.position.y) {
            cam.position.y = world.alien.position.y;
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        renderBackground();
        renderObjects();
    }

    public void renderBackground() {

        Gdx.gl.glClearColor(213f / 255, 237f / 255, 247f / 255, 0);

        batch.disableBlending();

        // Background at the bottom of the map
        if (cam.position.y < 16) {
            batch.begin();

            // It was easier to move parts of the image outside of the screen than to crop it in photo editing software
            batch.draw(Assets.background, 0, -4, 13, 16);
            batch.end();
        }
    }

    public void renderObjects() {
        batch.enableBlending();
        batch.begin();
        renderClouds();
        renderPlatforms();
        renderItems();
        renderEnemies();
        renderAlien();
        batch.end();
    }

    private void renderClouds() {

        for (Cloud cloud : world.clouds) {
            switch (cloud.type) {
                case 1:
                    batch.draw(Assets.cloud1, cloud.position.x, cloud.position.y, Cloud.CLOUD_WIDTH, Cloud.CLOUD_HEIGHT);
                    break;
                case 2:
                    batch.draw(Assets.cloud2, cloud.position.x, cloud.position.y, Cloud.CLOUD_WIDTH, Cloud.CLOUD_HEIGHT);
                    break;
                case 3:
                    batch.draw(Assets.cloud3, cloud.position.x, cloud.position.y, Cloud.CLOUD_WIDTH, Cloud.CLOUD_HEIGHT);
                    break;
            }
        }
    }

    private void renderAlien() {
        TextureRegion keyFrame;
        switch (world.alien.state) {
            case Alien.ALIEN_STATE_JUMP:
                keyFrame = Assets.alienJump;
                break;
            case Alien.ALIEN_STATE_STAND:
                keyFrame = Assets.alienFront;
                break;
            case Alien.ALIEN_STATE_FALL:
                keyFrame = Assets.alienFall.getKeyFrame(world.alien.stateTime, true);
                break;
            case Alien.ALIEN_STATE_HIT:
            default:
                keyFrame = Assets.alienHit;
        }

        float side = world.alien.velocity.x < 0 ? -1 : 1;
        if (side < 0) {
            batch.draw(keyFrame, world.alien.position.x + Alien.ALIEN_WIDTH / 2, world.alien.position.y - Alien.ALIEN_HEIGHT / 2,
                    side * Alien.ALIEN_WIDTH, Alien.ALIEN_HEIGHT);
        } else {
            batch.draw(keyFrame, world.alien.position.x - Alien.ALIEN_WIDTH / 2, world.alien.position.y - Alien.ALIEN_HEIGHT / 2,
                    side * Alien.ALIEN_WIDTH, Alien.ALIEN_HEIGHT);
        }
    }

    private void renderPlatforms() {
        for (Platform platform : world.platforms) {
            TextureRegion keyFrame = Assets.platform;
            if (platform.state == Platform.PLATFORM_STATE_BROKEN) {
                keyFrame = Assets.platformBroken;
            }

            batch.draw(keyFrame, platform.position.x - platform.orientation * Platform.PLATFORM_WIDTH / 2, platform.position.y - Platform.PLATFORM_HEIGHT / 2,
                    platform.orientation * Platform.PLATFORM_WIDTH, Platform.PLATFORM_HEIGHT);
        }
    }

    private void renderItems() {

        // Render springs
        for (Spring spring : world.springs) {
            if (spring.state == Spring.STATE_IN) {
                batch.draw(Assets.spring, spring.position.x - Spring.SPRING_WIDTH / 2, spring.position.y - Spring.SPRING_HEIGHT / 2,
                        Spring.SPRING_WIDTH, Spring.SPRING_HEIGHT);
            } else {
                batch.draw(Assets.springOut, spring.position.x - Spring.SPRING_WIDTH / 2, spring.position.y - Spring.SPRING_HEIGHT / 2,
                        Spring.SPRING_WIDTH, Spring.SPRING_HEIGHT_OUT);
            }
        }

        // Render coins
        for (Coin coin : world.coins) {
            TextureRegion keyFrame = Assets.coin.getKeyFrame(coin.stateTime, true);
            batch.draw(keyFrame, coin.position.x - Coin.COIN_WIDTH / 2, coin.position.y - Coin.COIN_HEIGHT / 2,
                    Coin.COIN_WIDTH, Coin.COIN_HEIGHT);
        }
    }

    private void renderEnemies() {

        // Render bees

        for (Bee bee : world.bees) {
            TextureRegion keyFrame = Assets.bee.getKeyFrame(bee.stateTime, true);

            // Bee must be drawn so that its head is in the same direction as the velocity.
            // Flip the image by a negative width.
            float direction = bee.velocity.x < 0 ? 1 : -1;
            if (direction < 0) {
                batch.draw(keyFrame, bee.position.x + Bee.BEE_WIDTH / 2, bee.position.y - Bee.BEE_HEIGHT / 2,
                        -Bee.BEE_WIDTH, Bee.BEE_HEIGHT);
            } else {
                batch.draw(keyFrame, bee.position.x - Bee.BEE_WIDTH / 2, bee.position.y - Bee.BEE_HEIGHT / 2,
                        Bee.BEE_WIDTH, Bee.BEE_HEIGHT);
            }
        }

        // Render wingmen

        for (WingMan wingMan : world.wingMen) {

            // I made a mistake creating the sprite sheet, sizes for different parts of the animation are not equal.
            // This is some hardcoded tweaking so that the animation looks acceptable, done by scaling some of the images.
            // Please don't do this next time, very time consuming! Learn from this mistake!!!!
            TextureRegion keyFrame = Assets.wingMan.getKeyFrame(wingMan.stateTime, true);
            int frameIndex = Assets.wingMan.getKeyFrameIndex(wingMan.stateTime % Assets.wingMan.getAnimationDuration());
            switch (frameIndex) {
                case 0:
                    batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2 + 0.12f, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                            WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 0.85f, 1.23f, 0);
                    break;
                case 1:
                case 7:
                    batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2 + 0.02f, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                            WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 0.97f, 1.05f, 0);
                    break;
                case 2:
                case 6:
                    batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                            WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 1, 1, 0);
                    break;
                case 3:
                case 5:
                    batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                            WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 1, 1, 0);
                    break;
                case 4:
                    batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2 + 0.1f, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2 - 0.17f, 0, 0,
                            WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 0.87f, 1.2f, 0);
                    break;
            }

            // Tweaking process
            /*
            batch.draw(Assets.wingMan.getKeyFrames()[0], wingMan.position.x - WingMan.WINGMAN_WIDTH / 2 + 0.12f, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                    WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 0.85f, 1.15f, 0);
            batch.draw(Assets.wingMan.getKeyFrames()[1], wingMan.position.x - 3*WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                    WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 1, 1, 0);
            batch.draw(Assets.wingMan.getKeyFrames()[1], wingMan.position.x - WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - -2*WingMan.WINGMAN_HEIGHT / 2, 0, 0,
                    WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT, 1, 1, 0);
            */

        }
    }
}

