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

    public SinglyNode getHead() {
        return head;
    }

    public SinglyNode getTail() {
        return tail;
    }

    // Main head and tail
    public SinglyNode head;
    public SinglyNode tail;

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
        spacing = 20;
    }

    public void addTail(int value) {
        Pair<Double, Double> initialPosition = getInitialPositionAtTail(
            getVertMid() + SinglyNode.radius
        );
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
                prevTail,
                tail
            );
        }
        ++size;
        arrangeNodes();
    }

    public void addHead(int value) {
        Pair<Double, Double> initialPosition = getInitialPositionAtHead(
            getVertMid() + SinglyNode.radius
        );
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
                    head,
                    head.next
            );
        ++size;
        arrangeNodes();
    }

    public void addAt(int value, int index) {
        if (index == 0) {
            addHead(value);
            return;
        }

        if (index == size) {
            addTail(value);
            return;
        }


        Pair<Double, Double> initialPosition = getInitialPositionAtHead(
            getVertMid() + SinglyNode.radius
        );
        SinglyNode nodeToAdd = new SinglyNode(
            initialPosition.getKey(),
            initialPosition.getValue() - SinglyNode.radius * 2
        );
        nodeToAdd.value = value;

        SinglyNode current = head;

        int previousIndex = index - 1;
        for (int i = 0; i < previousIndex; ++i) {
            current = current.next;
        }

        animation.disconnectNode(
            current,
            current.next
        );

        width = (size) * ((SinglyNode.radius + spacing) * 2);
        arrangeNodes();

        animation.addNode(nodeToAdd);

        nodeToAdd.next = current.next;
        current.next = nodeToAdd;

        animation.connectNode(current, nodeToAdd);
        animation.connectNode(nodeToAdd, nodeToAdd.next);

        ++size;
        arrangeNodes();
    }

    public void removeHead() {
        if (head == null) {
            return;
        }

        if (head == tail) {
            SinglyNode nodeToRemove = head;
            head = null;
            tail = null;
            headProperty.set(null);
            tailProperty.set(null);
            animation.removeNode(nodeToRemove);
        } else {
            SinglyNode nodeToRemove = head;
            head = head.next;
            headProperty.set(head);
            tailProperty.set(tail);
            animation.disconnectNode(nodeToRemove, head);
            animation.removeNode(nodeToRemove);
        }

        --size;
        width = (size) * ((SinglyNode.radius + spacing) * 2);
        arrangeNodes();
    }

    public void removeTail() {
        if (head == null) {
            return;
        }

        if (head == tail) {
            removeHead();
            return;
        }

        SinglyNode prevToNodeToRemove = head;
        while (prevToNodeToRemove.next != tail) {
            prevToNodeToRemove = prevToNodeToRemove.next;
        }

        SinglyNode nodeToRemove = tail;
        tail = prevToNodeToRemove;
        tail.next = null;

        headProperty.set(head);
        tailProperty.set(tail);

        animation.disconnectNode(prevToNodeToRemove, nodeToRemove);
        animation.removeNode(nodeToRemove);

        --size;
        width = (size) * ((SinglyNode.radius + spacing) * 2);
        arrangeNodes();
    }

    public void remove(int value) {
        if (head == null) {
            return;
        }

        if (head == tail) {

        } else {

        }
    }

    private Pair<Double, Double> getInitialPositionAtTail(double vertical) {
        if (head == null) {
            return new Pair<>(
                    getHorizontalMid() - SinglyNode.radius,
                    vertical
            );
        }

        return new Pair<>(
                tail.x.get() + ((SinglyNode.radius + spacing) * 2) * 2,
                vertical
        );
    }

    private Pair<Double, Double> getInitialPositionAtHead(double vertical) {
        if (head == null) {
            return new Pair<>(
                    getHorizontalMid() - SinglyNode.radius,
                    vertical
            );
        }

        return new Pair<>(
                head.x.get() - ((SinglyNode.radius + spacing) * 2) * 2,
                vertical
        );
    }

    private void arrangeNodes() {
        SinglyNode current = head;
        int index = 0;
        while (current != null) {
            current.x.set(getHorizontalMid() + index * ((SinglyNode.radius + spacing) * 2) - (width + (SinglyNode.radius + spacing) * 2) / 2.0);
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
        System.out.printf("] with size: %d\n", size);
    }

    private double getVertMid() {
        return canvas.getHeight() / 2;
    }

    private double getHorizontalMid() {
        return canvas.getWidth() / 2;
    }
}
