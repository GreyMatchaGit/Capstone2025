package edu.citu.procrammers.eva.utils.visuals;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

import java.util.HashMap;

public class DeleteCommand extends Command {
    int graphicId;
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
        canvas.getChildren().remove(graphicMap.get(graphicId));
        onFinished.run();
    }

    @Override
    public void undo(Runnable onUndo) {

    }

    @Override
    public String toString() {
        return "Delete Command";
    }
}
