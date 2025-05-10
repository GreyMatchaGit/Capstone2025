package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.models.data_structures.ArrayNode;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.animations.bubblesort.BubbleSortAnimations.*;

public class BubbleSortController implements Initializable {

    public AnchorPane apVisualizer;
    public ImageView imgBackBtn;
    public Button btnGenerate, btnSort, btnClear;
    public Button[] btns;

    private List<ArrayNode> arrayNodes;
    private List<Integer> numbers;
    private double centerY, centerX;
    private int capacity;

    int tempSize = 5;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupGlow(imgBackBtn);

        arrayNodes = new ArrayList<>();
        numbers = new ArrayList<>();
        btns = new Button[]{btnGenerate, btnSort, btnClear};
        capacity = 0;

        ArrayNode.initializeVisualizer(apVisualizer);
        apVisualizer.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    double centerX = (apVisualizer.getWidth() / 2);
                    double centerY = (apVisualizer.getHeight() / 2);
                    this.centerX = centerX;
                    this.centerY = centerY;
                });
            }
        });
    }

    private void generateBoxes() {
        if(capacity == 0) {
            Random r = new Random();
            int num;
            ParallelTransition pt = new ParallelTransition();
            for (int i = 0; i < tempSize; ++i) {
                double totalWidth = (capacity) * 50 + capacity * 5;
                double currentX = centerX + totalWidth;
                double currentY = centerY - 50;
                num = r.nextInt(100) + 1;
                numbers.add(num);
                VBox vBox = createBoxes(capacity, String.valueOf(num), currentX, currentY, arrayNodes);
                capacity++;

                FadeTransition fadeIn = fadeIn(vBox);
                pt.getChildren().add(fadeIn);
            }
            pt.play();

            updateCoordinates();
            for(ArrayNode node : arrayNodes) {
                System.out.print(node.getX() + " ");
            }
            System.out.println();
        }
    }

    public void onButtonClick(ActionEvent event) {
        String buttonID = ((Button)event.getSource()).getId();
        switch (buttonID) {
            case "btnGenerate" -> generateBoxes();
            case "btnSort" -> sort();
            case "btnClear" -> onClearOperation();
        }
    }

    private void sort() {
        for(ArrayNode node : arrayNodes) {
            System.out.print(node.getNumber() + " ");
        }
        System.out.println();
        int i;
        for (i = 0; i < tempSize; i++) {
            bubbleSortStep(0, 0, false);
        }
        btnSort.setDisable(true);
    }

    private void bubbleSortStep(int i, int j, boolean swapped) {
        if (i >= tempSize) return;

        if (j < tempSize - i - 1) {
            if (arrayNodes.get(j).getNumber() > arrayNodes.get(j + 1).getNumber()) {

                ParallelTransition pt = swapAnimation(j, j + 1);
                pt.setOnFinished(e -> {
                    // Continue to next step
//                    ArrayNode left = arrayNodes.get(j);
//                    ArrayNode right = arrayNodes.get(j + 1);
//                    arrayNodes.set(j, right);
//                    arrayNodes.set(j + 1, left);
//                    updateCoordinates();
//                    updateIndexes();
                    bubbleSortStep(i, j + 1, true);
                });
                PauseTransition pause = pause(0.25);
                pause.setOnFinished(e -> {
                    int temp = numbers.get(j);
                    numbers.set(j, numbers.get(j+1));
                    numbers.set(j+1, temp);
                    updateNumbers();
                    pt.play();
                });
                pause.play();
            } else {
                // No swap needed, continue to next comparison
                bubbleSortStep(i, j + 1, swapped);
            }
        } else {
            // End of inner loop; if no swaps were made, array is sorted
            if (!swapped) return;

            // Start next pass
            bubbleSortStep(i + 1, 0, false);
        }
    }

    private void updateCoordinates() {
        for(int i = 0; i < capacity; ++i) {
            ArrayNode node = arrayNodes.get(i);
            double currentX = node.getVBox().getLayoutX();
            node.getVBox().setLayoutX(currentX);
            node.setX(currentX);
        }
    }

    private void onClearOperation() {
            // Destroy All Boxes
        if(capacity != 0) {
            for (ArrayNode arrayNode : arrayNodes) {
                VBox vb = arrayNode.getVBox();
                fadeOut(vb).play();
            }
            arrayNodes.clear();
            numbers.clear();
            apVisualizer.getChildren().removeAll();
            capacity = 0;
            disableButtons(false);
        }
    }

    private ParallelTransition swapAnimation(int leftIndex, int rightIndex) {
        ArrayNode left = arrayNodes.get(leftIndex);
        ArrayNode right = arrayNodes.get(rightIndex);

        // First vertical translate
        TranslateTransition leftToUp = new TranslateTransition(Duration.millis(200), left.getVBox());
        leftToUp.setToY(-60);

        TranslateTransition rightToDown = new TranslateTransition(Duration.millis(200), right.getVBox());
        rightToDown.setToY(60);

        // Horizontal translate
        TranslateTransition leftToRight = new TranslateTransition(Duration.millis(600), left.getVBox());
        leftToRight.setToX(right.getVBox().getLayoutX() - left.getVBox().getLayoutX());
        TranslateTransition rightToLeft = new TranslateTransition(Duration.millis(600), right.getVBox());
        rightToLeft.setToX(left.getVBox().getLayoutX() - right.getVBox().getLayoutX());

        // Final vertical translate, remember left and right are swapped at this point but their names retain.
        TranslateTransition leftToDown = new TranslateTransition(Duration.millis(200), left.getVBox());
        leftToDown.setToY(0);
        TranslateTransition rightToUp = new TranslateTransition(Duration.millis(200), right.getVBox());
        rightToUp.setToY(0);

        // On finish
        leftToUp.setOnFinished(e -> leftToRight.play());
        rightToDown.setOnFinished(e -> rightToLeft.play());
        leftToRight.setOnFinished(e -> leftToDown.play());
        rightToLeft.setOnFinished(e -> rightToUp.play());
        leftToDown.setOnFinished(e -> {
//            left.getVBox().setLayoutX(left.getVBox().getLayoutX() + left.getVBox().getTranslateX());
//            left.getVBox().setLayoutY(left.getVBox().getLayoutY() + left.getVBox().getTranslateY());
            left.getVBox().setTranslateX(0);
            left.getVBox().setTranslateY(0);
        });
        rightToUp.setOnFinished(e -> {
//            right.getVBox().setLayoutX(right.getVBox().getLayoutX() + right.getVBox().getTranslateX());
//            right.getVBox().setLayoutY(right.getVBox().getLayoutY() + right.getVBox().getTranslateY());
            right.getVBox().setTranslateX(0);
            right.getVBox().setTranslateY(0);
        });

        // Play altogether
        ParallelTransition pt = new ParallelTransition(leftToUp, rightToDown, pause(1.75));
        return pt;
    }

    private void updateNumbers() {
        for(int i=0; i<tempSize; ++i) {
            arrayNodes.get(i).setNumber(numbers.get(i));
        }
    }

    private void updateIndexes() {
        for(int i=0; i<capacity; ++i) {
            ArrayNode arrayNode = arrayNodes.get(i);
            arrayNode.setIndex(String.valueOf(i));
        }
    }

    private void disableButtons(boolean b) {
        for(Button button: btns) {
            button.setDisable(b);
        }
    }

    public void navigatePreviousScreen() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(Academy);
    }
}
