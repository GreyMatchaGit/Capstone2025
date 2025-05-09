package edu.citu.procrammers.eva.utils.animations.linkedlist;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.models.data_structures.SinglyNode;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class SinglyNodeGraphic extends StackPane {
    public Circle background;
    public Text value;
    public SinglyNode node;

    public SinglyNodeGraphic(SinglyNode node) {
        this.node = node;
        setLayoutX(node.x.get());
        setLayoutY(node.y.get());
        value = createText();
        background = createBackground();
        getChildren().addAll(background, value);
        setLayoutPositionListeners();
    }

    private Text createText() {
        Text text = new Text(String.valueOf(node.value));
        text.setFont(Font.font(14));
        text.setFill(Color.valueOf("#E9DBD5"));
        return text;
    }

    private Circle createBackground() {
        Circle background = new Circle(SinglyNode.side);
        Image image = new Image(Eva.class.getResource(
            "media/img_loading_screen_bg.png"
        ).toExternalForm());
        background.setFill(new ImagePattern(image));
        background.setStroke(Color.valueOf("#E9DBD5"));
        background.setFill(Color.valueOf("#4E2D2D"));
        background.setStrokeWidth(3);
        return background;
    }

    private void setLayoutPositionListeners() {
        ChangeListener<Number> positionListener = (observable, oldValue, newValue) -> {
            setLayoutX(node.x.get());
            setLayoutY(node.y.get());
        };

        node.x.addListener(positionListener);
        node.y.addListener(positionListener);
    }
}
