package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class LoginController {

    public Pane fadePane;
    public ImageView imgLoginBtn, imgRegisterBtn;
    public TextField tfUsername, tfPassword;
    public StackPane registerContainer;

    @FXML
    public void initialize() {

        setupGlow(imgLoginBtn);
        setupGlow(imgRegisterBtn);


//        imgLoginBtn.setOnMouseEntered(e -> imgLoginBtn.setEffect(new Glow(0.3)));
//        imgRegisterBtn.setOnMouseEntered(e -> imgRegisterBtn.setEffect(new Glow(0.3)));
//
//        imgLoginBtn.setOnMouseExited(e -> imgLoginBtn.setEffect(null));
//        imgRegisterBtn.setOnMouseExited(e -> imgRegisterBtn.setEffect(null));
    }

    public void handleRegister() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        if (registerContainer.isVisible()) {
            registerContainer.setVisible(false);
            registerContainer.setDisable(true);
        } else {
            registerContainer.setVisible(true);
            registerContainer.setDisable(false);
        }
    }

    public void handleLogin() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
        fadeOut.setFromValue(0);
        fadeOut.setToValue(1);
        fadeOut.setOnFinished(e -> {
            SoundManager.fadeOutMusic(() -> NavService.navigateTo(Loading));
            NavService.navigateTo(Selection);
        });
        fadeOut.play();
    }
}
