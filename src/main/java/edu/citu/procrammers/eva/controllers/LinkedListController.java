package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.SinglyLinkedList;
import edu.citu.procrammers.eva.models.data_structures.SinglyNode;
import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.Constant.Page.DATA_PATH;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;

public class LinkedListController implements Initializable {
    public AnchorPane apChat;
    public ImageView imgChatbotBtn;
    @FXML
    StackPane canvasContainer;
    @FXML
    AnchorPane canvas;
    @FXML
    TextField tfValue, tfIndex;

    private SinglyLinkedList singlyLinkedList;
    private ChatBotController chatBotController;
    private JSONObject dataJSON;
    private boolean isChatbotVisible = false;
    private boolean firstPreviousWrite = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListeners();
        setUI();
        singlyLinkedList = new SinglyLinkedList(canvas);

        dataJSON = new JSONObject();
        ChatService.loadChatbot(chatBotController, apChat);
        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            apChat.setVisible(!isChatbotVisible);
            isChatbotVisible = !isChatbotVisible;
        });
    }

    private void writeDataJSON() {
        dataJSON.put("type", "linkedlist");
        dataJSON.put("size", singlyLinkedList.size);

        JSONArray elements = new JSONArray();

        SinglyNode curr = singlyLinkedList.getHead();

        if (curr != null) {
            dataJSON.put("head", curr.value);
        } else {
            dataJSON.put("head", JSONObject.NULL);
        }
        while(curr != null) {
            elements.put(curr.value);
            if(curr.next == null) {
                dataJSON.put("tail", curr.value);
            }
            curr = curr.next;
        }

        if (elements.isEmpty()) {
            dataJSON.put("tail", JSONObject.NULL);
        }

        dataJSON.put("elements", elements);

        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON() {
        if (firstPreviousWrite) {
            firstPreviousWrite = false;
            return;
        }
        dataJSON.put("previousSize", singlyLinkedList.size);

        JSONArray elements = new JSONArray();

        SinglyNode curr = singlyLinkedList.getHead();
        dataJSON.put("previousHead", curr.value);
        while(curr != null) {
            elements.put(curr.value);
            if(curr.next == null) {
                dataJSON.put("previousTail", curr.value);
            }
            curr = curr.next;
        }
        dataJSON.put("previousElements", elements);
        ChatService.updateData(dataJSON);
    }

    @FXML
    private void addTail() {
        String valueString = tfValue.getText();
        if (valueString.isEmpty())
            return;
        clearFields();
        disableAll();
        int value = Integer.parseInt(valueString);
        writePreviousDataJSON();
        singlyLinkedList.addTail(value);
        writeDataJSON();
        // TODO: Remove when finished
        singlyLinkedList.print();
        enableAll();
    }

    @FXML
    private void addHead() {
        String valueString = tfValue.getText();
        if (valueString.isEmpty())
            return;
        clearFields();
//        disableAll();
        int value = Integer.parseInt(valueString);
        writePreviousDataJSON();
        singlyLinkedList.addHead(value);
        writeDataJSON();
        // TODO: Remove when finished
        singlyLinkedList.print();
        enableAll();
    }

    @FXML
    private void addAt() {
        String valueString = tfValue.getText();
        String indexString = tfIndex.getText();
        if (valueString.isEmpty() || indexString.isEmpty())
            return;
        clearFields();
//        disableAll();
        int value = Integer.parseInt(valueString);
        int index = Integer.parseInt(indexString);
        writePreviousDataJSON();
        singlyLinkedList.addAt(value, index);
        writeDataJSON();
        // TODO: Remove when finished
        singlyLinkedList.print();
        enableAll();
    }

    @FXML
    private void removeHead() {
        clearFields();
//        disableAll();
        writePreviousDataJSON();
        singlyLinkedList.removeHead();
        writeDataJSON();
        // TODO: Remove when finished
        singlyLinkedList.print();
        enableAll();
    }

    @FXML
    private void removeTail() {
        clearFields();
//        disableAll();
        writePreviousDataJSON();
        singlyLinkedList.removeTail();
        writeDataJSON();
        // TODO: Remove when finished
        singlyLinkedList.print();
        enableAll();
    }

    private void clearFields() {
        tfValue.clear();
    }

    private void disableAll() {
        tfValue.setDisable(true);
    }

    private void enableAll() {
        tfValue.setDisable(false);
    }

    private void setListeners() {
        setVisualizerFitToParent();
        setTFValueInputRestrictions();
    }

    private void setUI() {

    }

    private void setVisualizerFitToParent() {
        canvasContainer.widthProperty().addListener(((observableValue, oldValue, newValue) -> canvas.setPrefWidth((Double) newValue)));
        canvasContainer.heightProperty().addListener(((observableValue, oldValue, newValue) -> canvas.setPrefHeight((Double) newValue)));
    }

    private void setTFValueInputRestrictions() {
        tfValue.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if ((!Character.isDigit(characterPressed) && characterPressed != '\b') || tfValue.getText().length() > 5) {
                String prompt = tfValue.getText();
                tfValue.setText( tfValue.getText(0, prompt.length() - 1));
                tfValue.positionCaret(prompt.length());
            }
        });
    }

    @FXML
    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
