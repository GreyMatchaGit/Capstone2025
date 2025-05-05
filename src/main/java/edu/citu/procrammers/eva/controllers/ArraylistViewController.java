package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static edu.citu.procrammers.eva.utils.Constant.Page.Chatbot;
import static edu.citu.procrammers.eva.utils.Constant.Page.PROMPT_PATH;

public class ArraylistViewController {

    private double centerX, centerY;

    public AnchorPane apVisualizer, apChat;
    public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    public TextField tfPrompt;
    public ImageView imgChatbotBtn;

    private List<Integer> arrayList;
    private List<StackPane> stackPanes;
    private List<VBox> vBoxes;
    private int size, capacity;

    private JSONObject dataJSON;

    public ChatBotController chatBotController;

    public void initialize() {
        arrayList = new ArrayList<>();
        stackPanes = new ArrayList<>();
        vBoxes = new ArrayList<>();
        size=0;
        capacity=0;
        dataJSON = new JSONObject();

        apVisualizer.widthProperty().addListener((obs, oldVal, newVal) -> {
            double centerX = newVal.doubleValue() / 2;
            this.centerX = centerX;
        });

        apVisualizer.heightProperty().addListener((obs, oldVal, newVal) -> {
            double centerY = newVal.doubleValue() / 2;
            this.centerY = centerY;

        });

        // Create initial nodes, capacity = 5
        Platform.runLater(() -> {
            for(int i=1; i<=5; ++i) {
                createBox("");
            }
        });

        imgChatbotBtn.setOnMouseClicked(e -> loadChatbot());
    }

