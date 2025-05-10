package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.Constant.Value;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import edu.citu.procrammers.eva.utils.animations.arraylist.ArrayListAnimations;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.json.JSONObject;

import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Color.*;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.animations.arraylist.ArrayListAnimations.traverseAnimation;

public class QueueViewController {

    private double centerX, centerY;

    @FXML private AnchorPane apVisualizer, apChat;
    @FXML private Button btnEnqueue, btnDequeue, btnFront, btnClear;
    @FXML private TextField tfPrompt;
    @FXML private ImageView imgBackBtn, imgChatbotBtn;

    private LinkedList<Integer> queue;
    private List<Rectangle> rectangles;
    private List<Label> labels;
    private List<VBox> vBoxes;


    private Node frontMarker;

    private JSONObject dataJSON;
    private ChatBotController chatBotController;

    private boolean isChatbotVisible = false;
    private int size = 0;

    public void initialize() {
        setupGlow(imgBackBtn);
        initLists();

        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    double centerX = apVisualizer.getWidth() / 2;
                    double centerY = apVisualizer.getHeight() / 2;
                    this.centerX = centerX;
                    this.centerY = centerY;

                    frontMarker = createFrontMarker();
                    apVisualizer.getChildren().add(frontMarker);
                });
            }
        });

        dataJSON = new JSONObject();
        ChatService.loadChatbot(chatBotController, apChat);
        imgChatbotBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            apChat.setVisible(!isChatbotVisible);
            isChatbotVisible = !isChatbotVisible;
        });
    }

    private void writeDataJSON() {
        dataJSON.put("type", "queue");
        dataJSON.put("size", queue.size());
        dataJSON.put("elements", queue.toString());
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        dataJSON.put("previousQueue", queue.toString());
        ChatService.updateData(dataJSON);
    }


    public void onButtonClick(ActionEvent event) {
        if(event.getSource() == btnEnqueue) {
            enqueue();
        } else if(event.getSource() == btnDequeue) {
            dequeue();
        } else if(event.getSource() == btnFront) {
            front();
        } else if(event.getSource() == btnClear) {
            onClearOperation();
        }
    }

    private int getNum() {
        String prompt = tfPrompt.getText().trim();
        int num = Integer.MIN_VALUE;
        try {
            num = Integer.parseInt(prompt);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid Input!");
        } finally {
            tfPrompt.setText("");
        }
        return num;
    }

    private void enqueue() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(size != 0) writePreviousDataJSON();

        queue.addLast(num);
        writeDataJSON();

        updateList();

        Label backLabel = labels.get(size);
        Rectangle backRectangle = rectangles.get(size);

        backLabel.setText(Integer.toString(num));

        FillTransition highlight = ArrayListAnimations.fillRectangle(0.3, backRectangle, DEFAULTR, POSITIVE);
        FillTransition reset = ArrayListAnimations.fillRectangle(0.3, backRectangle, POSITIVE, DEFAULTR);

        SequentialTransition st = new SequentialTransition(highlight, reset);
        st.play();

        ++size;
    }

    private void dequeue() {
        int num = queue.getFirst();
        if(num == Integer.MIN_VALUE) return;

        if(size == 0) return;

        VBox frontVBox = vBoxes.getFirst();
        Rectangle frontRectangle = rectangles.getFirst();

        FillTransition highlight = ArrayListAnimations.fillRectangle(0.3, frontRectangle, DEFAULTR, SEARCH);
        FillTransition reset = ArrayListAnimations.fillRectangle(0.3, frontRectangle, SEARCH, DEFAULTR);

        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
        nodeTransition.play();

        SequentialTransition traversal = new SequentialTransition();
        traversal.setOnFinished(e -> {
            TranslateTransition slideOut = ArrayListAnimations.slideX(frontVBox, -100);
            FadeTransition fade = ArrayListAnimations.fadeOut(frontVBox);
            slideOut.setOnFinished(event -> fade.play());
            fade.setOnFinished(event -> {
                writePreviousDataJSON();
                removeFirstChild(frontVBox);
                writeDataJSON();

                for (int i = 0; i < vBoxes.size(); i++) {
                    VBox vb = vBoxes.get(i);

                    double targetX = Value.FRONT_BOUNDARY_X + i * 55;

                    KeyValue kv = new KeyValue(vb.layoutXProperty(), targetX, Interpolator.EASE_BOTH);
                    KeyFrame kf = new KeyFrame(Duration.seconds(0.75), kv);

                    Timeline timeline = new Timeline(kf);
                    timeline.play();

                    if (vb.getChildren().size() >= 2) {
                        Node node = vb.getChildren().get(1);
                        if (node instanceof Label) {
                            ((Label) node).setText(Integer.toString(i));
                        }
                    }
                }

                --size;
                if(size == 0) frontMarker.setVisible(false);
            });
            slideOut.play();
        });

        traversal.play();
    }

    private void front() {
        int num = queue.getFirst();
        if(num == Integer.MIN_VALUE) return;
        if(size == 0) return;

        VBox frontVBox = vBoxes.getFirst();
        Rectangle frontRectangle = rectangles.getFirst();

        FillTransition highlight = ArrayListAnimations.fillRectangle(0.3, frontRectangle, DEFAULTR, SEARCH);
        FillTransition reset = ArrayListAnimations.fillRectangle(0.3, frontRectangle, SEARCH, DEFAULTR);

        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);

        SequentialTransition traversal = new SequentialTransition();
        traversal.getChildren().add(nodeTransition);

        traversal.setOnFinished(e -> {
            traverseAnimation(frontVBox, frontRectangle);
        });

        traversal.play();
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");
            writePreviousDataJSON();
            queue.clear();
            writeDataJSON();

            for (int i = 0; i < vBoxes.size(); ++i) {
                Rectangle r = rectangles.get(i);
                Label l = labels.get(i);
                l.setText("");

                FillTransition highlight = ArrayListAnimations.fillRectangle(0.6, r, DEFAULTR, NEGATIVE);

                Timeline tl = ArrayListAnimations.highlightNode(r,l,NEGATIVE,NEGATIVE);

                ParallelTransition pt = new ParallelTransition(highlight, tl);
                pt.play();
            }

            rectangles.clear();
            labels.clear();

            for (int i = vBoxes.size() - 1; i >= 0; --i) {
                VBox vb = vBoxes.get(i);
                destroyBox(vb);
                vBoxes.remove(i);
                System.out.println(i);
            }

            size = 0;
            frontMarker.setVisible(false);
        }
    }

    private void updateList() {
        if(apVisualizer.getChildren().getFirst() == frontMarker) frontMarker.setVisible(true);

        double currentX = (Value.FRONT_BOUNDARY_X + vBoxes.size() * 55);
        double currentY = centerY;

        VBox vBox = createBoxes(vBoxes.size(),"", currentX, currentY, false);
        vBox.setTranslateX(100);

        FadeTransition ft = ArrayListAnimations.fadeIn(vBox);
        TranslateTransition tt = ArrayListAnimations.slideX(vBox, -100);
        ft.play();
        ft.setOnFinished(e -> tt.play());
    }

    private void removeFirstChild(VBox child) {
        apVisualizer.getChildren().remove(child);
        vBoxes.removeFirst();
        rectangles.removeFirst();
        labels.removeFirst();
        queue.removeFirst();
    }

    // Better Utilities
    private VBox createBoxes(int pos, String num, double x, double y, boolean isTemp) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);

        Label value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");

        StackPane sp = new StackPane(rect, value);

        Label index = new Label(Integer.toString(pos));
        index.setStyle("-fx-font-weight: bold;");

        VBox vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");

        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);
        rectangles.add(pos, rect);
        labels.add(pos, value);
        vBoxes.add(pos, vbox);

        return vbox;
    }

    private void destroyBox(VBox vbox) {
        FadeTransition fadeOut = ArrayListAnimations.fadeOut(vbox);
        TranslateTransition slideLeft = ArrayListAnimations.slideX(vbox, -100);
        slideLeft.setOnFinished(event -> fadeOut.play());
        slideLeft.play();
        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    private Polygon createFrontMarker(){
        Polygon p = new Polygon();
        p.getPoints().addAll(0.0, 0.0, 20.0, 10.0, 0.0, 20.0);
        p.setFill(DEFAULTR);
        p.setLayoutX(Value.FRONT_BOUNDARY_X - 25);
        p.setLayoutY(centerY + 10);
        p.setVisible(false);

        return p;
    }

    private void initLists(){
        queue = new LinkedList<>();
        rectangles = new ArrayList<>();
        labels = new ArrayList<>();
        vBoxes = new ArrayList<>();
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        NavService.navigateTo(Academy);
    }
}
