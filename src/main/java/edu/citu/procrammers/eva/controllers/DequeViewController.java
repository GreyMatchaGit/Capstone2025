package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.Constant.Value;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class DequeViewController {

    @FXML private AnchorPane apVisualizer, apChat;
    @FXML private Button btnAddFirst, btnAddLast, btnRemoveFirst, btnRemoveLast, btnPeekFirst, btnPeekLast, btnClear;
    @FXML private TextField tfPrompt;
    @FXML private ImageView imgBackBtn, imgChatbotBtn;

    private LinkedList<Integer> deque = new LinkedList<>();
    private List<Rectangle> rectangles = new ArrayList<>();
    private List<Label> labels = new ArrayList<>();
    private List<VBox> vBoxes = new ArrayList<>();
    private int size = 0;

    private Node frontMarker;
    private Node backMarker;
    private double centerX, centerY;

    private boolean isChatbotVisible;

    private JSONObject dataJSON;
    private ChatBotController chatBotController;

    public void initialize() {
        setupGlow(imgBackBtn);

        isChatbotVisible = false;

        dataJSON = new JSONObject();

        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    centerX = apVisualizer.getWidth() / 2;
                    centerY = apVisualizer.getHeight() / 2;

                    Polygon tri1 = new Polygon(0,0, 20,10, 0,20);
                    tri1.setFill(NEGATIVE);
                    Polygon tri2 = new Polygon(20,0, 0,10, 20,20);
                    tri2.setFill(POSITIVE);
                    apVisualizer.getChildren().addAll(tri1, tri2);
                    frontMarker = tri1;
                    backMarker = tri2;

                    updateLayout();
                });
            }
        });

        ChatService.loadChatbot(chatBotController, apChat);
        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            if (isChatbotVisible) {
                apChat.setVisible(false);
            } else {
                apChat.setVisible(true);
            }
            isChatbotVisible = !isChatbotVisible;
        });
    }

    private void writeDataJSON() {
        dataJSON.put("type", "deque");
        dataJSON.put("size", deque.size());
        dataJSON.put("elements", deque.toString());
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON() {
        dataJSON.put("previousDeque", deque.toString());
        ChatService.fileWriter(dataJSON, DATA_PATH);
    }

    public void onButtonClick(ActionEvent event) {
        if (event.getSource() == btnAddFirst) addFirst();
        else if (event.getSource() == btnAddLast) addLast();
        else if (event.getSource() == btnRemoveFirst) removeFirst();
        else if (event.getSource() == btnRemoveLast) removeLast();
        else if (event.getSource() == btnPeekFirst) peekFirst();
        else if (event.getSource() == btnPeekLast) peekLast();
        else if (event.getSource() == btnClear) onClearOperation();
    }

    private int getNum() {
        try {
            int val = Integer.parseInt(tfPrompt.getText().trim());
            return val;
        } catch (NumberFormatException e) {
            System.err.println("Invalid Input!");
            return Integer.MIN_VALUE;
        } finally {
            tfPrompt.clear();
        }
    }

    private void addFirst() {
        int num = getNum(); if (num == Integer.MIN_VALUE) return;
        if (size > 0) writePreviousDataJSON();
        deque.addFirst(num);
        writeDataJSON();

        for (VBox vb : vBoxes) {
            TranslateTransition t = new TranslateTransition(Duration.seconds(0.5), vb);
            t.setByX(Value.OFFSET);
            t.play();
        }

        VBox newBox = createBoxes(0, num);
        newBox.setLayoutX(centerX - (size + 1) * Value.OFFSET / 2 - Value.BOX_SIZE / 2);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), newBox);
        slide.setByX(Value.OFFSET);
        slide.play();

        size++;
        slide.setOnFinished(e -> updateLayout());
    }

    private void addLast() {
        int num = getNum(); if (num == Integer.MIN_VALUE) return;
        if (size > 0) writePreviousDataJSON();
        deque.addLast(num);
        writeDataJSON();

        for (VBox vb : vBoxes) {
            TranslateTransition t = new TranslateTransition(Duration.seconds(0.5), vb);
            t.setByX(-Value.OFFSET);
            t.play();
        }

        VBox newBox = createBoxes(size, num);
        newBox.setLayoutX(centerX + (size + 1) * Value.OFFSET / 2 + Value.BOX_SIZE / 2);

        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.5), newBox);
        slide.setByX(-Value.OFFSET);
        slide.play();

        size++;
        slide.setOnFinished(e -> updateLayout());
    }

    private void updateLayout() {
        double totalWidth = (size - 1) * Value.OFFSET;
        double startX = centerX - totalWidth / 2;

        for (int i = 0; i < vBoxes.size(); i++) {
            VBox vb = vBoxes.get(i);
            vb.setTranslateX(0);
            vb.setLayoutX(startX + i * Value.OFFSET);
            Label idx = (Label) vb.getChildren().get(1);
            idx.setText(Integer.toString(i));
        }

        frontMarker.setLayoutX(startX - Value.OFFSET / 2);
        frontMarker.setLayoutY(centerY + 10);
        backMarker.setLayoutX(startX + (size - 1) * Value.OFFSET + Value.OFFSET / 2 + Value.OFFSET / 2);
        backMarker.setLayoutY(centerY + 10);

        frontMarker.setVisible(size > 0);
        backMarker.setVisible(size > 0);
    }

    private VBox createBoxes(int pos, int num) {
        Rectangle rect = new Rectangle(Value.BOX_SIZE, Value.BOX_SIZE);
        rect.setFill(DEFAULTR);
        rect.setStroke(DEFAULT); rect.setStrokeWidth(2);
        Label value = new Label(Integer.toString(num)); value.setStyle("-fx-font-weight:bold;");
        StackPane sp = new StackPane(rect, value);
        Label idx = new Label(Integer.toString(pos)); idx.setStyle("-fx-font-weight:bold;");
        VBox vbox = new VBox(2, sp, idx);
        vbox.setStyle("-fx-alignment:center;");
        apVisualizer.getChildren().add(vbox);
        rectangles.add(pos, rect);
        labels.add(pos, value);
        vBoxes.add(pos, vbox);
        return vbox;
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");
            writePreviousDataJSON();
            deque.clear();
            writeDataJSON();
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
            for (int i = vBoxes.size() - 1; i >= 0; --i) {
                VBox vb = vBoxes.get(i);
                destroyBox(vb);
                vBoxes.remove(i);
                System.out.println(i);
            }

            size = 0;
            frontMarker.setVisible(false);
            backMarker.setVisible(false);
            System.out.println("Deque cleared " +
                    deque.size() + " " +
                    rectangles.size() + " " +
                    labels.size() + " " +
                    vBoxes.size());
        }
    }

    private void removeFirst() {
        if (size == 0) return;
        writePreviousDataJSON();
        deque.removeFirst();
        VBox removed = vBoxes.removeFirst();
        rectangles.removeFirst();
        labels.removeFirst();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), removed);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            apVisualizer.getChildren().remove(removed);
            size--;
            updateLayout();
        });
        fade.play();
        writeDataJSON();
    }

    private void removeLast() {
        if (size == 0) return;
        writePreviousDataJSON();
        deque.removeLast();
        VBox removed = vBoxes.remove(size - 1);
        rectangles.remove(size - 1);
        labels.remove(size - 1);

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), removed);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            apVisualizer.getChildren().remove(removed);
            size--;
            updateLayout();
        });
        fade.play();
        writeDataJSON();
    }

    private void peekFirst() {
        int num = deque.getFirst();
        if(num == Integer.MIN_VALUE) return;
        if(size == 0) return ;

        VBox currentVBox = vBoxes.getFirst();
        Rectangle r = rectangles.getFirst();
        Label l = labels.getFirst();
        SequentialTransition traversal = new SequentialTransition();

        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
        traversal.getChildren().add(nodeTransition);

        // After traversal, do the slideY animation
        VBox finalCurrentVBox = currentVBox;
        Rectangle finalRectangle = r;
        traversal.setOnFinished(e -> {
            TranslateTransition tFirst = slideY(finalCurrentVBox, -100);
            PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
            FillTransition highlightSlide = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
            FillTransition resetSlide = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
            TranslateTransition tLast = slideY(finalCurrentVBox, 100);
            SequentialTransition fullSequence = new SequentialTransition(
                    tFirst,
                    pause,
                    highlightSlide,
                    resetSlide,
                    tLast
            );
            fullSequence.play();
        });

        traversal.play();
    }

    private void peekLast() {
        int num = deque.getLast();
        if(num == Integer.MIN_VALUE) return;
        if(size == 0) return ;

        VBox currentVBox = vBoxes.getLast();
        Rectangle r = rectangles.getLast();
        Label l = labels.getLast();
        SequentialTransition traversal = new SequentialTransition();

        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
        traversal.getChildren().add(nodeTransition);

        // After traversal, do the slideY animation
        VBox finalCurrentVBox = currentVBox;
        Rectangle finalRectangle = r;
        traversal.setOnFinished(e -> {
            TranslateTransition tFirst = slideY(finalCurrentVBox, -100);
            PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
            FillTransition highlightSlide = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
            FillTransition resetSlide = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
            TranslateTransition tLast = slideY(finalCurrentVBox, 100);
            SequentialTransition fullSequence = new SequentialTransition(
                    tFirst,
                    pause,
                    highlightSlide,
                    resetSlide,
                    tLast
            );
            fullSequence.play();
        });

        traversal.play();
        return;
    }

    private FadeTransition fadeOut(VBox vBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        return fadeOut;
    }

    private FillTransition fillRectangle(double duration, Rectangle r, Color from, Color to) {
        FillTransition fill = new FillTransition(Duration.seconds(duration), r);
        fill.setFromValue(from);
        fill.setToValue(to);

        return fill;
    }

    private TranslateTransition slideY(VBox vBox, double y) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByY(y);
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

    private void destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        TranslateTransition slideUp = slideY(vbox, -100);
        slideUp.setOnFinished(event -> fadeOut.play());
        slideUp.play();
        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }
}
