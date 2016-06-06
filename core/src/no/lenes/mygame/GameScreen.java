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

    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_OVER = 3;

    MyGame game;
    OrthographicCamera guiCam;
    Vector3 touchPoint;
    World world;
    WorldRenderer renderer;

    // Will be used for sounds
    World.WorldListener worldListener;

    int lastScore;
    int state;

    String scoreString;

    float sizeX = 720;
    float sizeY = 1280;

    public GameScreen(MyGame myGame) {
        this.game = myGame;

        state = GAME_READY;
        guiCam = new OrthographicCamera(sizeX, sizeY);
        guiCam.position.set(sizeX / 2, sizeY / 2, 0);
        touchPoint = new Vector3();

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

        world = new World(worldListener);
        renderer = new WorldRenderer(game.batcher, world);

        lastScore = 0;
        scoreString = "SCORE: 0";
    }

    public void update(float deltaTime) {
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
            world.update(deltaTime, Gdx.input.getAccelerometerX());
        } else {
            float accel = 0;
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) accel = 5f;
            if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) accel = -5f;
            world.update(deltaTime, accel);
        }
        if (world.score != lastScore) {
            lastScore = world.score;
            scoreString = "SCORE: " + lastScore;
        }
        if (world.state == World.WORLD_STATE_GAME_OVER) {
            state = GAME_OVER;
            if (lastScore >= Settings.highscores[4])
                scoreString = "NEW HIGHSCORE: " + lastScore;
            else
                scoreString = "SCORE: " + lastScore;
            Settings.addScore(lastScore);
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
                // drawBoundingBoxes();
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

    private void drawBoundingBoxes() {
        game.batcher.end();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shapeRenderer = new ShapeRenderer();
        shapeRenderer.setProjectionMatrix(guiCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1, 0, 0, 0.5f);
        Rectangle rect = world.alien.bounds;
        shapeRenderer.rect(rect.x * (sizeX / 9f), rect.y * (sizeY / 16f), rect.getWidth() * (sizeX / 9f), rect.getHeight() * (sizeY / 16f));
        try {
            rect = world.coins.get(0).bounds;
        }catch (Exception e) {
        }
        shapeRenderer.rect(rect.x* (sizeX / 9f), rect.y* (sizeY / 16f), rect.getWidth()* (sizeX / 9f), rect.getHeight()* (sizeY / 16f));
        try {
            rect = world.platforms.get(4).bounds;
        }catch (Exception e) {
        }
        shapeRenderer.rect(rect.x* (sizeX / 9f), rect.y* (sizeY / 16f), rect.getWidth()* (sizeX / 9f), rect.getHeight()* (sizeY / 16f));
        try {
            rect = world.bees.get(0).bounds;
        }catch (Exception e) {
        }
        shapeRenderer.rect(rect.x* (sizeX / 9f), rect.y* (sizeY / 16f), rect.getWidth()* (sizeX / 9f), rect.getHeight()* (sizeY / 16f));
        try {
            rect = world.wingMen.get(0).bounds;
        }catch (Exception e) {
        }
        shapeRenderer.rect(rect.x* (sizeX / 9f), rect.y* (sizeY / 16f), rect.getWidth()* (sizeX / 9f), rect.getHeight()* (sizeY / 16f));
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
        shapeRenderer.rect(0, 0, sizeX, sizeY);
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
        Assets.menuFont.draw(game.batcher, "READY?", sizeX / 2 - readySize[0] / 2, sizeY / 2 - readySize[1] / 2);
    }

    private void presentRunning() {
        Assets.scoreFont.draw(game.batcher, scoreString, sizeX / (40f * (9f / 16f)), sizeY - sizeY / 40f);
    }

    float[] pausedSize = getTextSize(Assets.menuFont, "PAUSED");

    private void presentPaused() {
        shadeScreen();
        Assets.menuFont.draw(game.batcher, "PAUSED", sizeX / 2 - pausedSize[0] / 2, sizeY / 2 - pausedSize[1] / 2);
    }

    float[] gameOverSize = getTextSize(Assets.menuFont, "GAME OVER!");

    private void presentGameOver() {
        shadeScreen();
        Assets.menuFont.draw(game.batcher, "GAME OVER!", sizeX / 2 - gameOverSize[0] / 2, sizeY * 3f / 4 - gameOverSize[1] / 2);
        float[] scoreTextSize = getTextSize(Assets.scoreFont, scoreString);
        Assets.scoreFont.draw(game.batcher, scoreString, sizeX / 2 - scoreTextSize[0] / 2, sizeY / 2 - scoreTextSize[1] / 2);
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
