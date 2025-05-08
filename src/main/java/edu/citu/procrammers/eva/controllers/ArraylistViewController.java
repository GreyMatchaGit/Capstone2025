package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static java.lang.Math.ceil;

public class ArraylistViewController implements Initializable {

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
    public Button[] btns;

    private List<ArrayNode> arrayNodes;
    private int size, capacity, maxCap;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGlow(imgBackBtn);

        arrayNodes = new ArrayList<>();
        btns = new Button[]{btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear};

        ArrayNode.initializeVisualizer(apVisualizer);

        size=0;
        capacity=0;
        maxCap = 20;

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

        tfPrompt.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if (!Character.isDigit(characterPressed) && characterPressed != ' ' && characterPressed != '\b') {
                String prompt = tfPrompt.getText();
                tfPrompt.setText( tfPrompt.getText(0, prompt.length() - 1));
                tfPrompt.positionCaret(prompt.length());
            }
        });

        // Create initial nodes, capacity = 5
        Platform.runLater(() -> {
            dynamicAddResizeList(5);
        });
    }

    public void onButtonClick(ActionEvent event) {
        String buttonID = ((Button)event.getSource()).getId();
        disableButtons(true);
        switch (buttonID) {
            case "btnAdd" -> addElement();
            case "btnAddAt" -> addAtElement();
            case "btnRemove" -> removeElement();
            case "btnRemoveAt" -> removeAtElement();
            case "btnSearch" -> searchElement();
            case "btnClear" -> onClearOperation();
        }
        disableButtons(false);
    }

    private boolean isDigitOrSpace(char character) {
        return Character.isDigit(character) || character == ' ' || character == '\b';
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

    private void dynamicAddResizeList(int n) {
        for(int i=0; i<n; ++i) {
            double currentX = (centerX) + capacity*30;
            double currentY = centerY-100;
            VBox vBox = createBoxes(capacity,"", currentX, currentY, false);
            FadeTransition ft = fadeIn(vBox);
            TranslateTransition tt = slideY(vBox, 100);
            ft.play();
            ft.setOnFinished(e -> tt.play());
        }
    }

    private void dynamicSubResizeList(int n) {
        int minCapacity = 5;
        int removable = Math.max(0, capacity - minCapacity);
        int actualRemove = Math.min(n, removable);

        // Highlight Everything to Delete
        for (int i=capacity-1, j=0; j<actualRemove; i--, j++) {
            ArrayNode arrayNode = arrayNodes.get(i);
            Rectangle r = arrayNode.getRectangle();
            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, NEGATIVE);
            highlight.play();
        }

        // Destroy All Boxes
        for (int i=capacity-1, j=0; j<actualRemove; i--, j++) {
            ArrayNode arrayNode = arrayNodes.get(i);
            VBox vb = arrayNode.getVBox();
            destroyBox(vb).play();
            arrayNodes.remove(i);
        }
        capacity -= actualRemove;
        shiftX(0, capacity, 25*actualRemove);
    }

    private void addElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(size == capacity) {
            // resize updateList()
            int additional = (int) ceil(capacity * 0.5);
            if(capacity != maxCap) {
                additional = Math.min(additional, maxCap - capacity);
                dynamicAddResizeList(additional);
            } else return;
        }

        ArrayNode arrayNode = arrayNodes.get(size);
        arrayNode.setValue(String.valueOf(num));
        Rectangle r = arrayNode.getRectangle();
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
            int additional = (int) ceil(capacity * 0.5);
            if(capacity != maxCap) {
                additional = Math.min(additional, maxCap - capacity);
                dynamicAddResizeList(additional);
            } else return;
        }
        int index = pos-1;
        ArrayNode origNode = arrayNodes.get(index);
        VBox origBox = origNode.getVBox();
        double currentX = origNode.getX();
        // Spawn box
        VBox vBox = createBoxes(index, String.valueOf(num), currentX-55, centerY-100, true);
        ArrayNode newNode = arrayNodes.get(index);
        Rectangle r = newNode.getRectangle();
        FadeTransition ft = fadeIn(vBox);
        TranslateTransition tt = slideY(vBox, 100);
        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, POSITIVE);
        FillTransition reset = fillRectangle(0.3, r, POSITIVE, DEFAULTR);
        SequentialTransition st = new SequentialTransition(highlight, reset);
        ft.play();
        ft.setOnFinished(e -> tt.play());
        tt.setOnFinished(e -> st.play());
        // Destroy last box
        ArrayNode toDelete = arrayNodes.get(capacity);
        apVisualizer.getChildren().remove(toDelete.getVBox());
        arrayNodes.remove(toDelete);
        shiftX(index, capacity, 55);
        updateIndexes();
        ++size;
    }

    private void removeElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        boolean isExisting = false;
        ArrayNode currentNode = null;
        int index;
        SequentialTransition traversal = new SequentialTransition();
        for (index = 0; index < size; index++) {
            currentNode = arrayNodes.get(index);
            Rectangle r = currentNode.getRectangle();
            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
            FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
            SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
            traversal.getChildren().add(nodeTransition);
            if (currentNode.getNumber() == num) {
                isExisting = true;
                break;
            }
        }

        // I had a stroke here
        if (isExisting) {
            VBox finalVBox = currentNode.getVBox();
            Rectangle finalRectangle = currentNode.getRectangle();
            int finalIndex = index;
            traversal.setOnFinished(e -> {
                PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
                FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, NEGATIVE);
                SequentialTransition fullSequence = new SequentialTransition(
                        pause,
                        highlight
                );
                fullSequence.setOnFinished(event -> {
                    --size;
                    createBoxes(capacity,
                            "",
                            (centerX) + capacity*30,
                            centerY,
                            false);
                    SequentialTransition st = destroyBox(finalVBox);
                    st.play();
                    PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
                    pt.setOnFinished(ee-> {
                        shiftX(0, finalIndex, 25);
                        shiftX(finalIndex, capacity, -30);
                    });
                    pt.play();
                    --capacity;
                    arrayNodes.remove(finalIndex);
                    updateIndexes();
                    if(size <= Math.ceil(2.0/3 * capacity)) {
                        System.out.println(Math.ceil(2.0/3 * capacity));
                        int remove = capacity - (int) ceil(capacity * 0.75);
                        dynamicSubResizeList(remove);
                    }
                });
                fullSequence.play();
            });
        } else {
            traversal.setOnFinished(e-> {
                for (int i = 0; i < capacity; i++) {
                    ArrayNode arrayNode = arrayNodes.get(i);
                    Rectangle rr = arrayNode.getRectangle();
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

        int index = pos-1;
        ArrayNode currentNode = arrayNodes.get(index);
        VBox finalVBox = currentNode.getVBox();
        Rectangle finalRectangle = currentNode.getRectangle();
        PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
        FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, NEGATIVE);
        SequentialTransition fullSequence = new SequentialTransition(
                pause,
                highlight
        );
        fullSequence.setOnFinished(event -> {
            --size;
            createBoxes(capacity,
                    "",
                    (centerX) + capacity*30,
                    centerY,
                    false);
            SequentialTransition st = destroyBox(finalVBox);
            st.play();
            PauseTransition pt = new PauseTransition(Duration.seconds(0.5));
            pt.setOnFinished(ee-> {
                shiftX(0, index, 25);
                shiftX(index, capacity, -30);
            });
            pt.play();
            --capacity;
            arrayNodes.remove(index);
            updateIndexes();
        });
        fullSequence.play();
        --size;

        if(size <= ceil(2.0/3 * capacity)) {
            int remove = (int) ceil(capacity * 0.75);
            dynamicSubResizeList(remove);
        }
    }

    private boolean searchElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return false;

        boolean isExisting = false;
        ArrayNode currentNode = null;
        int index;
        SequentialTransition traversal = new SequentialTransition();
        for (index = 0; index < size; index++) {
            currentNode = arrayNodes.get(index);
            Rectangle r = currentNode.getRectangle();
            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
            FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
            SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
            traversal.getChildren().add(nodeTransition);

            if (currentNode.getNumber() == num) {
                isExisting = true;
                break;
            }
        }

        if (isExisting) {
            VBox finalVBox = currentNode.getVBox();
            Rectangle finalRectangle = currentNode.getRectangle();
            traversal.setOnFinished(e -> {
                TranslateTransition tFirst = slideY(finalVBox, -100);
                PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
                FillTransition highlight = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
                FillTransition reset = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
                TranslateTransition tLast = slideY(finalVBox, 100);
                SequentialTransition fullSequence = new SequentialTransition(
                        tFirst,
                        pause,
                        highlight,
                        reset,
                        tLast
                );
                fullSequence.play();

                disableButtons(false);
            });
        } else {
            traversal.setOnFinished(e-> {
                for (int i = 0; i < capacity; i++) {
                    ArrayNode arrayNode = arrayNodes.get(i);
                    Rectangle rr = arrayNode.getRectangle();
                    FillTransition highlight = fillRectangle(0.3, rr, DEFAULTR, NEGATIVE);
                    FillTransition reset = fillRectangle(0.3, rr, NEGATIVE, DEFAULTR);
                    SequentialTransition st = new SequentialTransition(highlight, reset);
                    st.play();
                }

                disableButtons(false);
            });
        }
        traversal.play();
        System.out.println(size);

        return isExisting;
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");

            // Highlight Everything
            for (ArrayNode arrayNode : arrayNodes) {
                Rectangle r = arrayNode.getRectangle();
                arrayNode.setNumber(Integer.MIN_VALUE);
                FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, NEGATIVE);
                highlight.play();
            }

            // Destroy All Boxes
            for (ArrayNode arrayNode : arrayNodes) {
                VBox vb = arrayNode.getVBox();
                destroyBox(vb).play();
            }

            size = 0;
            capacity = 0;
            dynamicAddResizeList(5);
            disableButtons(false);
        }
    }

    // Better Utilities
    private VBox createBoxes(int pos, String num, double x, double y, boolean isTemp) {
        ArrayNode arrayNode = new ArrayNode(num, pos, x, y);
        arrayNodes.add(pos, arrayNode);
        VBox vbox = arrayNode.getVBox();

        if(!isTemp) {
            shiftX(0, capacity, -25);
            capacity++;
        }

        return vbox;
    }

    private void updateIndexes() {
        for(int i=0; i<capacity; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            arrayNode.setIndex(String.valueOf(i));
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
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.75), vBox);
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

    private SequentialTransition destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
        TranslateTransition slideUp = slideY(vbox, -100);

        return new SequentialTransition(slideUp, fadeOut);
    }

    private void shiftX(int start, int end ,int val) {
                for(int i=start; i<end; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            VBox vb = arrayNode.getVBox();
            arrayNode.setX(vb.getLayoutX() + val);
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    private void shiftXes(int start, int end, int val) {
        ParallelTransition pt = new ParallelTransition();
        for(int i=start; i<end; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            VBox vb = arrayNode.getVBox();
            arrayNode.setX(arrayNode.getX() + val);
            TranslateTransition tt = slideX(vb, arrayNode.getX());
            tt.play();
        }
    }

    private void disableButtons(boolean b) {
        for(Button btn : btns) {
            btn.setDisable(b);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
