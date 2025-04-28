package edu.citu.procrammers.eva.models.data_structures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Node {
    int element;
    public DoubleProperty x;
    public DoubleProperty y;
    private Node left;
    private Node right;
    private Node parent;
    public double leftWidth;
    public double rightWidth;

    public Node(int element) {
        x = new SimpleDoubleProperty(0);
        y = new SimpleDoubleProperty(0);
        this.element = element;
    }

    public Node(int element, double x, double y) {
        this.element = element;
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
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
}
