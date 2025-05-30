package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.models.strategy.hashtable.*;
import edu.citu.procrammers.eva.utils.*;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
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

    public AnchorPane apChat;
    public ImageView imgChatbotBtn;

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
    @FXML public ImageView imgBackBtn;

    private ChatBotController chatBotController;
    private JSONObject dataJSON;
    private boolean isChatbotVisible = false;

    private ArrayList<ArrayNode> arrayNodes;
    private int size = 0;
    private int capacity = -1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
//        visualizer = new HBox();
        setUI();
        setListeners();

        dataJSON = new JSONObject();

        Platform.runLater(() -> {
            ArrayNode.initializeVisualizer(visualizer);
            capacity = 5;
            initHashTable(capacity);
        });

        ChatService.loadChatbot(chatBotController, apChat);
        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            apChat.setVisible(!isChatbotVisible);
            isChatbotVisible = !isChatbotVisible;
        });
    }

    private void writeDataJSON(String operation) {
        List<Object> elements = getElements();

        dataJSON.put("type", "hashtable");
        dataJSON.put("size", arrayNodes.size());
        dataJSON.put("capacity", capacity);
        dataJSON.put("operation", operation);

        dataJSON.put("collisionHandling", cbCollision.getValue());
        dataJSON.put("compressionMethod", cbCompression.getValue());

        dataJSON.put("elements", elements);
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        List<Object> elements = getElements();
        dataJSON.put("previousSize", arrayNodes.size());
        dataJSON.put("previousCapacity", capacity);

        dataJSON.put("previousCollisionHandling", cbCollision.getValue());
        dataJSON.put("previousCompressionMethod", cbCompression.getValue());

        dataJSON.put("previousHashTable", elements);
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
                    if (val > SENTINEL) {
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
                pulseAllNodes(1000, arrayNodes);
                return;
            }

            writePreviousDataJSON();

            if (arrayNodeAtIndex.getNumber() == EMPTY) {
                System.out.println(LOGGER_PREFIX + String.format(" Index %d is empty.", index));
                select(arrayNodeAtIndex, 1000, MALACHITE);
                arrayNodeAtIndex.setNumber(value);
                ++size;
                writeDataJSON("add()");
            } else {
                System.out.println(LOGGER_PREFIX + String.format(" A collision occurred at index %d. Handling the collision...", index));
                handleCollision(index, value, "btnAdd");
            }

            System.out.println("Current size is " + size);

        } catch (RuntimeException e) {
            System.out.println(LOGGER_PREFIX + " " + e.getMessage());
        }
    }

    private void remove(int value) {
        int hashCode = getHashCode(value);
        int index = compress(hashCode);

        try {
            ArrayNode arrayNodeAtIndex = arrayNodes.get(index);
            writePreviousDataJSON();
            handleCollision(index, value, "btnRemove");
        } catch (RuntimeException e) {
            System.out.println(LOGGER_PREFIX + " " + e.getMessage());
        }
    }

    private void search(int value) {
        int hashCode = getHashCode(value);
        int index = compress(hashCode);

        try {
            ArrayNode arrayNodeAtIndex = arrayNodes.get(index);

            handleCollision(index, value, "btnSearch");

        } catch (RuntimeException e) {
            System.out.println(LOGGER_PREFIX + " " + e.getMessage());
        }
    }

    private void handleCollision(int index, int value, String buttonId) {
        CollisionStrategy collision = null;

        switch (cbCollision.getValue()) {
            case SEPARATE_CHAINING:
                collision = new SeparateChainingStrategy(arrayNodes, value, buttonId);
                break;
            case LINEAR_PROBING:
                collision = new LinearProbingStrategy(arrayNodes, value, buttonId);
                break;
            case QUADRATIC_PROBING:
                collision = new QuadraticProbingStrategy(arrayNodes, value, index, buttonId);
                break;
            default:
                System.out.println(
                    LOGGER_PREFIX +
                    " Choose a valid collision handling method. INVALID: " +
                    cbCollision.toString()
                );
                return;
        }

        handleCollisionHelper(index, value, collision, buttonId);
    }

    private void handleCollisionHelper(int index, int value, CollisionStrategy strategy, String buttonId) {
        ArrayNode currentNode = arrayNodes.get(index);
        int nextIndex = strategy.handleCollision(index);

        if (strategy instanceof SeparateChainingStrategy) {
            if (!buttonId.equals("btnAdd")) {
                highlightNode(currentNode, MALACHITE).play();
            }
            else {
                Platform.runLater( () -> select(currentNode, 1000, MALACHITE) );
                writeDataJSON("add()");
            }
            return;
        }

        if (nextIndex == FINISHED) {
            switch(buttonId) {
                case "btnAdd":
                    System.out.println("ADDING");

                    Platform.runLater(() -> select(currentNode, 1000, MALACHITE));
                    currentNode.setNumber(value);
                    size++;
                    writeDataJSON("add()");
                    System.out.println("Current size is " + size);
                    return;
                case "btnRemove":
                    select(currentNode, 1000, SUNSET_ORANGE);
                    pause(500, () -> {
                        currentNode.setNumber(SENTINEL);
                        System.out.printf("Current index[%d] is sentinel = %b\n", index, (currentNode.getNumber() == SENTINEL));
                        currentNode.setValue("-", true);

                        if (strategy instanceof LinearProbingStrategy) {
                            int i = strategy.getNext(index);
                            int iterations= 0;
                            while (iterations < arrayNodes.size() && (arrayNodes.get(i).getNumber() == EMPTY || arrayNodes.get(i).getNumber() == SENTINEL)) {
                                System.out.println("Clearing at index " + i);
                                arrayNodes.get(i).setNumber(EMPTY);
                                i = strategy.getPrevious(i);
                                System.out.printf("Previous index[%d] is sentinel = %b", i,  (arrayNodes.get(i).getNumber() == SENTINEL));
                                iterations++;
                            }
                        }

                        size--;
                        writeDataJSON("remove()");
                    });

                    return;
                case "btnSearch":
                    if (strategy.getIterations() != capacity) {
                        select(arrayNodes.get(index), 1000, Color.GREENYELLOW);
                    }
                    else {
                        select(arrayNodes.get(index), 1000, PASTEL_ORANGE);
                    }

                    return;
            }


        }
        else if (nextIndex == ERROR) {
            System.err.println("Infinite collisions occurred.");
            return;
        }

        Platform.runLater(() -> check(currentNode, 1000));

        pause(1000, () -> handleCollisionHelper(nextIndex, value, strategy, buttonId));
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
            if (SEPARATE_CHAINING.equals(cbCollision.getValue()) ) {
                System.out.println("SEPARATE_CHAINING");
                for (int i = 0; i < capacity; ++i) {
                    ArrayNode newNode = new ArrayNode();
                    newNode.setNumber(FULL);
                    newNode.setValue("↓");
                    arrayNodes.add(newNode);
                }
            }
            else {
                for (int i = 0; i < capacity; ++i) {
                    ArrayNode newNode = new ArrayNode();
                    newNode.setNumber(EMPTY);
                    System.out.println("newNode.getNumber() EMPTY = " + (newNode.getNumber() == EMPTY));
                    arrayNodes.add(newNode);
                }
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
            cbCollision.getItems().remove("");
            if (oldValue == null) return;
            if (!oldValue.equals(newValue)) {
                initHashTable(capacity);
            }

            writeDataJSON("switchCollision()");
        });
    }

    public static void select(ArrayNode arrayNode, long durationInMillis, Color color) {
        pulseNode(arrayNode, Constant.Color.DEFAULT, color).play();
        fadeColorTo(arrayNode, Constant.Color.DEFAULTR, color).play();

        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {
            fadeColorTo(arrayNode, color, Constant.Color.DEFAULTR).play();
        });

        pause.play();
    }

    public static PauseTransition unhighlightNode(ArrayNode arrayNode, long durationInMillis, Color color) {
        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {
            fadeColorTo(arrayNode, color, Constant.Color.DEFAULTR).play();
        });
        return pause;
    }

    public static ParallelTransition highlightNode(ArrayNode arrayNode, Color color) {
        ParallelTransition st = new ParallelTransition();

        st.getChildren().addAll(
                pulseNode(arrayNode, Constant.Color.DEFAULT, color),
                fadeColorTo(arrayNode, Constant.Color.DEFAULTR, color)
        );


        return st;
    }

    public static void pulseAllNodes(long durationInMillis, List<ArrayNode> arrayNodes) {
        for (ArrayNode arrayNode : arrayNodes) {
            select(arrayNode, durationInMillis, SUNSET_ORANGE);
        }
    }

    public static void check(ArrayNode arrayNode, long durationInMillis) {

        pulseNode(arrayNode, Constant.Color.DEFAULT, PASTEL_ORANGE).play();
        fadeColorTo(arrayNode, Constant.Color.DEFAULTR, PASTEL_ORANGE).play();

        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {
            fadeColorTo(arrayNode, PASTEL_ORANGE, Constant.Color.DEFAULTR).play();
        });

        pause.play();
    }

    public static Timeline fadeColorTo(ArrayNode arrayNode, Color from, Color to) {
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

    public static Timeline pulseNode(ArrayNode arrayNode, Color from, Color to) {
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

        cbCollision.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isEmpty()) {
                    setText("Collision"); // Displayed when "" is selected
                } else {
                    setText(item);
                }
            }
        });

        ArrayList<String> collisionMethods = new ArrayList<>();
        collisionMethods.add("");
        collisionMethods.add(CollisionMethod.LINEAR_PROBING);
        collisionMethods.add(CollisionMethod.QUADRATIC_PROBING);
        collisionMethods.add(SEPARATE_CHAINING);

        cbCollision.setItems(FXCollections.observableList(collisionMethods));
        cbCollision.setValue("");
    }

    public void clear() {
        pulseAllNodes(1000, arrayNodes);
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
        ArrayNode.apVisualizer = null;
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }

    public void pause(long durationInMillis, Runnable onFinished) {
        PauseTransition pause = new PauseTransition(Duration.millis(durationInMillis));
        pause.setOnFinished(event -> {onFinished.run(); });
        pause.play();
    }
}