package edu.citu.procrammers.eva.utils.visuals;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

import java.util.HashMap;

public class DrawEdgeCommand extends Command {
    int id;
    int startId;
    int endId;
    AnchorPane canvas;
    HashMap<Integer, Node> graphicMap;

    public DrawEdgeCommand(int id, int startId, int endId, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
        this.id = id;
        this.startId = startId;
        this.endId = endId;
        this.canvas = canvas;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinish) {
        Node v1 = graphicMap.get(startId);
        Node v2 = graphicMap.get(endId);

        Line line;

        if (graphicMap.containsKey(id)) {
            line = (Line) graphicMap.get(id);
        }
        else {
            System.out.println("creating new link");
            line = new Line(startId, endId, startId, endId);
            line.setStrokeWidth(2);
            line.setStroke(Color.valueOf("#E9DBD5"));
        }


        canvas.getChildren().add(line);

        line.toBack();

        line.startXProperty().bind(Bindings.createDoubleBinding(
                () -> v1.getLayoutX() + v1.getBoundsInParent().getWidth() / 2,
                v1.layoutXProperty(), v1.boundsInParentProperty()
        ));

        line.startYProperty().bind(Bindings.createDoubleBinding(
                () -> v1.getLayoutY() + v1.getBoundsInParent().getHeight() / 2,
                v1.layoutYProperty(), v1.boundsInParentProperty()
        ));

        line.endXProperty().bind(Bindings.createDoubleBinding(
                () -> v2.getLayoutX() + v2.getBoundsInParent().getWidth() / 2,
                v2.layoutXProperty(), v2.boundsInParentProperty()
        ));

        line.endYProperty().bind(Bindings.createDoubleBinding(
                () -> v2.getLayoutY() + v2.getBoundsInParent().getHeight() / 2,
                v2.layoutYProperty(), v2.boundsInParentProperty()
        ));


        graphicMap.put(id, line);
        onFinish.run();
    }

    @Override public void undo(Runnable onUndo) {
        canvas.getChildren().remove(graphicMap.get(id));
    }

    @Override
    public String toString() {
        return "Draw Edge Command";
    }
}
