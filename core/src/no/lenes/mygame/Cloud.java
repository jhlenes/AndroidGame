package no.lenes.mygame;

import java.util.Random;

public class Cloud extends DynamicGameObject {

    public static final float CLOUD_HEIGHT = 1.2f;
    public static final float CLOUD_WIDTH = 2f;

    public static final float CLOUD_VELOCITY = 0.05f;

    int type = 1;

    public Cloud(int type, float x, float y) {
        super(x, y, CLOUD_WIDTH, CLOUD_HEIGHT);
        this.type = type;
        velocity.x = (new Random()).nextFloat() > 0.5 ? -CLOUD_VELOCITY : CLOUD_VELOCITY;
    }

    public void update(float deltaTime) {

        // Update position and bounds
        position.add(velocity.x * deltaTime, 0);
    }

}
