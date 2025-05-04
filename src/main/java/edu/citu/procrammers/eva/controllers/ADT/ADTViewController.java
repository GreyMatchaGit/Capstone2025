package edu.citu.procrammers.eva.controllers.ADT;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.models.data_structures.BST;
import edu.citu.procrammers.eva.models.data_structures.Node;
import edu.citu.procrammers.eva.utils.visuals.AnimationManager;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import java.io.IOException;


public class ADTViewController {
    @FXML private Button btnBackward;
    @FXML private Button btnPlay;
    @FXML private Button btnForward;
    @FXML private ToggleButton tglIsContinuous;
    @FXML private Slider sliderSpeed;
    @FXML private AnchorPane apMain;
    @FXML private Button btnInsert;
    @FXML private TextField tfInput;

    private BST BST;
    private AnimationManager animationManager;

    public void initialize() {
        initializeSlider();

        apMain.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Width after layout: " + newVal.doubleValue());
            animationManager = new AnimationManager(apMain);
            BST = new BST(animationManager, apMain.getWidth(), apMain.getHeight(), apMain);
        });
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


    @FXML
    private void onButtonInsertClicked() {
        System.out.println("playing speed: " + animationManager.speed + " seconds ");
        int key = Integer.parseInt(tfInput.textProperty().getValue());

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
            Circle circle  = (Circle)(stackPane.getChildren().get(0));
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
//    private void highlightNodeInView(Node node) {
//        NodeController nodeController = nodeMap.get(node);
////        nodeController.getCircle().setStroke(Paint.valueOf("GREEN"));
//    }
}
