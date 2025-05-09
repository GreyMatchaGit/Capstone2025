package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.Stack;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class StackViewController {
    public Button btnPush, btnPop, btnTop, btnClear;
    public AnchorPane apVisualizer;
    public TextField tfPrompt;
    public ImageView imgBackBtn;
    public ImageView imgChatbotBtn;
    public AnchorPane apChat;

    private double centerX, centerY, pointY, startY;

    private Stack<Integer> stack;
    private Stack<StackPane> stackPanes;
    private Stack<Label> labels;
    private ParallelTransition pt;
    private double originalScaleX, originalScaleY;

    public JSONObject dataJSON;

    public ChatBotController chatBotController;

    private boolean isChatbotVisible;

    public void initialize() {

        setupGlow(imgBackBtn);

        isChatbotVisible = false;

        stack = new Stack<>();
        stackPanes = new Stack<>();
        labels = new Stack<>();
        pt = null;
        dataJSON = new JSONObject();

        // For Animation purposes
        startY = 100;
        apVisualizer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double centerX = newVal.doubleValue() / 2;
            this.centerX = centerX;
        });
        apVisualizer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double centerY = newVal.doubleValue() / 2;
            this.centerY = centerY;
        });

        Platform.runLater(() -> {
            centerX = apVisualizer.getWidth() / 2;
            centerY = apVisualizer.getHeight() / 2;
            pointY = apVisualizer.getHeight() * 0.9;
        });
        ChatService.updateData(new JSONObject());
        ChatService.loadChatbot(chatBotController, apChat);
        apChat.setVisible(false);

        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            if (isChatbotVisible) {
                apChat.setVisible(false);
            } else {
                apChat.setVisible(true);
            }
            isChatbotVisible = !isChatbotVisible;
        });
    }

    private void writeDataJSON() {
        dataJSON.put("stack", stack.toString());
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        dataJSON.put("previousStack", stack.toString());
        ChatService.fileWriter(dataJSON, DATA_PATH);
    }

    public void onButtonClick(ActionEvent e) {
        if(e.getSource() == btnPush) {
            pushElement();
        } else if(e.getSource() == btnPop) {
            popElement();
        } else if(e.getSource() == btnTop) {
            topElement();
        } else if(e.getSource() == btnClear) {
            clear();
        }
    }

    private int getNum() {
        String prompt = tfPrompt.getText();
        int num = Integer.MAX_VALUE;
        try {
            num = Integer.parseInt(prompt);
        } catch (NumberFormatException e) {
            System.err.println("Invalid input!");
        } finally {
            tfPrompt.setText("");
        }
        return num;
    }

    private void pushElement() {
        int num = getNum();
        if(num == Integer.MAX_VALUE) return;

        if(!stackPanes.isEmpty()) {
            StackPane sp = stackPanes.peek();
            Label l = labels.peek();
            l.setText("");
            pt.stop();
            sp.setOpacity(1);
            sp.setScaleX(originalScaleX);
            sp.setScaleY(originalScaleY);
        }

        StackPane sp = createBox(Integer.toString(num), centerX-100, startY);
        FadeTransition ft = fadeIn(sp);
        TranslateTransition tt = slideDown(sp);
        ParallelTransition pt = idleNode(sp);
        this.pt = pt;
        this.originalScaleX = sp.getScaleX();
        this.originalScaleY = sp.getScaleY();

        stack.push(num);
        writeDataJSON();
        stackPanes.push(sp);
        ft.play();
        pt.play();
        ft.setOnFinished(event -> tt.play());
    }

    private void popElement() {
        if (!stack.isEmpty() && !stackPanes.isEmpty()) {
            StackPane sp = stackPanes.pop();
            writePreviousDataJSON();
            stack.pop();
            writeDataJSON();
            labels.pop();
            FadeTransition ft = fadeOut(sp);
            TranslateTransition tt = slideUp(sp);

            if(!stack.isEmpty()) {
                int num = stack.peek();
                Label l = labels.peek();
                l.setText(Integer.toString(num));
            }

            tt.play();
            tt.setOnFinished(event -> ft.play());
            ft.setOnFinished(event -> apVisualizer.getChildren().remove(sp));
        }
    }

    private void topElement() {
        StackPane sp = createBox(Integer.toString(stack.peek()), centerX+200,centerY);
        FadeTransition ft = fadeIn(sp);
        ft.play();

        PauseTransition pauseT = new PauseTransition(Duration.seconds(2));
        pauseT.setOnFinished(e -> {
            FadeTransition fo = fadeOut(sp);
            fo.play();
            fo.setOnFinished(ee -> apVisualizer.getChildren().remove(sp));
        });
        pauseT.play();
    }

    private void clear() {
        while (!stack.isEmpty() && !stackPanes.isEmpty()) {
            popElement();
        }
    }

    // Utilities
    private StackPane createBox(String num, double x, double y) {
        Rectangle r = new Rectangle(200, 200, Color.WHITE);
        r.setStroke(Color.BLACK);
        r.setStrokeWidth(2);
        Label l = new Label(num);
        l.setStyle("-fx-font-weight: bold;");
        StackPane sp = new StackPane(r, l);
        sp.setLayoutX(centerX-100);
        sp.setLayoutY(startY);
        labels.push(l);
        apVisualizer.getChildren().add(sp);
        return sp;
    }

    // Animations
    private FadeTransition fadeIn(StackPane sp) {
        FadeTransition anim = new FadeTransition(Duration.seconds(1), sp);
        anim.setFromValue(0);
        anim.setToValue(1);
        anim.setCycleCount(1);

        return anim;
    }

    private FadeTransition fadeOut(StackPane sp) {
        FadeTransition anim = new FadeTransition(Duration.seconds(1), sp);
        anim.setFromValue(1);
        anim.setToValue(0);
        anim.setCycleCount(1);
        anim.setInterpolator(Interpolator.LINEAR);

        return anim;
    }

    private TranslateTransition slideDown(StackPane sp) {
        TranslateTransition anim = new TranslateTransition(Duration.seconds(1), sp);
        anim.setByY(centerY);
        anim.setCycleCount(1);
        anim.setInterpolator(Interpolator.LINEAR);

        return anim;
    }

    private TranslateTransition slideUp(StackPane sp) {
        TranslateTransition anim = new TranslateTransition(Duration.seconds(1), sp);
        anim.setByY(-(centerY-startY));
        anim.setCycleCount(1);
        anim.setInterpolator(Interpolator.LINEAR);

        return anim;
    }

    private ParallelTransition idleNode(StackPane sp) {
        FadeTransition fade = new FadeTransition(Duration.seconds(1), sp);
        fade.setFromValue(1);
        fade.setToValue(0.2);
        fade.setAutoReverse(true);
        fade.setCycleCount(Animation.INDEFINITE);
        fade.setInterpolator(Interpolator.LINEAR);

        ScaleTransition scale = new ScaleTransition(Duration.seconds(1), sp);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.25);
        scale.setToY(1.25);
        scale.setAutoReverse(true);
        scale.setCycleCount(Animation.INDEFINITE);
        scale.setInterpolator(Interpolator.LINEAR);

        return new ParallelTransition(fade, scale);
    }

    // Will ask later how to lock the camera
    private void shiftY(double val) {
        for(StackPane sp : stackPanes) {
            sp.setLayoutY(sp.getLayoutY() + val);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
