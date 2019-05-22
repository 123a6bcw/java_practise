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

    @Override
    public void start(Stage primaryStage) throws Exception{
        Group root = new Group();
        Scene scene = new Scene(root);

        primaryStage.setTitle("Cannon game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.setFullScreen(false); //TODO true

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        final Canvas canvas = new Canvas(primaryScreenBounds.getWidth(), primaryScreenBounds.getHeight());
        GraphicsContext graphic = canvas.getGraphicsContext2D();

        root.getChildren().add(canvas);
        primaryStage.show();

        game = new CanonGame(graphic);

        startCycle();
    }

    private void startCycle() {
        var keyFrame = new KeyFrame(Duration.millis(TICK), ae -> {
            game.drawObjects();
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