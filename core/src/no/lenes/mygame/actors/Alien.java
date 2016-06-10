package no.lenes.mygame.actors;


import no.lenes.mygame.World;

public class Alien extends DynamicGameObject {

    public static final int ALIEN_STATE_JUMP = 0;
    public static final int ALIEN_STATE_HIT = 1;
    public static final int ALIEN_STATE_FALL = 2;
    public static final int ALIEN_STATE_STAND = 3;

    public static final float ALIEN_JUMP_VELOCITY = 12;
    public static final float ALIEN_MOVE_VELOCITY = 18;

    public static final float ALIEN_WIDTH = 0.9f;
    public static final float ALIEN_HEIGHT = 1.279f;

    public static final float ALIEN_BOUNDSWIDTH = ALIEN_WIDTH * 0.7f;
    public static final float ALIEN_BOUNDSHEIGHT = ALIEN_HEIGHT;

    public int state;
    public float stateTime;

    public Alien(float x, float y) {
        super(x, y, ALIEN_BOUNDSWIDTH, ALIEN_BOUNDSHEIGHT);
        state = ALIEN_STATE_STAND;
        stateTime = 0;
    }

    public void update(float deltaTime) {

        // Calculate new velocity and position based on GRAVITY of the world
        velocity.add(World.GRAVITY.x * deltaTime, World.GRAVITY.y * deltaTime);
        if (state == ALIEN_STATE_HIT) {
            position.add(0, velocity.y * deltaTime);        // Should not be able to move after being hit
        } else {
            position.add(velocity.x * deltaTime, velocity.y * deltaTime);
        }

        // Update location of bounding rectangle
        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;

        // Handle changes in state
        if (velocity.y < 0 && state != ALIEN_STATE_HIT) {
            state = ALIEN_STATE_FALL;
        }
        if (velocity.y > 0 && state != ALIEN_STATE_HIT) {
            state = ALIEN_STATE_JUMP;
        }

        // Handle edge encounters by reappearing on the other side of the screen
        if (position.x < 0) position.x = World.WORLD_WIDTH;
        if (position.x > World.WORLD_WIDTH) position.x = 0;

        // Keep track of time in current state for animation purposes
        stateTime += deltaTime;
    }

    public void hitBee() {
        if (state != ALIEN_STATE_HIT) {
            velocity.set(0, 0);
            state = ALIEN_STATE_HIT;
            stateTime = 0;
        }
    }

    public void hitWingMan() {
        if (state != ALIEN_STATE_HIT) {
            velocity.set(0, 0);
            state = ALIEN_STATE_HIT;
            stateTime = 0;
        }
    }

    public void hitPlatform() {
        velocity.y = ALIEN_JUMP_VELOCITY;
        state = ALIEN_STATE_JUMP;
        stateTime = 0;
    }

    public void hitSpring() {
        velocity.y = ALIEN_JUMP_VELOCITY * 1.5f;
        state = ALIEN_STATE_JUMP;
        stateTime = 0;
    }

}
