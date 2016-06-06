package no.lenes.mygame;

public class WingMan extends DynamicGameObject {

    public static final float WINGMAN_WIDTH = 1.6f;
    public static final float WINGMAN_HEIGHT = 0.8f;

    public static final float WINGMAN_BOUNDSWIDTH = WINGMAN_WIDTH * 0.45f;
    public static final float WINGMAN_BOUNDSHEIGHT = WINGMAN_HEIGHT;

    public static final float WINGMAN_VELOCITY = 2.5f;

    float stateTime;

    public WingMan(float x, float y) {
        super(x, y, WINGMAN_BOUNDSWIDTH, WINGMAN_BOUNDSHEIGHT);
        velocity.set(WINGMAN_VELOCITY, 0);
    }

    public void update(float deltaTime) {

        // Update position and bounds
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;

        // Handle edge encounters by turning
        if (position.x < WINGMAN_WIDTH / 2) {
            position.x = WINGMAN_WIDTH / 2;
            velocity.x = -velocity.x;
        } else if (position.x > World.WORLD_WIDTH - WINGMAN_WIDTH / 2) {
            position.x = World.WORLD_WIDTH - WINGMAN_WIDTH / 2;
            velocity.x = -velocity.x;
        }

        // Keep track of time for animation purposes
        stateTime += deltaTime;
    }

}
