package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.utils.Constant;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.data.Database.Error.*;

public class LoginController implements Initializable {

    public Pane fadePane;
    public ImageView imgLoginBtn, imgRegisterBtn;
    public TextField tfUsername, tfPassword, tfRegisterUsername, tfRegisterPassword;
    public StackPane registerContainer;

    private static final boolean TESTING_MODE_ENABLED = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        imgLoginBtn.setOnMouseClicked(mouseEvent -> login());

        imgRegisterBtn.setOnMouseClicked(mouseEvent -> {
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
        });
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
                // TODO: UI for when username is taken.
                break;
            case EMPTY_USERNAME:
                // TODO: UI for when username is empty.
                break;
            case EMPTY_PASSWORD:
                // TODO: UI for when password is empty.
                break;
            case EMPTY_USERNAME_PASSWORD:
                // TODO: UI for when both username and password is empty.
                break;
            case TOO_LONG_USERNAME:
                // TODO: UI for when username exceeds the 20-char limit.
                break;
            case TOO_LONG_PASSWORD:
                // TODO: UI for when password exceeds the 25-char limit.
        }
    }

    private void handleLoginFailed() {
        // TODO: Add UI for failed login attempt.
        System.out.println("Login failed.");
    }

    private void handleLoginSuccessful() {
        // TODO: Add UI for successful login attempt.
        System.out.println("Successfully logged in.");
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
