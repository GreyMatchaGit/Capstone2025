package edu.citu.procrammers.eva.controllers.ADT;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.models.BTInorderIterator;
import edu.citu.procrammers.eva.models.BinaryTreeIterator;
import edu.citu.procrammers.eva.models.data_structures.BST;
import edu.citu.procrammers.eva.models.data_structures.Node;
import edu.citu.procrammers.eva.utils.TreeLayoutCalculator;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


public class ADTViewController {
    public TextField tfChat;
    public Button btnSubmit;
    public VBox vbChat;
    @FXML
    private AnchorPane apMain;
    @FXML
    private Button btnInsert;
    @FXML
    private TextField tfInput;
    private BST BST;

    public void initialize() {
        apMain.widthProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Width after layout: " + newVal.doubleValue());
            BST = new BST(apMain);
        });
        btnSubmit.setOnAction(e -> {
            loadChatbot();
        });
    }

    private void loadChatbot() {
        String input = tfChat.getText().trim();
        if (input.isEmpty()) { return;}

        tfChat.clear();

        Label chat = new Label(input);
        chat.setWrapText(true);

        vbChat.getChildren().add(chat);

        String apiKey = System.getenv("OPENAI_API_KEY");
        if(apiKey == null || apiKey.isEmpty()) {
            System.out.println("API Key not set");
            return;
        }

        JSONObject prompt = readJSON("prompt.json");
        JSONObject tree = readJSON("tree.json");
        if(prompt == null) return;

        updateContent(prompt, 2, input);
        sendChat(apiKey, prompt);

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

    public static void updateContent(JSONObject prompt, int index, String input) {
        prompt.getJSONArray("messages")
                .getJSONObject(index)
                .put("content", input);
    }

    public static JSONObject readJSON(String dir) {
        try {
            String content = Files.readString(Paths.get(dir));
            return new JSONObject(content);
        } catch (IOException e) {
            return null;
        }
    }



    @FXML
    private void onButtonInsertClicked() {
        int key = Integer.parseInt(tfInput.textProperty().getValue());

        Node newNode = BST.insertElement(key);

//        BTInorderIterator iterator = new BTInorderIterator(BST.getRoot());

        addNewNodeUI(newNode);

//        BTInorderIterator inorderIterator = new BTInorderIterator(BST.getRoot());
//
//        Node curr = null;
//        if (inorderIterator.hasNext()) {
//            curr = inorderIterator.next();
//            highlightNodeInView(curr);  // Custom method to update the visual state
//        }
//
//        addNewNodeUI(curr);
    }

    private void addNewNodeUI(Node node) {
        try {
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource("ADT_visuals/node-view.fxml"));
            javafx.scene.Node nodeView = loader.load();
            NodeController nodeController = loader.getController();

            String strNodeElem = Integer.toString(node.getElement());
            nodeController.setText(strNodeElem);

            System.out.println(node.getElement() + " added");
//            node.x.bind(nodeView.layoutXProperty());
//            node.y.bind(nodeView.layoutYProperty());
            StackPane stackPane = (StackPane) nodeView;
            Circle circle  = (Circle)(stackPane.getChildren().getFirst());
            circle.setRadius(20);

            apMain.getChildren().add(nodeView);
            nodeView.setLayoutX(node.x.getValue());
            nodeView.setLayoutY(node.y.getValue());

            node.x.addListener((observable, oldvalue, newValue) -> {
                nodeView.setLayoutX(newValue.doubleValue());
                System.out.printf("Node %d: x = %.2f\n", node.getElement(), newValue.doubleValue());
            });

            node.y.addListener((observable, oldvalue, newValue) -> {
                nodeView.setLayoutY(newValue.doubleValue());
            });

            if (BST.getRoot() != node) {
                Line line = new Line();

                line.setStartX(node.getParent().x.doubleValue());
                line.setStartY(node.getParent().y.doubleValue());

                line.setEndX(node.x.doubleValue());
                line.setEndY(node.y.doubleValue());

                apMain.getChildren().add(line);
            }



//            nodeView.setLayoutX(node.x.getValue());
//            nodeView.setLayoutY(node.y.getValue());

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
