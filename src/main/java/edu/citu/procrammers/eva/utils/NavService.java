package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class NavService {

    private static Stage mainStage = null;
    private static MainController mainController;

    public NavService(Stage mainStage) {
        NavService.mainStage = mainStage;
        setMainController();
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
        Scene screen = new Scene(
            loader.getRoot()
        );

        mainStage.setScene(screen);
    }

}
