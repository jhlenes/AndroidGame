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

    public static final float SIZE_X = 720;
    public static final float SIZE_Y = 1280;

    public MenuScreen(MyGame game) {
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

            // Play button
            if (touchPoint.y >= SIZE_Y * 4f / 7f - Assets.menuFont.getLineHeight() && touchPoint.y <= SIZE_Y * 4f / 7f) {
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

        GlyphLayout layout = new GlyphLayout(Assets.menuFont, "Alien Escape");
        Assets.menuFont.draw(game.batcher, "Alien Escape", SIZE_X / 2 - layout.width / 2, SIZE_Y * 4f / 5f);
        layout.setText(Assets.menuFont, "PLAY");
        Assets.menuFont.draw(game.batcher, "PLAY", SIZE_X / 2 - layout.width / 2, SIZE_Y * 4f / 7f);
        layout.setText(Assets.menuFont, "HIGHSCORES");
        Assets.menuFont.draw(game.batcher, "HIGHSCORES", SIZE_X / 2 - layout.width / 2, SIZE_Y * 4f / 7f - Assets.menuFont.getLineHeight());

        game.batcher.end();
    }

}
