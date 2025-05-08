package edu.citu.procrammers.eva.models.data_structures;

import edu.citu.procrammers.eva.utils.visuals.*;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class BST extends Tree {
    public final double w;
    public final double h;

    private AnchorPane canvas;

    public final double startingX;
    public final double first_print_pos_y;
    public final double print_max;

    public final double WIDTH_DELTA;
    public final double  HEIGHT_DELTA;
    public final double STARTING_Y;


    public final int FIRST_PRINT_POS_X;
    public final double PRINT_VERTICAL_GAP;
    public final int PRINT_HORIZONTAL_GAP;

    public boolean isStandard;

    private int id;
    public BST(AnimationManager animationManager, double width, double height, AnchorPane canvas) {
        super(animationManager, width, height);
        this.canvas = canvas;
        root = null;
        size = 0;
        w = width;
        h = height;

        FIRST_PRINT_POS_X  = 60;
        PRINT_VERTICAL_GAP  = 30;
        PRINT_HORIZONTAL_GAP = 60;

        startingX =  w / 2;
        first_print_pos_y  = h - 2 * PRINT_VERTICAL_GAP;
        print_max  = w - 20;

        WIDTH_DELTA  = 60;
        HEIGHT_DELTA = 60;
        STARTING_Y = 90;

        id = 0;
        isStandard = true;
    }


    public void addLeft(Node parent, Node child) {
        parent.setLeft(child);
        child.setParent(parent);
    }

    public void addRight(Node parent, Node child) {
        parent.setRight(child);
        child.setParent(parent);
    }

    private void implementAction() {

    }

    public List<Command> insertElement(int insertedValue) {
        List<Command> commands = new ArrayList<>();
        String insertee = Integer.toString(insertedValue);

        String graphicId;
        String x = Double.toString(startingX);
        String y = Double.toString(STARTING_Y);

        if (root == null) {
            root = new Node(insertedValue, id++, startingX, STARTING_Y);
            graphicId = Integer.toString(root.graphicId);
            System.out.println("Command Creation: id = " + graphicId);
            commands.add(am.newCommand("CreateCircle", insertee, graphicId, x, y));
            commands.add(am.newCommand("Stop"));
        }
        else {
            Node insertElem = new Node(insertedValue, id++, 100, 100);
            graphicId = Integer.toString(insertElem.graphicId);
            System.out.println("Command Creation: id = " + graphicId);

            x = Double.toString(100);
            y = Double.toString(100);

            commands.add(am.newCommand("CreateCircle", insertee, graphicId, x, y));
            commands.add(am.newCommand("Stop"));

            insert(insertElem, root, commands);

            resizeTree(commands);

        }

        return commands;
    }

    private void insert(Node elem, Node parent, List<Command> commands) {
        String parentId = Integer.toString(parent.graphicId);
        String childId = Integer.toString(elem.graphicId);

        commands.add(am.newCommand(
                "SetHighlight",
                parentId,
                Integer.toString(1)
        ));
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
        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand(
                "SetHighlight",
                parentId,
                Integer.toString(0)
        ));
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

                int lineId = id++;
                parent.outgoingLineId = lineId;
                elem.incomingLineId = lineId;


                commands.add(am.newCommand("Connect", Integer.toString(lineId), parentId, childId));
//                this.cmd("Connect", tree.graphicID, elem.graphicID, LINK_COLOR);
            }
            else
            {
//                this.cmd("CreateHighlightCircle", this.highlightID, HIGHLIGHT_CIRCLE_COLOR, tree.x, tree.y);
//                this.cmd("Move", this.highlightID, tree.left.x, tree.left.y);
//                this.cmd("Step");
//                this.cmd("Delete", this.highlightID);
                commands.add(am.newCommand("Stop"));
                this.insert(elem, parent.getLeft(), commands);
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

                parent.outgoingLineId = id;
                elem.incomingLineId = id;
                String lineId = Integer.toString(id++);
                commands.add(am.newCommand("Connect", lineId, parentId, childId));

                System.out.printf("Node %d at (%.2f, %.2f)\n", elem.element, elem.x.get(), elem.y.get());
//                elem.y = parent.y + HEIGHT_DELTA;
//                this.cmd("Move", elem.graphicID, elem.x, elem.y);
            }
            else {
//                this.cmd("CreateHighlightCircle", this.highlightID, HIGHLIGHT_CIRCLE_COLOR, tree.x, tree.y);
//                this.cmd("Move", this.highlightID, tree.right.x, tree.right.y);
//                this.cmd("Step");
//                this.cmd("Delete", this.highlightID);
                commands.add(am.newCommand("Stop"));
                this.insert(elem, parent.getRight(), commands);
            }
        }
    }

    private void resizeTree(List<Command> commands) {
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
            this.animateNewPositions(root, commands);
//            this.cmd("Step");
        }
    }

    private void animateNewPositions(Node node, List<Command> commands) {
        if (node != null) {
            String graphicId = Integer.toString(node.graphicId);

            String newX = Double.toString(node.x.get());
            String newY = Double.toString(node.y.get());
//            System.out.println("Moving: " + graphicId);
            commands.add(am.newCommand(new String[] {
                    "Move",
                    graphicId,
                    newX,
                    newY
            }));
            animateNewPositions(node.getLeft(), commands);
            animateNewPositions(node.getRight(), commands);
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

    public List<Command> deleteElement(int keyToDelete) {
        List<Command> commands = new ArrayList<>();
//        this.commands = [];
//        this.cmd("SetText", 0, "Deleting "+deletedValue);
//        this.cmd("Step");
//        this.cmd("SetText", 0, "");
//        this.highlightID = this.nextIndex++;
//        this.delete(root, keyToDelete, commands);
//        this.cmd("SetText", 0, "");
        delete(root, keyToDelete, commands);
        // Do delete
        return commands;
    }

    public void delete(Node node, int keyToDelete, List<Command> commands) {
        var leftchild = false;

        if (node != null) {
            String nodeId = Integer.toString(node.graphicId);
            if (node.getParent() != null)  {
                leftchild = node.getParent().getLeft() == node;
            }
            commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(1)));
//            if (keyToDelete < node.getElement()) {
//                this.cmd("SetText", 0, valueToDelete + " < " + tree.data + ".  Looking at left subtree");
//            }
//            else if (valueToDelete > tree.data) {
//                this.cmd("SetText",  0, valueToDelete + " > " + tree.data + ".  Looking at right subtree");
//            }
//            else {
//                this.cmd("SetText",  0, valueToDelete + " == " + tree.data + ".  Found node to delete");
//            }
//            this.cmd("Step");
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));

            if (keyToDelete == node.getElement()) {
                if (node.getLeft() == null && node.getRight() == null) {
//                    this.cmd("SetText", 0, "Node to delete is a leaf.  Delete it.");
//                    this.cmd("Delete", tree.graphicID);
                    commands.add(am.newCommand("Delete", Integer.toString(node.incomingLineId)));

                    commands.add(am.newCommand("Delete", nodeId));
                    if (leftchild && node.getParent() != null) {
                        node.getParent().setLeft(null);
                    }
                    else if (node.getParent() != null) {
                        node.getParent().setRight(null);
                    }
                    else {
                        root = null;
                    }
                    this.resizeTree(commands);
//                    this.cmd("Step");
                    commands.add(am.newCommand("Stop"));
                }
                else if (node.getLeft() == null) {
//                    this.cmd("SetText", 0, "Node to delete has no left child.  \nSet parent of deleted node to right child of deleted node.");
                    if (node.getParent() != null) {
//                        this.cmd("Disconnect",  tree.parent.graphicID, tree.graphicID);
                        commands.add(am.newCommand("Delete", Integer.toString(node.outgoingLineId)));
                        commands.add(am.newCommand("Delete", Integer.toString(node.incomingLineId)));
//                        commands.add(am.newCommand("Delete", Integer.toString(node.outgoingLineId)));

                        String parentId = Integer.toString(node.getParent().graphicId);
                        String childId = Integer.toString(node.getRight().graphicId);

                        commands.add(am.newCommand("Connect", Integer.toString(id++), parentId, childId));
//                        this.cmd("Step");
                        commands.add(am.newCommand("Stop"));
                        commands.add(am.newCommand("Delete", nodeId));
                        if (leftchild) {
                            node.getParent().setLeft(node.getRight());;
                        }
                        else {
                            node.getParent().setRight(node.getRight());
                        }
                        node.getRight().setParent(node.getParent());
                    }
                    else
                    {
                        commands.add(am.newCommand("Delete", nodeId));
                        root = node.getRight();
                        root.setParent(null);
                    }
                    this.resizeTree(commands);
                }
                else if (node.getRight() == null) {
//                    this.cmd("SetText", 0, "Node to delete has no right child.  \nSet parent of deleted node to left child of deleted node.");
                    if (node.getParent() != null) {
//                        this.cmd("Disconnect", node.parent.graphicID, node.graphicID);
                        commands.add(am.newCommand("Delete", Integer.toString(node.incomingLineId)));
                        commands.add(am.newCommand("Delete", Integer.toString(node.outgoingLineId)));
//                        this.cmd("Connect", node.parent.graphicID, node.left.graphicID, BST.LINK_COLOR);
//                        this.cmd("Step");
                        commands.add(am.newCommand("Stop"));
                        commands.add(am.newCommand("Delete", nodeId));

                        String parentId = Integer.toString(node.getParent().graphicId);
                        String childId = Integer.toString(node.getLeft().graphicId);
//
                        commands.add(am.newCommand("Connect", Integer.toString(id++), parentId, childId));
                        if (leftchild) {
                            node.getParent().setLeft(node.getLeft());
                        }
                        else {
                            node.getParent().setRight(node.getLeft());
                        }
                        node.getLeft().setParent(node.getParent());
                    }
                    else {
                        commands.add(am.newCommand("Delete", nodeId));
                        root = node.getLeft();
                        root.setParent(null);
                    }
                    this.resizeTree(commands);
                }
                else  {
                    if (isStandard) {
                        System.out.println("Standard mode");
                        standardMode(node, commands);
                    }
                    else {
                        System.out.println("Serato mode");
                        seratoMode(node, commands);
                    }
                }
            }
            else if (keyToDelete < node.getElement()) {
                if (node.getLeft() != null) {
//                    this.cmd("CreateHighlightCircle", this.highlightID, BST.HIGHLIGHT_CIRCLE_COLOR, node.x, node.y);
//                    this.cmd("Move", this.highlightID, node.left.x, node.left.y);
//                    this.cmd("Step");
                    commands.add(am.newCommand("Stop"));
//                    this.cmd("Delete", this.highlightID);
                }
                this.delete(node.getLeft(), keyToDelete, commands);
            }
            else {
                if (node.getRight() != null) {
//                    this.cmd("CreateHighlightCircle", this.highlightID, BST.HIGHLIGHT_CIRCLE_COLOR, node.x, node.y);
//                    this.cmd("Move", this.highlightID, node.right.x, node.right.y);
//                    this.cmd("Step");
                    commands.add(am.newCommand("Stop"));
//                    this.cmd("Delete", this.highlightID);
                }
                delete(node.getRight(), keyToDelete, commands);
            }
        }
        else
        {
//            this.cmd("SetText", 0, "Elemet "+valueToDelete+" not found, could not delete");
        }

    }


    private void standardMode(Node node, List<Command> commands) {
        String nodeId = Integer.toString(node.graphicId);
        // tree.left != null && tree.right != null{
//                    this.cmd("SetText", 0, "Node to delete has two childern.  \nFind largest node in left subtree.");

//                    this.highlightID = this.nextIndex;
//                    this.nextIndex += 1;
//                    this.cmd("CreateHighlightCircle", this.highlightID, BST.HIGHLIGHT_CIRCLE_COLOR, node.x, node.y);
//                    Node tmp = node;
        Node tmp = node.getLeft();
//                    String highlightID = Integer.toString(id++);
//                    this.cmd("Move", this.highlightID, tmp.x, tmp.y);
//                    commands.add(am.newCommand("Move", highlightID, tmpX, tmpY));
//                    this.cmd("Step");
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        while (tmp.getRight() != null)
        {
            tmp = tmp.getRight();
//                        tmpX = Double.toString(tmp.x.get());
//                        tmpY = Double.toString(tmp.y.get());
//                        commands.add(am.newCommand("Move", highlightID, tmpX, tmpY));
//                        this.cmd("Step");
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        }
//                    this.cmd("SetText", node.graphicID, " ");
//                    var labelID = this.nextIndex;
//                    this.nextIndex += 1;
//                    this.cmd("CreateLabel", labelID, tmp.data, tmp.x, tmp.y);
        node.setElement(tmp.element);

        String labelId = Integer.toString(id++);
        commands.add(am.newCommand("SetText", labelId, Integer.toString(tmp.element),
                Double.toString(tmp.x.get()), Double.toString(tmp.y.get())));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, ""));
        commands.add(am.newCommand("Move", labelId, Double.toString(node.x.get()), Double.toString(node.y.get())));

        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, Integer.toString(node.element)));
        commands.add(am.newCommand("Delete", labelId));
