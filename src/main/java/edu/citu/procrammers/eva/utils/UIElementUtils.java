package edu.citu.procrammers.eva.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
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


}
