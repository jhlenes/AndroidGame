package no.lenes.mygame.actors;

import no.lenes.mygame.World;

public class Bee extends DynamicGameObject {

    public static final float BEE_WIDTH = 1f;
    public static final float BEE_HEIGHT = 1f;
    public static final float BEE_VELOCITY = 2f;

    public static final float BEE_BOUNDSWIDTH = BEE_WIDTH * 0.8f;
    public static final float BEE_BOUNDSHEIGHT = BEE_HEIGHT * 0.7f;

    public float stateTime;

    public Bee(float x, float y) {
        super(x, y, BEE_BOUNDSWIDTH, BEE_BOUNDSHEIGHT);
        velocity.set(BEE_VELOCITY, 0);
    }

    public void update(float deltaTime) {

        // Update position
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);

        // Update location of bounding rectangle
        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;

        // Handle edge encounters by turning the bee in the other direction
        if (position.x < BEE_WIDTH / 2) {
            position.x = BEE_WIDTH / 2;
            velocity.x = -velocity.x;
        } else if (position.x > World.WORLD_WIDTH - BEE_WIDTH / 2) {
            position.x = World.WORLD_WIDTH - BEE_WIDTH / 2;
            velocity.x = -velocity.x;
        }

        // Keep track of time in current state for animation purposes
        stateTime += deltaTime;
    }

}
