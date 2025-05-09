package edu.citu.procrammers.eva.controllers.ADT;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.ChatBotController;
import edu.citu.procrammers.eva.models.data_structures.BST;
import edu.citu.procrammers.eva.models.data_structures.Node;
import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.EventListener;
import java.util.function.UnaryOperator;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;


public class ADTViewController {
    public AnchorPane apVisualizer;
    public AnchorPane apChat;
    public ImageView imgChatbotBtn;
    @FXML private Button btnBackward;
    @FXML private Button btnPlay;
    @FXML private Button btnForward;
    @FXML private Slider sliderSpeed;
    @FXML private AnchorPane apMain;
    @FXML private Button btnInsert;
    @FXML private TextField tfInput;
    @FXML private TextField tfDelete;
    @FXML private Button btnDelete;
    @FXML private ToggleButton tglSeratoMode;

    private BST BST;
    private AnimationManager animationManager;

    public ChatBotController chatBotController;

    public void initialize() {
        initializeSlider();
        initializeKeyboardListener();

        btnBackward.setDisable(false);
        btnPlay.setDisable(false);
        btnForward.setDisable(false);

        apMain.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Width after layout: " + newVal.doubleValue());
            animationManager = new AnimationManager(apMain);
            BST = new BST(animationManager, apMain.getWidth(), apMain.getHeight(), apMain);
            System.out.println("BST isStandard = " + BST.isStandard);
        });

        ChatService.updateData(new JSONObject());

        imgChatbotBtn.setOnMouseClicked(e -> {
            ChatService.loadChatbot(chatBotController, apChat);
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
        tfInput.setDisable(true);
        tfDelete.setDisable(true);

        btnInsert.setDisable(true);
        btnDelete.setDisable(true);

        btnBackward.setDisable(true);

        btnForward.setDisable(true);

        tglSeratoMode.setDisable(true);

        sliderSpeed.setDisable(true);
    }

    private void enableUI() {
        tfInput.setDisable(false);
        tfDelete.setDisable(false);

        btnInsert.setDisable(false);
        btnDelete.setDisable(false);

//        btnBackward.setDisable(!(animationManager.isContinuous && tfInput.isDisable()) );
//        btnPlay.setDisable(false);
//        btnForward.setDisable(!animationManager.isContinuous || !tfInput.isDisable());

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
//        disableUI();
        int key = Integer.parseInt(tfDelete.getText());
        clearFields();
        animationManager.commands = BST.deleteElement(key);
        animationManager.play(this::enableUI);
    }


    @FXML
    private void onButtonInsertClicked() {
        if (tfInput.getText().isEmpty()) {return;}
//        disableUI();
        System.out.println("playing speed: " + animationManager.speed + " seconds ");
        int key = Integer.parseInt(tfInput.textProperty().getValue());
        clearFields();

        animationManager.commands = BST.insertElement(key);
        animationManager.play(this::enableUI);
    }

    @FXML private void onButtonBackwardClicked() {
//        disableUI();
        animationManager.undo(this::enableUI);
    }

    @FXML private void onButtonPlayClicked() {
//        disableUI();
        animationManager.isContinuous = !animationManager.isContinuous;
        boolean continuous = animationManager.isContinuous;

        btnPlay.setDisable(false);
        btnPlay.setText(continuous ? "Pause" : "Play");

//        boolean hasCommands = !animationManager.commands.isEmpty();
//        btnBackward.setDisable(continuous && hasCommands);
//        btnForward.setDisable(continuous && hasCommands);

//        System.out.println("Has commands: " + !animationManager.commands.isEmpty());
//
//        System.out.println("isContinuous = " + animationManager.isContinuous);
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
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
