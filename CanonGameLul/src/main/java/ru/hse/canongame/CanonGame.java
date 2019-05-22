package ru.hse.canongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.canongame.drawables.Canon;
import ru.hse.canongame.drawables.DrawableObject;
import ru.hse.canongame.drawables.Terrain;

import java.util.ArrayList;
import java.util.List;

public class CanonGame {
    private GraphicsContext graphicsContext;
    private List<DrawableObject> objects = new ArrayList<>();

    private Canon canon;
    private Terrain terrain;

    private double gameScreenWidth;
    private double gameScreenHeight;

    public CanonGame(GraphicsContext graphicsContext, double gameScreenWidth, double gameScreenHeight) {
        this.graphicsContext = graphicsContext;
        this.gameScreenWidth = gameScreenWidth;
        this.gameScreenHeight = gameScreenHeight;

        terrain = new Terrain(graphicsContext, gameScreenWidth, gameScreenHeight);
        addObject(terrain);

        canon = new Canon(graphicsContext, gameScreenWidth, gameScreenHeight, terrain);
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
            default:
                break;
        }
    }

    public enum Command {
        RIGHT, LEFT, ROTATE_LEFT, ROTATE_RIGHT, FIRE, SMALL_BOMB, BIG_BOMB;
    }
}