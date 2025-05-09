package edu.citu.procrammers.eva.models.data_structures;

public class SinglyNode {

    // For drawing
    public double x, y;
    public static double side;

    // Node components
    public int value;
    public SinglyNode next;


    public SinglyNode() {
        x = 0;
        y = 0;
        value = 0;
        side = 25;
        next = null;
    }
}
