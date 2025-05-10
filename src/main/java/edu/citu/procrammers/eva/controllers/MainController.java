package edu.citu.procrammers.eva.controllers;

import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public Group scaleableGroup;
    public AnchorPane mainScreen;

    private Stage primaryStage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private double baseWidth = -1;
    private double baseHeight = -1;

    public void setPage(Node node) {
        mainScreen.getChildren().clear();
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        mainScreen.getChildren().addAll(node);
    }
}
