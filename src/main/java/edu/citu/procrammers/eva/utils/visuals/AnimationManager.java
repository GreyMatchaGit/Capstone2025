package edu.citu.procrammers.eva.utils.visuals;

import javafx.animation.PauseTransition;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class AnimationManager {
    private AnchorPane canvas;
    public List<Command> commands;
    private Stack<Command> history;
    public HashMap<Integer, Node> objects;
    private int currentIndex;
    public double speed;
    public boolean isContinuous;

    public AnimationManager(AnchorPane canvas) {
        this.canvas = canvas;
        commands = new ArrayList<>();
        history = new Stack<>();
        objects = new HashMap<>();
        currentIndex = 0;
        isContinuous = true;
        speed = .5;
    }

    public void setSpeed(double speed) {
        this.speed = 1- speed;
        if (this.speed < .1) {
            this.speed = .1;
        }
    }

    public Command newCommand(String... args) {
        int graphicId;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toUpperCase();
            switch (arg) {
                case "STOP":
                    return new StopCommand();
                case "CREATECIRCLE":
                    int key = Integer.parseInt(args[++i]);
                    graphicId = Integer.parseInt(args[++i]);
                    double x = Double.parseDouble(args[++i]);
                    double y = Double.parseDouble(args[++i]);
                    System.out.println("CREATENODE ID = " + graphicId);
                    return new DrawNodeCommand(key, graphicId, x, y, canvas, objects);
                case "SETHIGHLIGHT":
                    graphicId = Integer.parseInt(args[++i]);
                    int isOn = Integer.parseInt(args[++i]);

                    StackPane sp = (StackPane) objects.get(graphicId);
                    Circle circle = (Circle) sp.getChildren().getFirst();
                    return new SetHighlightCommand(isOn, circle);
                case "MOVE":
                    graphicId = Integer.parseInt(args[++i]);

                    System.out.println("Move Command: " + graphicId);

                    double newX = Double.parseDouble(args[++i]);
                    double newY = Double.parseDouble(args[++i]);

                    return new MoveCommand(graphicId, newX, newY, objects);
                case "CONNECT":
                    System.out.println("Connect Command creating...");
                    int lineId = Integer.parseInt(args[++i]);
                    int startId = Integer.parseInt(args[++i]);
                    int endId = Integer.parseInt(args[++i]);

                    return new DrawEdgeCommand(lineId, startId, endId, canvas, objects);
                case "DELETE":
                    System.out.println("Delete Command creating...");
                    graphicId = Integer.parseInt(args[++i]);

                    return new DeleteCommand(graphicId, canvas, objects);

                case "CHANGENODEELEMENTUI":
                    graphicId = Integer.parseInt(args[++i]);
                    String newElement = args[++i];

                    return new ChangeElementCommand(newElement, objects.get(graphicId));

                case "SETTEXT":
                    graphicId = Integer.parseInt(args[++i]);
                    String text = args[++i];
                    double tX = Double.parseDouble(args[++i]);
                    double tY = Double.parseDouble(args[++i]);

                    return new SetTextCommand(graphicId, text, tX, tY, canvas, objects);

            }
        }
        return null;
    }

    public int getCurrentIndex() { return currentIndex; }

    private void step() {
        System.out.println("Stepping...");
        while (currentIndex < commands.size()) {
            Command command = commands.get(currentIndex);
            System.out.println("Executing (index = " + currentIndex+ "): " + command);
            command.execute(() -> {});
            currentIndex++;
            if (command instanceof StopCommand) {
                break; // Stop after a chunk
            }
            history.push(command);
        }
    }

    public void undo() {
        if (history.isEmpty()) {
            System.out.println("No previous commands...");
            return;
        }

        currentIndex--; // Step back before undoing
        Command command = history.pop();
        if (command instanceof StopCommand) {
            System.out.println("Hit StopCommand, stopping undo.");
            return;
        }


        System.out.println("Undo Command (index = " + currentIndex+ "): " + command);
        command.undo(() -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(speed));
            pause.setOnFinished(e -> undo());
            pause.play();
        });
    }


    public void play(Runnable callback) {
        if (currentIndex >= commands.size()) {
            System.out.println("No more commands to play");
            history.clear();
            commands.clear();
            currentIndex = 0;
            callback.run();
            return;
        }

        history.push(new StopCommand());

        if (isContinuous) {
            step();
            PauseTransition pause = new PauseTransition(Duration.seconds(speed));
            pause.setOnFinished(e -> play(callback));
            pause.play();
        }
        else {
            step();
        }
    }






}
