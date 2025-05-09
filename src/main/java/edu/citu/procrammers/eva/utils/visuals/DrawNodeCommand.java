package edu.citu.procrammers.eva.utils.visuals;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.ADT.NodeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

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
            StackPane nodeView = (StackPane) graphicMap.get(graphicId);
            Circle circle;

            if (nodeView == null) {
                System.out.println("Creating node");
                FXMLLoader loader = new FXMLLoader(Eva.class.getResource("ADT_visuals/node-view.fxml"));
                nodeView = loader.load();

                NodeController nodeController = loader.getController();

                String strNodeElem = Integer.toString(key);
                nodeController.setText(strNodeElem);
                Text text = nodeController.getText();
                text.setFont(Font.font(14));
                text.setFill(Color.valueOf("#E9DBD5"));

                System.out.println("Node " + key + ", GraphicID: " + graphicId + " added");
                circle  = (Circle)(nodeView.getChildren().getFirst());
                circle.setRadius(25);

                circle.setStroke(Color.valueOf("#E9DBD5"));
                circle.setFill(Color.valueOf("#4E2D2D"));
                circle.setStrokeWidth(3);
//                circle.setStyle("-fx-stroke: #E9DBD5; -fx-stroke-width: 3;-fx-fill: #4E2D2D;");
            }

            circle  = (Circle)(nodeView.getChildren().getFirst());


            canvas.getChildren().add(nodeView);
            nodeView.setLayoutX(x - circle.getRadius());
            nodeView.setLayoutY(y - circle.getRadius());

            graphicMap.put(graphicId, nodeView);

            onFinished.run();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public void undo(Runnable onUndo) {
        canvas.getChildren().remove(graphicMap.get(graphicId));
        onUndo.run();
    }

    @Override
    public String toString() {
        return "Draw Node Command";
    }
}
