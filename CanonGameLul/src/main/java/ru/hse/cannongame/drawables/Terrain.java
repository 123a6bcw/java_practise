package ru.hse.cannongame.drawables;

import javafx.geometry.Point2D;
import ru.hse.cannongame.GameSettings;

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

    public Terrain(GameSettings gameSettings) {
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
}