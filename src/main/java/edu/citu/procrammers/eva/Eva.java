package edu.citu.procrammers.eva;

import edu.citu.procrammers.eva.data.AudioSettings;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class Eva extends Application {

    public static User currentUser = null;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(360);
        Database.getInstance();
        new NavService(primaryStage);
//        NavService.navigateTo(ADT);
        NavService.navigateTo(Splash);
        NavService.setFullScreen(true);
        primaryStage.show();
    }
}
