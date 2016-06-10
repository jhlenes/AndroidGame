package no.lenes.mygame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import no.lenes.mygame.actors.Bee;
import no.lenes.mygame.actors.Cloud;
import no.lenes.mygame.actors.Coin;
import no.lenes.mygame.actors.Platform;
import no.lenes.mygame.actors.Spring;
import no.lenes.mygame.actors.WingMan;

public class World {

    public interface WorldListener {
        void jump();

        void spring();

        void hit();

        void coin();
    }

    public static final int WORLD_WIDTH = 9;
    public static final int WORLD_HEIGHT = 16;
    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_GAME_OVER = 1;

    public static final Vector2 GRAVITY = new Vector2(0, -12);

    public final no.lenes.mygame.actors.Alien alien;
    public List<no.lenes.mygame.actors.Platform> platforms;
    public List<no.lenes.mygame.actors.Spring> springs;
    public List<no.lenes.mygame.actors.Bee> bees;
    public List<no.lenes.mygame.actors.WingMan> wingMen;
    public List<no.lenes.mygame.actors.Coin> coins;
    public List<no.lenes.mygame.actors.Cloud> clouds;

    public final WorldListener listener;
    public final Random rand;

    public float heightSoFar;
    public int score;
    public int state;

    public static final float MAX_JUMP_HEIGHT = no.lenes.mygame.actors.Alien.ALIEN_JUMP_VELOCITY * no.lenes.mygame.actors.Alien.ALIEN_JUMP_VELOCITY / (2 * -GRAVITY.y);
    float generatedHeight;

    public World(WorldListener listener) {
        this.alien = new no.lenes.mygame.actors.Alien(5, 2);
        this.platforms = new ArrayList<no.lenes.mygame.actors.Platform>();
        this.springs = new ArrayList<no.lenes.mygame.actors.Spring>();
        this.bees = new ArrayList<no.lenes.mygame.actors.Bee>();
        this.wingMen = new ArrayList<no.lenes.mygame.actors.WingMan>();
        this.coins = new ArrayList<no.lenes.mygame.actors.Coin>();
        this.clouds = new ArrayList<no.lenes.mygame.actors.Cloud>();
        this.listener = listener;
        rand = new Random();

        this.heightSoFar = 0;
        this.score = 0;
        this.state = WORLD_STATE_RUNNING;

        generatedHeight = 0;
        generateBeginning();
    }

    private void generateBeginning() {

        // The ground
        float y = no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT / 2;
        for (float x = no.lenes.mygame.actors.Platform.PLATFORM_WIDTH / 2; x < WORLD_WIDTH + no.lenes.mygame.actors.Platform.PLATFORM_WIDTH / 2; x += no.lenes.mygame.actors.Platform.PLATFORM_WIDTH) {
            platforms.add(new no.lenes.mygame.actors.Platform(no.lenes.mygame.actors.Platform.PLATFORM_TYPE_STATIC, x, y, true, false));
        }
        generatedHeight += MAX_JUMP_HEIGHT / 4 + rand.nextFloat() * 3;
        generateLevel();
    }