    private void loadChatbot() {
        try{
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource(Chatbot));
            BorderPane chatbotUI = loader.load();
            chatBotController = loader.getController();
            chatBotController.setParentContainer(apChat);
            apChat.getChildren().setAll(chatbotUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeDataJSON() {
        dataJSON.put("array", arrayList.toString());

        JSONObject prompt = ChatBotController.readJSON(PROMPT_PATH);
        if(prompt == null) return;

        try (FileWriter file = new FileWriter("data.json")) {
            file.write(dataJSON.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write data.json", e);
        }

        prompt.getJSONArray("messages")
                .getJSONObject(1)
                .put("content", dataJSON.toString());

        try (FileWriter file = new FileWriter("prompt.json")) {
            file.write(prompt.toString(2));
        } catch (IOException e) {
            System.out.println("Failed to write prompt.json");
        }
    }

    private void writePreviousDataJSON(){
        dataJSON.put("previousArray", arrayList.toString());

        try (FileWriter file = new FileWriter("data.json")) {
            file.write(dataJSON.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write data.json", e);
        }
    }

    public void onButtonClick(ActionEvent event) {
        if(event.getSource() == btnAdd) {
            addElement();
        } else if(event.getSource() == btnAddAt) {
            addAtElement();
        } else if(event.getSource() == btnRemove) {
            removeElement();
        } else if(event.getSource() == btnRemoveAt) {
            removeAtElement();
        } else if(event.getSource() == btnSearch) {
            searchElement();
        } else if(event.getSource() == btnClear) {
            onClearOperation();
        }
    }

    private int getNum() {
        String prompt = tfPrompt.getText();
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

    private Pair<Integer, Integer> getNumAndPos() {
        int num = Integer.MIN_VALUE;
        int pos;

        String prompt = tfPrompt.getText().trim();
        StringTokenizer st = new StringTokenizer(prompt, " ");

        try {
            num = Integer.parseInt(st.nextToken());
            pos = Integer.parseInt(st.nextToken());
            if (pos <= 0 || pos > size+1) {
                throw new IllegalArgumentException("Invalid Position");
            }
        } catch (NoSuchElementException | IllegalArgumentException e) {
            System.err.println("Invalid Arguments: " + e.getMessage());
            pos = -1;
        } finally {
            tfPrompt.setText("");
        }

        return new Pair<>(pos, num);
    }

    private void updateList(int n) {
        for(int i=1; i<=n; ++i) {
            createBox("");
        }
    }

    private void addElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;
        arrayList.add(num);
        writeDataJSON();

        if(size == capacity) {
            int additional = (int) Math.ceil(capacity * 0.5);
            updateList(additional);
        }

        StackPane sp = stackPanes.get(size);
        Rectangle r = null;
        Label l = null;
        for (Node n : sp.getChildren()) {
            if (n instanceof Label) {
                l = (Label)n;
                ((Label) n).setText(Integer.toString(num));
            } else if(n instanceof Rectangle) {
                r = (Rectangle) n;
            }

        }
        assert r!=null && l!=null;
        highlightNode(r, l);
        size++;
    }

    private void addAtElement() {
        Pair pair = getNumAndPos();
        int num = (int) pair.getValue();
        int pos = (int) pair.getKey();

        if(num == Integer.MIN_VALUE || pos == -1) return;

        if(size == capacity) {
            // resize updateList()
            int additional = (int) Math.ceil(capacity * 0.5);
            updateList(additional);
        }

        Rectangle r = null;
        Label l = null;
        if(pos == size+1) {
            arrayList.add(num);
            writeDataJSON();
            StackPane sp = stackPanes.get(pos-1);
            for (Node n : sp.getChildren()) {
                if (n instanceof Label) {
                    ((Label) n).setText(Integer.toString(num));
                    l = (Label) n;
                } else if(n instanceof Rectangle) {
                    r = (Rectangle)n;
                }
            }
        } else {
            arrayList.add(pos-1, num);
            writeDataJSON();
            for(int i = pos-1; i < stackPanes.size() && i < arrayList.size(); ++i) {
                for (Node n : stackPanes.get(i).getChildren()) {
                    if (n instanceof Label) {
                        ((Label) n).setText(Integer.toString(arrayList.get(i)));
                        if(i == pos-1) l = (Label) n;
                    } else if(n instanceof Rectangle) {
                        if(i == pos-1) r = (Rectangle)n;
                    }
                }
            }
        }
        assert r!=null && l!=null;
        highlightNode(r, l);
        ++size;
    }

    private void removeElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(arrayList.contains(num)) {
            int index = arrayList.indexOf(num);
            writePreviousDataJSON();
            arrayList.remove(index);
            writeDataJSON();
            Rectangle r = null;
            Label l = null;
            for(int i = index; i < stackPanes.size() || i < arrayList.size(); ++i) {
                for (Node n : stackPanes.get(i).getChildren()) {
                    if (n instanceof Label) {
                        String label = i < arrayList.size() ? Integer.toString(arrayList.get(i)) : "";
                        ((Label) n).setText(label);
                        l = (Label) n;
                    } else if(n instanceof Rectangle) {
                        r = (Rectangle) n;
                    }
                }
                if(i == index) {
                    highlightNode(r, l);
                    VBox vbox = (VBox) stackPanes.get(i).getParent();
                    phantomDelete(vbox.getLayoutX(), vbox.getLayoutY(), Integer.toString(num), i);
                }
            }

            --size;
        } else {
            System.err.println("Number is non-existent");
        }
    }

    private void removeAtElement() {
        int pos = getNum();

        if(pos <= 0 || pos > size || pos == Integer.MIN_VALUE) {
            System.err.println("Invalid Position");
            return;
        }

        writePreviousDataJSON();
        arrayList.remove(pos-1);
        writeDataJSON();
        for(int i = pos-1; i < stackPanes.size() || i < arrayList.size(); ++i) {
            for (Node n : stackPanes.get(i).getChildren()) {
                if (n instanceof Label) {
                    String label = i < arrayList.size() ? Integer.toString(arrayList.get(i)) : "";
                    ((Label) n).setText(label);
                }
            }
        }
        --size;
    }

    private void searchElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(arrayList.contains(num)) {
            int index = arrayList.indexOf(num);
            StackPane sp = stackPanes.get(index);
            Rectangle r = null;
            Label l = null;
            for (Node n : sp.getChildren()) {
                if (n instanceof Rectangle) {
                    r = (Rectangle) n;
                } else {
                    l = (Label) n;
                }
            }

            highlightNode(r, l);

        } else {
            System.err.println("Number not found!");
        }
    }

    private void onClearOperation() {
        tfPrompt.setText("");
        writePreviousDataJSON();
        arrayList.clear();
        writeDataJSON();

        // Highlight Everything
        for(StackPane sp : stackPanes) {
            Rectangle r = null;
            Label l = null;
            for(Node n : sp.getChildren()) {
                if(n instanceof Label) {
                    l = (Label) n;
                    l.setText("");
                } else if(n instanceof Rectangle) {
                    r = (Rectangle) n;
                }
            }
            highlightNode(r, l);
        }


        size = 0;
        int removed=0;
        for(int i=capacity-1; i>=5; --i, --capacity) {
            stackPanes.remove(i);
            VBox vb = vBoxes.get(i);
            destroyBox(vb);
            vBoxes.remove(i);
            removed++;
        }
        shiftX(25 * removed);
    }


    // Animation utils
    private void createBox(String num) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        Label value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");
        StackPane sp = new StackPane(rect, value);
        Label index = new Label(Integer.toString(capacity));
        index.setStyle("-fx-font-weight: bold;");
        VBox vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX((centerX) + capacity*30);
        vbox.setLayoutY(centerY-100);

        apVisualizer.getChildren().add(vbox);
        stackPanes.add(sp);
        vBoxes.add(vbox);

        shiftX(-25);
        capacity++;

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vbox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(1), vbox);
        slideDown.setByY(100); // Move down by 100 units
        slideDown.setCycleCount(1);

        fadeIn.setOnFinished(event -> slideDown.play());
        fadeIn.play();
    }

