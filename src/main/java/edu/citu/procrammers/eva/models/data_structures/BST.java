package edu.citu.procrammers.eva.models.data_structures;

import javafx.beans.property.DoubleProperty;
import javafx.scene.layout.AnchorPane;

public class BST extends Tree {
    public final double w;
    public final double h;

    public final double startingX;
    public final double first_print_pos_y;
    public final double print_max;

    public final double WIDTH_DELTA;
    public final double  HEIGHT_DELTA;
    public final double STARTING_Y;


    public final int FIRST_PRINT_POS_X;
    public final double PRINT_VERTICAL_GAP;
    public final int PRINT_HORIZONTAL_GAP;

    public BST(AnchorPane ap) {
        root = null;
        size = 0;
        w = ap.getWidth();
        h = ap.getHeight();

        FIRST_PRINT_POS_X  = 50;
        PRINT_VERTICAL_GAP  = 20;
        PRINT_HORIZONTAL_GAP = 50;

        startingX =  w / 2;
        first_print_pos_y  = h - 2 * PRINT_VERTICAL_GAP;
        print_max  = w - 10;

        WIDTH_DELTA  = 50;
        HEIGHT_DELTA = 50;
        STARTING_Y = 50;
    }

    public void addLeft(Node parent, Node child) {
        parent.setLeft(child);
        child.setParent(parent);
    }

    public void addRight(Node parent, Node child) {
        parent.setRight(child);
        child.setParent(parent);
    }

    public Node insertElement(int insertedValue) {
        if (root == null) {
            root = new Node(insertedValue, startingX, STARTING_Y);
            return root;
        }
        else {
            Node insertElem = new Node(insertedValue, 100, 100);
            insert(insertElem, root);
            resizeTree();
            return insertElem;
        }

    }

    private void insert(Node elem, Node parent) {
//        this.cmd("SetHighlight", tree.graphicID , 1);
//        this.cmd("SetHighlight", elem.graphicID , 1);

//        if (elem.element < parent.element)
//        {
//            this.cmd("SetText", 0,  elem.data + " < " + tree.data + ".  Looking at left subtree");
//        }
//        else
//        {
//            this.cmd("SetText",  0, elem.data + " >= " + tree.data + ".  Looking at right subtree");
//        }
//        this.cmd("Step");
//        this.cmd("SetHighlight", tree.graphicID, 0);
//        this.cmd("SetHighlight", elem.graphicID, 0);

        if (elem.element < parent.element)
        {
            if (parent.getLeft() == null)
            {
//                this.cmd("SetText", 0,"Found null tree, inserting element");
//
//                this.cmd("SetHighlight", elem.graphicID, 0);
                parent.setLeft(elem);
                elem.setParent(parent);
//                this.cmd("Connect", tree.graphicID, elem.graphicID, LINK_COLOR);
            }
            else
            {
//                this.cmd("CreateHighlightCircle", this.highlightID, HIGHLIGHT_CIRCLE_COLOR, tree.x, tree.y);
//                this.cmd("Move", this.highlightID, tree.left.x, tree.left.y);
//                this.cmd("Step");
//                this.cmd("Delete", this.highlightID);
                this.insert(elem, parent.getLeft());
            }
        }
        else
        {
            if (parent.getRight() == null) {
//                this.cmd("SetText",  0, "Found null tree, inserting element");
//                this.cmd("SetHighlight", elem.graphicID, 0);
                parent.setRight(elem);
                elem.setParent(parent);
//                this.cmd("Connect", tree.graphicID, elem.graphicID, LINK_COLOR);

                elem.x.set(parent.x.getValue() + WIDTH_DELTA/2);
                elem.y.set(parent.y.getValue() + HEIGHT_DELTA);
                System.out.printf("Node %d at (%.2f, %.2f)\n", elem.element, elem.x.get(), elem.y.get());
//                elem.y = parent.y + HEIGHT_DELTA;
//                this.cmd("Move", elem.graphicID, elem.x, elem.y);
            }
            else {
//                this.cmd("CreateHighlightCircle", this.highlightID, HIGHLIGHT_CIRCLE_COLOR, tree.x, tree.y);
//                this.cmd("Move", this.highlightID, tree.right.x, tree.right.y);
//                this.cmd("Step");
//                this.cmd("Delete", this.highlightID);
                this.insert(elem, parent.getRight());
            }
        }
    }

    private void resizeTree() {
        double startingPoint  = this.startingX;
        this.resizeWidths(this.root);
        if (this.root != null) {
            if (this.root.leftWidth > startingPoint) {
                startingPoint = this.root.leftWidth;
            }
            else if (this.root.rightWidth > startingPoint) {
                startingPoint = Math.max(this.root.leftWidth, 2 * startingPoint - this.root.rightWidth);
            }

            this.setNewPositions(this.root, startingPoint, STARTING_Y, 0);
//            this.animateNewPositions(this.treeRoot);
//            this.cmd("Step");
        }
    }

    private void setNewPositions(Node tree, double xPosition, double yPosition, double side) {
        if (tree != null) {
            tree.y.setValue(yPosition);
            if (side == -1) {
                xPosition = xPosition - tree.rightWidth;
            }
            else if (side == 1) {
                xPosition = xPosition + tree.leftWidth;
//                System.out.println("Left width: " + tree.leftWidth);
//                System.out.println("in right side x = " + xPx osition);
            }
            tree.x.set(xPosition);
//            System.out.printf("Node %d new pos(%.2f, %.2f)\n", tree.element, tree.x.get(), tree.y.get());
            this.setNewPositions(tree.getLeft(), xPosition, yPosition + HEIGHT_DELTA, -1);
            this.setNewPositions(tree.getRight(), xPosition, yPosition + HEIGHT_DELTA, 1);
        }

    }

    private double resizeWidths(Node tree) {
        if (tree == null) {
            return 0;
        }
        tree.leftWidth = Math.max(this.resizeWidths(tree.getLeft()), WIDTH_DELTA / 2);
        tree.rightWidth = Math.max(this.resizeWidths(tree.getRight()), WIDTH_DELTA / 2);
//        System.out.printf("Node %d width: %.2f, %.2f", tree.element, tree.leftWidth, tree.rightWidth);
        return tree.leftWidth + tree.rightWidth;
    }





    private Node insert(Node current, Node parent, int key) {
        if (current == null) {
            Node newNode = new Node(key);
            newNode.setParent(parent);
            if (parent == null) {
                System.out.println("Parent is null | adding");
            }
            if (root == null) {
                root = newNode;
            }
            size++;
            return newNode;
        }

        if (key < current.element) {
            System.out.println("on your left");
            current.setLeft(insert(current.getLeft(), current, key));
        } else if (key > current.element) {
            current.setRight(insert(current.getRight(), current, key));
        }

        return current;
    }

    @Override
    public Node insert(int num) {
        return insert(root, null, num);
    }

    @Override
    public int remove(int num) { // TODO
        return 0;
    }
    public Node getRoot() {
        return root;
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public void print(Node root) {
        if (root == null) {
            return ;
        }
        System.out.print(root.element + " ");
        print(root.getLeft());
        print(root.getRight());
    }

}
