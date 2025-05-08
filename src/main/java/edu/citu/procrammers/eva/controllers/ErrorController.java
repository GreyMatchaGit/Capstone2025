package edu.citu.procrammers.eva.controllers;

import com.sun.tools.javac.Main;
import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

import static edu.citu.procrammers.eva.utils.Constant.Page.Splash;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class ErrorController {
    public static String errorMsg = "Error";
    public Label lblErrorMsg;
    public Pane fadePane;
    public Button btnExit;

    @FXML
    public void initialize() {
        lblErrorMsg.setText(errorMsg);

        btnExit.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
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
