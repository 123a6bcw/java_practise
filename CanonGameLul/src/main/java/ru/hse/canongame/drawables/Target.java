package ru.hse.canongame.drawables;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.canongame.CanonGame;

public class Target extends DrawableObject {
    private double xRate;
    private double yRate;
    private boolean alive = true;

    public Target(CanonGame.GameSettings gameSettings, double xRate, double yRate) {
        super(gameSettings);
        this.xRate = xRate;
        this.yRate = yRate;
    }

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