//                    this.cmd("Move", labelID, node.x, node.y);
//                    this.cmd("SetText", 0, "Copy largest value of left subtree into node to delete.");

//                    this.cmd("Step");
//                    this.cmd("SetHighlight", node.graphicID, 0);
        commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));
//                    this.cmd("Delete", labelID);
//                    this.cmd("SetText", node.graphicID, node.data);
//                    this.cmd("Delete", this.highlightID);
//                    this.cmd("SetText", 0,"Remove node whose value we copied.");

        if (tmp.getLeft() == null)
        {
            if (tmp.getParent() != node)
            {
                tmp.getParent().setRight(null);
            }
            else {
                node.setLeft(null);
            }
            String tmpId = Integer.toString(tmp.graphicId);
//                        this.cmd("Delete", tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            commands.add(am.newCommand("Delete", tmpId));

            this.resizeTree(commands);
        }
        else {
//                        this.cmd("Disconnect", tmp.parent.graphicID,  tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            String tmpParentId = Integer.toString(tmp.getParent().graphicId);
            String tmpLeftId = Integer.toString(tmp.getLeft().graphicId);
            String tmpRightId = Integer.toString(tmp.getRight().graphicId);
            commands.add(am.newCommand("Connect", tmpParentId, tmpLeftId, tmpRightId));
            commands.add(am.newCommand("Stop"));
//                        this.cmd("Step");
//                        this.cmd("Delete", tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.graphicId)));
            if (tmp.getParent() != node) {
                tmp.getParent().setRight(tmp.getLeft());
//                            tmp.left.parent = tmp.parent;
                tmp.getLeft().setParent(tmp.getParent());
            }
            else {
                node.setLeft(tmp.getLeft());
//                            node.left = tmp.left;
                tmp.getLeft().setParent(node);
//                            tmp.left.parent = node;
            }
            this.resizeTree(commands);
        }
    }

    private void seratoMode(Node node, List<Command> commands) {
        String nodeId = Integer.toString(node.graphicId);
        // tree.left != null && tree.right != null{
//                    this.cmd("SetText", 0, "Node to delete has two childern.  \nFind largest node in left subtree.");

//                    this.highlightID = this.nextIndex;
//                    this.nextIndex += 1;
//                    this.cmd("CreateHighlightCircle", this.highlightID, BST.HIGHLIGHT_CIRCLE_COLOR, node.x, node.y);
//                    Node tmp = node;
        Node tmp = node.getRight();
//                    String highlightID = Integer.toString(id++);
//                    this.cmd("Move", this.highlightID, tmp.x, tmp.y);

        String tmpX = Double.toString(tmp.x.get());
        String tmpY = Double.toString(tmp.y.get());
//                    commands.add(am.newCommand("Move", highlightID, tmpX, tmpY));
//                    this.cmd("Step");
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        while (tmp.getLeft() != null)
        {
            tmp = tmp.getLeft();
//                        tmpX = Double.toString(tmp.x.get());
//                        tmpY = Double.toString(tmp.y.get());
//                        commands.add(am.newCommand("Move", highlightID, tmpX, tmpY));
//                        this.cmd("Step");
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        }
//                    this.cmd("SetText", node.graphicID, " ");
//                    var labelID = this.nextIndex;
//                    this.nextIndex += 1;
//                    this.cmd("CreateLabel", labelID, tmp.data, tmp.x, tmp.y);
        node.setElement(tmp.element);
        commands.add(am.newCommand("SetText", Integer.toString(id++), Integer.toString(tmp.element),
                Double.toString(tmp.x.get()), Double.toString(tmp.y.get())));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, Integer.toString(node.element)));
        commands.add(am.newCommand("Move", Integer.toString(id - 1), Double.toString(node.x.get()), Double.toString(node.y.get())));
        commands.add(am.newCommand("Stop"));
