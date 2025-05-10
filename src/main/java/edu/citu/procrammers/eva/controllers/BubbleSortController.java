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
import java.util.*;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.animations.bubblesort.BubbleSortAnimations.*;
import static java.util.Collections.swap;

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
        btnSort.setDisable(true);
        SequentialTransition sq = new SequentialTransition();
        for (int i = 0; i < tempSize; i++) {
            for (int j = 0; j < tempSize - i - 1; j++) {
                if (numbers.get(j + 1) < numbers.get(j)) {
                    sq.getChildren().addAll(swapAnimation(j, j + 1));
                    swap(numbers, j, j + 1);
                }
            }
        }
        sq.setOnFinished(e -> btnSort.setDisable(false));
        sq.play();

        for (int i = 0; i < tempSize; i++) {
            System.out.print(numbers.get(i) + ", ");
        }
        System.out.println();
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

    private Transition swapAnimation(int leftIndex, int rightIndex) {
        ArrayNode left = arrayNodes.get(leftIndex);
        ArrayNode right = arrayNodes.get(rightIndex);

        SequentialTransition st = new SequentialTransition();

        // First vertical translate
        TranslateTransition leftToUp = new TranslateTransition(Duration.millis(200), left.getVBox());
        leftToUp.setToY(-60);

        TranslateTransition rightToDown = new TranslateTransition(Duration.millis(200), right.getVBox());
        rightToDown.setToY(60);

        ParallelTransition firstMove = new ParallelTransition();
        firstMove.getChildren().addAll(leftToUp, rightToDown);

        // Horizontal translate
        TranslateTransition leftToRight = new TranslateTransition(Duration.millis(600), left.getVBox());
        leftToRight.setToX(right.getVBox().getLayoutX() - left.getVBox().getLayoutX());
        TranslateTransition rightToLeft = new TranslateTransition(Duration.millis(600), right.getVBox());
        rightToLeft.setToX(left.getVBox().getLayoutX() - right.getVBox().getLayoutX());

        ParallelTransition secondMove = new ParallelTransition();
        secondMove.getChildren().addAll(leftToRight, rightToLeft);

        // Final vertical translate, remember left and right are swapped at this point but their names retain.
        TranslateTransition leftToDown = new TranslateTransition(Duration.millis(300), left.getVBox());
        leftToDown.setToY(0);
        TranslateTransition rightToUp = new TranslateTransition(Duration.millis(300), right.getVBox());
        rightToUp.setToY(0);

        ParallelTransition thirdMove = new ParallelTransition();
        thirdMove.getChildren().addAll(leftToDown, rightToUp);

        st.getChildren().addAll(firstMove, secondMove, thirdMove);
        // On finish

        st.setOnFinished(e -> {
//            left.getVBox().setLayoutX(left.getVBox().getLayoutX() + left.getVBox().getTranslateX());
//            left.getVBox().setLayoutY(left.getVBox().getLayoutY() + left.getVBox().getTranslateY());
            left.getVBox().setTranslateX(0);
            left.getVBox().setTranslateY(0);

            right.getVBox().setTranslateX(0);
            right.getVBox().setTranslateY(0);

            int temp = right.getNumber();
            right.setNumber(left.getNumber());
            left.setNumber(temp);
            System.out.println("swap animation done");
        });

        // Play altogether
        return st;
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
