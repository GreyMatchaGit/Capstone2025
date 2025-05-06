package edu.citu.procrammers.eva.controllers;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.value.ChangeListener;
import javafx.fxml.Initializable;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    public Group scaleableGroup;
    public AnchorPane mainScreen;

    private Stage primaryStage;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void setStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private double baseWidth = -1;
    private double baseHeight = -1;

    public void setContentDynamic() {
        primaryStage.setOnShown(event -> {
            // Get the actual size after rendering
            baseWidth = primaryStage.getWidth();
            baseHeight = primaryStage.getHeight();

            ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
                double newWidth = primaryStage.getWidth();
                double newHeight = primaryStage.getHeight();

                // Update base dimensions for fullscreen mode
                if (primaryStage.isFullScreen()) {
                    baseWidth = newWidth;
                    baseHeight = newHeight;
                }

                if (baseWidth <= 0 || baseHeight <= 0) {
                    System.out.println("Base dimensions not yet valid.");
                    return;
                }

                // Stretch to fit the window
                double widthRatio = newWidth / baseWidth;
                double heightRatio = newHeight / baseHeight;

                double scaleRatioX = widthRatio;
                double scaleRatioY = heightRatio;

                // Apply the scaling to stretch content
                scaleableGroup.setScaleX(scaleRatioX);
                scaleableGroup.setScaleY(scaleRatioY);

                // Remove the translation for centering
                scaleableGroup.setTranslateX(0);
                scaleableGroup.setTranslateY(0);
            };

            primaryStage.widthProperty().addListener(resizeListener);
            primaryStage.heightProperty().addListener(resizeListener);
        });
    }



    public void setPage(Node node) {
        mainScreen.getChildren().clear();
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        mainScreen.getChildren().addAll(node);
    }
}
