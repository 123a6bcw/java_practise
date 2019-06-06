package ru.hse.cannongame;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * Class that set-ups application and it's settings and runs the game.
 */
public class Main extends Application {
    /**
     * How often, in mls, does the game field update.
     */
    public static final int TICK = 10; //Public, because Bullet class also uses this TICK. I guess, it's okay to make public static final fields.

    /**
     * Game controller.
     */
    private CannonGame game;

    /**
     * An infinitive loop of redrawing game field.
     */
    private Timeline timeline;

    /**
     * Infinity number of cycles of redrawing. Should be for about 5 days non-stop-playing, but actually
     * cycle repeats itself once ends.
     */
    private static final int INFINITY = 2000000000;

    /**
     * Game settings, mostly screen's width and height. Yeap, game supports resizability!
     */
    private CannonGame.GameSettings gameSettings;

    /**
     * Canvas!
     */
    private Canvas canvas;

    /**
     * Stage!!
     */
    private Stage primaryStage;

    /**
     * As I strongly believe (a foolish thought perhaps, but) 10mls TICK is more than enough to redraw the whole field,
     * thread that answers the user's command and thread that redraw objects on the map syncronize over this
     * object.
     */
    private final Object drawLock = new Object();

    /**
     * Change game settings and canvas to correspond current window's size.
     */
    private void resize() {
        gameSettings.setWidth(primaryStage.getWidth());
        gameSettings.setHeight(primaryStage.getHeight());

        if (canvas != null) {
            canvas.setWidth(gameSettings.getWidth());
            canvas.setHeight(gameSettings.getHeight());
        }
    }

    @Override
    public void start(Stage primaryStage) {
        var root = new Group();
        var scene = new Scene(root);
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Cannon game");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        //primaryStage.setFullScreen(false); // NOPE

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth() - 50); //So it would be easy to drag window...
        primaryStage.setHeight(primaryScreenBounds.getHeight() - 50);

        primaryStage.setMinWidth(600);
        primaryStage.setMinHeight(600);

        gameSettings = new CannonGame.GameSettings();

        resize();

        canvas = new Canvas(gameSettings.getWidth(), gameSettings.getHeight());
        gameSettings.setGraphicsContext(canvas.getGraphicsContext2D());

        root.getChildren().add(canvas);
        primaryStage.show();

        game = new CannonGame(gameSettings);

        scene.setOnKeyPressed(event -> {
            synchronized (drawLock) {
                switch (event.getCode()) {
                    case LEFT: case A:
                        game.applyCommand(CannonGame.Command.LEFT);
                        break;
                    case RIGHT: case D:
                        game.applyCommand(CannonGame.Command.RIGHT);
                        break;
                    case W: case UP:
                        game.applyCommand(CannonGame.Command.ROTATE_LEFT);
                        break;
                    case S: case DOWN:
                        game.applyCommand(CannonGame.Command.ROTATE_RIGHT);
                        break;
                    case SPACE:
                        game.applyCommand(CannonGame.Command.FIRE);
                        break;
                    case DIGIT1:
                        game.applyCommand(CannonGame.Command.SMALL_BOMB);
                        break;
                    case DIGIT2:
                        game.applyCommand(CannonGame.Command.BIG_BOMB);
                        break;
                }
            }
        });

        startCycle();
    }

    /**
     * Starts infinitive redrawing loop.
     */
    private void startCycle() {
        var keyFrame = new KeyFrame(Duration.millis(TICK), ae -> {
            synchronized (drawLock) {
                resize();
                gameSettings.getGraphicsContext().clearRect(0, 0, gameSettings.getWidth(), gameSettings.getHeight());
                game.drawObjects();

                if (game.getTerrain().getTargets().isEmpty()) {
                    timeline.stop();

                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setOnCloseRequest(event -> stop());
                        alert.setTitle("Cannon game");
                        alert.setHeaderText("Congratulations!");
                        alert.setContentText("You won!");
                        alert.showAndWait();
                    });
                }
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
        primaryStage.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}