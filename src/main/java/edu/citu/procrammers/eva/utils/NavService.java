package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;

public class NavService {

    private static Stage mainStage = null;
    public static MainController mainController;
    public static String previousPage = Selection;

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
        mainStage.setFullScreen(toFullScreen);
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

        scene.getStylesheets().add(Objects.requireNonNull(NavService.class.getResource("/edu/citu/procrammers/eva/css/fonts.css")).toExternalForm());

        mainStage.setScene(scene);
    }
}
