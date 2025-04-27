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

        KeyValue kvLogoUp = new KeyValue(imgLogo.translateYProperty(), -850, Interpolator.EASE_IN);
        KeyFrame kfLogoUp = new KeyFrame(Duration.seconds(.5), kvLogoUp);
        slideOutTimeline.getKeyFrames().add(kfLogoUp);

        KeyValue kvPlayDown = new KeyValue(imgPlayBtn.translateYProperty(), 850, Interpolator.EASE_IN);
        KeyFrame kfPlayDown = new KeyFrame(Duration.seconds(.5), kvPlayDown);
        slideOutTimeline.getKeyFrames().add(kfPlayDown);


        double centerX = imgProjectEvaBG.getBoundsInLocal().getWidth() / 2;
        double centerY = imgProjectEvaBG.getBoundsInLocal().getHeight() / 2;

        ParallelTransition zoomTransition = new ParallelTransition();

        Timeline scaleTimeline = new Timeline();
        KeyValue kvScaleX = new KeyValue(imgProjectEvaBG.scaleXProperty(), 1.6, Interpolator.EASE_OUT);
        KeyValue kvScaleY = new KeyValue(imgProjectEvaBG.scaleYProperty(), 1.6, Interpolator.EASE_OUT);
        KeyFrame kfZoom = new KeyFrame(Duration.seconds(0.8), kvScaleX, kvScaleY);
        scaleTimeline.getKeyFrames().add(kfZoom);

        Timeline translateTimeline = new Timeline();
        KeyValue kvTranslateX = new KeyValue(imgProjectEvaBG.translateXProperty(), -centerX * .6, Interpolator.EASE_OUT);
        KeyValue kvTranslateY = new KeyValue(imgProjectEvaBG.translateYProperty(), -centerY * .6, Interpolator.EASE_OUT);
        KeyFrame kfTranslate = new KeyFrame(Duration.seconds(0.8), kvTranslateX, kvTranslateY);
        translateTimeline.getKeyFrames().add(kfTranslate);

        Timeline bgOffsetTimeline = new Timeline();
        KeyValue kvOffsetX= new KeyValue(imgProjectEvaBG.translateXProperty(), 0, Interpolator.EASE_OUT);
        KeyValue kvOffsetY = new KeyValue(imgProjectEvaBG.translateYProperty(), -100, Interpolator.EASE_OUT);
        KeyFrame kfOffset = new KeyFrame(Duration.seconds(0.8), kvOffsetX, kvOffsetY);
        bgOffsetTimeline.getKeyFrames().add(kfOffset);

        zoomTransition.getChildren().addAll(scaleTimeline, translateTimeline, bgOffsetTimeline);


        final double originalX = spLoginContainer.getTranslateX();
        spLoginContainer.translateXProperty().set(originalX);

        Timeline loginContainerTimeline = new Timeline();
        KeyValue kvLoginContainerY = new KeyValue(spLoginContainer.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kfLoginContainer = new KeyFrame(Duration.seconds(0.5), kvLoginContainerY);
        loginContainerTimeline.getKeyFrames().add(kfLoginContainer);
        spLoginContainer.setVisible(true);

        zoomTransition.play();
        slideOutTimeline.play();
        loginContainerTimeline.play();
        fadePane.layoutXProperty().set(0);
        fadePane.layoutYProperty().set(0);
    }

    public void handleRegister() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        boolean shouldShow = !spRegisterContainer.isVisible();
        spRegisterContainer.setVisible(shouldShow);
        spRegisterContainer.setDisable(!shouldShow);
    }

    public void handleLogin() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        fadePane.layoutXProperty().set(0);
        fadePane.layoutYProperty().set(0);
        System.out.println("FadePane Y Property: " + fadePane.getLayoutY());;
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> {
            SoundManager.fadeOutMusic();
            System.out.println("(In SOF Func) FadePane Y Property: " + fadePane.getLayoutY());;
            NavService.navigateTo(Loading);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> NavService.navigateTo(Selection));
            delay.play();
        });

        System.out.println("(Out SOF Func) FadePane Y Property: " + fadePane.getLayoutY());;
        fadeOut.play();
    }
}
