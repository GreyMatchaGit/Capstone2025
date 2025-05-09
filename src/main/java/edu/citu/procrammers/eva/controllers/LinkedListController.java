package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.SinglyLinkedList;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;

public class LinkedListController implements Initializable {
    @FXML
    StackPane canvasContainer;
    @FXML
    AnchorPane canvas;
    @FXML
    TextField tfValue;

    private SinglyLinkedList singlyLinkedList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setListeners();
        setUI();
    }

    private void setUI() {

    }

    @FXML
    private void addTail() {
        String valueString = tfValue.getText();
        if (valueString.isEmpty())
            return;
        int value = Integer.parseInt(valueString);
        singlyLinkedList
    }

    private void setListeners() {
        setVisualizerFitToParent();
        setTFValueInputRestrictions();
    }

    private void setVisualizerFitToParent() {
        canvasContainer.widthProperty().addListener(((observableValue, oldValue, newValue) -> canvas.setPrefWidth((Double) newValue)));
        canvasContainer.heightProperty().addListener(((observableValue, oldValue, newValue) -> canvas.setPrefHeight((Double) newValue)));
    }

    private void setTFValueInputRestrictions() {
        tfValue.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if (!Character.isDigit(characterPressed) && characterPressed != '\b') {
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
