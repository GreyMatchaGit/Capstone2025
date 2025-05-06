package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class ArraylistViewController {

    private double centerX, centerY;
    private static final Color POSITIVE = Color.ORANGE;
    private static final Color NEGATIVE = Color.RED;
    private static final Color SEARCH = Color.GREENYELLOW;
    private static final Color DEFAULT = Color.BLACK;
    private static final Color DEFAULTR = Color.WHITE;

    public AnchorPane apVisualizer;
    public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    public TextField tfPrompt;
    public ImageView imgBackBtn;

    private List<Integer> arrayList;
    private List<StackPane> stackPanes;
    private List<Rectangle> rectangles;
    private List<Label> labels;
    private List<VBox> vBoxes;
    private int size, capacity;

    public void initialize() {

        setupGlow(imgBackBtn);

        arrayList = new ArrayList<>();
        stackPanes = new ArrayList<>();
        rectangles = new ArrayList<>();
        labels = new ArrayList<>();
        vBoxes = new ArrayList<>();
        size=0;
        capacity=0;

        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    double centerX = apVisualizer.getWidth() / 2;
                    double centerY = apVisualizer.getHeight() / 2;
                    this.centerX = centerX;
                    this.centerY = centerY;
                });
            }
        });

        // Create initial nodes, capacity = 5
        Platform.runLater(() -> {
            updateList(5);
        });
    }

    public void onButtonClick(ActionEvent event) {
        if(event.getSource() == btnAdd) {
            addElement();
        } else if(event.getSource() == btnAddAt) {
            addAtElement();
        } else if(event.getSource() == btnRemove) {
            removeElement();
        } else if(event.getSource() == btnRemoveAt) {
            removeAtElement();
        } else if(event.getSource() == btnSearch) {
            searchElement();
        } else if(event.getSource() == btnClear) {
            onClearOperation();
        }
    }

    private int getNum() {
        String prompt = tfPrompt.getText().trim();
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

    private Pair<Integer, Integer> getNumAndPos() {
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

    private void updateList(int n) {
        for(int i=0; i<n; ++i) {
            double currentX = (centerX) + capacity*30;
            double currentY = centerY-100;
            System.out.println(capacity+ " " +currentX);
            VBox vBox = createBoxes(capacity,"", currentX, currentY, false);
            FadeTransition ft = fadeIn(vBox);
            TranslateTransition tt = slideY(vBox, 100);
            ft.play();
            ft.setOnFinished(e -> tt.play());
        }

        System.out.println("ÄrrayList cleared " +
                arrayList.size() + " " +
                rectangles.size() + " " +
                labels.size() + " " +
                vBoxes.size() + " Capacity:" + capacity);
        for(VBox vb : vBoxes) {
            for(Node b : vb.getChildren()) {
                if(b instanceof StackPane) {
                    for(Node m: ((StackPane) b).getChildren()) {
                        if(m instanceof Label) {
                            System.out.println(((Label) m).getText());
                        }
                    }
                }
            }
        }
    }

    private void addElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;
        arrayList.add(num);

        if(size == capacity) {
            int additional = (int) Math.ceil(capacity * 0.5);
            updateList(additional);
        }

        Label l = labels.get(size);
        Rectangle r = rectangles.get(size);
        l.setText(Integer.toString(num));
        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, POSITIVE);
        FillTransition reset = fillRectangle(0.3, r, POSITIVE, DEFAULTR);
        SequentialTransition st = new SequentialTransition(highlight, reset);
        st.play();
        ++size;
    }

    private void addAtElement() {
        Pair pair = getNumAndPos();
        int num = (int) pair.getValue();
        int pos = (int) pair.getKey();

        if(num == Integer.MIN_VALUE || pos == -1) return;

        if(size == capacity) {
            // resize updateList()
            int additional = (int) Math.ceil(capacity * 0.5);
            updateList(additional);
        }

        int index = pos-1;
        arrayList.add(index, num);
        System.out.println(index);
        VBox origBox = vBoxes.get(index);
        double currentX = origBox.getLayoutX();
        // Spawn box
        VBox vBox = createBoxes(index, String.valueOf(num), currentX-55, centerY-100, true);
        Rectangle r = rectangles.get(index);
        FadeTransition ft = fadeIn(vBox);
        TranslateTransition tt = slideY(vBox, 100);
        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, POSITIVE);
        FillTransition reset = fillRectangle(0.3, r, POSITIVE, DEFAULTR);
        SequentialTransition st = new SequentialTransition(highlight, reset);
        ft.play();
        ft.setOnFinished(e -> tt.play());
        tt.setOnFinished(e -> st.play());
        // Destroy last box
        VBox lastbox = vBoxes.get(capacity);
        shiftX(index, capacity, 55);
        updateIndexes();
        vBoxes.remove(capacity);
        labels.remove(capacity);
        rectangles.remove(capacity);
        apVisualizer.getChildren().remove(lastbox);
        ++size;

        System.out.println("ÄrrayList cleared " +
                arrayList.size() + " " +
                rectangles.size() + " " +
                labels.size() + " " +
                vBoxes.size() + " Capacity:" + capacity);
        for(VBox vb : vBoxes) {
            for(Node n : vb.getChildren()) {
                if(n instanceof StackPane) {
                    for(Node m: ((StackPane) n).getChildren()) {
                        if(m instanceof Label) {
                            System.out.println(((Label) m).getText());
                        }
                    }
                }
            }
        }
    }

    private void removeElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        boolean isExisting = false;
        VBox currentVBox = null;
        Rectangle r = null;
        Label l;
        int index;
        SequentialTransition traversal = new SequentialTransition();
        for (index = 0; index < size; index++) {
            currentVBox = vBoxes.get(index);
            r = rectangles.get(index);
            l = labels.get(index);

            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
            FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
            SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
            traversal.getChildren().add(nodeTransition);

            if (arrayList.get(index) == num) {
                isExisting = true;
                break;
            }
        }

        // After traversal, do the slideY animation
        if (isExisting) {
            VBox finalCurrentVBox = currentVBox;
            Rectangle finalRectangle = r;
            traversal.setOnFinished(e -> {
                FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
                FillTransition reset = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
                SequentialTransition fullSequence = new SequentialTransition(
                        highlight,
                        reset
                );
                fullSequence.play();
                fullSequence.setOnFinished(ee -> {
                    destroyBox(finalCurrentVBox);
                    vBoxes.remove(finalCurrentVBox);
                });
            });
        } else {
            traversal.setOnFinished(e-> {
                for (int i = 0; i < size; i++) {
                    Rectangle rr = rectangles.get(i);
                    Label ll = labels.get(i);
                    FillTransition highlight = fillRectangle(0.3, rr, DEFAULTR, NEGATIVE);
                    FillTransition reset = fillRectangle(0.3, rr, NEGATIVE, DEFAULTR);
                    SequentialTransition st = new SequentialTransition(highlight, reset);
                    st.play();
                }
            });
        }
        traversal.play();

    }

    private void removeAtElement() {
        int pos = getNum();

        if(pos <= 0 || pos > size || pos == Integer.MIN_VALUE) {
            System.err.println("Invalid Position");
            return;
        }

        arrayList.remove(pos-1);
        for(int i = pos-1; i < stackPanes.size() || i < arrayList.size(); ++i) {
            for (Node n : stackPanes.get(i).getChildren()) {
                if (n instanceof Label) {
                    String label = i < arrayList.size() ? Integer.toString(arrayList.get(i)) : "";
                    ((Label) n).setText(label);
                }
            }
        }
        --size;
    }

    private boolean searchElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return false;

        boolean isExisting = false;
        VBox currentVBox = null;
        Rectangle r = null;
        Label l;
        int index;
        SequentialTransition traversal = new SequentialTransition();
        for (index = 0; index < size; index++) {
            currentVBox = vBoxes.get(index);
            r = rectangles.get(index);
            l = labels.get(index);

            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
            FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
            SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
            traversal.getChildren().add(nodeTransition);

            if (arrayList.get(index) == num) {
                isExisting = true;
                break;
            }
        }

        // After traversal, do the slideY animation
        if (isExisting) {
            VBox finalCurrentVBox = currentVBox;
            Rectangle finalRectangle = r;
            traversal.setOnFinished(e -> {
                TranslateTransition tFirst = slideY(finalCurrentVBox, -100);
                PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
                FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
                FillTransition reset = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
                TranslateTransition tLast = slideY(finalCurrentVBox, 100);
                SequentialTransition fullSequence = new SequentialTransition(
                        tFirst,
                        pause,
                        highlight,
                        reset,
                        tLast
                );
                fullSequence.play();
            });
        } else {
            traversal.setOnFinished(e-> {
                for (int i = 0; i < size; i++) {
                    Rectangle rr = rectangles.get(i);
                    Label ll = labels.get(i);

                    FillTransition highlight = fillRectangle(0.3, rr, DEFAULTR, NEGATIVE);
                    FillTransition reset = fillRectangle(0.3, rr, NEGATIVE, DEFAULTR);
                    SequentialTransition st = new SequentialTransition(highlight, reset);
                    st.play();
                }
            });
        }
        traversal.play();

        return isExisting;
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");
            arrayList.clear();

            // Highlight Everything
            for (int i = 0; i < vBoxes.size(); ++i) {
                Rectangle r = rectangles.get(i);
                Label l = labels.get(i);
                l.setText("");
                FillTransition highlight = fillRectangle(0.6, r, DEFAULTR, NEGATIVE);
                Timeline tl = highlightNode(r,l,NEGATIVE,NEGATIVE);
                ParallelTransition pt = new ParallelTransition(highlight, tl);
                pt.play();
            }

            // Destroy All Boxes
            rectangles.clear();
            labels.clear();
            for (int i = capacity - 1; i >= 0; --i, --capacity) {
                VBox vb = vBoxes.get(i);
                destroyBox(vb);
                vBoxes.remove(i);
                System.out.println(i);
            }

            updateList(5);
            size = 0;
            System.out.println("ÄrrayList cleared " +
                    arrayList.size() + " " +
                    rectangles.size() + " " +
                    labels.size() + " " +
                    vBoxes.size());
        }
    }

    // Better Utilities
    private VBox createBoxes(int pos, String num, double x, double y, boolean isTemp) {
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
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);
        rectangles.add(pos, rect);
        labels.add(pos, value);
        vBoxes.add(pos, vbox);
        if(!isTemp) {
            shiftX(0, capacity, -25);
            capacity++;
        }

        return vbox;
    }

    private void updateIndexes() {
        for(int i=0; i<capacity; ++i) {
            VBox vb = vBoxes.get(i);
            for(Node n : vb.getChildren()) {
                if(n instanceof Label) {
                    ((Label) n).setText(String.valueOf(i));
                }
            }
        }
    }


    // Better Animations
    private FadeTransition fadeIn(VBox vBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        return fadeIn;
    }

    private FadeTransition fadeOut(VBox vBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        return fadeOut;
    }

    private TranslateTransition slideY(VBox vBox, double y) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByY(y);
        slide.setCycleCount(1);

        return slide;
    }

    private TranslateTransition slideX(VBox vBox, double x) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByX(x);
        slide.setCycleCount(1);

        return slide;
    }

    private Timeline highlightNode(Rectangle r, Label l, Color from, Color to) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                ),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(r.strokeProperty(), to),
                        new KeyValue(l.textFillProperty(), to)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);

        return timeline;
    }

    private FillTransition fillRectangle(double duration, Rectangle r, Color from, Color to) {
        FillTransition fill = new FillTransition(Duration.seconds(duration), r);
        fill.setFromValue(from);
        fill.setToValue(to);

        return fill;
    }

    private void destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        TranslateTransition slideUp = slideY(vbox, -100);
        slideUp.setOnFinished(event -> fadeOut.play());
        slideUp.play();
        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    private void shiftX(int start, int end ,int val) {
        for(int i=start; i<end; ++i) {
            VBox vb = vBoxes.get(i);
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
