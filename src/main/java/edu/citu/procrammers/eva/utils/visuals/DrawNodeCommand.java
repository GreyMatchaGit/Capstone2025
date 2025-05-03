package edu.citu.procrammers.eva.utils.visuals;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.ADT.NodeController;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.io.IOException;
import java.util.HashMap;

public class DrawNodeCommand extends Command {
    HashMap<Integer, Node> graphicMap;
    AnchorPane canvas;
//    edu.citu.procrammers.eva.models.data_structures.Node node;

    int key;
    int graphicId;
    double x;
    double y;

    public DrawNodeCommand(int key, int graphicId, double x, double y, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
//        this.node = node;
        this.key = key;
        this.graphicId = graphicId;
        this.x = x;
        this.y = y;

        this.canvas = canvas;
        this.graphicMap = graphicMap;
    }

//    public DrawNodeCommand(edu.citu.procrammers.eva.models.data_structures.Node node, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
//        this.node = node;
//        this.canvas = canvas;
//        this.graphicMap = graphicMap;
//    }

    @Override
    public void execute(Runnable onFinished) {
        try {
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource("ADT_visuals/node-view.fxml"));
            Node nodeView = loader.load();

            NodeController nodeController = loader.getController();

            String strNodeElem = Integer.toString(key);
            nodeController.setText(strNodeElem);

            System.out.println("Node " + key + ", GraphicID: " + graphicId + " added");
            StackPane stackPane = (StackPane) nodeView;

            Circle circle  = (Circle)(stackPane.getChildren().getFirst());
            circle.setRadius(20);

            canvas.getChildren().add(nodeView);
            nodeView.setLayoutX(x - circle.getRadius());
            nodeView.setLayoutY(y - circle.getRadius());

//            node.x.addListener((observable, oldValue, newValue) -> {
//                Timeline timeline = new Timeline();
//                KeyValue kv = new KeyValue(nodeView.layoutXProperty(), newValue.doubleValue() - circle.getRadius());
//                KeyFrame kf = new KeyFrame(Duration.millis(300), kv); // 300ms animation
//                timeline.getKeyFrames().add(kf);
//                timeline.play();
//            });
//
//            node.y.addListener((observable, oldValue, newValue) -> {
//                Timeline timeline = new Timeline();
//                KeyValue kv = new KeyValue(nodeView.layoutYProperty(), newValue.doubleValue());
//                KeyFrame kf = new KeyFrame(Duration.millis(300), kv); // 300ms animation
//                timeline.getKeyFrames().add(kf);
//                timeline.play();
//            });


            graphicMap.put(graphicId, nodeView);

            onFinished.run();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
