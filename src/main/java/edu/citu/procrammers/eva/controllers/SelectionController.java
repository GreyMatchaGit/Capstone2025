package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class SelectionController {

    public Pane fadePane;
    public StackPane spAcademy;
    public StackPane spConquest;
    public ImageView imgSettingsBtn;

    @FXML
    public void initialize() {
        imgSettingsBtn.setOnMouseClicked(e -> NavService.navigateTo(Settings));
        spAcademy.setOnMouseClicked(e -> NavService.navigateTo(Academy));
        spConquest.setOnMouseClicked(e -> NavService.navigateTo(Conquest));
    }
}
