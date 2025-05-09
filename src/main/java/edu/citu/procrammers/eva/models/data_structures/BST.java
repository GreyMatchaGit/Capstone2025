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

        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand(
                "SetHighlight",
                parentId,
                Integer.toString(0)
        ));

        if (elem.element < parent.element)
        {
            if (parent.getLeft() == null)
            {
                parent.setLeft(elem);
                elem.setParent(parent);

                int lineId = id++;
                parent.outgoingLineId = lineId;
                elem.incomingLineId = lineId;

                commands.add(am.newCommand("Connect", Integer.toString(lineId), parentId, childId));
            }
            else
            {
                commands.add(am.newCommand("Stop"));
                this.insert(elem, parent.getLeft(), commands);
            }
        }
        else
        {
            if (parent.getRight() == null) {
                parent.setRight(elem);
                elem.setParent(parent);

                elem.x.set(parent.x.getValue() + WIDTH_DELTA/2);
                elem.y.set(parent.y.getValue() + HEIGHT_DELTA);

                int lineId = id++;
                parent.outgoingLineId = lineId;
                elem.incomingLineId = lineId;

                commands.add(am.newCommand("Connect", Integer.toString(lineId), parentId, childId));

            }
            else {
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
            commands.add(am.newCommand("Stop"));
        }
    }

    private void animateNewPositions(Node node, List<Command> commands) {
        if (node != null) {
            String graphicId = Integer.toString(node.graphicId);

            String newX = Double.toString(node.x.get());
            String newY = Double.toString(node.y.get());
            commands.add(am.newCommand("Move", graphicId, newX, newY));
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
            }
            tree.x.set(xPosition);
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
        return tree.leftWidth + tree.rightWidth;
    }

    public List<Command> deleteElement(int keyToDelete) {
        List<Command> commands = new ArrayList<>();
        delete(root, keyToDelete, commands);
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
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));

            if (keyToDelete == node.getElement()) {
                if (node.getLeft() == null && node.getRight() == null) {
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
                    commands.add(am.newCommand("Stop"));
                }
                else if (node.getLeft() == null) {
                    if (node.getParent() != null) {
                        commands.add(am.newCommand("Delete", Integer.toString(node.outgoingLineId)));
                        commands.add(am.newCommand("Delete", Integer.toString(node.incomingLineId)));

                        String parentId = Integer.toString(node.getParent().graphicId);
                        String childId = Integer.toString(node.getRight().graphicId);

                        commands.add(am.newCommand("Connect", Integer.toString(id++), parentId, childId));
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
                    if (node.getParent() != null) {
                        commands.add(am.newCommand("Delete", Integer.toString(node.incomingLineId)));
                        commands.add(am.newCommand("Delete", Integer.toString(node.outgoingLineId)));

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
                    commands.add(am.newCommand("Stop"));
                }
                this.delete(node.getLeft(), keyToDelete, commands);
            }
            else {
                if (node.getRight() != null) {
                    commands.add(am.newCommand("Stop"));
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

        Node tmp = node.getLeft();

        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        while (tmp.getRight() != null)
        {
            tmp = tmp.getRight();

            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        }

        node.setElement(tmp.element);

        String labelId = Integer.toString(id++);
        commands.add(am.newCommand("SetText", labelId, Integer.toString(tmp.element),
                Double.toString(tmp.x.get()), Double.toString(tmp.y.get())));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, ""));
        commands.add(am.newCommand("Move", labelId, Double.toString(node.x.get()), Double.toString(node.y.get())));

        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, Integer.toString(node.element)));
        commands.add(am.newCommand("Delete", labelId));

        commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));

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

            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            commands.add(am.newCommand("Delete", tmpId));

            this.resizeTree(commands);
        }
        else {
            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            String tmpParentId = Integer.toString(tmp.getParent().graphicId);
            String tmpLeftId = Integer.toString(tmp.getLeft().graphicId);
            String tmpRightId = Integer.toString(tmp.getRight().graphicId);
            commands.add(am.newCommand("Connect", tmpParentId, tmpLeftId, tmpRightId));
            commands.add(am.newCommand("Stop"));

            commands.add(am.newCommand("Delete", Integer.toString(tmp.graphicId)));
            if (tmp.getParent() != node) {
                tmp.getParent().setRight(tmp.getLeft());
                tmp.getLeft().setParent(tmp.getParent());
            }
            else {
                node.setLeft(tmp.getLeft());
                tmp.getLeft().setParent(node);
            }
            this.resizeTree(commands);
        }
    }

    private void seratoMode(Node node, List<Command> commands) {
        String nodeId = Integer.toString(node.graphicId);

        Node tmp = node.getRight();

        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
        commands.add(am.newCommand("Stop"));
        commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        while (tmp.getLeft() != null)
        {
            tmp = tmp.getLeft();

            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(1)));
            commands.add(am.newCommand("Stop"));
            commands.add(am.newCommand("SetHighlight", Integer.toString(tmp.graphicId), Integer.toString(0)));
        }

        node.setElement(tmp.element);
        commands.add(am.newCommand("SetText", Integer.toString(id++), Integer.toString(tmp.element),
                Double.toString(tmp.x.get()), Double.toString(tmp.y.get())));
        commands.add(am.newCommand("ChangeNodeElementUI", nodeId, Integer.toString(node.element)));
        commands.add(am.newCommand("Move", Integer.toString(id - 1), Double.toString(node.x.get()), Double.toString(node.y.get())));
        commands.add(am.newCommand("Stop"));

        commands.add(am.newCommand("SetHighlight", nodeId, Integer.toString(0)));

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

            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));
            commands.add(am.newCommand("Delete", tmpId));

            this.resizeTree(commands);
        }
        else {

            commands.add(am.newCommand("Delete", Integer.toString(tmp.incomingLineId)));

            String tmpParentId = Integer.toString(tmp.getParent().graphicId);
            String tmpLeftId = Integer.toString(tmp.getLeft().graphicId);
            String tmpRightId = Integer.toString(tmp.getRight().graphicId);
            commands.add(am.newCommand("Connect", tmpParentId, tmpLeftId, tmpRightId));
            commands.add(am.newCommand("Stop"));

            commands.add(am.newCommand("Delete", Integer.toString(tmp.graphicId)));
            if (tmp.getParent() != node) {
                tmp.getParent().setLeft(tmp.getRight());

                tmp.getRight().setParent(tmp.getParent());
            }
            else {
                node.setRight(tmp.getRight());

                tmp.getRight().setParent(node);
            }
            this.resizeTree(commands);
        }
    }

    @Override
    public Node insert(int num) {
        return null;
    }

    @Override
    public int remove(int num) { // TODO
        return 0;
    }
}
