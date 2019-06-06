package ru.hse.cannongame.drawables;

import javafx.scene.canvas.GraphicsContext;
import ru.hse.cannongame.GameSettings;

/**
 * Object that can be drawn on the GraphicContext object.
 */
public abstract class DrawableObject {
    /**
     * Actual game settings.
     */
    private GameSettings gameSettings;

    DrawableObject(GameSettings gameSettings) {
        this.gameSettings = gameSettings;
    }

    GraphicsContext getGraphics() {
        return gameSettings.getGraphicsContext();
    }

    /**
     * Draws this object on the GraphicsContext (provided by gameSettings).
     *
     * There are a lot of public and package-private guys in this project --- because I wanted to move all drawable guys to another package
     * and too lazy to redo it.
     */
    public abstract void draw();

    /**
     * False if object was destroyed etc, therefore no longer should be drawn on the screen.
     */
    public abstract boolean isAlive();

    double getGameScreenWidth() {
        return gameSettings.getWidth();
    }

    double getGameScreenHeight() {
        return gameSettings.getHeight();
    }

    GameSettings getGameSettings() {
        return gameSettings;
    }
}
