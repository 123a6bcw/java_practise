package ru.hse.cannongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.cannongame.drawables.Terrain;

/**
 * Game settings, most important --- width and height, as they may change during the execution
 * and this class stores their actual values.
 */
public class GameSettings {
    /**
     * Object to draw with.
     */
    private GraphicsContext graphicsContext;

    /**
     * Current game screen size.
     */
    private double width;
    private double height;

    /**
     * Terrain game is played on.
     */
    private Terrain terrain;

    public GraphicsContext getGraphicsContext() {
        return graphicsContext;
    }

    public void setGraphicsContext(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
}