package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavService {

    private static Stage mainStage = null;
    public static MainController mainController;

    public NavService(Stage mainStage) {
        NavService.mainStage = mainStage;
        mainStage.setFullScreenExitHint("");
        setMainController();
        mainController.setStage(mainStage);
//        mainController.setContentDynamic();
    }
    public static void navigateTo(String page) {
        FXMLLoader fxml = new FXMLLoader(
            Eva.class.getResource(page)
        );

        try {
            Node pageView = fxml.load();
            mainController.setPage(
                pageView
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setFullScreen(boolean toFullScreen) {
        if (toFullScreen)
            mainStage.setFullScreen(true);
        else
            mainStage.setFullScreen(false);
    }

    private static void setMainController() {
        FXMLLoader loader = new FXMLLoader(
            Eva.class.getResource("main-view.fxml")
        );

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        mainController = loader.getController();
        Scene scene = new Scene(
            loader.getRoot()
        );

        mainStage.setScene(scene);
    }
}
