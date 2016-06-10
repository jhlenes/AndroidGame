package no.lenes.mygame.actors;

import java.util.Random;

import no.lenes.mygame.*;

public class Platform extends DynamicGameObject {

    public static final float PLATFORM_WIDTH = 1.8f;
    public static final float PLATFORM_HEIGHT = 0.445f;

    public static final int PLATFORM_TYPE_STATIC = 0;
    public static final int PLATFORM_TYPE_MOVING = 1;

    public static final int PLATFORM_STATE_NORMAL = 0;
    public static final int PLATFORM_STATE_BROKEN = 1;

    public static final float PLATFORM_VELOCITY = 1.5f;

    // Points are scored when a platform is passed
    public boolean scored;

    // Some platform should not break
    public boolean breakable;

    public int type;
    public int state;
    public int orientation;

    public Platform(int type, float x, float y) {
        super(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        this.type = type;
        this.state = PLATFORM_STATE_NORMAL;
        if (type == PLATFORM_TYPE_MOVING) {
            velocity.x = PLATFORM_VELOCITY;
        }
        this.orientation = (new Random()).nextFloat() > 0.5 ? 1 : -1;
        scored = false;
        breakable = true;
    }

    public Platform(int type, float x, float y, boolean scored, boolean breakable) {
        this(type, x, y);
        this.scored = scored;
        this.breakable = breakable;
    }

    public void update(float deltaTime) {
        if (type == PLATFORM_TYPE_MOVING) {

            // Update position and bounds
            position.add(velocity.x * deltaTime, 0);
            bounds.x = position.x - PLATFORM_WIDTH / 2;
            bounds.y = position.y - PLATFORM_HEIGHT / 2;

            // Handle edge encounters by turning around
            if (position.x < PLATFORM_WIDTH / 2) {
                velocity.x = -velocity.x;
                position.x = PLATFORM_WIDTH / 2;
            } else if (position.x > World.WORLD_WIDTH - PLATFORM_WIDTH / 2) {
                velocity.x = -velocity.x;
                position.x = World.WORLD_WIDTH - PLATFORM_WIDTH / 2;
            }
        }
    }

}
