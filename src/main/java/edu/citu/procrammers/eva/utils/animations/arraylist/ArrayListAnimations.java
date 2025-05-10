package edu.citu.procrammers.eva.utils.animations.arraylist;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import javafx.animation.*;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.List;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;

public class ArrayListAnimations {

    public static FadeTransition fadeIn(VBox vBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        return fadeIn;
    }

    public static FadeTransition fadeOut(VBox vBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.75), vBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        return fadeOut;
    }

    public static TranslateTransition slideY(VBox vBox, double y) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.60), vBox);
        slide.setByY(y);
        slide.setCycleCount(1);

        return slide;
    }

    public static TranslateTransition slideX(VBox vBox, double x) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByX(x);
        slide.setCycleCount(1);

        return slide;
    }

    public static Timeline highlightNode(Rectangle r, Label l, Color from, Color to) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                ),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(r.strokeProperty(), to),
                        new KeyValue(l.textFillProperty(), to)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);

        return timeline;
    }

    public static FillTransition fillRectangle(double duration, Rectangle r, Color from, Color to) {
        FillTransition fill = new FillTransition(Duration.seconds(duration), r);
        fill.setFromValue(from);
        fill.setToValue(to);

        return fill;
    }

    public static SequentialTransition destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        TranslateTransition slideUp = slideY(vbox, -100);

        return new SequentialTransition(slideUp, fadeOut);
    }

    public static PauseTransition pause(double duration) {
        return new PauseTransition(Duration.seconds(duration));
    }

    public static void pulseNodeAll(List<ArrayNode> arrayNodes, SequentialTransition traversal) {
        ParallelTransition pulseNegativeFound = new ParallelTransition();
        for (ArrayNode node : arrayNodes) {
            Rectangle rr = node.getRectangle();
            FillTransition highlight = fillRectangle(0.3, rr, DEFAULTR, NEGATIVE);
            FillTransition reset = fillRectangle(0.3, rr, NEGATIVE, DEFAULTR);
            SequentialTransition st = new SequentialTransition(highlight, reset);
            pulseNegativeFound.getChildren().add(st);
        }
        traversal.getChildren().add(pulseNegativeFound);
    }

    public static void pulseNodeFound(VBox finalVBox, Rectangle finalRectangle, boolean toRemove) {
        TranslateTransition tFirst = slideY(finalVBox, -100);
        PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
        FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
        SequentialTransition fullSequence = new SequentialTransition(
                tFirst,
                pause,
                highlight,
                reset
        );
        if(!toRemove) {
            TranslateTransition tLast = slideY(finalVBox, 100);
            fullSequence.getChildren().add(tLast);
        }

        fullSequence.play();
    }
}
