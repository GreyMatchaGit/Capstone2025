package edu.citu.procrammers.eva.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public class ErrorController {
    public static String errorMsg = "Error";
    public Label lblErrorMsg;
    public Pane fadePane;

    @FXML
    public void initialize() {
        lblErrorMsg.setText(errorMsg);
    }
}
