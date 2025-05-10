package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.Splash;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;

public class ErrorController {
    public static String errorMsg = "Error";
    public Label lblErrorMsg;
    public Pane fadePane;
    public Button btnExit;

    @FXML
    public void initialize() {
        lblErrorMsg.setText(errorMsg);

        btnExit.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            FadeTransition ft = new FadeTransition(Duration.seconds(1), fadePane);
            ft.setFromValue(0);
            ft.setToValue(1);
            ft.setOnFinished(event -> {
                Eva.resetAppState();
                NavService.navigateTo(Splash);
                NavService.previousPage = null;
                fadePane.setOpacity(1);
            });
            ft.play();
        });

    }
}
