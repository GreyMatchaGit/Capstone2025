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
    private AnchorPane canves;
    public List<Command> commands;
    private Stack<Command> history;
    public HashMap<Integer, Node> objects;
    private int currentIndex;
    private long speed;
    private boolean isContinous;

    public AnimationManager(AnchorPane canves) {
        this.canves = canves;
        commands = new ArrayList<>();
        history = new Stack<>();
        objects = new HashMap<>();
        currentIndex = 0;
        isContinous = true;
    }

    public Command newCommand(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].toUpperCase();
            switch (arg) {
                case "CREATECIRCLE":
//                    int key = Integer.parseInt(args[++i]);
//                    double x = Double.parseDouble(args[++i]);
//                    double y = Double.parseDouble(args[++i]);
//                    edu.citu.procrammers.eva.models.data_structures.Node node = new edu.citu.procrammers.eva.models.data_structures.Node(key, x, y);
//                    return new DrawNodeCommand(node, canves);
                case "SETHIGHLIGHT":
                    int graphicId = Integer.parseInt(args[++i]);
                    int isOn = Integer.parseInt(args[++i]);

                    StackPane sp = (StackPane) objects.get(graphicId);
                    Circle circle = (Circle) sp.getChildren().getFirst();
                    return new SetHighlightCommand(isOn, circle);
            }
        }
        return null;
    }

    private void step() {
//        history.push(commands.get(currentIndex));
        commands.get(currentIndex).execute(() -> {});
        currentIndex++;
    }

    public void undo() {
//        commands.add(history.pop());
    }

    public void play() {
        System.out.println("playing..");
        if (currentIndex >= commands.size()) {
            System.out.println("No more commands to play");
            currentIndex = 0;
            return;
        }
        Command command = commands.get(currentIndex);

        if (isContinous) {
            command.execute(() -> {
                currentIndex++;
                PauseTransition pause = new PauseTransition(Duration.seconds(1));
                pause.setOnFinished(e -> play());
                pause.play();

            });
        }
        else {
//            step();
        }
    }





}
