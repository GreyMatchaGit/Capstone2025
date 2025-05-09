package edu.citu.procrammers.eva.models.data_structures;

import javafx.scene.layout.AnchorPane;

public class SinglyLinkedList {
    public final AnchorPane canvas;
    public SinglyNode head;
    public SinglyNode tail;
    public int size;

    public SinglyLinkedList(AnchorPane canvas) {
        this.canvas = canvas;
        head = null;
        size = 0;
    }

    public void addTail(int value) {
        SinglyNode nodeToAdd = new SinglyNode();
        nodeToAdd.value = value;
        if (isEmpty()) {
            head = nodeToAdd;
            tail = head;
        } else {
            tail.next = nodeToAdd;
            tail = tail.next;
        }
        ++size;
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

    public boolean isEmpty() {
        return size == 0;
    }
}
