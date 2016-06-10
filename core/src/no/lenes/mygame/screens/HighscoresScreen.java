package no.lenes.mygame.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;

import java.util.Set;

import no.lenes.mygame.Assets;
import no.lenes.mygame.MyGame;
import no.lenes.mygame.Settings;

public class HighscoresScreen extends ScreenAdapter {

    private MyGame game;
    private OrthographicCamera guiCam;
    private Vector3 touchPoint;

    private static final float SIZE_X = 720;
    private static final float SIZE_Y = 1280;

    public HighscoresScreen(MyGame game) {
        this.game = game;
        guiCam = new OrthographicCamera(SIZE_X, SIZE_Y);
        guiCam.position.set(SIZE_X / 2, SIZE_Y / 2, 0);
        touchPoint = new Vector3();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    private void update() {
        if (Gdx.input.justTouched()) {

            // Transform actual screen coordinates to game coordinates
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            // Back button
            if (touchPoint.y >= SIZE_Y * 4f / 7f - Assets.menuFont.getLineHeight() && touchPoint.y <= SIZE_Y * 4f / 7f) {
                game.setScreen(new GameScreen(game));
            }
        }
    }

    private void draw() {
        Gdx.gl.glClearColor(213f / 255, 237f / 255, 247f / 255, 0);
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);
        game.batcher.enableBlending();
        game.batcher.begin();

        // It was easier to move parts of the image outside of the screen than to crop it in photo editing software
        game.batcher.draw(Assets.background, 0, -4f / 9f * SIZE_X, 13f / 16f * SIZE_Y, SIZE_Y);

        GlyphLayout layout = new GlyphLayout(Assets.menuFont, "Highscores");
        Assets.menuFont.draw(game.batcher, "Highscores", SIZE_X / 2 - layout.width / 2, SIZE_Y * 5f / 6f);

        float y = SIZE_Y * 4.5f / 7;
        for (int i = 0; i < Settings.highscores.length; i++) {
            Assets.menuFont.draw(game.batcher, String.format("%2d. %d", (i + 1), Settings.highscores[i]), SIZE_X * 0.25f, y);
            y -= Assets.menuFont.getLineHeight();
        }

        game.batcher.end();
    }
}
