package edu.citu.procrammers.eva.models.data_structures;

import edu.citu.procrammers.eva.controllers.HashTableController;
import edu.citu.procrammers.eva.utils.Constant;
import edu.citu.procrammers.eva.utils.FullContainerException;
import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.EMPTY_STRING;
import static javafx.scene.paint.Color.GREENYELLOW;

public class ArrayNode {

    public static Pane apVisualizer;

    public static void initializeVisualizer(Pane anchorPane) {
        if (apVisualizer == null) {
            apVisualizer = anchorPane;
        }
    }

    private static final int BUCKET_CAPACITY = 4;
    private VBox vbox;
    private StackPane sp;
    private Rectangle rect;
    private Label value, index;
    private int number;
    private double x, y; // Coordinates

    private int id;

    // For separate chaining
    private VBox bucketContainer;
    private ArrayList<ArrayNode> bucket;

    public ArrayNode() {
        int x = 0, y = 0;
        rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        value = new Label(EMPTY_STRING);
        value.setStyle("-fx-font-weight: bold;");
        sp = new StackPane(rect, value);
        index = new Label(EMPTY_STRING);
        index.setStyle("-fx-font-weight: bold;");
        vbox = new VBox(2);
        bucket = new ArrayList<>();
        bucketContainer = new VBox();
        vbox.getChildren().addAll(sp, index, bucketContainer);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);

        number = 0;
    }

    public ArrayNode(String num, int i, double x, double y) {
        rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.WHITE);
        rect.setStrokeWidth(2);
        value = new Label(num);
        value.setStyle("-fx-font-weight: bold; -fx-text-fill: black;");
        sp = new StackPane(rect, value);
        index = new Label(Integer.toString(i));
        index.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        vbox = new VBox(2);
        bucket = new ArrayList<>();
        bucketContainer = new VBox();
        vbox.getChildren().addAll(sp, index, bucketContainer);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);

        number = Objects.equals(num, "") ? 0 : Integer.parseInt(num);
    }

    public ArrayNode(Pane parent) {
        int x = 0, y = 0;
        rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        value = new Label(EMPTY_STRING);
        value.setStyle("-fx-font-weight: bold;");
        sp = new StackPane(rect, value);
        index = new Label(EMPTY_STRING);
        index.setStyle("-fx-font-weight: bold;");
        vbox = new VBox(2);
        bucket = new ArrayList<>();
        bucketContainer = new VBox();
        vbox.getChildren().addAll(sp, index, bucketContainer);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        parent.getChildren().add(vbox);

        number = 0;
    }

    public int addToBucket(int number) {
        try {
            if (bucket.size() == BUCKET_CAPACITY) {
                throw new FullContainerException();
            }

            ArrayNode newNode = new ArrayNode(bucketContainer);
            newNode.id = bucket.size();
            newNode.setNumber(number);
            bucket.add(newNode);


        } catch (RuntimeException e) {
            System.out.println("[ArrayNode] " + e.getMessage());
        }
        return bucket.size();
    }

    public ArrayNode searchBucket(int number, SequentialTransition st) {
        for (ArrayNode node : bucket) {
            if (node.getNumber() == number) {
                st.getChildren().addAll( HashTableController.highlightNode(node, GREENYELLOW),
                        HashTableController.unhighlightNode(node, 400, GREENYELLOW));
                return node;
            }
            st.getChildren().addAll( HashTableController.highlightNode(node, PASTEL_ORANGE),
                    HashTableController.unhighlightNode(node, 400, PASTEL_ORANGE));

        }
        return null;
    }

    public int removeFromBucket(int number) {
        SequentialTransition st = new SequentialTransition();
        ArrayNode node = searchBucket(number, st);

        ObservableList<Animation> animations = st.getChildren();
        int size = animations.size();
        animations.remove(size - 2, size);

        animations.addAll(HashTableController.highlightNode(node, SUNSET_ORANGE),
                HashTableController.unhighlightNode(node, 400, SUNSET_ORANGE));

        st.setOnFinished(event -> {
            bucket.remove(node);
            bucketContainer.getChildren().remove(node.vbox);
            HashTableController.unhighlightNode(this, 400, Constant.Color.DEFAULTR).play();
        });
        st.play();

        return bucket.size();
    }

    public VBox getVBox() { return vbox; }
    public StackPane getStackPane() { return sp; }
    public Rectangle getRectangle() { return rect; }
    public Label getValue() { return value; }
    public Label getIndex() { return index; }
    public double getX() { return x; }
    public double getY() { return y; }
    public int getNumber() { return number; }

    public void setParent(AnchorPane parent) { apVisualizer = parent; }
    public void setVbox(VBox vbox) { this.vbox = vbox; }
    public void setSp(StackPane sp) { this.sp = sp; }
    public void setRect(Rectangle rect) { this.rect = rect; }
    public void setValue(String value) { this.value.setText(value); }
    public void setIndex(String index) { this.index.setText(index); }

    public void setNumber(Integer number) {
        this.number = number;

        if (number > Constant.HashTable.SENTINEL) {
            setValue(number.toString());
        }
        else
            setValue(EMPTY_STRING);
    }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }

    public ArrayList<ArrayNode> getBucket() { return bucket;}
}
