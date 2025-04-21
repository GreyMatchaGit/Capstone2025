package edu.citu.procrammers.eva.data;

import edu.citu.procrammers.eva.Eva;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class CustomCircle extends StackPane {
    ImageView image;
    Circle circle;

    public CustomCircle(Image image) {
        this.image.setImage(
            image
        );
        circle.setFill(
            new ImagePattern(
                image
            )
        );
    }
}
