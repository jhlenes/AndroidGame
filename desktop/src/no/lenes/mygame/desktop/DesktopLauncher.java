package no.lenes.mygame.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import no.lenes.mygame.MyGame;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.height = 900;
        config.width = 506;
        new LwjglApplication(new MyGame(), config);
    }

}
