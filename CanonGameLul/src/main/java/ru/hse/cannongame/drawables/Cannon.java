package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.transform.Affine;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Rotate;
import ru.hse.cannongame.CannonGame;
import ru.hse.cannongame.GameSettings;

/**
 * Cannon. A drawable thing that rides on the mountains and shoots targets.
 *
 * Most of the coordinates marked as 'Rate'. That means this coordinates are from 0 to 1 and represents position
 * on the screen related to the actual windows size (meaning, xRate \in [0, 1] -> x \in [0, gameScreenWidth]).
 */
public class Cannon extends DrawableObject {
    /**
     * Rated cannon step's length (from one button pressed).
     *
     * Not final, because maybe a shall want to add this as a controllable option later.
     */
    private final double STEP = 0.005;

    /**
     * How much does cannon's cannon rotate from one pressed button.
     */
    private final double ANGLE_STEP = 5;

    /**
     * Sizes of the body of the cannon.
     */
    private final double widthRate = 0.06;
    private final double heightRate = 0.06;

    /**
     * Sizes of the cannon's cannon.
     */
    private final double ovalHeightRate = 0.025;
    private final double ovalWidthRate = 0.025;

    /**
     * Coordinates of the left-up corner of the rectangle cannon's body.
     */
    private double xStartRate = 0.2;
    private double yStartRate = 0;
    private double xEndRate = 0.2;
    private double yEndRate = 0;

    /**
     * Coordinates in pixels.
     */
    private double xStart;
    private double yStart;
    private double xEnd;
    private double yEnd;

    private double bodyWidth;
    private double bodyHeight;
    private double ovalWidth;
    private double ovalHeight;

    /**
     * Angle of the cannon's cannon in which it shoots the balls.
     */
    private double ovalAngle = -45;

    /**
     * Oh... That's the coordinates of the point there oval (cannon's cannon) connect to the rectangle (body).
     */
    private double ovalXConRate;
    private double ovalYConRate;
    private double ovalXCon;
    private double ovalYCon;

    /**
     * Terrain on which cannon rides.
     */
    private Terrain terrain;

    /**
     * Updates all coordinates knowing only xStartRate, yStartRate and current gameScreen sizes.
     */
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

    public Cannon(GameSettings gameSettings, Terrain terrain) {
        super(gameSettings);

        this.terrain = terrain;

        moveVertical();
    }

    /**
     * Uses some dark magic with affine rotation of the screen in order to draw oval at a certain angle.
     */
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
            e.printStackTrace(); //rotation is always inversable, so whatever.
            return;
        }

        graphics.transform(affineInverse);

        graphics.setFill(Color.GRAY);
        graphics.fillRect(xStart, yStart, bodyWidth, bodyHeight);
    }

    /**
     * Moves cannon to the left. Does not move if goes off screen.
     *
     * Public because being used from CannonGame.
     */
    public void moveLeft() {
        xStartRate -= STEP;

        if (xStartRate < 0) {
            xStartRate = 0;
        }


        moveVertical();
    }

    /**
     * Moves to the right.
     */
    public void moveRight() {
        xStartRate += STEP;

        calculateCoordinates();

        if (xEndRate > 1) {
            xStartRate = 1 - widthRate;
        }

        moveVertical();
    }

    /**
     * Rotates the oval.
     */
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

    /**
     * Creates bullet as a drawable object, attached to the CannonGame.
     */
    public Bullet createBullet(CannonGame.BulletType bulletType) {
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

    /**
     * Founds the bottom-most vertical position for the cannon (for the given x coordinate) there it does not appears below the mountains.
     *
     * Uses binsearch. Too lazy to find a formula.
     */
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
                    if (cannonBelow(triangle.getLeftPoint(), triangle.getHighPoint()) || cannonBelow(triangle.getHighPoint(), triangle.getRightPoint()) ||
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

    /**
     * True if this cannon is below given segment (therefore cannon cannot be places here).
     */
    private boolean cannonBelow(Point2D leftPoint, Point2D rightPoint) {
        return pointBelow(new Point2D(xStartRate, yEndRate), leftPoint, rightPoint, true) || pointBelow(new Point2D(xEndRate, yEndRate), leftPoint, rightPoint, true);
    }

    /**
     * True if point is between x coordinates of the left and right point and either below or above of given segment
     * depending on doBelod.
     */
    private boolean pointBelow(Point2D point, Point2D leftPoint, Point2D rightPoint, boolean doBelow) {
        Line line = Line.getLineByTwoPoint(leftPoint, rightPoint);

        return point.getX() >= leftPoint.getX() && point.getX() <= rightPoint.getX() && ((line.getCoefficientA() * point.getX() + line.getCoefficientB() * point.getY() + line.getCoefficientC()) * (doBelow ? 1 : -1) > 0);
    }

    /**
     * For now always alive.
     */
    @Override
    public boolean isAlive() {
        return true;
    }

    Terrain getTerrain() {
        return terrain;
    }
}
