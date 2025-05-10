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
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.animations.arraylist.ArrayListAnimations.*;

public class DequeViewController {

    private double centerX, centerY;

    @FXML private AnchorPane apVisualizer, apChat;
    @FXML private Button btnAddFirst, btnAddLast, btnRemoveFirst, btnRemoveLast, btnPeekFirst, btnPeekLast, btnClear;
    @FXML private TextField tfPrompt;
    @FXML private ImageView imgBackBtn, imgChatbotBtn;

    private LinkedList<Integer> deque;
    private List<Rectangle> rectangles ;
    private List<Label> labels;
    private List<VBox> vBoxes;

    private Node frontMarker;
    private Node backMarker;

    private JSONObject dataJSON;
    private ChatBotController chatBotController;

    private boolean isChatbotVisible = false;
    private int size = 0;

    public void initialize() {
        setupGlow(imgBackBtn);
        initLists();

        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    centerX = apVisualizer.getWidth() / 2;
                    centerY = apVisualizer.getHeight() / 2;

                    Polygon rightPointingTriangle = new Polygon(0,0, 20,10, 0,20);
                    rightPointingTriangle.setFill(NEGATIVE);
                    Polygon leftPointingTriangle = new Polygon(20,0, 0,10, 20,20);
                    leftPointingTriangle.setFill(POSITIVE);
                    apVisualizer.getChildren().addAll(rightPointingTriangle, leftPointingTriangle);
                    frontMarker = rightPointingTriangle;
                    backMarker = leftPointingTriangle;

                    updateLayout();
                });
            }
        });

        dataJSON = new JSONObject();
        ChatService.loadChatbot(chatBotController, apChat);
        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            apChat.setVisible(!isChatbotVisible);
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

    private void removeFirst() {
        if (size == 0) return;
        writePreviousDataJSON();

        VBox firstVBox = vBoxes.removeFirst();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), firstVBox);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            removeFirstChild(firstVBox);
            size--;
            updateLayout();
        });
        fade.play();
        writeDataJSON();
    }

    private void removeLast() {
        if (size == 0) return;
        writePreviousDataJSON();

        VBox lastVBox = vBoxes.removeLast();

        FadeTransition fade = new FadeTransition(Duration.seconds(0.3), lastVBox);
        fade.setToValue(0);
        fade.setOnFinished(e -> {
            removeLastChild(lastVBox);
            size--;
            updateLayout();
        });
        fade.play();
        writeDataJSON();
    }

    private void peekFirst() {
        int num = deque.getFirst();
        if(num == Integer.MIN_VALUE) return;
        if(size == 0) return;

        VBox firstVbox = vBoxes.getFirst();
        Rectangle firstRectangle = rectangles.getFirst();

        FillTransition highlight = fillRectangle(0.3, firstRectangle, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, firstRectangle, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);

        SequentialTransition traversal = new SequentialTransition();
        traversal.getChildren().add(nodeTransition);
        traversal.setOnFinished(e -> {
            traverseAnimation(firstVbox, firstRectangle);
        });

        traversal.play();
    }

    private void peekLast() {
        int num = deque.getLast();
        if(num == Integer.MIN_VALUE) return;
        if(size == 0) return ;

        VBox lastVBox = vBoxes.getLast();
        Rectangle lastRectangle = rectangles.getLast();

        FillTransition highlight = fillRectangle(0.3, lastRectangle, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, lastRectangle, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);

        SequentialTransition traversal = new SequentialTransition();
        traversal.getChildren().add(nodeTransition);
        traversal.setOnFinished(e -> {
            traverseAnimation(lastVBox, lastRectangle);
        });

        traversal.play();
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");
            writePreviousDataJSON();
            deque.clear();
            writeDataJSON();

            for (int i = 0; i < vBoxes.size(); ++i) {
                Rectangle r = rectangles.get(i);

                Label l = labels.get(i);
                l.setText("");

                FillTransition highlight = fillRectangle(0.6, r, DEFAULTR, NEGATIVE);
                Timeline tl = highlightNode(r,l,NEGATIVE,NEGATIVE);
                ParallelTransition pt = new ParallelTransition(highlight, tl);
                pt.play();
            }

            rectangles.clear();
            labels.clear();
            for (int i = vBoxes.size() - 1; i >= 0; --i) {
                VBox vb = vBoxes.get(i);
                destroyBox(vb);
                vBoxes.remove(i);
            }

            size = 0;
            frontMarker.setVisible(false);
            backMarker.setVisible(false);
        }
    }

    private void updateLayout() {
        frontMarker.setVisible(size > 0);
        backMarker.setVisible(size > 0);

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
    }

    private void removeFirstChild(VBox child) {
        apVisualizer.getChildren().remove(child);
        deque.removeFirst();
        rectangles.removeFirst();
        labels.removeFirst();
    }

    private void removeLastChild(VBox child) {
        apVisualizer.getChildren().remove(child);
        deque.removeLast();
        rectangles.removeLast();
        labels.removeLast();
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

    private void destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        TranslateTransition slideUp = slideY(vbox, -100);
        slideUp.setOnFinished(e -> fadeOut.play());
        slideUp.play();
        fadeOut.setOnFinished(e -> apVisualizer.getChildren().remove(vbox));
    }

    private void initLists() {
        deque = new LinkedList<>();
        rectangles = new ArrayList<>();
        labels = new ArrayList<>();
        vBoxes = new ArrayList<>();
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }
}
