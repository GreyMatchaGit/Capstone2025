package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.data.Database.Error.*;
import static edu.citu.procrammers.eva.data.Database.Error.TOO_LONG_PASSWORD;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.MUSIC_MAINMENU;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class MainMenuController {

    public Pane fadePane;
    public ImageView imgProjectEvaBG, imgPlayBtn, imgLogo;
    public TextField tfUsername, tfPassword, tfRegisterUsername, tfRegisterPassword;
    public StackPane spRegisterContainer, spLoginContainer;
    public Label registerStatusMessage, loginStatusMessage;

    private static final boolean TESTING_MODE_ENABLED = true;

    private Timeline logoPulseTimeline;
    private Timeline playBtnPulseTimeline;

    @FXML
    public void initialize() {
        SoundManager.playBackgroundMusic(MUSIC_MAINMENU, true);
        setupGlow(imgPlayBtn);

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
            SoundManager.playSFX(SFX_BUTTON_CLICK);
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

        KeyValue kvLogoUp = new KeyValue(imgLogo.translateYProperty(), -250, Interpolator.EASE_IN);
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

        ParallelTransition zoomTransition = new ParallelTransition();

        Timeline scaleTimeline = new Timeline();
        KeyValue kvScaleX = new KeyValue(imgProjectEvaBG.scaleXProperty(), 1.6, Interpolator.EASE_OUT);
        KeyValue kvScaleY = new KeyValue(imgProjectEvaBG.scaleYProperty(), 1.6, Interpolator.EASE_OUT);
        KeyFrame kfZoom = new KeyFrame(Duration.seconds(0.8), kvScaleX, kvScaleY);
        scaleTimeline.getKeyFrames().add(kfZoom);

        zoomTransition.getChildren().addAll(scaleTimeline);

        Timeline loginContainerTimeline = new Timeline();
        KeyValue kvLoginContainerOpacity = new KeyValue(spLoginContainer.opacityProperty(), 1, Interpolator.EASE_OUT);
        KeyValue kvLoginContainrY = new KeyValue(spLoginContainer.translateYProperty(), 0, Interpolator.EASE_OUT);
        KeyFrame kfLoginContainer = new KeyFrame(Duration.seconds(0.5), kvLoginContainerOpacity, kvLoginContainrY);
        loginContainerTimeline.getKeyFrames().add(kfLoginContainer);


        zoomTransition.setOnFinished(e -> {
            spLoginContainer.setOpacity(0);
            spLoginContainer.setVisible(true);
            loginContainerTimeline.play();
        });

        zoomTransition.play();
        slideOutTimeline.play();
    }

    private void handleRegisterSuccessful() {
        tfUsername.setText(tfRegisterUsername.getText());
        tfPassword.setText(tfRegisterPassword.getText());

        if (TESTING_MODE_ENABLED) {
            System.out.println("TESTING MODE ENABLED for registration");
            handleRegister();
        } else {
            login();
        }
    }

    private void handleRegisterFailed(int errorCode) {
        switch (errorCode) {
            case USERNAME_TAKEN:
                registerStatusMessage.setText("Username is already taken.");
                break;
            case EMPTY_USERNAME:
                registerStatusMessage.setText("Please enter a username.");
                break;
            case EMPTY_PASSWORD:
                registerStatusMessage.setText("Please enter a password.");
                break;
            case EMPTY_USERNAME_PASSWORD:
                registerStatusMessage.setText("Please enter both username and password.");
                break;
            case TOO_LONG_USERNAME:
                registerStatusMessage.setText("Username must be 20 characters or fewer.");
                break;
            case TOO_LONG_PASSWORD:
                registerStatusMessage.setText("Password must be 25 characters or fewer.");
                break;
            default:
                registerStatusMessage.setText("Registration failed. Please try again.");
        }
    }


    private void handleLoginFailed() {
        loginStatusMessage.setText("Invalid username or password.");
    }


    private void handleLoginSuccessful() {
        System.out.println("Successfully logged in.");

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


    public void login() {
        String username = tfUsername.getText();
        String password = tfPassword.getText();

        Eva.currentUser = Database.getInstance().login(
            username.trim(),
            password.trim()
        );

        if (Eva.currentUser == null) {
            handleLoginFailed();
        } else {
            handleLoginSuccessful();
        }
    }

    public void handleRegister() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        toggleRegisterPane();

        String username = tfRegisterUsername.getText();
        String password = tfRegisterPassword.getText();

        int registerResult = Database.getInstance().register(
                username.trim(),
                password.trim()
        );

        if (registerResult == NO_ERROR) {
            handleRegisterSuccessful();
        } else {
            handleRegisterFailed(registerResult);
        }
    }

    public void handleLogin() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
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

        login();
    }

    @FXML
    private void toggleRegisterPane() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        spRegisterContainer.setVisible(!spRegisterContainer.isVisible());
    }
}
