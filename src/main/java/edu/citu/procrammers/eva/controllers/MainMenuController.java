package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

import static edu.citu.procrammers.eva.utils.Constant.Page.Menu;

public class MainMenuController {

    public ImageView imgPlayBtn, imgLogo;

    @FXML
    public void initialize() {

        imgPlayBtn.setOnMouseClicked(e -> NavService.navigateTo(Menu));
    }

}
