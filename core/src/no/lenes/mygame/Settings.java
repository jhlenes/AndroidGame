package no.lenes.mygame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

public class Settings {

    public static boolean soundEnabled = true;
    public static int[] highscores = new int[]{0, 0, 0, 0, 0};

    /**
     * So little data is being stored, therefore everything is stored in the preferences
     */
    public final static Preferences prefs = Gdx.app.getPreferences("prefs");

    public static void load() {

        if (prefs.contains("soundEnabled")) { // Not the first time opening app

            // Sound setting
            soundEnabled = prefs.getBoolean("soundEnabled");

            // Highscores
            for (int i = 0; i < highscores.length; i++) {
                highscores[i] = prefs.getInteger("highscore" + i);
            }
        }
    }


    public static void save() {

        // Sound setting
        prefs.putBoolean("soundEnabled", soundEnabled);

        // Highscores
        for (int i = 0; i < highscores.length; i++) {
            prefs.putInteger("highscore" + i, highscores[i]);
        }

        prefs.flush();
    }

    public static boolean addScore(int score) {
        for (int i = 0; i < 5; i++) {
            if (score > highscores[i]) {
                for (int j = 4; j > i; j--) {
                    highscores[j] = highscores[j - 1];
                }
                highscores[i] = score;
                return true;
            }
        }
        return false;
    }

}
