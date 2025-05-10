package edu.citu.procrammers.eva.models.data_structures;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.UUID;

public class SinglyNode {

    // For drawing
    private String id;
    public DoubleProperty x, y;
    public static Double radius = 25.0;

    // Node components
    public int value;
    public SinglyNode next;

    public SinglyNode() {
        id = UUID.randomUUID().toString();
        x = new SimpleDoubleProperty(0);
        y = new SimpleDoubleProperty(0);
        value = 0;
        next = null;
    }

    public SinglyNode(double x, double y) {
        id = UUID.randomUUID().toString();
        this.x = new SimpleDoubleProperty(x);
        this.y = new SimpleDoubleProperty(y);
        value = 0;
        next = null;
    }

    public String getId() {
        return id;
    }
}