    private void generateLevel() {

        // TODO: Implement a random level generating algorithm

        float y = generatedHeight;
        while (y < heightSoFar + WORLD_HEIGHT) {

            // Add platforms
            int type = rand.nextFloat() > 0.8f ? no.lenes.mygame.actors.Platform.PLATFORM_TYPE_MOVING : no.lenes.mygame.actors.Platform.PLATFORM_TYPE_STATIC;
            float x = rand.nextFloat() * (WORLD_WIDTH - no.lenes.mygame.actors.Platform.PLATFORM_WIDTH) + no.lenes.mygame.actors.Platform.PLATFORM_WIDTH / 2;
            no.lenes.mygame.actors.Platform platform = new no.lenes.mygame.actors.Platform(type, x, y);
            platforms.add(platform);

            // Add springs
            if (rand.nextFloat() > 0.9f && platform.type != no.lenes.mygame.actors.Platform.PLATFORM_TYPE_MOVING) {
                no.lenes.mygame.actors.Spring spring = new no.lenes.mygame.actors.Spring(x, y + no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT / 2 + no.lenes.mygame.actors.Spring.SPRING_HEIGHT / 2);
                springs.add(spring);
                platform.breakable = false;
            }

            // Add coins
            if (rand.nextFloat() > 0.6f) {
                no.lenes.mygame.actors.Coin coin = new no.lenes.mygame.actors.Coin(x + rand.nextFloat() * 3,
                        y + no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT / 2 + no.lenes.mygame.actors.Coin.COIN_HEIGHT / 2 + rand.nextFloat() * 3);
                coins.add(coin);
            }

            // Add clouds
            if (y > WORLD_HEIGHT / 2 && rand.nextFloat() > 0.4) {
                no.lenes.mygame.actors.Cloud cloud = new no.lenes.mygame.actors.Cloud(MathUtils.random(1, 3), rand.nextFloat() * 4, y);
                clouds.add(cloud);
            }

            // Add bees
            if (y > WORLD_HEIGHT * 2 && rand.nextFloat() > 0.8f) {
                no.lenes.mygame.actors.Bee bee = new no.lenes.mygame.actors.Bee(x,
                        y + no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT / 2 + no.lenes.mygame.actors.Bee.BEE_HEIGHT / 2 + rand.nextFloat() * 3);
                bees.add(bee);
            }

            // Add wingmen
            if (y > WORLD_HEIGHT * 4 && rand.nextFloat() > 0.9f) {
                no.lenes.mygame.actors.WingMan wingMan = new no.lenes.mygame.actors.WingMan(x,
                        y + no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT / 2 + no.lenes.mygame.actors.WingMan.WINGMAN_HEIGHT / 2 + rand.nextFloat() * 3);
                wingMen.add(wingMan);
            }

            y += MAX_JUMP_HEIGHT - 0.5f;
            y -= rand.nextFloat() * (MAX_JUMP_HEIGHT / 3);
        }
        generatedHeight = y;
    }

    public void update(float deltaTime, float accelX) {
        updateAlien(deltaTime, accelX);
        updatePlatforms(deltaTime);
        updateBees(deltaTime);
        updateWingMen(deltaTime);
        updateCoins(deltaTime);
        updateClouds(deltaTime);
        checkCollisions();
        checkGameOver();
        generateLevel();
    }

    private void updateAlien(float deltaTime, float accelX) {

        // Alien can't die at the very beginning
        if (alien.position.y <= no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT + no.lenes.mygame.actors.Alien.ALIEN_HEIGHT / 2) {
            alien.hitPlatform();
            alien.position.y = no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT + no.lenes.mygame.actors.Alien.ALIEN_HEIGHT / 2;
        }

        // Alien's x-velocity is determined by accelerometer
        alien.velocity.x = -accelX / 10 * no.lenes.mygame.actors.Alien.ALIEN_MOVE_VELOCITY; // accelZ ranges between [-10,10]

        alien.update(deltaTime);

        if (alien.position.y > heightSoFar) {
            heightSoFar = alien.position.y;
        }
    }

    // TODO: 06.06.2016 Check if removal is working properly
    private void updatePlatforms(float deltaTime) {
        Iterator<no.lenes.mygame.actors.Platform> iterator = platforms.iterator();
        while (iterator.hasNext()) {
            no.lenes.mygame.actors.Platform platform = iterator.next();
            platform.update(deltaTime);

            if (platform.position.y + no.lenes.mygame.actors.Platform.PLATFORM_HEIGHT < heightSoFar - WORLD_HEIGHT / 2) {
                iterator.remove();
            }
            if (platform.position.y < alien.position.y && !platform.scored) {
                platform.scored = true;
                score++;
            }
        }
    }


    private void updateBees(float deltaTime) {

        Iterator<no.lenes.mygame.actors.Bee> iterator = bees.iterator();
        while (iterator.hasNext()) {
            no.lenes.mygame.actors.Bee bee = iterator.next();
            bee.update(deltaTime);
            if (bee.position.y + Bee.BEE_HEIGHT < heightSoFar - WORLD_HEIGHT / 2) {
                iterator.remove();
            }
        }
    }

