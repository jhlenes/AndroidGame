package no.lenes.mygame;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class GameScreen extends ScreenAdapter {

    private static final int GAME_READY = 0;
    private static final int GAME_RUNNING = 1;
    private static final int GAME_PAUSED = 2;
    private static final int GAME_OVER = 3;

    private MyGame game;
    private OrthographicCamera guiCam;
    private Vector3 touchPoint;
    private World world;
    private WorldRenderer renderer;

    // Will be used for sounds
    private World.WorldListener worldListener;

    private int lastScore;
    private int state;

    private String scoreString;

    private static final float SIZE_X = 720;
    private static final float SIZE_Y = 1280;

    public GameScreen(MyGame myGame) {
        this.game = myGame;
        state = GAME_READY;

        // Camera stuff
        guiCam = new OrthographicCamera(SIZE_X, SIZE_Y);
        guiCam.position.set(SIZE_X / 2, SIZE_Y / 2, 0);
        touchPoint = new Vector3();

        // Used for sounds
        worldListener = new World.WorldListener() {
            @Override
            public void jump() {
                //Assets.playSound(Assets.jumpSound);
            }

            @Override
            public void spring() {
                //Assets.playSound(Assets.highJumpSound);
            }

            @Override
            public void hit() {
                //Assets.playSound(Assets.hitSound);
            }

            @Override
            public void coin() {
                //Assets.playSound(Assets.coinSound);
            }
        };

        // Create the game world
        world = new World(worldListener);

        // Create the thing that draws the world to the screen
        renderer = new WorldRenderer(game.batcher, world);

        // Keep track of score
        lastScore = 0;
        scoreString = "SCORE: 0";
    }

    public void update(float deltaTime) {

        // If it goes to long between frame updates, don't make too big changes
        if (deltaTime > 0.1f) {
            deltaTime = 0.1f;
        }

        switch (state) {
            case GAME_READY:
                updateReady();
                break;
            case GAME_RUNNING:
                updateRunning(deltaTime);
                break;
            case GAME_PAUSED:
                updatePaused();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }
    }

    private void updateReady() {
        if (Gdx.input.justTouched()) {
            state = GAME_RUNNING;
        }
    }

    private void updateRunning(float deltaTime) {
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
        }

        Application.ApplicationType appType = Gdx.app.getType();

        // should work also with Gdx.input.isPeripheralAvailable(Peripheral.Accelerometer)
        if (appType == Application.ApplicationType.Android) {
            world.update(deltaTime, Gdx.input.getAccelerometerX()); // Acceleration is between [-10, 10]
        } else { // Desktop
            float accel = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) accel = 5f;
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) accel = -5f;
            world.update(deltaTime, accel);
        }

        if (world.score != lastScore) { // Don't update unless we have to
            lastScore = world.score;
            scoreString = "SCORE: " + lastScore;
        }

        // Check if the game has ended
        if (world.state == World.WORLD_STATE_GAME_OVER) {
            state = GAME_OVER;
            if (Settings.addScore(lastScore)) {
                scoreString = "NEW HIGHSCORE: " + lastScore;
            } else {
                scoreString = "SCORE: " + lastScore;
            }
            Settings.save();
        }
    }

    private void updatePaused() {
        if (Gdx.input.justTouched()) {
            state = GAME_RUNNING;
        }
    }

    private void updateGameOver() {
        if (Gdx.input.justTouched()) {
            // TODO: 09.06.2016 Instead of creating a new GameScreen, reset everything instead
            game.setScreen(new GameScreen(game));
        }
    }

    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);
        game.batcher.enableBlending();
        game.batcher.begin();
        switch (state) {
            case GAME_READY:
                presentReady();
                break;
            case GAME_RUNNING:
                //drawBoundingBoxes();
                presentRunning();
                break;
            case GAME_PAUSED:
                presentPaused();
                break;
            case GAME_OVER:
                presentGameOver();
                break;
        }
        game.batcher.end();
    }

    /**
     * Used for debugging
     */
    private void drawBoundingBoxes() {
        game.batcher.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(guiCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 0.5f);
        Rectangle rect = world.alien.bounds;
        shapeRenderer.rect(rect.x * (SIZE_X / 9f), rect.y * (SIZE_Y / 16f), rect.getWidth() * (SIZE_X / 9f), rect.getHeight() * (SIZE_Y / 16f));
        try {
            rect = world.coins.get(0).bounds;
        } catch (Exception e) {
        }
        shapeRenderer.rect(rect.x * (SIZE_X / 9f), rect.y * (SIZE_Y / 16f), rect.getWidth() * (SIZE_X / 9f), rect.getHeight() * (SIZE_Y / 16f));
        try {
            rect = world.platforms.get(4).bounds;
        } catch (Exception e) {
        }
        shapeRenderer.rect(rect.x * (SIZE_X / 9f), rect.y * (SIZE_Y / 16f), rect.getWidth() * (SIZE_X / 9f), rect.getHeight() * (SIZE_Y / 16f));
        try {
            rect = world.bees.get(0).bounds;
        } catch (Exception e) {
        }
        shapeRenderer.rect(rect.x * (SIZE_X / 9f), rect.y * (SIZE_Y / 16f), rect.getWidth() * (SIZE_X / 9f), rect.getHeight() * (SIZE_Y / 16f));
        try {
            rect = world.wingMen.get(0).bounds;
        } catch (Exception e) {
        }
        shapeRenderer.rect(rect.x * (SIZE_X / 9f), rect.y * (SIZE_Y / 16f), rect.getWidth() * (SIZE_X / 9f), rect.getHeight() * (SIZE_Y / 16f));
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batcher.begin();
    }

    private void shadeScreen() {
        // All this to draw a rectangle?????
        game.batcher.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(guiCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f);
        shapeRenderer.rect(0, 0, SIZE_X, SIZE_Y);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        game.batcher.begin();
    }

    GlyphLayout layout = new GlyphLayout();

    private float[] getTextSize(BitmapFont font, String text) {
        layout.setText(font, text);
        return new float[]{layout.width, layout.height};
    }

    float[] readySize = getTextSize(Assets.menuFont, "READY?");

    private void presentReady() {
        shadeScreen();
        Assets.menuFont.draw(game.batcher, "READY?", SIZE_X / 2 - readySize[0] / 2, SIZE_Y / 2 + readySize[1] / 2);
    }

    private void presentRunning() {
        Assets.scoreFont.draw(game.batcher, scoreString, SIZE_X / (40f * (9f / 16f)), SIZE_Y - SIZE_Y / 40f);
    }

    float[] pausedSize = getTextSize(Assets.menuFont, "PAUSED");

    private void presentPaused() {
        shadeScreen();
        Assets.menuFont.draw(game.batcher, "PAUSED", SIZE_X / 2 - pausedSize[0] / 2, SIZE_Y / 2 + pausedSize[1] / 2);
    }

    float[] gameOverSize = getTextSize(Assets.menuFont, "GAME OVER!");

    private void presentGameOver() {
        shadeScreen();
        Assets.menuFont.draw(game.batcher, "GAME OVER!", SIZE_X / 2 - gameOverSize[0] / 2, SIZE_Y * 3f / 4 + gameOverSize[1] / 2);
        float[] scoreTextSize = getTextSize(Assets.scoreFont, scoreString);
        Assets.scoreFont.draw(game.batcher, scoreString, SIZE_X / 2 - scoreTextSize[0] / 2, SIZE_Y / 2 + scoreTextSize[1] / 2);
    }


    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    @Override
    public void pause() {
        if (state == GAME_RUNNING) {
            state = GAME_PAUSED;
        }
    }

}
