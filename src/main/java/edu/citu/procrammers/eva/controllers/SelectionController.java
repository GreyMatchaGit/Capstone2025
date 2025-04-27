package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupScalingAnimation;

public class SelectionController {

    public Pane fadePane;
    public StackPane spAcademy;
    public StackPane spConquest;
    public ImageView imgSettingsBtn;

    @FXML
    public void initialize() {

        setupGlow(spAcademy, spConquest, imgSettingsBtn);
        setupScalingAnimation( 1.1, spAcademy, spConquest);

        imgSettingsBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Settings);
        });
        spAcademy.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Academy);
        });
        spConquest.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Conquest);
        });


    }
}
