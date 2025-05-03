package edu.citu.procrammers.eva.utils.visuals;

import javafx.beans.binding.Bindings;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Line;

import java.util.HashMap;

public class DrawEdgeCommand extends Command {
    int startId;
    int endId;
    AnchorPane canvas;
    HashMap<Integer, Node> graphicMap;

    public DrawEdgeCommand(int startId, int endId, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
        this.startId = startId;
        this.endId = endId;
        this.canvas = canvas;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinish) {
        Node v1 = graphicMap.get(startId);
        Node v2 = graphicMap.get(endId);

        Line line = new Line();

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



//        line.startXProperty().bind(v1.layoutXProperty());
//        line.startYProperty().bind(v1.layoutYProperty());
//
//        line.endXProperty().bind(v2.layoutXProperty());
//        line.endYProperty().bind(v2.layoutYProperty());

        onFinish.run();
    }
}
