package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ArrayNode;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

import java.net.URL;
import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class HashTableController implements Initializable {

    private double centerX, centerY;

    public HBox visualizer;
    public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    public TextField tfPrompt;
    public ImageView imgBackBtn;

    private ArrayList<ArrayNode> arrayNodes;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupGlow(imgBackBtn);
        addListeners();
        setUI();
        Platform.runLater(() -> {
            ArrayNode.initializeVisualizer(visualizer);
            initHashTable(5);
        });
    }

    @FXML

    private void onButtonClick(ActionEvent event) {
        try {
            String BUTTON_ID =  ((Button) event.getSource()).getId();
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }

    private void initHashTable(int capacity) {

        arrayNodes = new ArrayList<>();
        visualizer.getChildren().clear();

        try {
            for (int i = 0; i < capacity; ++i) {
                ArrayNode newNode = new ArrayNode();
                arrayNodes.add(newNode);
            }
        } catch (RuntimeException e) {
            System.out.println(e.getMessage());
        }
    }

    private void addListeners() {

        tfPrompt.setOnKeyTyped(keyEvent -> {
            char characterPressed = keyEvent.getCharacter().charAt(0);
            if (!Character.isDigit(characterPressed) && characterPressed != ' ') {
                String prompt = tfPrompt.getText();
                tfPrompt.setText( tfPrompt.getText(0, prompt.length() - 1));
                tfPrompt.positionCaret(prompt.length());
            }
        });
    }

    private void setUI() {

        visualizer.setMaxHeight(Region.USE_PREF_SIZE);
        visualizer.setMaxWidth(Region.USE_PREF_SIZE);
        visualizer.setSpacing(10.0);
    }
}
