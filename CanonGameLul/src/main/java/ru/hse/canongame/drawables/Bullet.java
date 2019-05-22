package ru.hse.canongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.canongame.CanonGame;
import ru.hse.canongame.Main;

public class Bullet extends DrawableObject {
    private static final int TICK = Main.TICK;

    private long ticksPassed = 0;

    private double mass = -1;

    private double diameterRate;

    private double explosionRate;

    private double xRate;
    private double yRate;

    private double initialYRate;

    private double x;
    private double y;

    private double diameter;

    private double speedRate = 0.0006;
    private double initialXSpeedRate;
    private double initialYSpeedRate;

    private static final double gravityRate = 0.000009;

    private double angle;

    private Canon canon;

    private boolean alive = true;

    public Bullet(CanonGame.GameSettings gameSettings, Canon canon) {
        super(gameSettings);
        this.canon = canon;
    }

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
        graphics.fillOval(x - diameter /2, y - diameter, diameter, diameter);

        ticksPassed++;
    }

    private double getDistBetweenPoints(Point2D a, Point2D b) {
        return getDistBetweenPoints(a.getX(), a.getY(), b.getX(), b.getY());
    }

    private double getDistBetweenPoints(double x1, double y1, double x2, double y2) {
        double dxPoint = x1 - x2;
        double dyPoint = y1 - y2;
        return Math.sqrt(dxPoint * dxPoint + dyPoint * dyPoint);
    }

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

    public double getMass() {
        return mass;
    }

    public void setMass(double mass) {
        this.mass = mass;

        speedRate /= mass;
    }

    public double getDiameterRate() {
        return diameterRate;
    }

    public void setDiameterRate(double diameterRate) {
        this.diameterRate = diameterRate;
    }

    public double getxRate() {
        return xRate;
    }

    public void setxRate(double xRate) {
        this.xRate = xRate;
    }

    public double getyRate() {
        return yRate;
    }

    public void setyRate(double yRate) {
        this.yRate = yRate;

        initialYRate = yRate;
    }

    public double getExplosionRate() {
        return explosionRate;
    }

    public void setExplosionRate(double explosionRate) {
        this.explosionRate = explosionRate;
    }

    public void setAngle(double angle) {
        this.angle = angle;

        if (mass < 0) {
            throw new IllegalStateException("Angle cannot be assigned before mass");
        }

        initialXSpeedRate = speedRate * Math.cos(Math.toRadians(angle));
        initialYSpeedRate = speedRate * Math.sin(Math.toRadians(angle));
    }
}
