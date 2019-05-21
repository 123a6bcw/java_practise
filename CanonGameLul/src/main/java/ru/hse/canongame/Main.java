package ru.hse.canongame;

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

import java.util.Objects;

/*
Main class that simply runs application and has some general function.
 */
public class Main extends Application {

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

        System.out.println(scene.getWidth());

        root.getChildren().add(canvas);
        primaryStage.show();

        graphic.setFill(Color.BLUE);
        Thread thread = new Thread(() -> {
            for (int i = 1; i <= 100; i++) {
                graphic.fillRect(i, i, 10, 10);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    break;
                }
            }
        });
        thread.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}