package edu.citu.procrammers.eva.utils.visuals;

import edu.citu.procrammers.eva.controllers.ADT.NodeController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;

import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;

public class MoveCommand extends Command {
    int graphicId;
    double prevX;
    double prevY;
    double newX;
    double newY;
    HashMap<Integer, Node> graphicMap;

    public MoveCommand(int graphicId, double newX, double newY, HashMap<Integer, Node> graphicMap) {
//        System.out.println(node);
        this.graphicId = graphicId;
        this.newX = newX;
        this.newY = newY;
        this.prevX = newX;
        this.prevY = newY;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinished) {;
        Timeline timeline = new Timeline();
//        Circle circle = ((Circle) ((StackPane) node).getChildren().getFirst());
        Node node = graphicMap.get(graphicId);


        System.out.println("Not found so will create one...");
        this.prevX = node.getLayoutX();
        this.prevY = node.getLayoutY();

        double centerX = 25;
        double centerY = 25;

        if (node instanceof Text) {
            Bounds bounds = node.getLayoutBounds();
            centerX = bounds.getCenterX();
            centerY = bounds.getCenterY();
        }

        KeyValue kvX = new KeyValue(node.layoutXProperty(), newX - centerX);
        KeyValue kvY = new KeyValue(node.layoutYProperty(), newY - centerY);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kvX, kvY); // 300ms animation
        timeline.getKeyFrames().add(kf);
        timeline.play();

        System.out.printf("Graphic %d moved from (%.2f, %.2f) to (%.2f, %.2f)\n", graphicId, prevX-25, prevY-25, newX, newY);
        onFinished.run();

    }

    @Override public void undo(Runnable onUndo) {
        Node node = graphicMap.get(graphicId);
        Timeline timeline = new Timeline();
        KeyValue kvX = new KeyValue(node.layoutXProperty(), prevX);
        KeyValue kvY = new KeyValue(node.layoutYProperty(), prevY);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kvX, kvY); // 300ms animati
        timeline.getKeyFrames().add(kf);// on
        timeline.play();
    }

    @Override
    public String toString() {
        return "Move Command";
    }
}
