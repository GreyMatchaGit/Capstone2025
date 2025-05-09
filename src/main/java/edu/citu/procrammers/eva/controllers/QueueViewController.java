package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.Constant.Value;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
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
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

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
    private int size;

    private Node frontMarker;

    private JSONObject dataJSON;

    private ChatBotController chatBotController;

    private boolean isChatbotVisible;

    public void initialize() {
        setupGlow(imgBackBtn);

        isChatbotVisible = false;

        dataJSON = new JSONObject();

        queue = new LinkedList<>();
        rectangles = new ArrayList<>();
        labels = new ArrayList<>();
        vBoxes = new ArrayList<>();
        size=0;

        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    double centerX = apVisualizer.getWidth() / 2;
                    double centerY = apVisualizer.getHeight() / 2;
                    this.centerX = centerX;
                    this.centerY = centerY;

                    Polygon triangle = new Polygon();
                    triangle.getPoints().addAll(0.0, 0.0, 20.0, 10.0, 0.0, 20.0); // right pointing
                    triangle.setFill(DEFAULT);
                    triangle.setLayoutX(Value.FRONT_BOUNDARY_X - 25);
                    triangle.setLayoutY(centerY + 10);
                    triangle.setVisible(false);

                    apVisualizer.getChildren().add(triangle);
                    frontMarker = triangle;
                });
            }
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
        dataJSON.put("type", "queue");
        dataJSON.put("size", queue.size());
        dataJSON.put("elements", queue.toString());
        ChatService.updateData(dataJSON);
    }

    private void writePreviousDataJSON(){
        dataJSON.put("previousQueue", queue.toString());
        ChatService.fileWriter(dataJSON, DATA_PATH);
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

    private void updateList() {
        if(apVisualizer.getChildren().getFirst() == frontMarker) { frontMarker.setVisible(true); }
        double currentX = (Value.FRONT_BOUNDARY_X + vBoxes.size() * 55);
        double currentY = centerY;
        System.out.println(vBoxes.size()+ " " +currentX);
        VBox vBox = createBoxes(vBoxes.size(),"", currentX, currentY, false);
        FadeTransition ft = fadeIn(vBox);
        vBox.setTranslateX(100);
        TranslateTransition tt = slideX(vBox, -100);
        ft.play();
        ft.setOnFinished(e -> tt.play());

        System.out.println("Queue cleared " +
                queue.size() + " " +
                rectangles.size() + " " +
                labels.size() + " " +
                vBoxes.size());

        for(VBox vb : vBoxes) {
            for(Node b : vb.getChildren()) {
                if(b instanceof StackPane) {
                    for(Node m: ((StackPane) b).getChildren()) {
                        if(m instanceof Label) {
                            System.out.println(((Label) m).getText());
                        }
                    }
                }
            }
        }
    }

    private void enqueue() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(size != 0){
            writePreviousDataJSON();
        }
        queue.addLast(num);
        writeDataJSON();

        updateList();

        Label l = labels.get(size);
        Rectangle r = rectangles.get(size);

        l.setText(Integer.toString(num));

        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, POSITIVE);
        FillTransition reset = fillRectangle(0.3, r, POSITIVE, DEFAULTR);
        SequentialTransition st = new SequentialTransition(highlight, reset);
        st.play();

        ++size;
    }

    private void dequeue() {
        int num = queue.getFirst();
        if(num == Integer.MIN_VALUE) return;

        if(size == 0) return;

        VBox currentVBox = vBoxes.getFirst();
        Rectangle r = rectangles.getFirst();
        Label l = labels.getFirst();

        SequentialTransition traversal = new SequentialTransition();
        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
        nodeTransition.play();

        traversal.setOnFinished(e -> {
            TranslateTransition slideOut = slideX(currentVBox, -100);
            FadeTransition fade = fadeOut(currentVBox);
            slideOut.setOnFinished(event -> fade.play());
            fade.setOnFinished(event -> {
                writePreviousDataJSON();
                apVisualizer.getChildren().remove(currentVBox);
                vBoxes.removeFirst();
                rectangles.removeFirst();
                labels.removeFirst();
                queue.removeFirst();
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


    private boolean front() {
        int num = queue.getFirst();
        if(num == Integer.MIN_VALUE) return false;
        if(size == 0) return false;

        VBox currentVBox = vBoxes.getFirst();
        Rectangle r = rectangles.getFirst();
        Label l = labels.getFirst();
        SequentialTransition traversal = new SequentialTransition();

        FillTransition highlight = fillRectangle(0.3, r, DEFAULTR, SEARCH);
        FillTransition reset = fillRectangle(0.3, r, SEARCH, DEFAULTR);
        SequentialTransition nodeTransition = new SequentialTransition(highlight, reset);
        traversal.getChildren().add(nodeTransition);

        // After traversal, do the slideY animation
        VBox finalCurrentVBox = currentVBox;
        Rectangle finalRectangle = r;
        traversal.setOnFinished(e -> {
            TranslateTransition tFirst = slideY(finalCurrentVBox, -100);
            PauseTransition pause = new PauseTransition(Duration.seconds(0.45));
            FillTransition highlightSlide = fillRectangle(0.3, finalRectangle, DEFAULTR, SEARCH);
            FillTransition resetSlide = fillRectangle(0.3, finalRectangle, SEARCH, DEFAULTR);
            TranslateTransition tLast = slideY(finalCurrentVBox, 100);
            SequentialTransition fullSequence = new SequentialTransition(
                    tFirst,
                    pause,
                    highlightSlide,
                    resetSlide,
                    tLast
            );
            fullSequence.play();
        });

        traversal.play();
        return true;
    }

    private void onClearOperation() {
        if(size != 0) {
            tfPrompt.setText("");
            writePreviousDataJSON();
            queue.clear();
            writeDataJSON();
            // Highlight Everything
            for (int i = 0; i < vBoxes.size(); ++i) {
                Rectangle r = rectangles.get(i);
                Label l = labels.get(i);
                l.setText("");
                FillTransition highlight = fillRectangle(0.6, r, DEFAULTR, NEGATIVE);
                Timeline tl = highlightNode(r,l,NEGATIVE,NEGATIVE);
                ParallelTransition pt = new ParallelTransition(highlight, tl);
                pt.play();
            }

            // Destroy All Boxes
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
            System.out.println("Queue cleared " +
                    queue.size() + " " +
                    rectangles.size() + " " +
                    labels.size() + " " +
                    vBoxes.size());
        }
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

    // Better Animations
    private FadeTransition fadeIn(VBox vBox) {
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vBox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        return fadeIn;
    }

    private FadeTransition fadeOut(VBox vBox) {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vBox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        return fadeOut;
    }

    private TranslateTransition slideY(VBox vBox, double y) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByY(y);
        slide.setCycleCount(1);

        return slide;
    }

    private TranslateTransition slideX(VBox vBox, double x) {
        TranslateTransition slide = new TranslateTransition(Duration.seconds(0.75), vBox);
        slide.setByX(x);
        slide.setCycleCount(1);

        return slide;
    }

    private Timeline highlightNode(Rectangle r, Label l, Color from, Color to) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                ),
                new KeyFrame(Duration.seconds(0.3),
                        new KeyValue(r.strokeProperty(), to),
                        new KeyValue(l.textFillProperty(), to)
                ),
                new KeyFrame(Duration.seconds(0.6),
                        new KeyValue(r.strokeProperty(), from),
                        new KeyValue(l.textFillProperty(), from)
                )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);

        return timeline;
    }

    private FillTransition fillRectangle(double duration, Rectangle r, Color from, Color to) {
        FillTransition fill = new FillTransition(Duration.seconds(duration), r);
        fill.setFromValue(from);
        fill.setToValue(to);

        return fill;
    }

    private void destroyBox(VBox vbox) {
        FadeTransition fadeOut = fadeOut(vbox);
        TranslateTransition slideLeft = slideX(vbox, -100);
        slideLeft.setOnFinished(event -> fadeOut.play());
        slideLeft.play();
        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    private void shiftX(int start, int end ,int val) {
        for(int i=start; i<end; ++i) {
            VBox vb = vBoxes.get(i);
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
