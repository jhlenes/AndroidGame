package no.lenes.mygame.actors;

public class Spring extends GameObject {

    public static final int STATE_IN = 0;
    public static final int STATE_OUT = 1;

    public static final float SPRING_WIDTH = 0.9f;
    public static final float SPRING_HEIGHT = 0.478f;
    public static final float SPRING_HEIGHT_OUT = 0.683f;

    public int state;

    public Spring(float x, float y) {
        super(x, y, SPRING_WIDTH, SPRING_HEIGHT);
        state = STATE_IN;
    }

}
