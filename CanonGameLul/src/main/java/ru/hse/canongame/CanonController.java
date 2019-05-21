package ru.hse.canongame;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Stage;

public class CanonController {

    @FXML protected GridPane gridPane;
    private static final int SIZE = 3;

    private Double wid;
    private Double hei;

    @FXML
    public void initialize() {
    }

    @FXML
    protected void close(ActionEvent event) throws InterruptedException {
        ((Stage) gridPane.getScene().getWindow()).close();
    }
}