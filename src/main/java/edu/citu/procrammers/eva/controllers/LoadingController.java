package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.animation.*;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;

public class LoadingController {
    public ImageView imgWumpus;
    public Pane fadePane;

    @FXML
    public void initialize() {
        KeyFrame up = new KeyFrame(Duration.millis(200),
                new KeyValue(imgWumpus.translateYProperty(), -20)
        );

        KeyFrame down = new KeyFrame(Duration.millis(400),
                new KeyValue(imgWumpus.translateYProperty(), 0)
        );

        Timeline bounce = new Timeline(up, down);

        PauseTransition pause = new PauseTransition(Duration.millis(800));

        SequentialTransition bounceCycle = new SequentialTransition(bounce, pause);

//        Disabled loop for now
//        bounceCycle.setCycleCount(Animation.INDEFINITE);

        bounceCycle.setCycleCount(2);
        bounceCycle.setOnFinished(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                NavService.navigateTo(Selection);
            });
            fadeOut.play();
        });

        bounceCycle.play();
    }
}
