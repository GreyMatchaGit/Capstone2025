package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import edu.citu.procrammers.eva.utils.animations.arraylist.ArrayListAnimations;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import org.json.JSONObject;
import javafx.util.Pair;
import org.json.JSONObject;

import java.net.URL;
import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.animations.arraylist.ArrayListAnimations.*;
import static java.lang.Math.ceil;

public class ArraylistViewController implements Initializable {

    private double centerX, centerY;

    @FXML private AnchorPane apVisualizer, apChat;
    @FXML private Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    @FXML private ImageView imgChatbotBtn;
    @FXML private TextField tfPromptNum, tfPromptPos;
    @FXML private ImageView imgBackBtn;
    public Button[] btns;

    private List<ArrayNode> arrayNodes;
    private List<ArrayNode> arrayList;
    private int size, capacity, maxCap;
    private JSONObject dataJSON;

    private boolean isChatbotVisible;

    public ChatBotController chatBotController;

    private JSONObject dataJSON;

    private boolean isChatbotVisible = false;

    private ChatBotController chatBotController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGlow(imgBackBtn, imgChatbotBtn);
        arrayNodes = new ArrayList<>();
        btns = new Button[]{btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear};

        ArrayNode.initializeVisualizer(apVisualizer);

        size=0;
        capacity=0;
        maxCap = 20;
        dataJSON = new JSONObject();

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

