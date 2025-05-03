package edu.citu.procrammers.eva.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

public class QueueViewController {

    private double centerX, centerY;

    public AnchorPane apVisualizer;
    public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    public TextField tfPrompt;

    private LinkedList<Integer> queue;
    private List<StackPane> stackPanes;
    private List<VBox> vBoxes;
    private int size, capacity;

    public void initialize() {
        queue = new LinkedList<>();
        stackPanes = new ArrayList<>();
        vBoxes = new ArrayList<>();
        size=0;
        capacity=0;

        apVisualizer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double centerX = newVal.doubleValue() / 2;
            this.centerX = centerX;
        });

        apVisualizer.heightProperty().addListener((obs, oldVal, newVal) -> {
            double centerY = newVal.doubleValue() / 2;
            this.centerY = centerY;

        });
    }

    public void onButtonClick(ActionEvent event) {
        if(event.getSource() == btnAdd) {
            enqueue();
        } else if(event.getSource() == btnRemove) {
            dequeue();
        } else if(event.getSource() == btnSearch) {
            front();
        } else if(event.getSource() == btnClear) {
            onClearOperation();
        }
    }

    public int getNum() {
        String prompt = tfPrompt.getText();
        int num = Integer.MIN_VALUE;
        try {
            num = Integer.parseInt(prompt);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Input!");
        } finally {
            tfPrompt.setText("");
        }
        return num;
    }

    public Pair<Integer, Integer> getNumAndPos() {
        int num = Integer.MIN_VALUE;
        int pos;

        String prompt = tfPrompt.getText().trim();
        StringTokenizer st = new StringTokenizer(prompt, " ");

        try {
            num = Integer.parseInt(st.nextToken());
            pos = Integer.parseInt(st.nextToken());
            if (pos <= 0 || pos > size+1) {
                throw new IllegalArgumentException("Invalid Position");
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.err.println("Invalid Arguments: " + e.getMessage());
            pos = -1;
        } finally {
            tfPrompt.setText("");
        }

        return new Pair<>(pos, num);
    }

    public void enqueue() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;
        queue.add(num);
        createBox(String.valueOf(num));

        StackPane sp = stackPanes.get(size);
        Rectangle r = null;
        Label l = null;
        for (Node n : sp.getChildren()) {
            if (n instanceof Label) {
                l = (Label)n;
                ((Label) n).setText(Integer.toString(num));
            } else if(n instanceof Rectangle) {
                r = (Rectangle) n;
            }

        }
        assert r!=null && l!=null;
        highlightNode(r, l);
        size++;
    }

    public void dequeue() {
        int num = queue.get(0);
        if(num == Integer.MIN_VALUE) return;

        int index = queue.indexOf(num);
        queue.removeFirst();

        Rectangle r = null;
        Label l = null;
        for(int i = index; i < stackPanes.size() || i < queue.size(); ++i) {
            for (Node n : stackPanes.get(i).getChildren()) {
                if (n instanceof Label) {
                    String label = i < queue.size() ? Integer.toString(queue.get(i)) : "";
                    ((Label) n).setText(label);
                    l = (Label) n;
                } else if(n instanceof Rectangle) {
                    r = (Rectangle) n;
                }
            }
            if(i == index) {
                highlightNode(r, l);
                VBox vbox = (VBox) stackPanes.get(i).getParent();
                phantomDelete(vbox.getLayoutX(), vbox.getLayoutY(), Integer.toString(num), i);
            }
        }

        --size;

    }

    public void front() {
        int num = queue.get(0);
        if(num == Integer.MIN_VALUE) return;

        if(queue.contains(num)) {
            int index = queue.indexOf(num);
            StackPane sp = stackPanes.get(index);
            Rectangle r = null;
            Label l = null;
            for (Node n : sp.getChildren()) {
                if (n instanceof Rectangle) {
                    r = (Rectangle) n;
                } else {
                    l = (Label) n;
                }
            }

            highlightNode(r, l);

        } else {
            System.err.println("Number not found!");
        }
    }

    public void onClearOperation() {
        tfPrompt.setText("");
        queue.clear();

        // Highlight Everything
        for(StackPane sp : stackPanes) {
            Rectangle r = null;
            Label l = null;
            for(Node n : sp.getChildren()) {
                if(n instanceof Label) {
                    l = (Label) n;
                    l.setText("");
                } else if(n instanceof Rectangle) {
                    r = (Rectangle) n;
                }
            }
            highlightNode(r, l);
        }


        size = 0;
        int removed=0;
        for(int i=capacity-1; i>=5; --i, --capacity) {
            stackPanes.remove(i);
            VBox vb = vBoxes.get(i);
            destroyBox(vb);
            vBoxes.remove(i);
            removed++;
        }
        shiftX(25 * removed);
    }


    // Animation utils
    public void createBox(String num) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        Label value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");
        StackPane sp = new StackPane(rect, value);
        Label index = new Label(Integer.toString(capacity));
        index.setStyle("-fx-font-weight: bold;");
        VBox vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX((centerX) + (size * 40));
        vbox.setLayoutY(centerY);

        apVisualizer.getChildren().add(vbox);
        stackPanes.add(sp);
        vBoxes.add(vbox);

        shiftX(-25);
        capacity++;

        // Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vbox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        // Create slide-down animation
        TranslateTransition slideLeft = new TranslateTransition(Duration.seconds(1), vbox);
        slideLeft.setByX(-100); // Move down by 100 units
        slideLeft.setCycleCount(1);

        // Play both animations in sequence
        fadeIn.setOnFinished(event -> slideLeft.play());
        fadeIn.play();
    }

    public void highlightNode(Rectangle r, Label l) {
        // Create color change animation (black to orange and back)
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(r.strokeProperty(), Color.BLACK),
                        new KeyValue(l.textFillProperty(), Color.BLACK)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(r.strokeProperty(), Color.ORANGE),
                        new KeyValue(l.textFillProperty(), Color.ORANGE)
                ),
                new KeyFrame(Duration.seconds(1.25),
                        new KeyValue(r.strokeProperty(), Color.BLACK),
                        new KeyValue(l.textFillProperty(), Color.BLACK)
                )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    public void destroyBox(VBox vbox) {

        // Fadeout animation
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vbox);
        fadeOut.setFromValue(1); // Start from transparent
        fadeOut.setToValue(0);   // End at fully visible
        fadeOut.setCycleCount(1);

        // Create slide-up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.seconds(1), vbox);
        slideUp.setByY(-100);
        slideUp.setCycleCount(1);

        // Play both animations in sequence
        slideUp.setOnFinished(event -> fadeOut.play());
        slideUp.play();

        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    public void shiftX(int val) {
        for(VBox vb : vBoxes) {
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    public void phantomDelete(double x, double y, String num, int indexing) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        Label value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");
        StackPane sp = new StackPane(rect, value);
        Label index = new Label(Integer.toString(indexing));
        index.setStyle("-fx-font-weight: bold;");
        VBox vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);
        highlightNode(rect, value);
        destroyBox(vbox);
    }
}
