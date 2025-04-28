package edu.citu.procrammers.eva.models;

import edu.citu.procrammers.eva.models.data_structures.Node;
import java.util.Stack;

public class BTInorderIterator implements BinaryTreeIterator {
    private final Stack<Node> stack = new Stack<>();
    private Node current;

    public BTInorderIterator(Node root) {
        this.current = root;
    }

    @Override
    public boolean hasNext() {
        return current != null || !stack.isEmpty();
    }

    @Override
    public Node next() {
        // Always dynamically walk down to the leftmost available node
//        while (current != null) {
//            stack.push(current);
//            current = current.getLeft();
//        }

        stack.push(current);
        current = current.getLeft();

        Node next = stack.pop();

        // After visiting current, go to right subtree
        current = next.getRight();

        return next;
    }
}


