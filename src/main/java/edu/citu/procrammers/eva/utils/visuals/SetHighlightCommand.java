package edu.citu.procrammers.eva.utils.visuals;

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
            circle.setFill(Paint.valueOf("PINK"));
        }
        else {
            System.out.println("turning off");
            circle.setFill(Paint.valueOf("3366cc"));
        }
        onFinished.run();
    }

    @Override public void undo(Runnable onFinished) {
        if (isOn == 0) {
            System.out.println("turning on");
            circle.setFill(Paint.valueOf("PINK"));
        }
        else {
            System.out.println("turning off");
            circle.setFill(Paint.valueOf("3366cc"));
        }
    }
}
