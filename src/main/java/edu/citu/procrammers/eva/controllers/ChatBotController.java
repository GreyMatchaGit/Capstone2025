package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class ChatBotController {
    public VBox vbConversation;
    public TextField tfChatBox;
    public Button btnSubmit;
    public Button btnClose;

    private Pane parentContainer;

//    private static ChatBotController instance;
//
//    private ChatBotController() {}
//
//    public static ChatBotController getInstance() {
//        if (instance == null) {
//            instance = new ChatBotController();
//        }
//        return instance;
//    }


    public void initialize() {
        btnSubmit.setOnAction(e -> handleChatSubmit());
        btnClose.setOnAction(e -> {
            onCloseButtonClicked();
        });
    }

    public void setParentContainer(Pane parentContainer) {
        this.parentContainer = parentContainer;
    }

    @FXML
    private void onCloseButtonClicked() {
        if (parentContainer != null) {
            parentContainer.getChildren().clear();
        }
    }
    //TODO centralize all autobots and roll out

/*
* When clicking the submit button we create an JSONObject instance of prompt.json
* Then we write our input on prompt.json's user content
* We then pass our prompt.json with updated values and pass it on the chat request actual params
*/

    private void handleChatSubmit() {
        String input = tfChatBox.getText().trim();
        if (input.isEmpty()) return;

        Label message = new Label(input);
        message.setWrapText(true);

        vbConversation.getChildren().add(message);

        tfChatBox.clear();

        String apiKey = System.getenv("OPENAI_API_KEY");
        if (apiKey == null) {
            showError("API key not set.");
            return;
        }

        JSONObject promptJSON = ChatService.readJSON(PROMPT_PATH);
        if (promptJSON == null) {
            showError("Error reading prompt.json.");
            return;
        }

        updatePrompt(promptJSON, 2, input);
        sendChatRequest(apiKey, promptJSON);
    }

    private void updatePrompt(JSONObject promptJSON, int index, String input) {
        promptJSON.getJSONArray("messages")
                .getJSONObject(index)
                .put("content", input);
    }

    private void sendChatRequest(String apiKey, JSONObject promptJSON) {
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer " + apiKey)
                        .POST(HttpRequest.BodyPublishers.ofString(promptJSON.toString()))
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
