package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.SinglyLinkedList;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;
import javafx.animation.Animation;
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
        singlyLinkedList = new SinglyLinkedList(canvas);
    }

    @FXML
    private void addTail() {
        String valueString = tfValue.getText();
        if (valueString.isEmpty())
            return;
        clearFields();
        disableAll();
        int value = Integer.parseInt(valueString);
        singlyLinkedList.addTail(value);
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
        disableAll();
        int value = Integer.parseInt(valueString);
        singlyLinkedList.addHead(value);
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
