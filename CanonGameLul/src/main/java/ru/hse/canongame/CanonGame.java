package ru.hse.canongame;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.canongame.drawables.Canon;
import ru.hse.canongame.drawables.DrawableObject;

import java.util.ArrayList;
import java.util.List;

public class CanonGame {
    private GraphicsContext graphicsContext;
    private List<DrawableObject> objects = new ArrayList<>();

    public CanonGame(GraphicsContext graphicsContext) {
        this.graphicsContext = graphicsContext;

        objects.add(new Canon(graphicsContext));
    }

    void addObject(DrawableObject drawableObject) {
        objects.add(drawableObject);
    }

    void drawObjects() {
        for (var drawableObject : objects) {
            drawableObject.draw();
        }
    }
}