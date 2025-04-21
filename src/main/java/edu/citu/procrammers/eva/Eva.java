package edu.citu.procrammers.eva;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.application.Application;
import javafx.stage.Stage;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class Eva extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new NavService(primaryStage);
        NavService.navigateTo(
            Splash
        );
        NavService.setFullScreen(true);
        primaryStage.show();
    }
}
