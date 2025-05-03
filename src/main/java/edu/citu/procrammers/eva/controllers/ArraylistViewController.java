package edu.citu.procrammers.eva.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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

public class ArraylistViewController {

    public VBox vbChat;
    public TextField tfInput;
    public Button btnSubmit;
    private double centerX, centerY;

    public AnchorPane apVisualizer;
    public Button btnAdd, btnAddAt, btnRemove, btnRemoveAt, btnSearch, btnClear;
    public TextField tfPrompt;

    private List<Integer> arrayList;
    private List<StackPane> stackPanes;
    private List<VBox> vBoxes;
    private int size, capacity;

    private JSONObject arrayJSON;

    public void initialize() {
        arrayList = new ArrayList<>();
        stackPanes = new ArrayList<>();
        vBoxes = new ArrayList<>();
        size=0;
        capacity=0;
        arrayJSON = new JSONObject();

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

        btnSubmit.setOnAction(e -> {
            loadChatbot();
        });
    }

    private void loadChatbot() {
        String input = tfInput.getText().trim();
        if (input.isEmpty()) { return;}

        tfInput.clear();

        Label chat = new Label(input);
        chat.setWrapText(true);

        vbChat.getChildren().add(chat);

        String apiKey = System.getenv("OPENAI_API_KEY");
        if(apiKey == null || apiKey.isEmpty()) {
            System.out.println("API Key not set");
            return;
        }

        JSONObject prompt = readJSON("prompt.json");
        if(prompt == null) return;

        updateContent(prompt, 1, arrayJSON.toString());
        updateContent(prompt, 2, input);
        sendChat(apiKey, prompt);
    }

    public static void updateContent(JSONObject prompt, int index, String input) {
        prompt.getJSONArray("messages")
                .getJSONObject(index)
                .put("content", input);
    }

    private void sendChat(String apiKey, JSONObject prompt) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(prompt.toString()))
                        .build();

                HttpClient client = HttpClient.newHttpClient();
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                JSONObject jsonResponse = new JSONObject(response.body());
                String replyText = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                Label reply = new Label(replyText);
                reply.setWrapText(true);
                Platform.runLater(() -> vbChat.getChildren().add(reply));

            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> System.out.println("Failed to communicate with API: " + e.getMessage()));
            }
        });
    }

    private void writeJSON() {
        arrayJSON.put("array", arrayList.toString());

        JSONObject prompt = readJSON("prompt.json");
        if(prompt == null) return;

        try (FileWriter file = new FileWriter("array.json")) {
            file.write(arrayJSON.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write array.json", e);
        }

        prompt.getJSONArray("messages")
                .getJSONObject(1)
                .put("content", arrayJSON.toString());

        try (FileWriter file = new FileWriter("prompt.json")) {
            file.write(prompt.toString(2));
        } catch (IOException e) {
            System.out.println("Failed to write prompt.json");
        }
    }

    private void writePreviousStateJSON(){
        arrayJSON.put("previousArray", arrayList.toString());

        try (FileWriter file = new FileWriter("array.json")) {
            file.write(arrayJSON.toString(2));
        } catch (IOException e) {
            throw new RuntimeException("Failed to write array.json", e);
        }
    }

    private JSONObject readJSON(String dir) {
        try {
            String content = Files.readString(Paths.get(dir));
            return new JSONObject(content);
        } catch (IOException e) {
            return null;
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

    public int getNum() {
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

    public Pair<Integer, Integer> getNumAndPos() {
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

    public void updateList(int n) {
        for(int i=1; i<=n; ++i) {
            createBox("");
        }
    }

    public void addElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;
        arrayList.add(num);
        writeJSON();

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

    public void addAtElement() {
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
            writeJSON();
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
            writeJSON();
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

    public void removeElement() {
        int num = getNum();
        if(num == Integer.MIN_VALUE) return;

        if(arrayList.contains(num)) {
            int index = arrayList.indexOf(num);
            writePreviousStateJSON();
            arrayList.remove(index);
            writeJSON();
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

    public void removeAtElement() {
        int pos = getNum();

        if(pos <= 0 || pos > size || pos == Integer.MIN_VALUE) {
            System.err.println("Invalid Position");
            return;
        }

        writePreviousStateJSON();
        arrayList.remove(pos-1);
        writeJSON();
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

    public void searchElement() {
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

    public void onClearOperation() {
        tfPrompt.setText("");
        writePreviousStateJSON();
        arrayList.clear();
        writeJSON();

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
    public void createBox(String num) {
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

        // Create fade-in animation
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), vbox);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.setCycleCount(1);

        // Create slide-down animation
        TranslateTransition slideDown = new TranslateTransition(Duration.seconds(1), vbox);
        slideDown.setByY(100); // Move down by 100 units
        slideDown.setCycleCount(1);

        // Play both animations in sequence
        fadeIn.setOnFinished(event -> slideDown.play());
        fadeIn.play();
    }

    public void highlightNode(Rectangle r, Label l) {
        // Create color change animation (black to orange and back)
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

    public void destroyBox(VBox vbox) {

        // Fadeout animation
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), vbox);
        fadeOut.setFromValue(1); // Start from transparent
        fadeOut.setToValue(0);   // End at fully visible
        fadeOut.setCycleCount(1);

        // Create slide-up animation
        TranslateTransition slideUp = new TranslateTransition(Duration.seconds(1), vbox);
        slideUp.setByY(-100);
        slideUp.setCycleCount(1);

        // Play both animations in sequence
        slideUp.setOnFinished(event -> fadeOut.play());
        slideUp.play();

        fadeOut.setOnFinished(event -> apVisualizer.getChildren().remove(vbox));
    }

    public void shiftX(int val) {
        for(VBox vb : vBoxes) {
            vb.setLayoutX(vb.getLayoutX() + val);
        }
    }

    public void phantomDelete(double x, double y, String num, int indexing) {
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
