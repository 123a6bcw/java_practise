package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.cannongame.GameSettings;

/**
 * Class that represent a drawable... triangle!
 *
 * As triangles forms mountain, all triangles oriented from left to right from down to up and then from up to down.
 * So there is leftPoint, which goes up-right to the highPoint, and then from highPoint down-right to the rightPoint.
 */
public class Triangle extends DrawableObject {
    /**
     * First point in order.
     */
    private Point2D leftPoint;

    /**
     * Second one.
     */
    private Point2D highPoint;

    /**
     * Last point.
     */
    private Point2D rightPoint;

    public Triangle(GameSettings gameSettings) {
        super(gameSettings);
    }

    /**
     * Draws only line from leftPoint to highPoint and from highPoint to rightPoint.
     */
    @Override
    public void draw() {
        GraphicsContext graphics = getGraphics();
        graphics.setFill(Color.GREEN);

        var leftPointPixels = transformToPixels(leftPoint);
        var highPointPixels = transformToPixels(highPoint);
        var rightPointPixels = transformToPixels(rightPoint);

        graphics.strokeLine(leftPointPixels.getX(), leftPointPixels.getY(), highPointPixels.getX(), highPointPixels.getY());
        graphics.strokeLine(highPointPixels.getX(), highPointPixels.getY(), rightPointPixels.getX(), rightPointPixels.getY());
    }

    /**
     * Always alive.
     */
    @Override
    public boolean isAlive() {
        return true;
    }

    public Point2D getLeftPoint() {
        return leftPoint;
    }

    public void setLeftPoint(Point2D leftPoint) {
        this.leftPoint = leftPoint;
    }

    public Point2D getHighPoint() {
        return highPoint;
    }

    public void setHighPoint(Point2D highPoint) {
        this.highPoint = highPoint;
    }

    public Point2D getRightPoint() {
        return rightPoint;
    }

    public void setRightPoint(Point2D rightPoint) {
        this.rightPoint = rightPoint;
    }

    /**
     * Transform point from rate -relation coordinate to the coordinates in pixel on canvas.
     */
    private Point2D transformToPixels(Point2D point) {
        return new Point2D(point.getX() * getGameScreenWidth(), point.getY() * getGameScreenHeight());
    }
}