package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static edu.citu.procrammers.eva.utils.Constant.Page.Academy;

public class ChatBotController {
    public VBox vbConversation;
    public TextField tfChatBox;
    public Button btnSubmit;
    public Button btnClose;

    public void initialize() {
        btnSubmit.setOnAction(e -> handleChatSubmit());
        btnClose.setOnAction(e -> NavService.navigateTo(Academy));
    }

    private void handleChatSubmit() {
        String input = tfChatBox.getText().trim();
        if (input.isEmpty()) return;

        //Styling
        Label message = new Label(input);
        message.setWrapText(true);

        vbConversation.getChildren().add(message);

        tfChatBox.clear();


        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) {
            showError("API key not set.");
            return;
        }

        JSONObject promptJson = readPromptJson();
        if (promptJson == null) {
            showError("Error reading prompt.json.");
            return;
        }

        updatePromptWithInput(promptJson, input);
        sendChatRequest(apiKey, promptJson);
    }

    private JSONObject readPromptJson() {
        try {
            String content = Files.readString(Paths.get("prompt.json"));
            return new JSONObject(content);
        } catch (IOException e) {
            return null;
        }
    }

    private void updatePromptWithInput(JSONObject promptJson, String input) {
        promptJson.getJSONArray("messages")
                .getJSONObject(1)
                .put("content", input);
    }

    private void sendChatRequest(String apiKey, JSONObject promptJson) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(promptJson.toString()))
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

                Platform.runLater(() -> vbConversation.getChildren().add(reply));

            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showError("Failed to communicate with API: " + e.getMessage()));
            }
        });
    }

    private void showError(String message) {
        System.out.println(message);
    }
}
