package edu.citu.procrammers.eva.utils;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.lang.reflect.Array;

import static edu.citu.procrammers.eva.utils.Constant.EMPTY_STRING;

public class ArrayNode {

    public static Pane apVisualizer;

    public static void initializeVisualizer(Pane anchorPane) {
        if (apVisualizer == null) {
            apVisualizer = anchorPane;
        }
    }

    private VBox vbox;
    private StackPane sp;
    private Rectangle rect;
    private Label value, index;
    private int number;
    private double x, y; // Coordinates

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
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);

        number = 0;
    }

    public ArrayNode(String num, int i, double x, double y) {
        rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");
        sp = new StackPane(rect, value);
        index = new Label(Integer.toString(i));
        index.setStyle("-fx-font-weight: bold;");
        vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);

        number = num == "" ? 0 : Integer.parseInt(num);
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
    public void setValue(String value) {
        this.value.setText(value);
        if(value.isEmpty()) number = 0;
        else number = Integer.parseInt(value);
    }
    public void setIndex(String index) { this.index.setText(index); }
    public void setNumber(int number) { this.number = number; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
}
