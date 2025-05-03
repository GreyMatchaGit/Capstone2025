package edu.citu.procrammers.eva.utils.visuals;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;

import javafx.util.Duration;

import java.util.HashMap;

public class MoveCommand extends Command {
    int graphicId;
    double newX;
    double newY;
    HashMap<Integer, Node> graphicMap;

    public MoveCommand(int graphicId, double newX, double newY, HashMap<Integer, Node> graphicMap) {
//        System.out.println(node);
        this.graphicId = graphicId;
        this.newX = newX;
        this.newY = newY;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinished) {;
        Timeline timeline = new Timeline();
//        Circle circle = ((Circle) ((StackPane) node).getChildren().getFirst());
        Node node = graphicMap.get(graphicId);

        KeyValue kvX = new KeyValue(node.layoutXProperty(), newX - 20);
        KeyValue kvY = new KeyValue(node.layoutYProperty(), newY - 20);
        KeyFrame kf = new KeyFrame(Duration.millis(300), kvX, kvY); // 300ms animation
        timeline.getKeyFrames().add(kf);
        timeline.play();
        onFinished.run();
    }
}
