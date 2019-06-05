package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.cannongame.CanonGame;
import ru.hse.cannongame.Main;

/**
 * Bullet that flies on the screen and blows on the ground.
 */
public class Bullet extends DrawableObject {
    /**
     * How many times flies between to drawing steps.
     */
    private static final int TICK = Main.TICK;

    /**
     * How many ticks has passed since this bullet has been created/
     */
    private long ticksPassed = 0;

    /**
     * Mass of the bullet. Simply a coefficient that decrease initial bullet's speed.
     */
    private double mass = -1;

    /**
     * Diameter of the bullet. Look at Canon class for the explanation of the "Rate" suffix.
     */
    private double diameterRate;

    /**
     * Blow diameter -- meaning all target in this diameter shall be blown after bullet drops on the ground.
     */
    private double explosionRate;

    /**
     * Position of... I don't know. I guess, center of the bullet, but I'm not sure at that point.
     */
    private double xRate;
    private double yRate;

    /**
     * Initial y position of the bullet.
     */
    private double initialYRate;

    /**
     * Coordinates in pixels.
     */
    private double x;
    private double y;

    private double diameter;

    /**
     * Initial bullet's speed (at the angle).
     */
    private double speedRate = 0.0006;

    /**
     * Speed projected on X and Y coordinates.
     */
    private double initialXSpeedRate;
    private double initialYSpeedRate;

    /**
     * Gravitational acceleration.
     */
    private static final double gravityRate = 0.000009;

    /**
     * Initial! angle that bullet are shoot at.
     */
    private double angle;

    /**
     * Cannon that started this mess.
     */
    private Canon canon;

    /**
     * Has bullet successfully hit the ground or not yet.
     */
    private boolean alive = true;

    Bullet(CanonGame.GameSettings gameSettings, Canon canon) {
        super(gameSettings);
        this.canon = canon;
    }

    /**
     * Updates coordinates on the screen with current gameScreen sizes.
     */
    private void resize() {
        x = xRate * getGameScreenWidth();
        y = yRate * getGameScreenHeight();

        double minimumDimension = getGameScreenWidth();
        if (getGameScreenHeight() < minimumDimension) {
            minimumDimension = getGameScreenHeight();
        }

        diameter = diameterRate * minimumDimension;
    }

    @Override
    public void draw() {
        xRate += initialXSpeedRate * TICK;

        long time = TICK * ticksPassed;
        yRate = initialYRate + initialYSpeedRate * time + (gravityRate * time * time) / 2;

        resize();

        for (var trianle : canon.getTerrain().getTriangles()) {
            var line1 = DrawableObject.Line.getLineByTwoPoint(trianle.getLeftPoint(), trianle.getHighPoint());
            var line2 = DrawableObject.Line.getLineByTwoPoint(trianle.getHighPoint(), trianle.getRightPoint());
            if (checkForExplosion(line1) || checkForExplosion(line2)) {
                break;
            }
        }

        GraphicsContext graphics = getGraphics();
        graphics.setFill(Color.BLACK);
        graphics.fillOval(x - diameter /2, y - diameter, diameter, diameter); //??????????????????????????????????????
        //I HATE GEOMETRY

        ticksPassed++;
    }

    /**
     * Distance between two points.
     */
    private double getDistBetweenPoints(Point2D a, Point2D b) {
        return getDistBetweenPoints(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private double getDistBetweenPoints(double x1, double y1, double x2, double y2) {
        double dxPoint = x1 - x2;
        double dyPoint = y1 - y2;
        return Math.sqrt(dxPoint * dxPoint + dyPoint * dyPoint);
    }

    /**
     * True if this bullet are close enough to the given line and therefore should explode.
     */
    private boolean checkForExplosion(Line line) {
        var ratePoint = new Point2D(xRate, yRate - diameterRate/2);

        double minDist = Math.abs(line.applyPoint(ratePoint)) / (Math.sqrt(line.getA() * line.getA() + line.getB() * line.getB()));

        double distToPoint1 = getDistBetweenPoints(ratePoint, line.getBeginPoint());
        double distToPoint2 = getDistBetweenPoints(ratePoint, line.getEndPoint());

        Line perpLine = DrawableObject.Line.getNormalLineViaPoint(line, ratePoint);
        double d1 = perpLine.applyPoint(line.getBeginPoint());
        double d2 = perpLine.applyPoint(line.getEndPoint());

        if (d1 * d2 > 0) {
            minDist = Math.min(distToPoint1, distToPoint2);
        }

        if (minDist <= diameterRate /2) {
            alive = false;

            for (var target : canon.getTerrain().getTargets()) {
                if (getDistBetweenPoints(xRate, yRate - diameterRate/2, target.getxRate(), target.getyRate()) <= explosionRate) {
                    target.kill();
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public boolean isAlive() {
        return alive;
    }

    private double getMass() {
        return mass;
    }

    void setMass(double mass) {
        this.mass = mass;

        speedRate /= mass;
    }

    private double getDiameterRate() {
        return diameterRate;
    }

    void setDiameterRate(double diameterRate) {
        this.diameterRate = diameterRate;
    }

    private double getxRate() {
        return xRate;
    }

    void setxRate(double xRate) {
        this.xRate = xRate;
    }

    private double getyRate() {
        return yRate;
    }

    void setyRate(double yRate) {
        this.yRate = yRate;

        initialYRate = yRate;
    }

    private double getExplosionRate() {
        return explosionRate;
    }

    void setExplosionRate(double explosionRate) {
        this.explosionRate = explosionRate;
    }

    void setAngle(double angle) {
        this.angle = angle;

        if (mass < 0) {
            throw new IllegalStateException("Angle cannot be assigned before mass");
        }

        initialXSpeedRate = speedRate * Math.cos(Math.toRadians(angle));
        initialYSpeedRate = speedRate * Math.sin(Math.toRadians(angle));
    }
}
