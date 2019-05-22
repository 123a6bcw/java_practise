package ru.hse.canongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import ru.hse.canongame.CanonGame;

/**
 * TODO
 */
public class Canon extends DrawableObject {
    private final double STEP = 0.005;
    private final double ANGLE_STEP = 5;

    private final double widthRate = 0.06;
    private final double heightRate = 0.06;
    private final double ovalHeightRate = 0.025;
    private final double ovalWidthRate = 0.025;


    private double xStartRate = 0.2;
    private double yStartRate = 0;
    private double xEndRate = 0.2;
    private double yEndRate = 0;

    private double xStart;
    private double yStart;

    private double xEnd;
    private double yEnd;

    private double bodyWidth;
    private double bodyHeight;
    private double ovalWidth;
    private double ovalHeight;
    private double ovalAngle = -45;

    private double ovalXCon;
    private double ovalYCon;

    private Terrain terrain;

    private void calculateCoordinates() {
        xStart = xStartRate * getGameScreenWidth();
        yStart = yStartRate * getGameScreenHeight();

        xEndRate = xStartRate + widthRate;
        yEndRate = yStartRate + heightRate;

        xEnd = xEndRate * getGameScreenWidth();
        yEnd = yEndRate * getGameScreenHeight();

        ovalXCon = (xStart + xEnd) / 2;
        ovalYCon = yStart;

        bodyWidth = getGameScreenWidth() * widthRate;
        bodyHeight = getGameScreenHeight() * heightRate;
        ovalWidth = getGameScreenWidth() * ovalWidthRate;
        ovalHeight = getGameScreenHeight() * ovalHeightRate;
    }

    public Canon(CanonGame.GameSettings gameSettings, Terrain terrain) {
        super(gameSettings);

        this.terrain = terrain;

        moveVertical();
    }

    @Override
    public void draw() {
        GraphicsContext graphics = getGraphics();

        calculateCoordinates();

        var affine = new Affine(new Rotate(ovalAngle, ovalXCon, ovalYCon));
        graphics.transform(affine);
        var point = affine.transform(ovalXCon, ovalYCon);
        graphics.setFill(Color.BLUE);
        graphics.fillOval(point.getX(), point.getY() - ovalHeight / 2, ovalWidth, ovalHeight);

        Affine affineInverse = null;

        try {
            affineInverse = affine.createInverse();
        } catch (NonInvertibleTransformException e) {
            e.printStackTrace();
            return;
        }

        graphics.transform(affineInverse);

        graphics.setFill(Color.GRAY);
        graphics.fillRect(xStart, yStart, bodyWidth, bodyHeight);
    }

    public void moveLeft() {
        xStartRate -= STEP;

        if (xStartRate < 0) {
            xStartRate = 0;
        }


        moveVertical();
    }

    public void moveRight() {
        xStartRate += STEP;

        calculateCoordinates();

        if (xEndRate > 1) {
            xStartRate = 1 - widthRate;
        }

        moveVertical();
    }

    public void rotateRight() {
        ovalAngle -= ANGLE_STEP;
        if (ovalAngle < -150) {
            ovalAngle = -150; //TODO magic constant
        }
    }

    public void rotateLeft() {
        ovalAngle += ANGLE_STEP;
        if (ovalAngle > -30) {
            ovalAngle = -30; //TODO
        }
    }

    public Bullet createBullet(CanonGame.BulletType bulletType) {
        switch (bulletType) {
            case BIG_BULLET:
                var bigBullet = new Bullet(getGameSettings());
                return bigBullet;
            case SMALL_BULLET:
                var smallBullet = new Bullet(getGameSettings());
                return smallBullet;
            default:
                return null;
        }
    }

    private void moveVertical() {
        double yLow = 0;
        double yHigh = 1;

        for (int i = 0; i < 1000; i++) {
            double yMid = (yHigh + yLow) / 2;

            yStartRate = yMid;
            calculateCoordinates();

            boolean accept = true;

            if (yEndRate > 1) {
                accept = false;
            } else {
                for (var triangle : terrain.getTriangles()) {
                    if (canonBelow(triangle.getLeftPoint(), triangle.getHighPoint()) || canonBelow(triangle.getHighPoint(), triangle.getRightPoint()) ||
                            pointBelow(triangle.getHighPoint(), new Point2D(xStartRate, yEndRate), new Point2D(xEndRate, yEndRate), false)) {
                        accept = false;
                        break;
                    }
                }
            }

            if (accept) {
                yLow = yMid;
            } else {
                yHigh = yMid;
            }
        }

        yStartRate = yLow;
        calculateCoordinates();
    }

    private boolean canonBelow(Point2D leftPoint, Point2D rightPoint) {
        return pointBelow(new Point2D(xStartRate, yEndRate), leftPoint, rightPoint, true) || pointBelow(new Point2D(xEndRate, yEndRate), leftPoint, rightPoint, true);
    }

    private boolean pointBelow(Point2D point, Point2D leftPoint, Point2D rightPoint, boolean doBelow) {
        double A = leftPoint.getY() - rightPoint.getY();
        double B = rightPoint.getX() - leftPoint.getX();
        double C = leftPoint.getX() * rightPoint.getY() - rightPoint.getX() * leftPoint.getY();

        return point.getX() >= leftPoint.getX() && point.getX() <= rightPoint.getX() && ((A * point.getX() + B * point.getY() + C) * (doBelow ? 1 : -1) > 0);
    }

    @Override
    public boolean isAlive() {
        return true;
    }
}
