package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.MainMenu;

public class SplashController {

    public ImageView imgGlow, imgLogo;

    @FXML
    public void initialize() {
        imgLogo.setOpacity(0);
        imgLogo.setScaleX(0.5);
        imgLogo.setScaleY(0.5);

        imgGlow.setOpacity(0);
        imgGlow.setScaleX(0);
        imgGlow.setScaleY(0);

        Timeline logoTimeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(imgLogo.opacityProperty(), 0),
                        new KeyValue(imgLogo.scaleXProperty(), 0.95),
                        new KeyValue(imgLogo.scaleYProperty(), 0.95)
                ),
                new KeyFrame(Duration.millis(800),
                        new KeyValue(imgLogo.opacityProperty(), 1.25),
                        new KeyValue(imgLogo.scaleXProperty(), 1.25),
                        new KeyValue(imgLogo.scaleYProperty(), 1.25)
                ),
                new KeyFrame(Duration.millis(1700),
                        new KeyValue(imgLogo.opacityProperty(), 1),
                        new KeyValue(imgLogo.scaleXProperty(), 1),
                        new KeyValue(imgLogo.scaleYProperty(), 1)
                )
        );

        Timeline glowTimeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(imgGlow.opacityProperty(), 0),
                        new KeyValue(imgGlow.scaleXProperty(), 0),
                        new KeyValue(imgGlow.scaleYProperty(), 0)
                ),
                new KeyFrame(Duration.millis(1100),
                        new KeyValue(imgGlow.opacityProperty(), 1.2),
                        new KeyValue(imgGlow.scaleXProperty(), 1.2),
                        new KeyValue(imgGlow.scaleYProperty(), 1.2)
                ),
                new KeyFrame(Duration.millis(2000),
                        new KeyValue(imgGlow.opacityProperty(), 1),
                        new KeyValue(imgGlow.scaleXProperty(), 1),
                        new KeyValue(imgGlow.scaleYProperty(), 1)
                )
        );

        PauseTransition initialPause = new PauseTransition(Duration.seconds(1.5));
        initialPause.setOnFinished(e -> {
            SoundManager.playBackgroundMusic("sfx/logo_ding.MP3", false);
            logoTimeline.play();
            glowTimeline.play();

            PauseTransition afterAnimation = new PauseTransition(Duration.seconds(3));
            afterAnimation.setOnFinished(event -> {
                SoundManager.stopMusic();
                NavService.navigateTo(MainMenu);
            });
            afterAnimation.play();
        });
        initialPause.play();
    }


}
