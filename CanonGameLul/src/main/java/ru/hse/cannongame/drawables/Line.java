package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;

/**
 * Actually either a segment or the line.
 */
public class Line {
    /**
     * This means coefficients in line's equtation:
     * A x + B y + C = 0
     */
    private double coefficientA;
    private double coefficientB;
    private double coefficientC;

    /**
     * Not null if segment.
     */
    private Point2D beginPoint;
    private Point2D endPoint;

    private Line(double coefficientA, double coefficientB, double coefficientC, Point2D beginPoint, Point2D endPoint) {
        this.coefficientA = coefficientA;
        this.coefficientB = coefficientB;
        this.coefficientC = coefficientC;
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
        double A = -line.getCoefficientB();
        double B = line.getCoefficientA();
        double C = -point.getY() * line.getCoefficientA() + point.getX() * line.getCoefficientB();

        return new Line(A, B, C, null, null);
    }

    /**
     * Calculates expression
     * A x0 + B y0 + C
     * In order to compare it with zero.
     */
    double applyPoint(Point2D point) {
        return coefficientA * point.getX() + coefficientB * point.getY() + coefficientC;
    }

    double getCoefficientA() {
        return coefficientA;
    }

    double getCoefficientB() {
        return coefficientB;
    }

    double getCoefficientC() {
        return coefficientC;
    }

    Point2D getBeginPoint() {
        return beginPoint;
    }

    Point2D getEndPoint() {
        return endPoint;
    }
}