        tfPromptNum.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if (!Character.isDigit(characterPressed) && characterPressed != ' ' && characterPressed != '\b') {
                String prompt = tfPromptNum.getText();
                tfPromptNum.setText( tfPromptNum.getText(0, prompt.length() - 1));
                tfPromptNum.positionCaret(prompt.length());
            }
        });

        // Create initial nodes, capacity = 5
        Platform.runLater(() -> {
            dynamicAddResizeList(5);
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
        dataJSON.put("type", "arraylist");
        dataJSON.put("size", arrayNodes.size());
        dataJSON.put("capacity", capacity);

        List<Integer> toList = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            toList.add(Integer.valueOf(arrayNodes.get(i).getValue().getText()));
        }

        dataJSON.put("elements", toList.toString());
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        List<Integer> toList = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            toList.add(Integer.valueOf(arrayNodes.get(i).getValue().getText()));
        }
        dataJSON.put("previousArray", toList.toString());
        dataJSON.put("previousCapacity", capacity);
        ChatService.fileWriter(dataJSON, DATA_PATH);
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
        String prompt = tfPromptNum.getText().trim();
        int num = Integer.MIN_VALUE;
        try {
            num = Integer.parseInt(prompt);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Input!");
        } finally {
            tfPromptNum.clear();
        }
        return num;
    }

    private int getPos() {
        String prompt = tfPromptPos.getText().trim();
        int pos = -1;
        try {
            pos = Integer.parseInt(prompt);
            if(pos < 1 || pos > size + 1) throw new IllegalArgumentException();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Input!");
            pos = -1;
        } finally {
            tfPromptPos.clear();
        }
        return pos;
    }

    private void dynamicAddResizeList(int n) {
        for(int i=0; i<n; ++i) {
            double totalWidth = (capacity) * 50 + capacity * 5;
            double currentX = centerX + totalWidth;
            double currentY = centerY-50;
            System.out.println("Pos " + capacity +
                    " X: " + currentX + " Y: " + currentY + " Capacity: " + capacity);
            VBox vBox = createBoxes(capacity,"", currentX, currentY);
            capacity++;

            FadeTransition ft = fadeIn(vBox);
            TranslateTransition tt = slideY(vBox, 50);
            ft.play();
            ft.setOnFinished(e -> tt.play());
        }

        for(int i = 0; i < capacity; ++i) {
            ArrayNode node = arrayNodes.get(i);
            node.setX(node.getVBox().getLayoutX());
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
            apVisualizer.getChildren().remove(vb);
            capacity--;
        }

        for(int i = 0; i < capacity; ++i) {
            ArrayNode node = arrayNodes.get(i);
            node.setX(node.getVBox().getLayoutX());
        }
    }

    private void addElement() {
        int num = getNum();
        tfPromptPos.clear();
        if(num == Integer.MIN_VALUE) {
            disableButtons(false);
            return;
        }
        if(num == Integer.MIN_VALUE) return;
        writeDataJSON();

        if(size != 0){
            writePreviousDataJSON();
        }

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
        writeDataJSON();
    }

    private void addAtElement() {
        int num = getNum();
        int pos = getPos();

        if(num == Integer.MIN_VALUE || pos == -1) {
            disableButtons(false);
            return;
        }

        if(size != 0){
            writePreviousDataJSON();
        }

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
        double currentX = origNode.getX();

        VBox vBox = createBoxes(index, String.valueOf(num), currentX-55, centerY-100);
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

        ArrayNode toDelete = arrayNodes.get(capacity);
        apVisualizer.getChildren().remove(toDelete.getVBox());
        arrayNodes.remove(toDelete);
        shiftX(index, capacity, 55);
        updateIndexes();
        ++size;
        writeDataJSON();
    }

    private void removeElement() {
        int num = getNum();
        tfPromptPos.clear();
        if(num == Integer.MIN_VALUE) {
            disableButtons(false);
            return;
        }

        SequentialTransition traversal = new SequentialTransition();
        ArrayNode currentNode = searchHelper(num, traversal);

        if (currentNode != null) {
            VBox vbox = currentNode.getVBox();
            traversal.setOnFinished(e -> {
                SequentialTransition st = destroyBox(vbox);
                st.setOnFinished(event -> {
                    writePreviousDataJSON();
                    apVisualizer.getChildren().remove(vbox);
                    shiftX(arrayNodes.indexOf(currentNode), capacity, -55);
                    capacity--;
                    size--;
                    arrayNodes.remove(currentNode);
                    dynamicAddResizeList(1);
                    updateIndexes();
                });
                st.play();
                disableButtons(false);
            });
        } else {
            pulseNodeAll(arrayNodes, traversal);
            disableButtons(false);
        }
        traversal.play();

        if(size <= ceil(2.0/3 * capacity)) {
            int remove = (int) ceil(capacity * 0.25);
            dynamicSubResizeList(remove);
        }
        writeDataJSON();

        for(int i = 0; i < arrayNodes.size(); ++i) {
            System.out.println(arrayNodes.get(i).getNumber());
        }

    }

    private void removeAtElement() {
        int pos = getPos();
        tfPromptNum.clear();

        if(pos <= 0 || pos > size) {
            System.err.println("Invalid Position");
            disableButtons(false);
            return;
        }

        int index = pos-1;
        ArrayNode currentNode = arrayNodes.get(index);
        VBox vbox = currentNode.getVBox();
            SequentialTransition st = destroyBox(vbox);
            st.setOnFinished(event -> {
                writePreviousDataJSON();
                apVisualizer.getChildren().remove(vbox);
                shiftX(arrayNodes.indexOf(currentNode), capacity, -55);
                capacity--;
                size--;
                arrayNodes.remove(currentNode);
                dynamicAddResizeList(1);
                updateIndexes();
                if(size <= ceil(2.0/3 * capacity)) {
                    int remove = (int) ceil(capacity * 0.75);
                    dynamicSubResizeList(remove);
                }
            });
            st.play();
        disableButtons(false);

    }

    private void searchElement() {
        int num = getNum();
        tfPromptPos.clear();
        if(num == Integer.MIN_VALUE) {
            disableButtons(false);
            return;
        }

        SequentialTransition traversal = new SequentialTransition();
        ArrayNode currentNode = searchHelper(num, traversal);

        if (currentNode != null) {
            traversal.setOnFinished(e -> {
                pulseNodeFound(currentNode.getVBox(), currentNode.getRectangle(), false);
                disableButtons(false);
            });
        } else {
            pulseNodeAll(arrayNodes, traversal);
            disableButtons(false);
        }

        traversal.play();

    }

    private ArrayNode searchHelper(int num, SequentialTransition traversal) {

        int index;
        for (index = 0; index < size; index++) {
            ArrayNode currentNode = arrayNodes.get(index);
            Rectangle r = currentNode.getRectangle();
            FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
            FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
            SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
            traversal.getChildren().add(nodeTransition);

            if (currentNode.getNumber() == num) {
                return currentNode;
            }
        }

        return null;
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPromptNum.clear();
            tfPromptPos.clear();
            writePreviousDataJSON();
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
            arrayNodes.clear();
            apVisualizer.getChildren().removeAll();

            PauseTransition pt = new PauseTransition(Duration.seconds(1.5));
            pt.play();
            pt.setOnFinished(e -> {
                size = 0;
                capacity = 0;
                dynamicAddResizeList(5);
                writePreviousDataJSON();
                disableButtons(false);
            });
        }
    }

    // Better Utilities
    private VBox createBoxes(int pos, String num, double x, double y) {
        System.out.println("Pos " + pos +
                " X: " + x + " Y: " + y + " Capacity: " + capacity);
        ArrayNode arrayNode = new ArrayNode(num, pos, x, y);
        arrayNodes.add(pos, arrayNode);
        VBox vbox = arrayNode.getVBox();

        return vbox;
    }

    private void updateIndexes() {
        for(int i=0; i<capacity; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            arrayNode.setIndex(String.valueOf(i));
        }
    }

    // Better Animations
    private void shiftX(int start, int end ,double val) {
        for(int i=start; i<end; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            VBox vb = arrayNode.getVBox();
            arrayNode.setX(vb.getLayoutX() + val);
            vb.setLayoutX(arrayNode.getX());
        }
    }

    private void disableButtons(boolean b) {
        for(Button btn : btns) {
            btn.setDisable(b);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }
}
