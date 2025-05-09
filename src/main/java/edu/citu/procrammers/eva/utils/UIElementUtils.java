package edu.citu.procrammers.eva.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.control.Slider;
import javafx.scene.effect.Glow;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class UIElementUtils {

    @SafeVarargs
    public static <E extends Node> void setupGlow(E... components) {
        for (E c : components) {
            c.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> c.setEffect(new Glow(0.3)));
            c.addEventHandler(MouseEvent.MOUSE_EXITED, e -> c.setEffect(null));
        }
    }

    public static <E extends Node> void setupGlow(Node c) {
        c.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> c.setEffect(new Glow(0.3)));
        c.addEventHandler(MouseEvent.MOUSE_EXITED, e -> c.setEffect(null));
    }

    @SafeVarargs
    public static <E extends Node> void setupScalingAnimation(double scaleFactor, E... components) {
        for (E c : components) {
            Timeline scaleUp = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(c.scaleXProperty(), 1),
                            new KeyValue(c.scaleYProperty(), 1)
                    ),
                    new KeyFrame(Duration.seconds(0.2),
                            new KeyValue(c.scaleXProperty(), scaleFactor),
                            new KeyValue(c.scaleYProperty(), scaleFactor)
                    )
            );

            Timeline scaleDown = new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(c.scaleXProperty(), scaleFactor),
                            new KeyValue(c.scaleYProperty(), scaleFactor)
                    ),
                    new KeyFrame(Duration.seconds(0.2),
                            new KeyValue(c.scaleXProperty(), 1),
                            new KeyValue(c.scaleYProperty(), 1)
                    )
            );

            c.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> {
                scaleDown.stop();
                scaleUp.playFromStart();
            });

            c.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
                scaleUp.stop();
                scaleDown.playFromStart();
            });
        }
    }

    public static void setupSliderUI(Slider... sliders) {
        for (Slider s: sliders) {
            s.valueProperty().addListener((obs, oldVal, newVal) -> {
                double percent = newVal.doubleValue() / s.getMax();
                String style = String.format(
                        "-fx-background-color: linear-gradient(to right, #4F2D2A %.2f%%, #C7AFAD %.2f%%);"
                                + "-fx-background-insets: 0; -fx-background-radius: 0;",
                        percent * 100, percent * 100
                );
                s.lookup(".track").setStyle(style);
            });
        }
    }

}
