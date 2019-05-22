package ru.hse.canongame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
    private CanonGame.GameSettings gameSettings;

    private Canvas canvas;
    private Stage primaryStage;

    private final Object drawLock = new Object();

    private void resize() {
        gameSettings.setWidth(primaryStage.getWidth());
        gameSettings.setHeight(primaryStage.getHeight());

        if (canvas != null) {
            canvas.setWidth(gameSettings.getWidth());
            canvas.setHeight(gameSettings.getHeight());
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene scene = new Scene(root);
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Cannon game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.setFullScreen(false); //TODO true

        primaryStage.widthProperty().addListener((observableValue, number, number2) -> setCurrentWidthToStage(number2));
        primaryStage.heightProperty().addListener((observableValue, number, number2) -> setCurrentHeightToStage(number2));

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth() - 50);
        primaryStage.setHeight(primaryScreenBounds.getHeight() - 50);

        gameSettings = new CanonGame.GameSettings();

        resize();

        canvas = new Canvas(gameSettings.getWidth(), gameSettings.getHeight());
        gameSettings.setGraphicsContext(canvas.getGraphicsContext2D());

        root.getChildren().add(canvas);
        primaryStage.show();

        game = new CanonGame(gameSettings);

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

    private void setCurrentWidthToStage(Number number2) {
        primaryStage.setWidth((double) number2);
    }

    private void setCurrentHeightToStage(Number number2) {
        primaryStage.setHeight((double) number2);
    }

    private void startCycle() {
        var keyFrame = new KeyFrame(Duration.millis(TICK), ae -> {
            synchronized (drawLock) {
                resize();
                gameSettings.getGraphicsContext().clearRect(0, 0, gameSettings.getWidth(), gameSettings.getHeight());
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