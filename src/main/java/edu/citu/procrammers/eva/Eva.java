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
            ADT
        );
        NavService.setFullScreen(false);
        primaryStage.show();
    }
}
