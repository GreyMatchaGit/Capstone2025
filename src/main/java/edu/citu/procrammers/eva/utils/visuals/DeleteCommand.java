package edu.citu.procrammers.eva.utils.visuals;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.util.HashMap;

public class DeleteCommand extends Command {
    Node node;
    int graphicId;
    double x;
    double y;
    AnchorPane canvas;
    HashMap<Integer, Node> graphicMap;

    public DeleteCommand(int graphicId, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
        this.graphicId = graphicId;
        this.canvas = canvas;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinished) {
        System.out.println("Deleting graphic: " + graphicId);
        node = graphicMap.get(graphicId);

        x = node.getLayoutX();
        y = node.getLayoutY();

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(event -> {
            canvas.getChildren().remove(node);
            onFinished.run();  // callback after removal
        });
        fade.play();

//        canvas.getChildren().remove(node);
//
//        onFinished.run();
    }

    @Override
    public void undo(Runnable onUndo) {
        canvas.getChildren().add(node);

        if (node instanceof Line) {
            node.toBack();
        }
        else {
            node.toFront();
        }
        node.setLayoutX(x);
        node.setLayoutY(y);

        FadeTransition fade = new FadeTransition(Duration.millis(500), node);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.setOnFinished(event -> {
            onUndo.run();  // callback after removal
        });
        fade.play();

    }

    @Override
    public String toString() {
        return "Delete Command";
    }
}
