package edu.citu.procrammers.eva.utils.visuals;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

public class MoveCommand extends Command {
    Node node;
    double newX;
    double newY;

    public MoveCommand(Node node, double newX, double newY) {
        this.node = node;
        this.newX = newX;
        this.newY = newY;
    }

    @Override
    public void execute(Runnable onFinished) {;
        Timeline timeline = new Timeline();
//        KeyValue kv = new KeyValue(node.layoutXProperty(), newX - circle.getRadius());
//        KeyFrame kf = new KeyFrame(Duration.millis(300), kv); // 300ms animation
//        timeline.getKeyFrames().add(kf);
        timeline.play();
    }
}
