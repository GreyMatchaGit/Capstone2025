package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.models.strategy.hashtable.*;
import edu.citu.procrammers.eva.utils.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

import static edu.citu.procrammers.eva.controllers.HashTableController.CollisionMethod.*;
import static edu.citu.procrammers.eva.controllers.HashTableController.ErrorHandling.LOGGER_PREFIX;
import static edu.citu.procrammers.eva.utils.Constant.HashTable.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.DATA_PATH;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class HashTableController implements Initializable {

    public static class ErrorHandling {
        public static String LOGGER_PREFIX = "[HashTableController]";
    }

    public static class CompressionMethod {
        public static String MAD = "MAD";
    }

    public static class CollisionMethod {
        public static final String SEPARATE_CHAINING = "Separate Chaining";
        public static final String LINEAR_PROBING = "Linear Probing";
        public static final String QUADRATIC_PROBING = "Quadratic Probing";
    }

    @FXML public HBox visualizer;
    @FXML public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    @FXML public TextField tfPrompt;
    @FXML public ComboBox<String> cbCompression, cbCollision;
    @FXML public ImageView imgBackBtn, imgChatbotBtn;
    @FXML public AnchorPane apChat;

    private ArrayList<ArrayNode> arrayNodes;
    private int size = 0;
    private int capacity = -1;
    private boolean isChatbotVisible = false;

    private JSONObject dataJSON;

    private ChatBotController chatBotController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUI();
        setListeners();

        dataJSON = new JSONObject();

        Platform.runLater(() -> {
            ArrayNode.initializeVisualizer(visualizer);
            capacity = 5;
            initHashTable(capacity);
        });

        ChatService.updateData(new JSONObject());
        ChatService.loadChatbot(chatBotController, apChat);
        apChat.setVisible(false);

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
        dataJSON.put("type", "hashtable");
        dataJSON.put("size", arrayNodes.size());
        dataJSON.put("capacity", capacity);
        dataJSON.put("collisionHandling", cbCollision.getValue());
        dataJSON.put("compressionMethod", cbCompression.getValue());

        List<Object> elements = getElements();

        dataJSON.put("elements", elements);
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        dataJSON.put("previousCollisionHandling", cbCollision.getValue());
        dataJSON.put("previousCompressionMethod", cbCompression.getValue());

        List<Object> elements = getElements();

        dataJSON.put("previousSize", arrayNodes.size());
        dataJSON.put("previousHashTable", elements);
        dataJSON.put("previousCapacity", capacity);
        ChatService.fileWriter(dataJSON, DATA_PATH);
    }

    private List<Object> getElements(){
        List<Object> values = new ArrayList<>();

        switch(cbCollision.getValue()){
            case SEPARATE_CHAINING:
                for (ArrayNode node : arrayNodes) {
                    List<Integer> bucketList = new ArrayList<>();
                    int mainVal = node.getNumber();
                    if (mainVal > SENTINEL) {
                        bucketList.add(mainVal);
                    }

                    for (ArrayNode bucketNode : node.getBucket()) {
                        int val = bucketNode.getNumber();
                        if (val > SENTINEL) {
                            bucketList.add(val);
                        }
                    }

                    values.add(bucketList);
                }
                break;

            case LINEAR_PROBING:
            case QUADRATIC_PROBING:
                for (ArrayNode node : arrayNodes) {
                    int val = node.getNumber();
                    if (val != EMPTY) {
                        values.add(val);
                    } else {
                        values.add(null);
                    }
                }
                break;

            default:
                System.out.println(LOGGER_PREFIX + "Choose a valid collision handling method. INVALID: " + cbCollision.toString());
                return null;
        }

        return values;
    }

    private void add(int value) {
        int hashCode = getHashCode(value);
        int index = compress(hashCode);

      try {
            ArrayNode arrayNodeAtIndex = arrayNodes.get(index);

            if (size == capacity) {
                System.out.println("Array is currently full.");
                pulseAllNodes(1000);
                return;
            }

            if(size != 0) writePreviousDataJSON();

            if (arrayNodeAtIndex.getNumber() <= SENTINEL) {
                System.out.println(LOGGER_PREFIX + String.format(" Index %d is empty.", index));
                select(arrayNodeAtIndex, 1000, MALACHITE);
                arrayNodeAtIndex.setNumber(value);
                ++size;
            } else {
                System.out.println(LOGGER_PREFIX + String.format(" A collision occurred at index %d. Handling the collision...", index));
                handleCollision(index, value);
            }

            System.out.println("Current size is " + size);
            writeDataJSON();
        } catch (RuntimeException e) {
            System.out.println(LOGGER_PREFIX + " " + e.getMessage());
        }
    }

    private void remove(int value) {
        // TODO: Implement remove algorithm
    }

    private void search(int value) {
        // TODO: Implement search algorithm
    }

    private void handleCollision(int index, int value) {
        CollisionStrategy collision = null;

        switch (cbCollision.getValue()) {
            case SEPARATE_CHAINING:
                collision = new SeparateChainingStrategy(arrayNodes, value);
                break;
            case LINEAR_PROBING:
                collision = new LinearProbingStrategy(arrayNodes, value);
                break;
            case QUADRATIC_PROBING:
                collision = new QuadraticProbingStrategy(arrayNodes, value, index);
                break;
            default:
                System.out.println(
                    LOGGER_PREFIX +
                    " Choose a valid collision handling method. INVALID: " +
                    cbCollision.toString()
                );
                return;
        }

        handleCollisionHelper(index, value, collision);
    }

    private void handleCollisionHelper(int index, int value, CollisionStrategy strategy) {
        ArrayNode currentNode = arrayNodes.get(index);

        int nextIndex = strategy.handleCollision(index);

        if (nextIndex == FINISHED) {
            Platform.runLater(() -> select(currentNode, 1000, MALACHITE));

            // TODO: Implement adding, remove, search
            currentNode.setNumber(value);

            ++size;
            System.out.println("Current size is " + size);
            return;
        }

        Platform.runLater(() -> check(currentNode, 1000));

        PauseTransition pause = new PauseTransition(Duration.millis(1000));
        pause.setOnFinished(event -> {
            handleCollisionHelper(nextIndex, value, strategy);
        });

        pause.play();
    }

    private int getHashCode(int value)  {
        return value;
    }

    private int compress(int hashCode) {
        CompressionStrategy compressionStrategy = new MadStrategy(7, 3, 5, arrayNodes.size());
        return compressionStrategy.compress(hashCode);
    }

    private void initHashTable(int capacity) {
        arrayNodes = new ArrayList<>();
        visualizer.getChildren().clear();
        size = 0;

        try {
            for (int i = 0; i < capacity; ++i) {
                ArrayNode newNode = new ArrayNode();
                newNode.setNumber(EMPTY);
                arrayNodes.add(newNode);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setListeners() {
        // tfPrompt should only accept digits and space
        tfPrompt.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if (!Character.isDigit(characterPressed) && characterPressed != ' ' && characterPressed != '\b') {
                String prompt = tfPrompt.getText();
                tfPrompt.setText( tfPrompt.getText(0, prompt.length() - 1));
                tfPrompt.positionCaret(prompt.length());
            }
        });

        // when the collision method is changed, reset the hashtable
        cbCollision.valueProperty().addListener((observableValue, oldValue, newValue) -> {
            if (oldValue == null) return;
            if (!oldValue.equals(newValue)) {
                initHashTable(capacity);
            }
        });
    }

    private void select(ArrayNode arrayNode, long durationInMillis, Color color) {
        pulseNode(arrayNode, Constant.Color.DEFAULT, color).play();
        fadeColorTo(arrayNode, Constant.Color.DEFAULTR, color).play();

        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {
            fadeColorTo(arrayNode, color, Constant.Color.DEFAULTR).play();
        });

        pause.play();
    }

    private void pulseAllNodes(long durationInMillis) {
        for (ArrayNode arrayNode : arrayNodes) {
            select(arrayNode, durationInMillis, SUNSET_ORANGE);
        }
    }

    private void check(ArrayNode arrayNode, long durationInMillis) {

        pulseNode(arrayNode, Constant.Color.DEFAULT, PASTEL_ORANGE).play();
        fadeColorTo(arrayNode, Constant.Color.DEFAULTR, PASTEL_ORANGE).play();

        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {
            fadeColorTo(arrayNode, PASTEL_ORANGE, Constant.Color.DEFAULTR).play();
        });

        pause.play();
    }

    private Timeline fadeColorTo(ArrayNode arrayNode, Color from, Color to) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(arrayNode.getRectangle().fillProperty(), from)
            ),
            new KeyFrame(Duration.seconds(0.5),
                new KeyValue(arrayNode.getRectangle().fillProperty(), to)
            )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(false);

        return timeline;
    }

    private Timeline pulseNode(ArrayNode arrayNode, Color from, Color to) {
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(arrayNode.getRectangle().strokeProperty(), from),
                new KeyValue(arrayNode.getValue().textFillProperty(), from)
            ),
            new KeyFrame(Duration.seconds(0.6),
                new KeyValue(arrayNode.getRectangle().strokeProperty(), to),
                new KeyValue(arrayNode.getValue().textFillProperty(), to)
            ),
            new KeyFrame(Duration.seconds(0.9),
                new KeyValue(arrayNode.getRectangle().strokeProperty(), from),
                new KeyValue(arrayNode.getValue().textFillProperty(), from)
            )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);

        return timeline;
    }

    private void setUI() {
        setupGlow(imgBackBtn);
        visualizer.setSpacing(10.0);

        ArrayList<String> collisionMethods = new ArrayList<>();
        collisionMethods.add(CollisionMethod.LINEAR_PROBING);
        collisionMethods.add(CollisionMethod.QUADRATIC_PROBING);
        collisionMethods.add(SEPARATE_CHAINING);
        cbCollision.setItems(FXCollections.observableList(collisionMethods));
    }

    public void clear() {
        pulseAllNodes(1000);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(1300));
        pauseTransition.setOnFinished(actionEvent -> initHashTable(5));
        pauseTransition.play();
    }

    @FXML
    private void onButtonClick(ActionEvent event) {
        try {
            String BUTTON_ID =  ((Button) event.getSource()).getId();
            String input = tfPrompt.getText();
            int value = 0;
            if (!input.isEmpty()) {
                value = Integer.parseInt(input);
            }

            switch (BUTTON_ID) {
                case "btnAdd" -> add(value);
                case "btnRemove" -> remove(value);
                case "btnSearch" -> search(value);
                case "btnClear" -> clear();
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }

    public void pause(long durationInMillis) {
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            System.out.println(LOGGER_PREFIX + " " + e.getMessage());
        }
    }
}
