package ru.hse.canongame.drawables;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * TODO
 */
public class Canon extends DrawableObject {
    private int x = 0;
    private int y = 0;

    public Canon(GraphicsContext graphicsContext) {
        super(graphicsContext);
    }

    @Override
    public void draw() {
        GraphicsContext graphics = getGraphics();
        graphics.setFill(Color.BLUE);
        graphics.fillRect(x, y, 100, 100);
        x += 10;
        y += 10;
    }
}
