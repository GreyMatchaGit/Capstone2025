package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.Loading;
import static edu.citu.procrammers.eva.utils.Constant.Page.Login;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class MainMenuController {

    public ImageView imgPlayBtn, imgLogo;
    public Pane fadePane;

    @FXML
    public void initialize() {

        SoundManager.playBackgroundMusic("music/main_menu_music.m4a", true);
        setupGlow(imgPlayBtn);

        Timeline logoPulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imgLogo.scaleXProperty(), 0.9),
                        new KeyValue(imgLogo.scaleYProperty(), 0.9)
                ),
                new KeyFrame(Duration.seconds(1.5),
                        new KeyValue(imgLogo.scaleXProperty(), 1.1),
                        new KeyValue(imgLogo.scaleYProperty(), 1.1)
                )
        );
        logoPulseTimeline.setCycleCount(Animation.INDEFINITE);
        logoPulseTimeline.setAutoReverse(true);
        logoPulseTimeline.play();

        Timeline playBtnPulseTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imgPlayBtn.scaleXProperty(), 0.9),
                        new KeyValue(imgPlayBtn.scaleYProperty(), 0.9)
                ),
                new KeyFrame(Duration.seconds(2),
                        new KeyValue(imgPlayBtn.scaleXProperty(), 1.05),
                        new KeyValue(imgPlayBtn.scaleYProperty(), 1.05)
                )
        );
        playBtnPulseTimeline.setCycleCount(Animation.INDEFINITE);
        playBtnPulseTimeline.setAutoReverse(true);
        playBtnPulseTimeline.play();

        imgPlayBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> SoundManager.fadeOutMusic(() -> NavService.navigateTo(Login)));
            fadeOut.play();
        });


    }
}
