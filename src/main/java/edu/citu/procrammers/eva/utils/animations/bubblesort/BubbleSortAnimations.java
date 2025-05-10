package edu.citu.procrammers.eva.utils.animations.bubblesort;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import javafx.animation.*;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;


public class BubbleSortAnimations {

    public static VBox createBoxes(int pos, String num, double x, double y, List<ArrayNode> arrayNodes) {
        ArrayNode arrayNode = new ArrayNode(num, pos, x, y);
        VBox vbox = arrayNode.getVBox();
        arrayNodes.add(pos, arrayNode);

        return vbox;
    }

    public static FadeTransition fadeIn(VBox vBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        return fadeIn;
    }

    public static FadeTransition fadeOut(VBox vBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vBox);
        fadeIn.setFromValue(1);
        fadeIn.setToValue(0);
        fadeIn.setCycleCount(1);

        return fadeIn;
    }

    public static TranslateTransition slideX(VBox vBox, double x) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.60), vBox);
        slide.setByX(x);
        slide.setCycleCount(1);

        return slide;
    }

    public static TranslateTransition slideY(VBox vBox, double y) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.60), vBox);
        slide.setByY(y);
        slide.setCycleCount(1);

        return slide;
    }

    public static PauseTransition pause(double duration) {
        return new PauseTransition(Duration.seconds(duration));
    }

    public static ParallelTransition swapNodes(ArrayNode left, ArrayNode right) {
        VBox leftVBox = left.getVBox();
        VBox rightVBox = right.getVBox();
        double distance = Math.abs(left.getX() - right.getX());

        ParallelTransition anim = new ParallelTransition();
        TranslateTransition slideUpLeft = slideY(leftVBox, -100);
        TranslateTransition slideDownLeft = slideY(leftVBox, 100);
        TranslateTransition slideRightLeft = slideX(leftVBox, distance);

        TranslateTransition slideDownRight = slideY(rightVBox, 100);
        TranslateTransition slideUpRight = slideY(rightVBox, -100);
        TranslateTransition slideLeftRight = slideX(rightVBox, -distance);

        SequentialTransition leftTransition = new SequentialTransition(slideUpLeft, slideRightLeft, slideDownLeft);
        SequentialTransition rightTransition = new SequentialTransition(slideDownRight, slideLeftRight, slideUpRight);

        anim.getChildren().addAll(leftTransition, rightTransition);

        return anim;
    }

}
