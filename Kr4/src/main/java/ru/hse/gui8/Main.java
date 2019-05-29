package ru.hse.gui8;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Class for majong game.
 */
public class Main extends Application {
    /**
     * Size of the board. Can be odd.
     */
    private static int n;

    /**
     * Main stage of the window.
     */
    private Stage primaryStage;

    /**
     * State of the game.
     */
    private GridState gridState;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;

        primaryStage.setTitle("Find pair");

        Group root = new Group();
        Scene scene = new Scene(root);
        //primaryStage.setFullScreen(true);
        //primaryStage.setResizable(false);

        GridPane gridpane = new GridPane();
        gridState = new GridState(n);

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int xPos = i;
                final int yPos = j;

                gridState.buttons[i][j] = new Button();

                if (!gridState.centralCoordinates.equals(i, j)) {
                    gridState.buttons[i][j].setOnAction(event -> {
                        if (!gridState.canPressButtons) {
                            return;
                        }

                        gridState.react(new GridState.Coordinates(xPos, yPos));
                    });
                } else {
                    gridState.buttons[i][j].setDisable(true);
                }

                gridpane.add(gridState.buttons[i][j], i, j);
            }
        }

        root.getChildren().add(gridpane);

        primaryStage.setMinWidth(300);
        primaryStage.setMinHeight(300);

        primaryStage.widthProperty().addListener((observableValue, number, number2) -> setCurrentWidthToStage(number2, gridState.buttons));
        primaryStage.heightProperty().addListener((observableValue, number, number2) -> setCurrentHeightToStage(number2, gridState.buttons));

        /*Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth() - 50); //So it would be easy to drag window...
        primaryStage.setHeight(primaryScreenBounds.getHeight() - 50);
         */


        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Resize buttons after windows width changes.
     */
    private void setCurrentWidthToStage(Number number2, Button[][] buttons) {
        primaryStage.setWidth((double) number2);

        resize(buttons);
    }

    /**
     * Resize buttons after windows height changes.
     */
    private void setCurrentHeightToStage(Number number2, Button[][] buttons) {
        primaryStage.setHeight((double) number2);

        resize(buttons);
    }

    /**
     * Button's margin from window's bottom.
     */
    private static final int MARGIN_HEIGHT = 100;

    /**
     * Button's margin from window's right border.
     */
    private static final int MARGIN_WIDTH = 100;


    /**
     * Resize buttons after windows size changes.
     */
    private void resize(Button[][] buttons) {
        double minWidth = (primaryStage.getWidth() - MARGIN_HEIGHT) / n;
        double minHeight = (primaryStage.getHeight() - MARGIN_WIDTH) / n;

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                buttons[i][j].setMinSize(minWidth, minHeight);
                //buttons[i][j].setMaxSize(minWidth, minHeight);
            }
        }
    }

    /**
     * Current state of the game.
     */
    //Not private because I won't be able to test it otherwise :(
    static class GridState {
        /**
         * True if user can press buttons..
         */
        private volatile boolean canPressButtons = true;

        /**
         * Buttons on the screen.
         */
        private Button[][] buttons;

        /**
         * Executor of doing stuff after user press the wrong pair of cells.
         */
        private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

        /**
         * Coordinates on the screen (of the button, in [0, n) x [0, n] )
         */
        private static class Coordinates {
            private int x;
            private int y;

            private int getX() {
                return x;
            }

            private int getY() {
                return y;
            }

            private Coordinates(int x, int y) {
                this.x = x;
                this.y = y;
            }

            /**
             * Returns button with given coordinate in the array.
             */
            private Button getButtonByCoordinate(Button[][] buttons) {
                return buttons[x][y];
            }

            /**
             * Returns int with given coordinates in the array.
             */
            private int getIntByCoordinate(int[][] numbers) {
                return numbers[x][y];
            }

            private boolean equals(Coordinates coordinates) {
                return this.x == coordinates.getX() && this.y == coordinates.getY();
            }

            private boolean equals(int x, int y) {
                return this.x == x && this.y == y;
            }
        }


        /**
         * If n is odd, coordinates of the central button, otherwise (-1, -1).
         * If n is odd, central button is empty and not-clickable.
         */
        @NotNull
        private final Coordinates centralCoordinates;

        /**
         * Numbers corresponds to the buttons.
         */
        private int[][] numbers;

        public int[][] getNumbers() {
            return numbers;
        }

        /**
         * Last pressed button (if it was not a pair).
         */
        private Coordinates lastPressed;

        /**
         * Number of clickable cells.
         */
        private int leftCells;

        GridState(int n) {
            buttons = new Button[n][n];
            numbers = new int[n][n];
            if (n % 2 == 1) {
                centralCoordinates = new Coordinates(n / 2, n / 2);
            } else {
                centralCoordinates = new Coordinates(-1, -1);
            }

            leftCells = n * n;
            if (n % 2 == 1) {
                leftCells--;
            }

            var numbersToAdd = new LinkedList<Integer>();
            for (int i = 0; i < n*n/2; i++) {
                numbersToAdd.add(i);
                numbersToAdd.add(i);
            }
            Collections.shuffle(numbersToAdd);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (centralCoordinates.equals(i, j)) {
                        numbers[i][j] = -1;
                        continue;
                    }

                    numbers[i][j] = numbersToAdd.pop();
                }
            }
        }

        /**
         * Reaction on the pressed button.
         */
        private void react(final Coordinates coordinates) {
            coordinates.getButtonByCoordinate(buttons).setText(String.valueOf(coordinates.getIntByCoordinate(numbers)));

            if (lastPressed != null) {
                if (lastPressed.getIntByCoordinate(numbers) == coordinates.getIntByCoordinate(numbers)) {
                    lastPressed.getButtonByCoordinate(buttons).setDisable(true);
                    coordinates.getButtonByCoordinate(buttons).setDisable(true);
                    leftCells -= 2;

                    if (leftCells == 0) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);

                        alert.setTitle("Victory!");
                        alert.setHeaderText(null);
                        alert.setContentText("You won!!!");

                        alert.showAndWait();
                    }
                } else {
                    final Coordinates localLastPressed = lastPressed;

                    canPressButtons = false;
                    executor.schedule(() -> {
                        Platform.runLater(() -> {
                            localLastPressed.getButtonByCoordinate(buttons).setText("");
                            coordinates.getButtonByCoordinate(buttons).setText("");
                            canPressButtons = true;
                        });
                    }, 300, TimeUnit.MILLISECONDS);
                }

                lastPressed = null;
            } else {
                lastPressed = coordinates;
            }
        }
    }

    @Override
    public void stop() {
        gridState.executor.shutdown();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Wrong number of arguments");
            return;
        }

        try {
            n = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Given argument is not integer");
            return;
        }

        if (n <= 1) {
            System.out.println("Given argument cannot be less or equal to 1");
            return;
        }

        launch(args);
    }
}
