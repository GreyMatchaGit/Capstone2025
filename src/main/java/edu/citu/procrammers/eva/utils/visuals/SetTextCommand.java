package edu.citu.procrammers.eva.utils.visuals;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.HashMap;

public class SetTextCommand extends Command {
    int id;
    String elem;
    double x;
    double y;
    AnchorPane canvas;
    HashMap<Integer, Node> graphicMap;
    SetTextCommand(int id, String elem, double x, double y, AnchorPane canvas, HashMap<Integer, Node> graphicMap) {
        this.id = id;
        this.elem = elem;
        this.x = x;
        this.y = y;
        this.canvas = canvas;
        this.graphicMap = graphicMap;
    }

    @Override
    public void execute(Runnable onFinished) {
        Text text = new Text(elem);
        canvas.getChildren().add(text);

        // Measure the text dimensions
        text.setFont(Font.font(14));
        Bounds bounds = text.getLayoutBounds();
        double textWidth = bounds.getCenterX();
        double textHeight = bounds.getCenterY();


//        Circle redDot = new Circle(1); // radius 3 pixels
//        redDot.setFill(Color.RED);
//        redDot.setLayoutX(x); // absolute center X
//        redDot.setLayoutY(y); // absolute center Y
//        canvas.getChildren().add(redDot); // Add separately to canvas
//        // Center the text at (x, y)
//        text.setLayoutX(x);
//        text.setLayoutY(y); // vertical tweak for baseline alignment

        text.setLayoutX(x - textWidth);
        text.setLayoutY(y - textHeight);
        graphicMap.put(id, text);
        onFinished.run();
    }

    @Override
    public void undo(Runnable onUndo) {

    }

    @Override
    public String toString() {
        return "Set Text Command";
    }
}
