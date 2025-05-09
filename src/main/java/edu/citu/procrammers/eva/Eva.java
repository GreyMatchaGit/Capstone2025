package edu.citu.procrammers.eva;

import edu.citu.procrammers.eva.data.AudioSettings;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class Eva extends Application {

    public static User currentUser = null;

    public static void resetAppState() {
        currentUser = null;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(360);

        Image appIcon = new Image(getClass().getResourceAsStream("/edu/citu/procrammers/eva/media/icon_project_logo.png"));
        primaryStage.getIcons().add(appIcon);
        
        Database.getInstance();
        new NavService(primaryStage);

        NavService.navigateTo(Splash);
//        NavService.navigateTo(Arrays);

        NavService.setFullScreen(true);
        primaryStage.show();
    }
}