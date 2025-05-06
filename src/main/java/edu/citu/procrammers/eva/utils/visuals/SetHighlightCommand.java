package edu.citu.procrammers.eva.utils.visuals;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

public class SetHighlightCommand extends Command {
    Circle circle;
    int isOn ;
    public SetHighlightCommand(int isOn, Circle circle) {
        this.circle = circle;
        this.isOn = isOn;
    }

    @Override
    public void execute(Runnable onFinished) {
        if (isOn == 1) {
            System.out.println("turning on");

            circle.setStroke(Color.GREEN);
            circle.setFill(Color.WHITE);
            circle.setStrokeWidth(3); // Optional: make stroke more visible
//            circle.setFill(Paint.valueOf("PINK"));
//            circle.setStyle("-fx-stroke: green; -fx-background-color: white");
        }
        else {
            System.out.println("turning off");
            circle.setStroke(Color.BLACK);
//            circle.setFill(Color.WHITE);
            circle.setStrokeWidth(2); // Optional: make stroke more visible
//            circle.setStyle("-fx-stroke: black; -fx-background-color: white");
//            circle.setStyle("");
//            circle.setFill(Paint.valueOf("3366cc"));
        }
        onFinished.run();
    }

    @Override public void undo(Runnable onFinished) {
        if (isOn == 0) {
            System.out.println("turning on");
            circle.setStroke(Color.BLACK);
//            circle.setFill(Color.WHITE);
            circle.setStrokeWidth(2); // Optional: make stroke more visible
            // Set to highlighted (green stroke, white fill)


//            circle.setStyle("-fx-stroke: black; -fx-background-color: white");
//            circle.setStyle("");
//            circle.setFill(Paint.valueOf("PINK"));
        }
        else {
            System.out.println("turning off");
            circle.setStroke(Color.GREEN);
            circle.setFill(Color.WHITE);
            circle.setStrokeWidth(3); // Optional: make stroke more visible
//            circle.setStyle("-fx-stroke: green; -fx-background-color: white");
//            circle.setStyle("-fx-stroke: green");
//            circle.setFill(Paint.valueOf("3366cc"));
        }
    }

    @Override
    public String toString() {
        return "Set Highlight Command";
    }
}
