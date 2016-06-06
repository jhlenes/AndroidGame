package no.lenes.mygame;

public class Coin extends GameObject {

    public static final float COIN_HEIGHT = 0.7f;
    public static final float COIN_WIDTH = 0.7f;
    public static final int COIN_SCORE = 10;

    public static final float COIN_BOUNDSWIDTH = COIN_WIDTH * 0.8f;
    public static final float COIN_BOUNDSHEIGHT = COIN_HEIGHT * 0.8f;

    float stateTime;

    public Coin(float x, float y) {
        super(x, y, COIN_BOUNDSWIDTH, COIN_BOUNDSHEIGHT);
        stateTime = 0;
    }

    public void update(float deltaTime) {

        // Keep track of time for animation purposes
        stateTime += deltaTime;
    }

}
