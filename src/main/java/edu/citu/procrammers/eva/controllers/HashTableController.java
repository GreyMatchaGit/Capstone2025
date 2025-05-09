package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.strategy.hashtable.*;
import edu.citu.procrammers.eva.utils.ArrayNode;
import edu.citu.procrammers.eva.utils.Constant;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

import static edu.citu.procrammers.eva.controllers.HashTableController.CollisionMethod.*;
import static edu.citu.procrammers.eva.controllers.HashTableController.errorHandling.LOGGER_PREFIX;
import static edu.citu.procrammers.eva.utils.Constant.HashTable.EMPTY;
import static edu.citu.procrammers.eva.utils.Constant.HashTable.FINISHED;
import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class HashTableController implements Initializable {

    public static class errorHandling {
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
    @FXML public ImageView imgBackBtn;

    private ArrayList<ArrayNode> arrayNodes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUI();
        setListeners();

        Platform.runLater(() -> {
            ArrayNode.initializeVisualizer(visualizer);
            initHashTable(5);
        });
    }

    public void startAlgorithm() {

    }

    private void test() {
        arrayNodes.get(0).addToBucket(5);
        arrayNodes.get(0).addToBucket(4);
    }

    private void add(int value) {
        int hashCode = getHashCode(value);
        int index = compress(hashCode);

        try {
            ArrayNode arrayNodeAtIndex = arrayNodes.get(index);

            if (arrayNodeAtIndex.getNumber() == EMPTY) {
                System.out.println(LOGGER_PREFIX + String.format(" Index %d is empty.", index));
                select(arrayNodeAtIndex, 700);
                arrayNodeAtIndex.setNumber(value);
                select(arrayNodeAtIndex, 300);
            } else {
                System.out.println(LOGGER_PREFIX + String.format(" A collision occurred at index %d. Handling the collision...", index));
                handleCollision(index, value);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
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
                System.out.println(LOGGER_PREFIX +
                        " Choose a valid collision handling method. INVALID: " +
                        cbCollision.toString());
                return;
        }

        handleCollisionHelper(index, value, collision);
    }

    private void handleCollisionHelper(int index, int value, CollisionStrategy strategy) {
        if (index == FINISHED)
            return;

        handleCollisionHelper(strategy.handleCollision(index), value, strategy);
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
    }

    private boolean select(ArrayNode arrayNode, long durationInMillis) {
        pulseNode(arrayNode, Constant.Color.DEFAULT, Color.GREEN).play();
        fadeColorTo(arrayNode, Constant.Color.DEFAULTR, Color.valueOf("#a0e052")).play();
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            System.out.println("[HashTableController] Error: " + e.getMessage());
            return false;
        }
        fadeColorTo(arrayNode, Color.valueOf("#a0e052"), Constant.Color.DEFAULTR).play();
        return true;
    }

    private boolean deselect(ArrayNode arrayNode, long durationInMillis) {
        pulseNode(arrayNode, Constant.Color.POSITIVE, Constant.Color.DEFAULT).play();
        try {
            Thread.sleep(durationInMillis);
        } catch (InterruptedException e) {
            System.out.println("[HashTableController] Error: " + e.getMessage());
            return false;
        }
        return true;
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
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(arrayNode.getRectangle().strokeProperty(), to),
                        new KeyValue(arrayNode.getValue().textFillProperty(), to)
                ),
                new KeyFrame(Duration.seconds(0.6),
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

    @FXML
    private void onButtonClick(ActionEvent event) {
        try {
            String BUTTON_ID =  ((Button) event.getSource()).getId();
            String input = tfPrompt.getText();
            int value = Integer.parseInt(input);

            switch (BUTTON_ID) {
                case "btnAdd" -> add(value);
                case "btnRemove" -> remove(value);
                case "btnSearch" -> search(value);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
