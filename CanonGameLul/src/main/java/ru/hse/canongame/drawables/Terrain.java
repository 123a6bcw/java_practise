package ru.hse.canongame.drawables;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import ru.hse.canongame.CanonGame;

import java.util.ArrayList;
import java.util.List;

public class Terrain extends DrawableObject {
    private List<Triangle> triangles = new ArrayList<>();

    private List<Target> targets = new ArrayList<>();

    public Terrain(CanonGame.GameSettings gameSettings) {
        super(gameSettings);

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

    @Override
    public boolean isAlive() {
        return true;
    }

    List<Triangle> getTriangles() {
        return triangles;
    }

    public List<Target> getTargets() {
        return targets;
    }

    class Triangle extends DrawableObject {
        private Point2D leftPoint;
        private Point2D highPoint;
        private Point2D rightPoint;


        public Triangle(CanonGame.GameSettings gameSettings) {
            super(gameSettings);
        }

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

        private Point2D transformToPixels(Point2D point) {
            return new Point2D(point.getX() * getGameScreenWidth(), point.getY() * getGameScreenHeight());
        }
    }
}