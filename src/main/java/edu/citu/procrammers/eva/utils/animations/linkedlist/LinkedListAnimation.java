package edu.citu.procrammers.eva.utils.animations.linkedlist;

import edu.citu.procrammers.eva.models.data_structures.SinglyLinkedList;
import edu.citu.procrammers.eva.models.data_structures.SinglyNode;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.geometry.Bounds;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;

public class LinkedListAnimation {

    public AnchorPane canvas;
    public SinglyLinkedList linkedList;
    private HashMap<String, SinglyNode> nodes;
    private HashMap<String, SinglyNodeGraphic> graphicNodes;
    private ArrayList<Arrow> arrows;
    public Text headLabel;
    public Text tailLabel;

    public LinkedListAnimation(AnchorPane canvas, SinglyLinkedList linkedList) {
        this.canvas = canvas;
        this.linkedList = linkedList;
        nodes = new HashMap<>();
        graphicNodes = new HashMap<>();
        arrows = new ArrayList<>();
        headLabel = new Text("HEAD");
        tailLabel = new Text(" TAIL");
        headLabel.setStyle("-fx-font-size: 14px; -fx-fill: #E9DBD5;");
        tailLabel.setStyle("-fx-font-size: 14px; -fx-fill: #E9DBD5;");
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

    public void removeNode(SinglyNode node) {
        canvas.getChildren().remove(graphicNodes.get(node.getId()));
        nodes.remove(node.getId());
        updatePointers();
    }

    public void disconnectNode(SinglyNode leftGraphic, SinglyNode rightGraphic) {
        int index = 0;
        for (Arrow arrow : arrows) {
            if (arrow.leftID.equals(leftGraphic.getId()) && arrow.rightID.equals(rightGraphic.getId())) {
                canvas.getChildren().remove(arrow);
                break;
            }
            ++index;
        }
        if (index != arrows.size())
            arrows.remove(index);
    }

    public void connectNode(SinglyNode left, SinglyNode right) {
        SinglyNodeGraphic leftGraphic = getNodeGraphic(left);
        SinglyNodeGraphic rightGraphic = getNodeGraphic(right);
        if (leftGraphic == null || rightGraphic == null){
            System.out.println("One of the nodes is null.");
            return;
        }

        // Create the link (a line representation)
        Arrow link = new Arrow(leftGraphic.getNodeId(), rightGraphic.getNodeId());
        arrows.add(link);
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

        if (!canvas.getChildren().contains(headLabel)) {
            canvas.getChildren().add(headLabel);
        }
        if (!canvas.getChildren().contains(tailLabel)) {
            canvas.getChildren().add(tailLabel);
        }

        if (linkedList.head == null) {
            System.out.println("Linkedlist is empty...");
            headLabel.setVisible(false);
            tailLabel.setVisible(false);
            return;
        }

        headLabel.setVisible(true);
        tailLabel.setVisible(true);

        Bounds headBounds = headLabel.getLayoutBounds();
        Bounds tailBounds = tailLabel.getLayoutBounds();

        double headWidth = headBounds.getWidth();
        double tailWidth =  tailBounds.getWidth();

        SinglyNodeGraphic headGraphic = getNodeGraphic(linkedList.headProperty.get());
        SinglyNodeGraphic tailGraphic = getNodeGraphic(linkedList.tailProperty.get());

        System.out.println("LinkedList head is " + linkedList.headProperty.get().value);
        System.out.println("LinkedList tail is " + linkedList.tailProperty.get().value);

        if (oldHead != null && oldHeadListener != null) {
            oldHead.layoutXProperty().removeListener(oldHeadListener);
        }
        if (oldTail != null && oldTailListener != null) {
            oldTail.layoutXProperty().removeListener(oldTailListener);
        }

        InvalidationListener headListener = (_) -> {
            headLabel.setLayoutX(headGraphic.getLayoutX() + SinglyNode.radius - headWidth / 2.0);
            headLabel.setLayoutY(headGraphic.getLayoutY() - SinglyNode.radius * 0.5);
        };

        InvalidationListener tailListener = (_) -> {
            tailLabel.setLayoutX(tailGraphic.getLayoutX() + SinglyNode.radius - tailWidth / 2.0);
            tailLabel.setLayoutY(tailGraphic.getLayoutY() + SinglyNode.radius * 3);
        };


        oldHead = headGraphic;
        oldTail = tailGraphic;
        oldHeadListener = headListener;
        oldTailListener = tailListener;

        headGraphic.layoutXProperty().addListener(headListener);
        tailGraphic.layoutXProperty().addListener(tailListener);
    }

    public SinglyNodeGraphic getNodeGraphic(SinglyNode node) {
        return graphicNodes.get(node.getId());
    }
}