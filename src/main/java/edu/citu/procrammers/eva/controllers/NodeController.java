package edu.citu.procrammers.eva.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;


public class NodeController {
    @FXML
    StackPane stackPane;
    @FXML
    Circle circle;
    @FXML
    Text text;
//    @FXML
//    private final StackPane rootPane; // or VBox, Pane, etc.

//    NodeController(StackPane rootPane) {
//        this.rootPane = rootPane;
//    }

//    public javafx.scene.Node getRoot() {
//        return rootPane;
//    }

    public void initialize() {

    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public Circle getCircle() {
        return circle;
    }

    public void setCircle(Circle circle) {
        this.circle = circle;
    }

    public Text getText() {
        return text;
    }

    public void setText(String text) {
        this.text.setText(text);
    }
}
