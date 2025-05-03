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
            circle.setStroke(Paint.valueOf("RED"));
        }
        else {
            System.out.println("turning off");
            circle.setStroke(Paint.valueOf("BLACK"));
        }
        onFinished.run();
    }
}