    private void highlightNode(Rectangle r, Label l) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(r.strokeProperty(), Color.BLACK),
                        new KeyValue(l.textFillProperty(), Color.BLACK)
                ),
                new KeyFrame(Duration.seconds(1),
                        new KeyValue(r.strokeProperty(), Color.ORANGE),
                        new KeyValue(l.textFillProperty(), Color.ORANGE)
                ),
                new KeyFrame(Duration.seconds(1.25),
                        new KeyValue(r.strokeProperty(), Color.BLACK),
                        new KeyValue(l.textFillProperty(), Color.BLACK)
                )
        );
        timeline.setCycleCount(1);
        timeline.setAutoReverse(true);
        timeline.play();
    }

    private void destroyBox(VBox vbox) {

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vbox);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setCycleCount(1);

        TranslateTransition slideUp = new TranslateTransition(Duration.seconds(1), vbox);
        slideUp.setByY(-100);
        slideUp.setCycleCount(1);

        slideUp.setOnFinished(event -> fadeOut.play());
        slideUp.play();

        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    private void shiftX(int val) {
        for(VBox vb : vBoxes) {
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    private void phantomDelete(double x, double y, String num, int indexing) {
        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.WHITE);
        rect.setStroke(Color.BLACK);
        rect.setStrokeWidth(2);
        Label value = new Label(num);
        value.setStyle("-fx-font-weight: bold;");
        StackPane sp = new StackPane(rect, value);
        Label index = new Label(Integer.toString(indexing));
        index.setStyle("-fx-font-weight: bold;");
        VBox vbox = new VBox(2);
        vbox.getChildren().addAll(sp, index);
        vbox.setStyle("-fx-alignment: center;");
        vbox.setLayoutX(x);
        vbox.setLayoutY(y);

        apVisualizer.getChildren().add(vbox);
        highlightNode(rect, value);
        destroyBox(vbox);
    }
}
