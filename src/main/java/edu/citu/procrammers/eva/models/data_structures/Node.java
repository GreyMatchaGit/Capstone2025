package edu.citu.procrammers.eva.models.data_structures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Node {
    public int graphicId;
    int element;
    public DoubleProperty x;
    public DoubleProperty y;
    private Node left;
    private Node right;
    private Node parent;
    public double leftWidth;
    public double rightWidth;

    public int outgoingLineId;

    public int incomingLineId;

    public Node(int element) {
        x = new SimpleDoubleProperty(0);
        y = new SimpleDoubleProperty(0);
        this.element = element;
        leftWidth = 0;
        rightWidth = 0;
    }

    public Node(int element, int graphicId, double x, double y) {
        this.graphicId = graphicId;
        this.element = element;
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        leftWidth = 0;
        rightWidth = 0;
    }

    public int getElement() {
        return element;
    }

    public void setElement(int element) {
        this.element = element;
    }

    public Node getLeft() {
        return left;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public Node getRight() {
        return right;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return String.format("GID: %d, Element: %d (%.2f, %.2f)", graphicId, element, x.get(), y.get());
    }
}
