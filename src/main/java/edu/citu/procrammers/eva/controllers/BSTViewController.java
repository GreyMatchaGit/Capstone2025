package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.BST;
import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import org.json.JSONObject;

import java.util.function.UnaryOperator;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;


public class BSTViewController {
    @FXML private AnchorPane apChat;
    @FXML private ImageView imgChatbotBtn;
    @FXML private Button btnBackward;
    @FXML private Button btnPlay;
    @FXML private Button btnForward;
    @FXML private Slider sliderSpeed;
    @FXML private AnchorPane apCanvas;
    @FXML private Button btnInsert;
    @FXML private TextField tfInput;
    @FXML private TextField tfDelete;
    @FXML private Button btnDelete;
    @FXML private ToggleButton tglSeratoMode;

    private BST BST;
    private AnimationManager animationManager;

    public ChatBotController chatBotController;

    private boolean isChatbotVisible;

    public void initialize() {
        initializeSlider();
        initializeKeyboardListener();

        isChatbotVisible = false;

        btnBackward.setDisable(true);
        btnPlay.setDisable(false);
        btnForward.setDisable(true);

        tfInput.setPromptText("Value to insert");
        tfDelete.setPromptText("Value to delete");

        apCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Width after layout: " + newVal.doubleValue());
            animationManager = new AnimationManager(apCanvas);
            BST = new BST(animationManager, apCanvas.getWidth(), apCanvas.getHeight(), apCanvas);
            System.out.println("BST isStandard = " + BST.isStandard);
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

    private void initializeKeyboardListener() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            if (newText.matches("-?\\d*")) {
                return change; // Allow change
            }
            return null; // Reject change
        };
        tfInput.setTextFormatter(new TextFormatter<>(filter));
        tfDelete.setTextFormatter(new TextFormatter<>(filter));

        tfInput.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onButtonInsertClicked();
            }
        });

        tfDelete.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                onDeleteButtonClicked();
            }
        });
    }

    private void clearFields() {
        tfInput.clear();
        tfDelete.clear();
    }

    private void disableUI() {
        System.out.println("Disabling UI with CURRENTINDEX = " + animationManager.getCurrentIndex());
        System.out.println("isContinuous = " + animationManager.isContinuous);
        tfInput.setDisable(true);
        tfDelete.setDisable(true);

        btnInsert.setDisable(true);
        btnDelete.setDisable(true);

        btnBackward.setDisable(animationManager.isContinuous);
        btnForward.setDisable(animationManager.isContinuous);

        tglSeratoMode.setDisable(true);

        sliderSpeed.setDisable(true);
    }

    private void enableUI() {
        tfInput.setDisable(false);
        tfDelete.setDisable(false);

        btnInsert.setDisable(false);
        btnDelete.setDisable(false);

        btnBackward.setDisable(animationManager.getCurrentIndex() == 0);
        btnPlay.setDisable(false);
        btnForward.setDisable(animationManager.getCurrentIndex() == 0);

        tglSeratoMode.setDisable(false);

        sliderSpeed.setDisable(false);
    }

    private void initializeSlider () {
        sliderSpeed.setShowTickLabels(true);
        sliderSpeed.setShowTickMarks(true);
        sliderSpeed.setMin(0);
        sliderSpeed.setMax(1);
        sliderSpeed.setValue(.5);
        sliderSpeed.setBlockIncrement(1);

        sliderSpeed.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                System.out.println("Slider drag finished. Final value: " + sliderSpeed.getValue());
                animationManager.setSpeed(sliderSpeed.getValue());
            }
        });

// 2. Update after click on the track
        sliderSpeed.setOnMouseReleased(event -> {
            if (!sliderSpeed.isValueChanging()) {
                System.out.println("Slider clicked. Final value: " + sliderSpeed.getValue());
                animationManager.setSpeed(sliderSpeed.getValue());
            }
        });
    }

    @FXML private void onDeleteButtonClicked() {
        if (tfDelete.getText().isEmpty()) {return;}
        disableUI();
        int key = Integer.parseInt(tfDelete.getText());
        clearFields();
        animationManager.commands = BST.deleteElement(key);
        animationManager.play(this::enableUI);
    }

    @FXML
    private void onButtonInsertClicked() {
        if (tfInput.getText().isEmpty()) { return; }
        disableUI();

        System.out.println("playing speed: " + animationManager.speed + " seconds ");
        int key = Integer.parseInt(tfInput.textProperty().getValue());
        clearFields();

        animationManager.commands = BST.insertElement(key);
        animationManager.play(this::enableUI);
    }

    @FXML private void onButtonBackwardClicked() {
        animationManager.undo();
    }

    @FXML private void onButtonPlayClicked() {
        animationManager.isContinuous = !animationManager.isContinuous;
        boolean continuous = animationManager.isContinuous;

        disableUI();

        btnPlay.setDisable(false);
        btnPlay.setText(continuous ? "Pause" : "Play");

        animationManager.play(this::enableUI);
    }

    @FXML private void onButtonForwardClick() {
        animationManager.play(this::enableUI);
    }

    @FXML
    private void changeSuccessorMode() {
        BST.isStandard = !tglSeratoMode.isSelected();
        System.out.println("isStandard Mode = " + BST.isStandard);
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }
}
