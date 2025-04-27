package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;

public class ConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgProfileBtn;

    @FXML
    public void initialize() {
        imgBackMenuBtn.setOnMouseClicked(e -> NavService.navigateTo(Selection));

    }
}