//                    this.cmd("Move", labelID, node.x, node.y);
//                    this.cmd("SetText", 0, "Copy largest value of left subtree into node to delete.");

//                    this.cmd("Step");
//                    this.cmd("SetHighlight", node.graphicID, 0);
        commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));
//                    this.cmd("Delete", labelID);
//                    this.cmd("SetText", node.graphicID, node.data);
//                    this.cmd("Delete", this.highlightID);
//                    this.cmd("SetText", 0,"Remove node whose value we copied.");

        if (tmp.getRight() == null)
        {
            if (tmp.getParent() != node)
            {
                tmp.getParent().setLeft(null);
            }
            else {
                node.setRight(null);
            }
            String tmpId = Integer.toString(tmp.graphicId);
//                        this.cmd("Delete", tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            commands.add(am.newCommand("Delete", tmpId));

            this.resizeTree(commands);
        }
        else {
//                        this.cmd("Disconnect", tmp.parent.graphicID,  tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
//                        this.cmd("Connect", tmp.parent.graphicID, tmp.left.graphicID, BST.LINK_COLOR);

            String tmpParentId = Integer.toString(tmp.getParent().graphicId);
            String tmpLeftId = Integer.toString(tmp.getLeft().graphicId);
            String tmpRightId = Integer.toString(tmp.getRight().graphicId);
            commands.add(am.newCommand("Connect", tmpParentId, tmpLeftId, tmpRightId));
            commands.add(am.newCommand("Stop"));
//                        this.cmd("Step");
//                        this.cmd("Delete", tmp.graphicID);
            commands.add(am.newCommand("Delete", Integer.toString(tmp.graphicId)));
            if (tmp.getParent() != node) {
                tmp.getParent().setLeft(tmp.getRight());
//                            tmp.left.parent = tmp.parent;
                tmp.getRight().setParent(tmp.getParent());
            }
            else {
                node.setRight(tmp.getRight());
//                            node.left = tmp.left;
                tmp.getRight().setParent(node);
//                            tmp.left.parent = node;
            }
            this.resizeTree(commands);
        }
    }

//    private void addCommand(String... args) {
//        for (int i = 0; i < args.length; i++) {
//
//        }
//    }


    @Override
    public Node insert(int num) {
//        return insert(root, null, num);
        return null;
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
