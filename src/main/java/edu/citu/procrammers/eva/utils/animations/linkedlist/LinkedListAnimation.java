package edu.citu.procrammers.eva.utils.animations.linkedlist;

import edu.citu.procrammers.eva.models.data_structures.SinglyNode;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkedListAnimation {

    public AnchorPane canvas;
    private HashMap<String, SinglyNode> nodes;
    private ArrayList<SinglyNode> nodesArray;

    public LinkedListAnimation(AnchorPane canvas) {
        this.canvas = canvas;
        nodes = new HashMap<>();
    }

    public void drawNode(SinglyNode node) {
        SinglyNodeGraphic nodeGraphic  = new SinglyNodeGraphic(node);
        canvas.getChildren().add(nodeGraphic);
    }

    public void addNode(SinglyNode node) {
        nodes.put(node.getId(), node);
        drawNode(node);
    }

    public void removeNode(SinglyNode node) {
        nodes.remove(node.getId());
    }
}
