package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

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

        setupGlow(spAcademy, spConquest);
        setupScalingAnimation( 1.1, spAcademy, spConquest);

        spAcademy.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic();
                NavService.navigateTo(Academy);
            });

            fadeOut.play();
        });
        spConquest.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic();
                NavService.navigateTo(Conquest);
            });

            fadeOut.play();
        });

        spAcademy.setOnMouseEntered(e -> SoundManager.playSFX("sfx/academy_hover.mp3"));
        spConquest.setOnMouseEntered(e -> SoundManager.playSFX("sfx/conquest_hover.mp3"));

    }
}
