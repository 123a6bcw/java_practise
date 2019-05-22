package ru.hse.canongame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Objects;

public class Main extends Application {
    private static final int TICK = 10;

    private CanonGame game;
    private Timeline timeline;
    private static final int INFINITY = 2000000000;
    private double width;
    private double height;

    private GraphicsContext graphic;

    private final Object drawLock = new Object();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Cannon game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false); //TODO true

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        width = primaryScreenBounds.getWidth();
        height = primaryScreenBounds.getHeight();

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);

        final Canvas canvas = new Canvas(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        graphic = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        primaryStage.show();

        game = new CanonGame(graphic, width, height);

        scene.setOnKeyPressed(event -> {
            synchronized (drawLock) {
                switch (event.getCode()) {
                    case LEFT: case A:
                        game.applyCommand(CanonGame.Command.LEFT);
                        break;
                    case RIGHT: case D:
                        game.applyCommand(CanonGame.Command.RIGHT);
                        break;
                    case W: case UP:
                        game.applyCommand(CanonGame.Command.ROTATE_LEFT);
                        break;
                    case S: case DOWN:
                        game.applyCommand(CanonGame.Command.ROTATE_RIGHT);
                        break;
                    case SPACE:
                        game.applyCommand(CanonGame.Command.FIRE);
                        break;
                    case DIGIT1:
                        game.applyCommand(CanonGame.Command.SMALL_BOMB);
                        break;
                    case DIGIT2:
                        game.applyCommand(CanonGame.Command.BIG_BOMB);
                        break;
                }
            }
        });

        startCycle();
    }

    private void startCycle() {
        var keyFrame = new KeyFrame(Duration.millis(TICK), ae -> {
            synchronized (drawLock) {
                graphic.clearRect(0, 0, width, height);
                game.drawObjects();
            }
        });

        timeline = new Timeline(keyFrame);

        timeline.setAutoReverse(true);
        timeline.setCycleCount(INFINITY);
        timeline.setOnFinished(ae -> {
            timeline.stop();
            startCycle();
        });
        timeline.play();
    }

    @Override
    public void stop() {
        timeline.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }
}