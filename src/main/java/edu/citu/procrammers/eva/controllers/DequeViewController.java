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

        ChatService.updateData(new JSONObject());
        ChatService.loadChatbot(chatBotController, apChat);
        apChat.setVisible(false);

        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
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
        deque.clear(); size = 0;
        apVisualizer.getChildren().removeAll(vBoxes);
        rectangles.clear(); labels.clear(); vBoxes.clear();
        writeDataJSON();
        updateLayout();
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }

    private void removeFirst() {
        if (size == 0) return;
        writePreviousDataJSON();
        deque.removeFirst();
        VBox removed = vBoxes.remove(0);
        rectangles.remove(0);
        labels.remove(0);

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
        if (size == 0) return;
        Rectangle rect = rectangles.get(0);
        FillTransition ft = new FillTransition(Duration.seconds(0.5), rect);
        ft.setToValue(SEARCH);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }

    private void peekLast() {
        if (size == 0) return;
        Rectangle rect = rectangles.get(size - 1);
        FillTransition ft = new FillTransition(Duration.seconds(0.5), rect);
        ft.setToValue(SEARCH);
        ft.setAutoReverse(true);
        ft.setCycleCount(2);
        ft.play();
    }
}
