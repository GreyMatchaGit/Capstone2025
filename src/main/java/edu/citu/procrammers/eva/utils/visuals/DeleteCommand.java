package edu.citu.procrammers.eva.utils.visuals;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

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

        canvas.getChildren().remove(node);

        onFinished.run();
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

        onUndo.run();
    }

    @Override
    public String toString() {
        return "Delete Command";
    }
}
