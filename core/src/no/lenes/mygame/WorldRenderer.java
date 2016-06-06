package no.lenes.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class WorldRenderer {

    static final float FRUSTUM_WIDTH = 9;
    static final float FRUSTUM_HEIGHT = 16;
    World world;
    OrthographicCamera cam;
    SpriteBatch batch;

    public WorldRenderer(SpriteBatch batch, World world) {
        this.world = world;
        this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        this.cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
        this.batch = batch;
    }

    public void render() {
        if (world.alien.position.y > cam.position.y) {
            cam.position.y = world.alien.position.y;
        }
        cam.update();
        batch.setProjectionMatrix(cam.combined);
        renderBackground();
        renderObjects();
    }

    public void renderBackground() {
        Gdx.gl.glClearColor(208f / 255, 244f / 255, 247f / 255, 0);
        //batch.disableBlending();
        //batch.begin();
        //batch.draw(Assets.background, 0, 0, 720, 1280);
        //batch.end();
    }

    public void renderObjects() {
        batch.enableBlending();
        batch.begin();
        renderAlien();
        renderPlatforms();
        renderItems();
        renderEnemies();
        batch.end();
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
        if (side < 0)
            batch.draw(keyFrame, world.alien.position.x + Alien.ALIEN_WIDTH / 2, world.alien.position.y - Alien.ALIEN_HEIGHT / 2,
                    side * Alien.ALIEN_WIDTH, Alien.ALIEN_HEIGHT);
        else
            batch.draw(keyFrame, world.alien.position.x - Alien.ALIEN_WIDTH / 2, world.alien.position.y - Alien.ALIEN_HEIGHT / 2,
                    side * Alien.ALIEN_WIDTH, Alien.ALIEN_HEIGHT);
    }

    private void renderPlatforms() {
        int len = world.platforms.size();
        for (int i = 0; i < len; i++) {
            Platform platform = world.platforms.get(i);
            TextureRegion keyFrame = Assets.platform;
            if (platform.state == Platform.PLATFORM_STATE_BROKEN) {
                keyFrame = Assets.platformBroken;
            }

            batch.draw(keyFrame, platform.position.x - platform.orientation * Platform.PLATFORM_WIDTH / 2, platform.position.y - Platform.PLATFORM_HEIGHT / 2,
                    platform.orientation * Platform.PLATFORM_WIDTH, Platform.PLATFORM_HEIGHT);
        }


    }

    private void renderItems() {
        int len = world.springs.size();
        for (int i = 0; i < len; i++) {
            Spring spring = world.springs.get(i);
            if (spring.state == Spring.STATE_IN) {
                batch.draw(Assets.spring, spring.position.x - Spring.SPRING_WIDTH / 2, spring.position.y - Spring.SPRING_HEIGHT / 2,
                        Spring.SPRING_WIDTH, Spring.SPRING_HEIGHT);
            } else {
                batch.draw(Assets.springOut, spring.position.x - Spring.SPRING_WIDTH / 2, spring.position.y - Spring.SPRING_HEIGHT / 2,
                        Spring.SPRING_WIDTH, Spring.SPRING_HEIGHT_OUT);
            }

        }

        len = world.coins.size();
        for (int i = 0; i < len; i++) {
            Coin coin = world.coins.get(i);
            TextureRegion keyFrame = Assets.coin.getKeyFrame(coin.stateTime, true);
            batch.draw(keyFrame, coin.position.x - Coin.COIN_WIDTH / 2, coin.position.y - Coin.COIN_HEIGHT / 2,
                    Coin.COIN_WIDTH, Coin.COIN_HEIGHT);
        }
    }

    private void renderEnemies() {
        int len = world.bees.size();
        for (int i = 0; i < len; i++) {
            Bee bee = world.bees.get(i);
            TextureRegion keyFrame = Assets.bee.getKeyFrame(bee.stateTime, true);
            float side = bee.velocity.x < 0 ? 1 : -1;
            if (side < 0)
                batch.draw(keyFrame, bee.position.x + Bee.BEE_WIDTH / 2, bee.position.y - Bee.BEE_HEIGHT / 2,
                        side * Bee.BEE_WIDTH, Bee.BEE_HEIGHT);
            else
                batch.draw(keyFrame, bee.position.x - Bee.BEE_WIDTH / 2, bee.position.y - Bee.BEE_HEIGHT / 2,
                        side * Bee.BEE_WIDTH, Bee.BEE_HEIGHT);
        }

        len = world.wingMen.size();
        for (int i = 0; i < len; i++) {
            WingMan wingMan = world.wingMen.get(i);
            TextureRegion keyFrame = Assets.wingMan.getKeyFrame(wingMan.stateTime, true);
            float side = wingMan.velocity.x < 0 ? 1 : -1;
            if (side < 0)
                batch.draw(keyFrame, wingMan.position.x + WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2,
                        side * WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT);
            else
                batch.draw(keyFrame, wingMan.position.x - WingMan.WINGMAN_WIDTH / 2, wingMan.position.y - WingMan.WINGMAN_HEIGHT / 2,
                        side * WingMan.WINGMAN_WIDTH, WingMan.WINGMAN_HEIGHT);
        }
    }

}
