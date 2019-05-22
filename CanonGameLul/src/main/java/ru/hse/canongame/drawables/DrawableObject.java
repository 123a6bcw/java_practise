package ru.hse.canongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import ru.hse.canongame.CanonGame;
import ru.hse.canongame.drawables.Bullet;
import ru.hse.canongame.drawables.Target;

import java.util.ArrayList;
import java.util.List;


public abstract class DrawableObject {
    private CanonGame.GameSettings gameSettings;

    public DrawableObject(CanonGame.GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    public GraphicsContext getGraphics() {
        return gameSettings.getGraphicsContext();
    }

    public abstract void draw();
    public abstract boolean isAlive();

    public double getGameScreenWidth() {
        return gameSettings.getWidth();
    }

    public double getGameScreenHeight() {
        return gameSettings.getHeight();
    }

    public CanonGame.GameSettings getGameSettings() {
        return gameSettings;
    }

    public static class Line {
        private double A;
        private double B;
        private double C;
        private Point2D beginPoint;
        private Point2D endPoint;

        private Line(double A, double B, double C, Point2D beginPoint, Point2D endPoint) {
            this.A = A;
            this.B = B;
            this.C = C;
            this.beginPoint = beginPoint;
            this.endPoint = endPoint;
        }

        public static Line getLineByTwoPoint(Point2D point1, Point2D point2) {
            double A = point1.getY() - point2.getY();
            double B = point2.getX() - point1.getX();
            double C = point1.getX() * point2.getY() - point2.getX() * point1.getY();
            return new Line(A, B, C, point1, point2);
        }

        public static Line getNormalLineViaPoint(Line line, Point2D point) {
            double A = -line.getB();
            double B = line.getA();
            double C = -point.getY() * line.getA() + point.getX() * line.getB();

            return new Line(A, B, C, null, null);
        }

        public double applyPoint(Point2D point) {
            return A * point.getX() + B * point.getY() + C;
        }

        public double getA() {
            return A;
        }

        public double getB() {
            return B;
        }

        public double getC() {
            return C;
        }

        public Point2D getBeginPoint() {
            return beginPoint;
        }

        public Point2D getEndPoint() {
            return endPoint;
        }
    }
}