    private void updateWingMen(float deltaTime) {
        Iterator<no.lenes.mygame.actors.WingMan> iterator = wingMen.iterator();
        while (iterator.hasNext()) {
            no.lenes.mygame.actors.WingMan wingMan = iterator.next();
            wingMan.update(deltaTime);
            if (wingMan.position.y + WingMan.WINGMAN_HEIGHT < heightSoFar - WORLD_HEIGHT / 2) {
                iterator.remove();
            }
        }
    }

    private void updateCoins(float deltaTime) {
        Iterator<no.lenes.mygame.actors.Coin> iterator = coins.iterator();
        while (iterator.hasNext()) {
            no.lenes.mygame.actors.Coin coin = iterator.next();
            coin.update(deltaTime);
            if (coin.position.y + no.lenes.mygame.actors.Coin.COIN_HEIGHT < heightSoFar - WORLD_HEIGHT / 2) {
                iterator.remove();
            }
        }
    }

    public void updateClouds(float deltaTime) {
        Iterator<no.lenes.mygame.actors.Cloud> iterator = clouds.iterator();
        while (iterator.hasNext()) {
            Cloud cloud = iterator.next();
            cloud.update(deltaTime);
            if (cloud.position.y + 1 < heightSoFar - WORLD_HEIGHT / 2) {
                iterator.remove();
            }
        }
    }

    private void checkCollisions() {
        checkPlatformCollisions();
        checkEnemyCollisions();
        checkItemCollisions();
    }

    private void checkPlatformCollisions() {
        if (alien.velocity.y > 0 || alien.state == no.lenes.mygame.actors.Alien.ALIEN_STATE_HIT) {
            return;
        }
        for (int i = 0; i < platforms.size(); i++) {
            no.lenes.mygame.actors.Platform platform = platforms.get(i);
            if (alien.position.y > platform.position.y) {
                if (alien.bounds.overlaps(platform.bounds)) {
                    if (platform.state == no.lenes.mygame.actors.Platform.PLATFORM_STATE_BROKEN) {
                        platforms.remove(i);
                    }
                    alien.hitPlatform();
                    if (rand.nextFloat() > 0.5f && platform.breakable) {
                        platform.state = Platform.PLATFORM_STATE_BROKEN;
                    }
                    listener.jump();
                    break;
                }
            }
        }
    }

    private void checkEnemyCollisions() {

        if (alien.state == no.lenes.mygame.actors.Alien.ALIEN_STATE_HIT) {
            return;
        }

        // Bees
        for (int i = 0; i < bees.size(); i++) {
            if (alien.bounds.overlaps(bees.get(i).bounds)) {
                alien.hitBee();
                listener.hit();
            }
        }

        // WingMen
        for (int i = 0; i < wingMen.size(); i++) {
            if (alien.bounds.overlaps(wingMen.get(i).bounds)) {
                alien.hitWingMan();
                listener.hit();
            }
        }
    }

    private void checkItemCollisions() {

        if (alien.state == no.lenes.mygame.actors.Alien.ALIEN_STATE_HIT) {
            return;
        }

        // Coins
        for (int i = 0; i < coins.size(); i++) {
            if (alien.bounds.overlaps(coins.get(i).bounds)) {
                coins.remove(i);
                listener.coin();
                score += Coin.COIN_SCORE;
            }
        }

        // Springs
        if (alien.velocity.y > 0) return;
        for (int i = 0; i < springs.size(); i++) {
            no.lenes.mygame.actors.Spring spring = springs.get(i);
            if (spring.state == no.lenes.mygame.actors.Spring.STATE_IN && alien.position.y > spring.position.y) {
                if (alien.bounds.overlaps(spring.bounds)) {
                    alien.hitSpring();
                    spring.state = Spring.STATE_OUT;
                    listener.spring();
                }
            }
        }
    }


    private void checkGameOver() {
        if (alien.position.y < heightSoFar - WORLD_HEIGHT / 2) {
            state = WORLD_STATE_GAME_OVER;
        }
    }

}
