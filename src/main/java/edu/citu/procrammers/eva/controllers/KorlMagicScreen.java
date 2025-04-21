package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.utils.NavService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class KorlMagicScreen implements Initializable {

    @FXML
    Circle randomCircle, randomCircle1, randomCircle2, randomCircle3, moon;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        randomCircle.setStrokeWidth(0);
        randomCircle1.setStrokeWidth(0);
        randomCircle2.setStrokeWidth(0);
        randomCircle3.setStrokeWidth(0);
        moon.setStrokeWidth(0);
        try {
            randomCircle.setFill(
                    new ImagePattern(
                            new Image(
                                    Eva.class.getResource("earth.png").openStream()
                            )
                    )
            );
            randomCircle1.setFill(
                    new ImagePattern(
                            new Image(
                                    Eva.class.getResource("mars.png").openStream()
                            )
                    )
            );
            randomCircle2.setFill(
                    new ImagePattern(
                            new Image(
                                    Eva.class.getResource("venus.png").openStream()
                            )
                    )
            );
            randomCircle3.setFill(
                    new ImagePattern(
                            new Image(
                                    Eva.class.getResource("mercury.png").openStream()
                            )
                    )
            );
            moon.setFill(
                    new ImagePattern(
                            new Image(
                                    Eva.class.getResource("moon.png").openStream()
                            )
                    )
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Platform.runLater(() -> {
            animate(
                    randomCircle,
                    randomCircle1,
                    randomCircle2,
                    randomCircle3
            );
            setupMoon();
        });
    }

    private double angle = 0; // Angle in radians

    private void setupMoon() {
        double orbitRadius = randomCircle.getRadius() + 20; // Orbit distance from center
        double moonRadius = randomCircle.getRadius() / 4;
        moon.setRadius(moonRadius);

        Timeline timeline = new Timeline(
                new KeyFrame(
                        Duration.millis(1000 / 60.0),
                        event -> {
                            double centerX = randomCircle.getLayoutX();
                            double centerY = randomCircle.getLayoutY();

                            // Update angle
                            angle += Math.toRadians(1); // 1 degree per frame
                            if (angle > 2 * Math.PI) {
                                angle -= 2 * Math.PI;
                            }

                            // Calculate moon's position in orbit
                            double x = centerX + orbitRadius * Math.cos(angle);
                            double y = centerY + orbitRadius * Math.sin(angle);

                            moon.setLayoutX(x);
                            moon.setLayoutY(y);
                        }
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void animate(Circle... circles) {
//
//        Random random = new Random();
//        AnchorPane mainScreen  = NavService.mainController.mainScreen;
//
//        final int[] xDirs = {-1, 1};
//        final int[] yDirs = {-1, 1};
//
//        for (Circle circle : circles) {
//            double randomX = random.nextInt((int) mainScreen.getWidth());
//            double randomY = random.nextInt((int) mainScreen.getHeight());
//            circle.setLayoutX(randomX);
//            circle.setLayoutY(randomY);
//
//            final int[] dirX = {random.nextInt(2)};
//            final int[] dirY = {random.nextInt(2)};
//
//            circle.setCursor(Cursor.HAND);
//            circle.setOnMouseClicked(event -> {
//                if (event.getButton() == MouseButton.PRIMARY) {
//                    int prevDirX = dirX[0];
//                    int prevDirY = dirY[0];
//                    while (dirX[0] == prevDirX && dirY[0] == prevDirY) {
//                        dirX[0] = random.nextInt(2);
//                        dirY[0] = random.nextInt(2);
//                    }
//                } else if (event.getButton() == MouseButton.SECONDARY) {
//                    circle.setRadius(
//                            random.nextInt(80) + 20
//                    );
//                }
//            });
//
//            final Timeline timeline = new Timeline(
//                    new KeyFrame(
//                            Duration.millis(1000 / 60.0),
//                            actionEvent -> {
//                                if (circle.getLayoutX() < 0 + circle.getRadius())
//                                    dirX[0] = 1;
//
//                                if (circle.getLayoutX() >= mainScreen.getWidth() - circle.getRadius())
//                                    dirX[0] = 0;
//
//                                if (circle.getLayoutY() < 0 + circle.getRadius())
//                                    dirY[0] = 1;
//
//                                if (circle.getLayoutY() >= mainScreen.getHeight() - circle.getRadius())
//                                    dirY[0] = 0;
//
//                                circle.setLayoutX(
//                                        circle.getLayoutX() + xDirs[dirX[0]] * 4
//                                );
//                                circle.setLayoutY(
//                                        circle.getLayoutY() + yDirs[dirY[0]] * 4
//                                );
//                            }
//                    )
//            );
//
//            timeline.setCycleCount(Timeline.INDEFINITE);
//            timeline.play();
//        }
    }
}
