package ru.hse.cannongame.drawables;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.cannongame.GameSettings;

/**
 * Target to shoot.
 *
 * Package-private as it interacts with bullets (poor little thing).
 */
public class Target extends DrawableObject {
    /**
     * Coefficient from 0 to 1, representing position of the point on the screen from 0 to gameScreenWidth.
     */
    private double xRate;

    /**
     * Same for height.
     */
    private double yRate;

    /**
     * Was target successfully destroyed.
     */
    private boolean alive = true;

    public Target(GameSettings gameSettings, double xRate, double yRate) {
        super(gameSettings);
        this.xRate = xRate;
        this.yRate = yRate;
    }

    /**
     * Small red square.
     */
    @Override
    public void draw() {
        GraphicsContext graphics = getGraphics();
        graphics.setFill(Color.RED);
        graphics.fillRect((xRate - 0.005) * getGameScreenWidth(), (yRate - 0.005) * getGameScreenHeight(), 0.01 * getGameScreenWidth(), 0.01 * getGameScreenHeight());
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    /**
     * Destroy poor little target.
     */
    void kill() {
        alive = false;
    }

    double getxRate() {
        return xRate;
    }

    double getyRate() {
        return yRate;
    }
}
