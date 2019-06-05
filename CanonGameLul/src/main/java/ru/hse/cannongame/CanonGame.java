package ru.hse.cannongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.cannongame.drawables.Canon;
import ru.hse.cannongame.drawables.DrawableObject;
import ru.hse.cannongame.drawables.Terrain;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller of game logic.
 */
public class CanonGame {
    /**
     * All drawable object's attached to the game. There can be more -- ones who attached to the attached object's.
     */
    private List<DrawableObject> objects = new ArrayList<>();

    /**
     * Game settings!
     */
    private GameSettings gameSettings;

    /**
     * Canon!
     */
    private Canon canon;

    /**
     * Terrain!
     */
    private Terrain terrain;

    /**
     * Type of the bullet to use. For now supported are only SMALL_BULLET and BIG_BULLET.
     */
    private BulletType bulletType = BulletType.SMALL_BULLET;

    public CanonGame(GameSettings gameSettings) {
        this.gameSettings = gameSettings;

        terrain = new Terrain(gameSettings);
        addObject(terrain);

        canon = new Canon(gameSettings, terrain);
        addObject(canon);
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
                canon.moveLeft();
                break;
            case RIGHT:
                canon.moveRight();
                break;
            case ROTATE_LEFT:
                canon.rotateLeft();
                break;
            case ROTATE_RIGHT:
                canon.rotateRight();
                break;
            case FIRE:
                addObject(canon.createBullet(bulletType));
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

    /**
     * Game settings, most important --- width and height, as they may change during the execution
     * and this class stores their actual values.
     */
    public static class GameSettings {
        private GraphicsContext graphicsContext;
        private double width;
        private double height;

        public GraphicsContext getGraphicsContext() {
            return graphicsContext;
        }

        public void setGraphicsContext(GraphicsContext graphicsContext) {
            this.graphicsContext = graphicsContext;
        }

        public double getWidth() {
            return width;
        }

        public void setWidth(double width) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight(double height) {
            this.height = height;
        }
    }

    public Terrain getTerrain() {
        return terrain;
    }
}