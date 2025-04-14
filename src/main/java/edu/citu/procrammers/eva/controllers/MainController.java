package edu.citu.procrammers.eva.controllers;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public AnchorPane mainScreen;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void setPage(Node node) {
        mainScreen.getChildren().clear();
        mainScreen.getChildren().addAll(node);
    }
}
