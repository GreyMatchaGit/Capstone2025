package edu.citu.procrammers.eva.models.data_structures;

import edu.citu.procrammers.eva.utils.animations.linkedlist.LinkedListAnimation;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

public class SinglyLinkedList {

    public final AnchorPane canvas;
    public final LinkedListAnimation animation;

    public double width;
    public double spacing;

    // Main head and tail
    private SinglyNode head;
    private SinglyNode tail;

    // Properties to bind to the UI
    public final ObjectProperty<SinglyNode> headProperty = new SimpleObjectProperty<>(null);
    public final ObjectProperty<SinglyNode> tailProperty = new SimpleObjectProperty<>(null);

    public int size;

    public SinglyLinkedList(AnchorPane canvas) {
        this.canvas = canvas;
        animation = new LinkedListAnimation(canvas, this);
        head = null;
        tail = null;
        size = 0;
        width = 0;
        spacing = 10;
    }

    public void addTail(int value) {
        Pair<Double, Double> initialPosition = getInitialPositionAtTail();
        SinglyNode nodeToAdd = new SinglyNode(
                initialPosition.getKey(),
                initialPosition.getValue()
        );
        nodeToAdd.value = value;

        width = (size) * ((SinglyNode.radius + spacing) * 2);
        arrangeNodes();

        SinglyNode prevTail = null;
        if (isEmpty()) {
            head = nodeToAdd;
            tail = head;
        } else {
            prevTail = tail;
            tail.next = nodeToAdd;
            tail = tail.next;
        }

        // Synchronize the properties
        headProperty.set(head);
        tailProperty.set(tail);

        animation.addNode(nodeToAdd);
        if (prevTail != null) {
            animation.connectNode(
                    animation.getNodeGraphic(prevTail),
                    animation.getNodeGraphic(tail)
            );
        }
        ++size;
        arrangeNodes();
    }

    public void addHead(int value) {
        Pair<Double, Double> initialPosition = getInitialPositionAtHead();
        SinglyNode nodeToAdd = new SinglyNode(
                initialPosition.getKey(),
                initialPosition.getValue()
        );
        nodeToAdd.value = value;

        width = (size) * ((SinglyNode.radius + spacing) * 2);
        arrangeNodes();

        if (isEmpty()) {
            head = nodeToAdd;
            tail = head;
        } else {
            nodeToAdd.next = head;
            head = nodeToAdd;
        }

        // Synchronize the properties
        headProperty.set(head);
        tailProperty.set(tail);

        animation.addNode(nodeToAdd);
        if (head.next != null)
            animation.connectNode(
                    animation.getNodeGraphic(head),
                    animation.getNodeGraphic(head.next)
            );
        ++size;
        arrangeNodes();
    }

    private Pair<Double, Double> getInitialPositionAtTail() {
        if (head == null) {
            return new Pair<>(
                    getHorizontalMid() - SinglyNode.radius,
                    getVertMid() + SinglyNode.radius
            );
        }

        return new Pair<>(
                tail.x.get() + ((SinglyNode.radius + spacing) * 2) * 2,
                getVertMid() + SinglyNode.radius
        );
    }

    private Pair<Double, Double> getInitialPositionAtHead() {
        if (head == null) {
            return new Pair<>(
                    getHorizontalMid() - SinglyNode.radius,
                    getVertMid() + SinglyNode.radius
            );
        }

        return new Pair<>(
                head.x.get() - ((SinglyNode.radius + spacing) * 2) * 2,
                getVertMid() + SinglyNode.radius
        );
    }

    private void arrangeNodes() {
        SinglyNode current = head;
        int index = 0;
        while (current != null) {
            current.x.set(getHorizontalMid() + index * ((SinglyNode.radius + spacing) * 2) - (width + (SinglyNode.radius + spacing) * 2) / 2.0);
            if (current != head)
                current.y.set(getVertMid() + SinglyNode.radius);
            current = current.next;
            ++index;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public void print() {
        System.out.print("[");
        SinglyNode current = head;
        while (current != null) {
            System.out.print(current.value);
            if (current != tail)
                System.out.print(", ");
            current = current.next;
        }
        System.out.println("]");
    }

    private double getVertMid() {
        return canvas.getHeight() / 2;
    }

    private double getHorizontalMid() {
        return canvas.getWidth() / 2;
    }
}
