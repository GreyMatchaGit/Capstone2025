package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class LoginController {

    public Pane fadePane;
    public ImageView imgLoginBtn, imgRegisterBtn;
    public TextField tfUsername, tfPassword;
    public StackPane registerContainer;

    public void handleRegister() {
        if (registerContainer.isVisible()) {
            registerContainer.setVisible(false);
            registerContainer.setDisable(true);
        } else {
            registerContainer.setVisible(true);
            registerContainer.setDisable(false);
        }
    }

    public void handleLogin() {
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
