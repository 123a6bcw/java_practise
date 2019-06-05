package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.cannongame.CanonGame;

import java.util.ArrayList;
import java.util.List;

/**
 * Game field. Contains "mountains" and targets to shoot.
 */
public class Terrain extends DrawableObject {
    /**
     * Game field consist of mountain-triangles (that could also be lines).
     * Triangles (and targets) are drawable object, but they are attached only to the terrain object.
     */
    private List<Triangle> triangles = new ArrayList<>();

    /**
     * Targets to shoot.
     */
    private List<Target> targets = new ArrayList<>();

    public Terrain(CanonGame.GameSettings gameSettings) {
        super(gameSettings);

        /*
        Hardcoded creation of the game field.
        It's rather simple, but whatever.
         */
        Triangle triangle = new Triangle(gameSettings);

        triangle.setLeftPoint(new Point2D(0, 1));
        triangle.setHighPoint(new Point2D(0.15, 0.35));
        triangle.setRightPoint(new Point2D(0.3, 0.90));

        triangles.add(triangle);

        triangle = new Triangle(gameSettings);
        triangle.setLeftPoint(new Point2D(0.3, 0.90));
        triangle.setHighPoint(new Point2D(0.6, 0.90));
        triangle.setRightPoint(new Point2D(0.6, 0.90));

        triangles.add(triangle);

        triangle = new Triangle(gameSettings);
        triangle.setLeftPoint(new Point2D(0.6, 0.90));
        triangle.setHighPoint(new Point2D(0.7, 0.30));
        triangle.setRightPoint(new Point2D(0.8, 0.70));

        triangles.add(triangle);

        triangle = new Triangle(gameSettings);
        triangle.setLeftPoint(new Point2D(0.8, 0.70));
        triangle.setHighPoint(new Point2D(1, 0.30));
        triangle.setRightPoint(new Point2D(1, 0.30));

        triangles.add(triangle);

        var target = new Target(gameSettings, 0.5, 0.90);
        targets.add(target);
        target = new Target(gameSettings, 0.7, 0.30);
        targets.add(target);
    }

    /**
     * Draws all triangles and targets, destory target's that supposed to be destroyed.
     */
    @Override
    public void draw() {
        for (var triangle : triangles) {
            triangle.draw();
        }

        for (var iterator = targets.iterator(); iterator.hasNext();) {
            var target = iterator.next();
            if (!target.isAlive()) {
                iterator.remove();
            } else {
                target.draw();
            }
        }
    }

    /**
     * Terrain is always alive.
     */
    @Override
    public boolean isAlive() {
        return true;
    }

    //Package private, because other classes uses this triangles. As I said, a lot of package-private guys.
    //Yeah, I could put all drawables guys in one class, but it's like super awkward.
    List<Triangle> getTriangles() {
        return triangles;
    }

    public List<Target> getTargets() {
        return targets;
    }

    /**
     * Class that represent a drawable... triangle!
     *
     * As triangles forms mountain, all triangles oriented from left to right from down to up and then from up to down.
     * So there is leftPoint, which goes up-right to the highPoint, and then from highPoint down-right to the rightPoint.
     */
    class Triangle extends DrawableObject {
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

        private Triangle(CanonGame.GameSettings gameSettings) {
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

        Point2D getLeftPoint() {
            return leftPoint;
        }

        private void setLeftPoint(Point2D leftPoint) {
            this.leftPoint = leftPoint;
        }

        Point2D getHighPoint() {
            return highPoint;
        }

        private void setHighPoint(Point2D highPoint) {
            this.highPoint = highPoint;
        }

        Point2D getRightPoint() {
            return rightPoint;
        }

        private void setRightPoint(Point2D rightPoint) {
            this.rightPoint = rightPoint;
        }

        /**
         * Transform point from rate -relation coordinate to the coordinates in pixel on canvas.
         */
        private Point2D transformToPixels(Point2D point) {
            return new Point2D(point.getX() * getGameScreenWidth(), point.getY() * getGameScreenHeight());
        }
    }

    /**
     * Target to shoot.
     *
     * Package-private as it interacts with bullets (poor little thing).
     */
    class Target extends DrawableObject {
        /**
         * Coefficient from 0 to 1, representing position of the point on the screen from 0 to gameScreenWidth.
         */
        private double xRate;

        /**
         * Same for height.
         */
        private double yRate;

        /**
         * Was target successfully destroyed.
         */
        private boolean alive = true;

        private Target(CanonGame.GameSettings gameSettings, double xRate, double yRate) {
            super(gameSettings);
            this.xRate = xRate;
            this.yRate = yRate;
        }

        /**
         * Small red square.
         */
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

        /**
         * Destroy poor little target.
         */
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
}