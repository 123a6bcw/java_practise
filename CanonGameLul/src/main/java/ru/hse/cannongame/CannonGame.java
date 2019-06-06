package ru.hse.cannongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.cannongame.drawables.Cannon;
import ru.hse.cannongame.drawables.DrawableObject;
import ru.hse.cannongame.drawables.Terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of game logic.
 */
public class CannonGame {
    /**
     * All drawable object's attached to the game. There can be more -- ones who attached to the attached object's.
     */
    private List<DrawableObject> objects = new ArrayList<>();

    /**
     * Game settings!
     */
    private GameSettings gameSettings;

    /**
     * Cannon!
     */
    private Cannon cannon;

    /**
     * Type of the bullet to use. For now supported are only SMALL_BULLET and BIG_BULLET.
     */
    private BulletType bulletType = BulletType.SMALL_BULLET;

    public CannonGame(GameSettings gameSettings) {
        this.gameSettings = gameSettings;

        gameSettings.setTerrain(new Terrain(gameSettings));
        addObject(gameSettings.getTerrain());

        cannon = new Cannon(gameSettings);
        addObject(cannon);
    }

    /**
     * Adds drawable object to the list of drawable object's.
     */
    private void addObject(DrawableObject drawableObject) {
        objects.add(drawableObject);
    }

    /**
     * Draws all drawable object (and their drawable children, if there any (there are)).
     */
    void drawObjects() {
        for (var iterator = objects.iterator(); iterator.hasNext();) {
            DrawableObject object = iterator.next();
            if (!object.isAlive()) {
                iterator.remove();
            } else {
                object.draw();
            }
        }
    }

    /**
     * Reaction on user's command.
     */
    void applyCommand(Command command) {
        switch (command) {
            case LEFT:
                cannon.moveLeft();
                break;
            case RIGHT:
                cannon.moveRight();
                break;
            case ROTATE_LEFT:
                cannon.rotateLeft();
                break;
            case ROTATE_RIGHT:
                cannon.rotateRight();
                break;
            case FIRE:
                addObject(cannon.createBullet(bulletType));
                break;
            case SMALL_BOMB:
                bulletType = BulletType.SMALL_BULLET;
                break;
            case BIG_BOMB:
                bulletType = BulletType.BIG_BULLET;
                break;
        }
    }

    /**
     * Type of the bullet to shoot.
     */
    public enum BulletType {
        SMALL_BULLET, BIG_BULLET;
    }

    /**
     * List of user's commands.
     */
    public enum Command {
        RIGHT, LEFT, ROTATE_LEFT, ROTATE_RIGHT, FIRE, SMALL_BOMB, BIG_BOMB;
    }
}