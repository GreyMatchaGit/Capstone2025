package edu.citu.procrammers.eva.models.data_structures;

import edu.citu.procrammers.eva.utils.animations.linkedlist.LinkedListAnimation;
import edu.citu.procrammers.eva.utils.animations.linkedlist.SinglyNodeGraphic;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;
import edu.citu.procrammers.eva.utils.visuals.Command;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class SinglyLinkedList {

    public final AnchorPane canvas;
    public final LinkedListAnimation animation;
    // From start of head to end of tail
    public double width;
    public double spacing;

    public SinglyNode head;
    public SinglyNode tail;
    public int size;

    public SinglyLinkedList(AnchorPane canvas) {
        this.canvas = canvas;
        animation = new LinkedListAnimation(canvas);
        head = null;
        size = 0;
        width = 0;
        spacing = 10;
    }

    public void addTail(int value) {
        SinglyNode nodeToAdd = new SinglyNode();
        nodeToAdd.value = value;
        nodeToAdd.y.set(getVertMid() - SinglyNode.side / 2.0);
        if (isEmpty()) {
            head = nodeToAdd;
            tail = head;
        } else {
            tail.next = nodeToAdd;
            tail = tail.next;
        }
        animation.addNode(nodeToAdd);
        width = size * SinglyNode.side * 2;
        ++size;
        arrangeNodes();
    }

    public void addHead(int value) {
        SinglyNode nodeToAdd = new SinglyNode();
        nodeToAdd.value = value;
        if (isEmpty()) {
            head = nodeToAdd;
            tail = head;
        } else {
            nodeToAdd.next = head;
            head = nodeToAdd;
        }
        ++size;
    }

    private void arrangeNodes() {
        SinglyNode current = head;
        int index = 0;
        while (current != null) {
            current.x.set(getHorizontalMid() + index * (SinglyNode.side * 2) - (width + SinglyNode.side * 2) / 2.0);
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
