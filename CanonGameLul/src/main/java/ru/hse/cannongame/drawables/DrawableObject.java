package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import ru.hse.cannongame.CannonGame;

/**
 * Object that can be drawn on the GraphicContext object.
 */
public abstract class DrawableObject {
    /**
     * Actual game settings.
     */
    private CannonGame.GameSettings gameSettings;

    DrawableObject(CannonGame.GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    GraphicsContext getGraphics() {
        return gameSettings.getGraphicsContext();
    }

    /**
     * Draws this object on the GraphicsContext (provided by gameSettings).
     *
     * There are a lot of public and package-private guys in this project --- because I wanted to move all drawable guys to another package
     * and too lazy to redo it.
     */
    public abstract void draw();

    /**
     * False if object was destroyed etc, therefore no longer should be drawn on the screen.
     */
    public abstract boolean isAlive();

    double getGameScreenWidth() {
        return gameSettings.getWidth();
    }

    double getGameScreenHeight() {
        return gameSettings.getHeight();
    }

    CannonGame.GameSettings getGameSettings() {
        return gameSettings;
    }

    /**
     * Actually either a segment or the line.
     */
    static class Line {
        /**
         * This means coefficients in line's equtation:
         * A x + B y + C = 0
         */
        private double A;
        private double B;
        private double C;

        /**
         * Not null if segment.
         */
        private Point2D beginPoint;
        private Point2D endPoint;

        private Line(double A, double B, double C, Point2D beginPoint, Point2D endPoint) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.beginPoint = beginPoint;
            this.endPoint = endPoint;
        }

        /**
         * Creates line by two points.
         */
        static Line getLineByTwoPoint(Point2D point1, Point2D point2) {
            double A = point1.getY() - point2.getY();
            double B = point2.getX() - point1.getX();
            double C = point1.getX() * point2.getY() - point2.getX() * point1.getY();
            return new Line(A, B, C, point1, point2);
        }

        /**
         * Creates line perpendicular to the given one and starting from the given point.
         */
        static Line getNormalLineViaPoint(Line line, Point2D point) {
            double A = -line.getB();
            double B = line.getA();
            double C = -point.getY() * line.getA() + point.getX() * line.getB();

            return new Line(A, B, C, null, null);
        }

        /**
         * Calculates expression
         * A x0 + B y0 + C
         * In order to compare it with zero.
         */
        double applyPoint(Point2D point) {
            return A * point.getX() + B * point.getY() + C;
        }

        double getA() {
            return A;
        }

        double getB() {
            return B;
        }

        double getC() {
            return C;
        }

        Point2D getBeginPoint() {
            return beginPoint;
        }

        Point2D getEndPoint() {
            return endPoint;
        }
    }
}
