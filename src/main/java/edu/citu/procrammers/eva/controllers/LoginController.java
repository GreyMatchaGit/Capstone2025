package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.utils.Constant;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class LoginController implements Initializable {

    public Pane fadePane;
    public ImageView imgLoginBtn, imgRegisterBtn;
    public TextField tfUsername, tfPassword;
    public StackPane registerContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imgLoginBtn.setOnMouseClicked(mouseEvent -> {
            String username = tfUsername.getText();
            String password = tfPassword.getText();

            Eva.currentUser = Database.getInstance().login(
                username,
                password
            );

            if (Eva.currentUser == null) {
                handleLoginFailed();
            } else {
                handleLoginSuccessful();
            }
        });
    }

    private void handleLoginFailed() {
        // TODO: Add UI for failed login attempt.
    }

    private void handleLoginSuccessful() {
        // TODO: Add UI for successful login attempt.
        NavService.navigateTo(Loading);
    }

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
