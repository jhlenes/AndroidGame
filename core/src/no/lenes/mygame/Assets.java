package no.lenes.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public class Assets {

    public static Texture items;
    public static TextureRegion alienFront;
    public static TextureRegion alienHit;
    public static TextureRegion alienJump;
    public static Animation alienFall;
    public static Animation bee;
    public static Animation wingMan;
    public static Animation coin;
    public static TextureRegion spring;
    public static TextureRegion springOut;
    public static TextureRegion platform;
    public static TextureRegion platformBroken;
    public static TextureRegion wingLeft;
    public static TextureRegion wingRight;
    public static TextureRegion cloud1;
    public static TextureRegion cloud2;
    public static TextureRegion cloud3;
    public static BitmapFont scoreFont;
    public static BitmapFont menuFont;

    public static Texture loadTexture(String file) {
        return new Texture(Gdx.files.internal(file));
    }

    public static void load() {
        items = loadTexture("items.png");
        alienFront = new TextureRegion(items, 430, 301, 104, 149);
        alienHit = new TextureRegion(items, 534, 301, 109, 150);
        alienJump = new TextureRegion(items, 643, 301, 107, 152);
        alienFall = new Animation(0.3f,
                new TextureRegion(items, 750, 301, 109, 154),
                new TextureRegion(items, 859, 301, 110, 157)
        );
        bee = new Animation(0.15f,
                new TextureRegion(items, 174, 301, 128, 128),
                new TextureRegion(items, 302, 301, 128, 128)
        );
        wingMan = new Animation(0.1f,
                new TextureRegion(items, 0, 301, 174, 126),
                new TextureRegion(items, 432, 178, 206, 107),
                new TextureRegion(items, 0, 178, 216, 101),
                new TextureRegion(items, 216, 178, 216, 101),
                new TextureRegion(items, 783, 178, 182, 123),
                new TextureRegion(items, 216, 178, 216, 101),
                new TextureRegion(items, 0, 178, 216, 101),
                new TextureRegion(items, 432, 178, 206, 107)
        );
        coin = new Animation(0.2f,
                new TextureRegion(items, 785, 0, 84, 84),
                new TextureRegion(items, 701, 0, 84, 84),
                new TextureRegion(items, 0, 84, 84, 84),
                new TextureRegion(items, 869, 0, 84, 84),
                new TextureRegion(items, 0 + 84, 84, -84, 84),
                new TextureRegion(items, 701 + 84, 0, -84, 84)
        );
        spring = new TextureRegion(items, 556, 0, 145, 77);
        springOut = new TextureRegion(items, 638, 178, 145, 110);
        platform = new TextureRegion(items, 464, 84, 380, 94);
        platformBroken = new TextureRegion(items, 84, 84, 380, 94);
        wingLeft = new TextureRegion(items, 386, 0, 85, 74);
        wingRight = new TextureRegion(items, 471, 0, 85, 74);
        cloud1 = new TextureRegion(items, 129, 0, 128, 71);
        cloud2 = new TextureRegion(items, 0, 0, 129, 71);
        cloud3 = new TextureRegion(items, 257, 0, 129, 71);

        // Font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        scoreFont = generator.generateFont(parameter);
        parameter.size = 80;
        menuFont = generator.generateFont(parameter);
        generator.dispose();
    }

    public static void playSound(Sound sound) {
        if (Settings.soundEnabled) sound.play(1);
    }

}