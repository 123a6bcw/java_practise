package ru.hse.cannongame;

import javafx.scene.canvas.GraphicsContext;

/**
 * Game settings, most important --- width and height, as they may change during the execution
 * and this class stores their actual values.
 */
public class GameSettings {
    private GraphicsContext graphicsContext;
    private double width;
    private double height;

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
}