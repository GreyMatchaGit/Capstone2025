package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class MainMenuController {

    public Pane fadePane;
    public ImageView imgLoginBtn, imgRegisterBtn, imgProjectEvaBG, imgPlayBtn, imgLogo;
    public TextField tfUsername, tfPassword, tfRegisterUsername, tfRegisterPassword;
    public StackPane spRegisterContainer, spLoginContainer;

    private Timeline logoPulseTimeline;
    private Timeline playBtnPulseTimeline;

    @FXML
    public void initialize() {
        SoundManager.playBackgroundMusic("music/main_menu_music.m4a", true);
        setupGlow(imgLoginBtn, imgRegisterBtn, imgPlayBtn);

        imgProjectEvaBG.setPreserveRatio(true);

        imgProjectEvaBG.setScaleX(1.0);
        imgProjectEvaBG.setScaleY(1.0);

        imgProjectEvaBG.setTranslateX(0);
        imgProjectEvaBG.setTranslateY(0);

        logoPulseTimeline = new Timeline(
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

        playBtnPulseTimeline = new Timeline(
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
            logoPulseTimeline.stop();
            playBtnPulseTimeline.stop();

            imgLogo.setScaleX(1.0);
            imgLogo.setScaleY(1.0);
            imgPlayBtn.setScaleX(1.0);
            imgPlayBtn.setScaleY(1.0);

            SoundManager.fadeOutMusic(this::playEntranceAnimations);
        });
    }

    private void playEntranceAnimations() {

        Timeline slideOutTimeline = new Timeline();

        KeyValue kvLogoUp = new KeyValue(imgPlayBtn.translateYProperty(), -350, Interpolator.EASE_IN);
        KeyValue kvLogoFadeOut = new KeyValue(imgLogo.opacityProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kfLogoFadeOut = new KeyFrame(Duration.seconds(0.5), kvLogoFadeOut, kvLogoUp);
        slideOutTimeline.getKeyFrames().add(kfLogoFadeOut);

        KeyValue kvPlayDown = new KeyValue(imgPlayBtn.translateYProperty(), 350, Interpolator.EASE_IN);
        KeyValue kvPlayFadeOut = new KeyValue(imgPlayBtn.opacityProperty(), 0, Interpolator.EASE_IN);
        KeyFrame kfPlayFadeOut = new KeyFrame(Duration.seconds(0.5), kvPlayFadeOut, kvPlayDown);
        slideOutTimeline.getKeyFrames().add(kfPlayFadeOut);

        slideOutTimeline.setOnFinished(e -> {
            imgPlayBtn.setVisible(false);
            imgPlayBtn.setDisable(true);
            imgLogo.setVisible(false);
            imgLogo.setDisable(true);
        });

        double centerY = imgProjectEvaBG.getBoundsInLocal().getHeight() / 2;

        ParallelTransition zoomTransition = new ParallelTransition();

        Timeline scaleTimeline = new Timeline();
        KeyValue kvScaleX = new KeyValue(imgProjectEvaBG.scaleXProperty(), 1.6, Interpolator.EASE_OUT);
        KeyValue kvScaleY = new KeyValue(imgProjectEvaBG.scaleYProperty(), 1.6, Interpolator.EASE_OUT);
        KeyFrame kfZoom = new KeyFrame(Duration.seconds(0.8), kvScaleX, kvScaleY);
        scaleTimeline.getKeyFrames().add(kfZoom);



        zoomTransition.getChildren().addAll(scaleTimeline);

        Timeline loginContainerTimeline = new Timeline();
        KeyValue kvLoginContainerOpacity = new KeyValue(spLoginContainer.opacityProperty(), 1, Interpolator.EASE_OUT);
        KeyFrame kfLoginContainer = new KeyFrame(Duration.seconds(0.5), kvLoginContainerOpacity);
        loginContainerTimeline.getKeyFrames().add(kfLoginContainer);

        spLoginContainer.setOpacity(0);
        spLoginContainer.setVisible(true);
        loginContainerTimeline.play();

        zoomTransition.play();
        slideOutTimeline.play();
    }

    public void handleRegister() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        boolean shouldShow = !spRegisterContainer.isVisible();
        spRegisterContainer.setVisible(shouldShow);
        spRegisterContainer.setDisable(!shouldShow);
    }

    public void handleLogin() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> {
            SoundManager.fadeOutMusic();
            NavService.navigateTo(Loading);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> NavService.navigateTo(Selection));
            delay.play();
        });

        fadeOut.play();
    }
}
