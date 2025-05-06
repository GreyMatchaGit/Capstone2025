package edu.citu.procrammers.eva.controllers.ADT;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.models.data_structures.BST;
import edu.citu.procrammers.eva.models.data_structures.Node;
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
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;
import org.w3c.dom.Text;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.EventListener;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;


public class ADTViewController {
    @FXML private Button btnBackward;
    @FXML private Button btnPlay;
    @FXML private Button btnForward;
    @FXML private ToggleButton tglIsContinuous;
    @FXML private Slider sliderSpeed;
    @FXML private AnchorPane apMain;
    @FXML private Button btnInsert;
    @FXML private TextField tfInput;
    @FXML private TextField tfDelete;
    @FXML private Button btnDelete;
    @FXML private ToggleButton tglSeratoMode;

    private BST BST;
    private AnimationManager animationManager;

    public void initialize() {
        initializeSlider();
        initializeStyles();
        initializeKeyboardListener();
        apMain.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Width after layout: " + newVal.doubleValue());
            animationManager = new AnimationManager(apMain);
            BST = new BST(animationManager, apMain.getWidth(), apMain.getHeight(), apMain);
        });
    }

    private void initializeKeyboardListener() {
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
        int key = Integer.parseInt(tfDelete.getText());
        clearFields();
        animationManager.commands = BST.deleteElement(key);
        animationManager.play();
    }


    @FXML
    private void onButtonInsertClicked() {
        System.out.println("playing speed: " + animationManager.speed + " seconds ");
        int key = Integer.parseInt(tfInput.textProperty().getValue());
        clearFields();

        animationManager.commands = BST.insertElement(key);
        animationManager.play();
//        Node newNode = BST.insertElement(key);
//        addNewNodeUI(newNode);
    }

    @FXML private void onButtonBackwardClicked() {
        animationManager.undo();
    }

    @FXML private void onButtonPlayClicked() {
        toggleContinuous();
        animationManager.play();
    }

    @FXML private void onButtonForwardClick() {
        animationManager.play();
    }

    @FXML private void toggleContinuous() {
        animationManager.isContinuous = !animationManager.isContinuous;
    }

    private void addNewNodeUI(Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource("ADT_visuals/node-view.fxml"));
            javafx.scene.Node nodeView = loader.load();
            NodeController nodeController = loader.getController();

            String strNodeElem = Integer.toString(node.getElement());
            nodeController.setText(strNodeElem);

            System.out.println(node.getElement() + " added");
            StackPane stackPane = (StackPane) nodeView;
            Circle circle  = (Circle)(stackPane.getChildren().getFirst());
            circle.setRadius(20);

            apMain.getChildren().add(nodeView);
            nodeView.setLayoutX(node.x.getValue() - circle.getRadius());
            nodeView.setLayoutY(node.y.getValue() - circle.getRadius());

            node.x.addListener((observable, oldValue, newValue) -> {
                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(nodeView.layoutXProperty(), newValue.doubleValue() - circle.getRadius());
                KeyFrame kf = new KeyFrame(Duration.millis(300), kv); // 300ms animation
                timeline.getKeyFrames().add(kf);
                timeline.play();
            });

            node.y.addListener((observable, oldValue, newValue) -> {
                Timeline timeline = new Timeline();
                KeyValue kv = new KeyValue(nodeView.layoutYProperty(), newValue.doubleValue());
                KeyFrame kf = new KeyFrame(Duration.millis(300), kv); // 300ms animation
                timeline.getKeyFrames().add(kf);
                timeline.play();
            });

            if (BST.getRoot() != node) {
                Line line = new Line();

                // Bind the startX and startY to the parent's position
                line.setStartX(node.getParent().x.get());
                node.getParent().x.addListener((obs, oldX, newX) -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                            new KeyValue(line.startXProperty(), newX.doubleValue())));
                    timeline.play();
                });
                line.setStartY(node.getParent().y.get());
                node.getParent().y.addListener((obs, oldX, newX) -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                            new KeyValue(line.startYProperty(), newX.doubleValue())));
                    timeline.play();
                });

                // Initialize endX and endY
                line.setEndX(node.x.get());
                line.setEndY(node.y.get());

                node.getParent().x.addListener((obs, oldX, newX) -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                            new KeyValue(line.startXProperty(), newX.doubleValue())));
                    timeline.play();
                });


                node.x.addListener((obs, oldX, newX) -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(500),
                            new KeyValue(line.endXProperty(), newX.doubleValue())));
                    timeline.play();
                });

                // Animate endY when node.y changes
                node.y.addListener((obs, oldY, newY) -> {
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.millis(300),
                            new KeyValue(line.endYProperty(), newY.doubleValue())));
                    timeline.play();
                });
                apMain.getChildren().add(line);
                line.toBack();
            }

            System.out.printf("Node %d at (%.2f, %.2f)\n", node.getElement(), node.x.get(), node.y.get());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void changeSuccessorMode() {
        BST.isStandard = tglSeratoMode.isSelected();
    }

    private void initializeStyles() {
        Scene scene = NavService.mainController.mainScreen.getScene();

        scene.getStylesheets().add(Eva.class.getResource("styles/ADT-view.css").toExternalForm());

        tglIsContinuous.getStyleClass().add("switch-toggle");

        Region thumb = new Region();
        thumb.getStyleClass().add("thumb");
        tglIsContinuous.setGraphic(thumb);

//        tglIsContinuous.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            if (newVal) {
//                thumb.setTranslateX(24);
//            } else {
//                thumb.setTranslateX(4);
//            }
//        });

        tglSeratoMode.getStyleClass().add("switch-toggle");

        Region thumb2 = new Region();
        thumb2.getStyleClass().add("thumb");
        tglSeratoMode.setGraphic(thumb2);
//        tglIsContinuous.selectedProperty().addListener((obs, oldVal, newVal) -> {
//            double targetX = newVal ? 24 : 4;
//
//            Timeline timeline = new Timeline(
//                    new KeyFrame(Duration.millis(100),
//                            new KeyValue(thumb.translateXProperty(), targetX)
//                    )
//            );
//            timeline.play();
//        });

//        btnPlay.getStyleClass().addAll("button", "play-button");
//////        btnPlay.setText("Play");
//        btnBackward.getStyleClass().addAll("button", "backward-button");
//        btnForward.getStyleClass().addAll("button", "forward-button");
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
//    private void highlightNodeInView(Node node) {
//        NodeController nodeController = nodeMap.get(node);
////        nodeController.getCircle().setStroke(Paint.valueOf("GREEN"));
//    }
}
