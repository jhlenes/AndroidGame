package no.lenes.mygame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.lenes.mygame.screens.*;

public class MyGame extends Game {

    // This is used by all screens
    public SpriteBatch batcher;

    @Override
    public void create() {
        batcher = new SpriteBatch();
        Settings.load();
        Assets.load();
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

}
