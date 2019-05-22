package ru.hse.canongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.canongame.drawables.Canon;
import ru.hse.canongame.drawables.DrawableObject;
import ru.hse.canongame.drawables.Terrain;

import java.util.ArrayList;
import java.util.List;

public class CanonGame {
    private List<DrawableObject> objects = new ArrayList<>();

    private GameSettings gameSettings;
    private Canon canon;
    private Terrain terrain;

    private BulletType bulletType;

    public CanonGame(GameSettings gameSettings) {
        this.gameSettings = gameSettings;

        terrain = new Terrain(gameSettings);
        addObject(terrain);

        canon = new Canon(gameSettings, terrain);
        addObject(canon);
    }

    private void addObject(DrawableObject drawableObject) {
        objects.add(drawableObject);
    }

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

    public enum BulletType {
        SMALL_BULLET, BIG_BULLET;
    }

    public enum Command {
        RIGHT, LEFT, ROTATE_LEFT, ROTATE_RIGHT, FIRE, SMALL_BOMB, BIG_BOMB;
    }

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
}