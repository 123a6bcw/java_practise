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

    private double ovalXConRate;
    private double ovalYConRate;

    private Terrain terrain;

    private void calculateCoordinates() {
        xStart = xStartRate * getGameScreenWidth();
        yStart = yStartRate * getGameScreenHeight();

        xEndRate = xStartRate + widthRate;
        yEndRate = yStartRate + heightRate;

        xEnd = xEndRate * getGameScreenWidth();
        yEnd = yEndRate * getGameScreenHeight();

        ovalXConRate = (xStartRate + xEndRate) / 2;
        ovalYConRate = yStartRate;

        ovalXCon = ovalXConRate * getGameScreenWidth();
        ovalYCon = ovalYConRate * getGameScreenHeight();

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
        var bullet = new Bullet(getGameSettings(), this);

        switch (bulletType) {
            case SMALL_BULLET:
                bullet.setMass(0.3);
                bullet.setDiameterRate(0.03);
                bullet.setExplosionRate(0.03);
                break;
            case BIG_BULLET:
                bullet.setMass(0.8);
                bullet.setDiameterRate(0.06);
                bullet.setExplosionRate(0.1);
                break;
        }

        bullet.setAngle(ovalAngle);

        calculateCoordinates();

        var affine = new Affine(new Rotate(-ovalAngle, ovalXConRate, ovalYConRate));
        var point = affine.transform(ovalXConRate, ovalYConRate);
        var endPoint = point.add(ovalWidthRate, 0);

        Affine affineInvert = null;

        try {
            affineInvert = affine.createInverse();
        } catch (NonInvertibleTransformException e) {
            return null; //TODO throw something
        }

        var resultPoint = affineInvert.transform(endPoint.getX(), endPoint.getY());
        bullet.setxRate(resultPoint.getX());
        bullet.setyRate(resultPoint.getY());

        return bullet;
    }

    private void moveVertical() {
        double yLow = 0;
        double yHigh = 1;

        for (int i = 0; i < 1000; i++) { //TODO magic constant
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
        Line line = Line.getLineByTwoPoint(leftPoint, rightPoint);

        return point.getX() >= leftPoint.getX() && point.getX() <= rightPoint.getX() && ((line.getA() * point.getX() + line.getB() * point.getY() + line.getC()) * (doBelow ? 1 : -1) > 0);
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    Terrain getTerrain() {
        return terrain;
    }
}
