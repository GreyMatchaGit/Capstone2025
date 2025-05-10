package edu.citu.procrammers.eva.utils.animations.linkedlist;

import edu.citu.procrammers.eva.models.data_structures.SinglyLinkedList;
import edu.citu.procrammers.eva.models.data_structures.SinglyNode;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class LinkedListAnimation {

    public AnchorPane canvas;
    public SinglyLinkedList linkedList;
    private HashMap<String, SinglyNode> nodes;
    private HashMap<String, SinglyNodeGraphic> graphicNodes;
    private ArrayList<SinglyNode> nodesArray;
    public Text head;
    public Text tail;

    public LinkedListAnimation(AnchorPane canvas, SinglyLinkedList linkedList) {
        this.canvas = canvas;
        this.linkedList = linkedList;
        nodes = new HashMap<>();
        graphicNodes = new HashMap<>();
        nodesArray = new ArrayList<>();
        head = new Text("HEAD");
        tail = new Text(" TAIL");
    }

    public void drawNode(SinglyNode node) {
        SinglyNodeGraphic nodeGraphic  = new SinglyNodeGraphic(node);
        graphicNodes.put(node.getId(), nodeGraphic);
        canvas.getChildren().add(nodeGraphic);
        updatePointers();
    }

    public void addNode(SinglyNode node) {
        nodes.put(node.getId(), node);
        drawNode(node);
    }

    public void connectNode(SinglyNodeGraphic leftGraphic, SinglyNodeGraphic rightGraphic) {
        if (leftGraphic == null || rightGraphic == null) return;

        // Create the link (a line representation)
        Arrow link = new Arrow();
        link.setStrokeWidth(2);
        link.setArrowStyle("-fx-stroke: #E9DBD5;");

        // Bind the line's start and end to the graphical representation positions
        link.startXProperty().bind(Bindings.createDoubleBinding(
                () -> leftGraphic.getLayoutX() + SinglyNode.radius,
                leftGraphic.layoutXProperty()
        ));

        link.startYProperty().bind(Bindings.createDoubleBinding(
                () -> leftGraphic.getLayoutY() + SinglyNode.radius,
                leftGraphic.layoutYProperty()
        ));

        link.endXProperty().bind(Bindings.createDoubleBinding(
                () -> rightGraphic.getLayoutX() - 2,
                rightGraphic.layoutXProperty()
        ));

        link.endYProperty().bind(Bindings.createDoubleBinding(
                () -> rightGraphic.getLayoutY() + SinglyNode.radius,
                rightGraphic.layoutYProperty()
        ));

        System.out.println("LINK HAS BEEN CREATED");

        // Add the link to the canvas at the bottom layer
        canvas.getChildren().addFirst(link);
    }

    SinglyNodeGraphic oldHead = null;
    SinglyNodeGraphic oldTail = null;
    InvalidationListener oldHeadListener = null;
    InvalidationListener oldTailListener = null;

    public void updatePointers() {
        head.setStyle("-fx-font-size: 14px; -fx-fill: #E9DBD5;");
        tail.setStyle("-fx-font-size: 14px; -fx-fill: #E9DBD5;");

        if (!canvas.getChildren().contains(head)) {
            canvas.getChildren().add(head);
        }
        if (!canvas.getChildren().contains(tail)) {
            canvas.getChildren().add(tail);
        }

        if (linkedList.headProperty.get() == null) {
            System.out.println("Linkedlist is empty...");
            head.setVisible(false);
            tail.setVisible(false);
            return;
        }

        head.setVisible(true);
        tail.setVisible(true);

        Bounds headBounds = head.getLayoutBounds();
        Bounds tailBounds = tail.getLayoutBounds();

        double headWidth = headBounds.getWidth();
        double tailWidth =  tailBounds.getWidth();

        SinglyNodeGraphic headGraphic = getNodeGraphic(linkedList.headProperty.get());
        SinglyNodeGraphic tailGraphic = getNodeGraphic(linkedList.tailProperty.get());

        System.out.println("LinkedList head is " + linkedList.headProperty.get().value);

        if (oldHeadListener != null) {
            oldHead.layoutXProperty().removeListener(oldHeadListener);
        }
        if (oldTailListener != null) {
            oldTail.layoutXProperty().removeListener(oldTailListener);
        }

        InvalidationListener headListener = (_) -> {
            System.out.println();
            head.setLayoutX(headGraphic.getLayoutX() + SinglyNode.radius - headWidth / 2.0);
            head.setLayoutY(headGraphic.getLayoutY() - SinglyNode.radius * 0.5);
        };

        InvalidationListener tailListener = (_) -> {
            tail.setLayoutX(tailGraphic.getLayoutX() + SinglyNode.radius - tailWidth / 2.0);
            tail.setLayoutY(tailGraphic.getLayoutY() + SinglyNode.radius * 3);
        };


        oldHead = headGraphic;
        oldTail = tailGraphic;
        oldHeadListener = headListener;
        oldTailListener = tailListener;

        headGraphic.layoutXProperty().addListener(headListener);
        tailGraphic.layoutXProperty().addListener(tailListener);
    }

    public void removeNode(SinglyNode node) {
        nodes.remove(node.getId());
    }

    public SinglyNodeGraphic getNodeGraphic(SinglyNode node) {
        return graphicNodes.get(node.getId());
    }
}