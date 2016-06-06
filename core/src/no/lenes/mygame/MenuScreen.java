package no.lenes.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector3;

public class MenuScreen extends ScreenAdapter {

    MyGame game;
    OrthographicCamera guiCam;
    Vector3 touchPoint;

    float sizeX = 720;
    float sizeY = 1280;

    public MenuScreen(MyGame game) {
        this.game = game;
        guiCam = new OrthographicCamera(sizeX, sizeY);
        guiCam.position.set(sizeX / 2, sizeY / 2, 0);
        touchPoint = new Vector3();
    }

    @Override
    public void render(float delta) {
        update();
        draw();
    }

    private void update() {
        // TODO: 06.06.2016 This is not working at all!
        if (Gdx.input.justTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            if (touchPoint.y >= sizeY * 4f / 7f - Assets.menuFont.getLineHeight() && touchPoint.y <= sizeY * 4f / 7f) {
                game.setScreen(new GameScreen(game));
            }
        }

    }

    private void draw() {
        Gdx.gl.glClearColor(208f / 255, 244f / 255, 247f / 255, 0);
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);
        game.batcher.enableBlending();
        game.batcher.begin();

        GlyphLayout layout = new GlyphLayout(Assets.menuFont, "THE GAME");
        Assets.menuFont.draw(game.batcher, "THE GAME", sizeX / 2 - layout.width / 2, sizeY * 4f / 5f);
        layout.setText(Assets.menuFont, "PLAY");
        Assets.menuFont.draw(game.batcher, "PLAY", sizeX / 2 - layout.width / 2, sizeY * 4f / 7f);
        layout.setText(Assets.menuFont, "HIGHSCORES");
        Assets.menuFont.draw(game.batcher, "HIGHSCORES", sizeX / 2 - layout.width / 2, sizeY * 4f / 7f - Assets.menuFont.getLineHeight());

        game.batcher.end();
    }

}
