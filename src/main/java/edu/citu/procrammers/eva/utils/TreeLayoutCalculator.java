package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.controllers.ADT.NodeController;
import edu.citu.procrammers.eva.models.data_structures.Node;
import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.AnchorPane;

import java.util.Map;

public class TreeLayoutCalculator {
    private static final double NODE_WIDTH = 50;
    private static final double NODE_HEIGHT = 50;
    private static final double HORIZONTAL_SPACING = 40;
    private static final double VERTICAL_SPACING = 80;
    private Map<Node, NodeController> nodeMap;

    public TreeLayoutCalculator(Map<Node, NodeController> nodeMap) {
        this.nodeMap = nodeMap;
    }

    public void add(Node node, AnchorPane pane) {
        if (node == null) return;

        // Calculate position
        DoubleProperty x = node.x;
        DoubleProperty y = node.y;

        NodeController controller = nodeMap.get(node);
//        javafx.scene.Node nodeView = controller.getRoot(); // assuming your controller has a getRoot()

//        AnchorPane.setLeftAnchor(nodeView, x);
//        AnchorPane.setTopAnchor(nodeView, y);
    }


    public void restructure() {
        // TODO: restructure the entire tree
    }



//    private double getX(Node node) {
//        int depth = getDepth(node);
//        int index = getIndexAtLevel(node);
//        double offset = Math.pow(2, getTreeHeight(node.getRoot()) - depth) * (NODE_WIDTH + HORIZONTAL_SPACING) / 2;
//        return index * (NODE_WIDTH + HORIZONTAL_SPACING) + offset;
//    }
//
//    private double getY(Node node) {
//        int depth = getDepth(node);
//        return depth * (NODE_HEIGHT + VERTICAL_SPACING);
//    }
//
//    private int getDepth(Node node) {
//        int depth = 0;
//        Node parent = getParent(node.getRoot(), node);
//        while (parent != null) {
//            depth++;
//            parent = getParent(node.getRoot(), parent);
//        }
//        return depth;
//    }
//
//    private int getIndexAtLevel(Node node) {
//        // You can improve this to get proper horizontal index based on in-order traversal
//        return inOrderIndex(node.getRoot(), node, new int[]{0});
//    }
//
//    private int inOrderIndex(Node root, Node target, int[] counter) {
//        if (root == null) return -1;
//        int left = inOrderIndex(root.getLeft(), target, counter);
//        if (left != -1) return left;
//
//        if (root == target) return counter[0];
//        counter[0]++;
//
//        return inOrderIndex(root.getRight(), target, counter);
//    }
//
//    private Node getParent(Node current, Node child) {
//        if (current == null || child == null) return null;
//        if (current.getLeft() == child || current.getRight() == child) return current;
//
//        Node leftSearch = getParent(current.getLeft(), child);
//        if (leftSearch != null) return leftSearch;
//
//        return getParent(current.getRight(), child);
//    }
//
//    private int getTreeHeight(Node root) {
//        if (root == null) return 0;
//        return 1 + Math.max(getTreeHeight(root.getLeft()), getTreeHeight(root.getRight()));
//    }
}
