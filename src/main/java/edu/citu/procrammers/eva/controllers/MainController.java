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

    /**
     * TODO: This function creates the surrounding white borders.
     */
    public void setContentDynamic() {
        primaryStage.setOnShown(event -> {
            // Get the actual size after rendering
            baseWidth = primaryStage.getWidth();
            baseHeight = primaryStage.getHeight();

            ChangeListener<Number> resizeListener = (observable, oldValue, newValue) -> {
                double newWidth = primaryStage.getWidth();
                double newHeight = primaryStage.getHeight();

                if (baseWidth <= 0 || baseHeight <= 0) {
                    System.out.println("Base dimensions not yet valid.");
                    return;
                }

                double widthRatio = newWidth / baseWidth;
                double heightRatio = newHeight / baseHeight;

                double scaleRatio = Math.min(widthRatio, heightRatio);

                if (Double.isNaN(scaleRatio) || Double.isInfinite(scaleRatio)) {
                    System.out.println("Invalid scale: " + scaleRatio);
                    return;
                }

                double dx = (newWidth - (baseWidth * scaleRatio)) / 2.0;
                double dy = (newHeight - (baseHeight * scaleRatio)) / 2.0;


                scaleableGroup.setScaleX(scaleRatio);
                scaleableGroup.setScaleY(scaleRatio);
                scaleableGroup.setTranslateX(dx);
                scaleableGroup.setTranslateY(dy);

                System.out.printf("Scale: %.2f | TranslateX: %.2f | TranslateY: %.2f\n", scaleRatio, dx, dy);
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
