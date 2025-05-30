package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static edu.citu.procrammers.eva.utils.Constant.ORACLE_MESSAGES;
import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;

public class ChatBotController {

    @FXML private VBox vbConversation;
    @FXML private TextField tfChatBox;
    @FXML private Button btnSubmit;
    @FXML private Button btnClose;

    private Pane parentContainer;

    @FXML
    public void initialize() {
        if (btnClose != null) {
            btnClose.setOnAction(e -> {
                onCloseButtonClicked();
            });
        }

        tfChatBox.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent ke){
                if (ke.getCode().equals(KeyCode.ENTER)) {
                    handleChatSubmit();
                }
            }
        });

        btnSubmit.setOnAction(e -> handleChatSubmit());
    }

    @FXML
    private void onCloseButtonClicked() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        if (parentContainer != null) {
            parentContainer.getChildren().clear();
        }
    }

/*
* When clicking the submit button we create an JSONObject instance of prompt.json
* Then we write our input on prompt.json's user content
* We then pass our prompt.json with updated values and pass it on the chat request actual params
*/

    private void handleChatSubmit() {
        SoundManager.playSFX(SFX_BUTTON_CLICK);
        String input = tfChatBox.getText().trim();
        if (input.isEmpty()) return;

        HBox userBubble = createUserBubble(input);
        HBox loadingBubble = createLoadingBubble();
        vbConversation.getChildren().add(userBubble);
        vbConversation.getChildren().add(loadingBubble);

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
        sendChatRequest(apiKey, promptJSON, loadingBubble);
    }


    private void updatePrompt(JSONObject promptJSON, int index, String input) {
        promptJSON.getJSONArray("messages")
                .getJSONObject(index)
                .put("content", input);
    }

    private void sendChatRequest(String apiKey, JSONObject promptJSON, HBox loadingBubble) {
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

                String summary = null;
                int index = replyText.indexOf("Summary:");

                if (index != -1) {
                    summary = replyText.substring(index).trim();
                    replyText = replyText.substring(0, index).trim();
                }

                if(summary != null) {
                    ChatService.updateSummary(summary);
                }

                HBox botBubble = createBotBubble(replyText);
                Platform.runLater(() -> {
                    vbConversation.getChildren().add(botBubble);
                    vbConversation.getChildren().remove(loadingBubble);});

            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showError("Failed to communicate with API: " + e.getMessage()));
            }
        });
    }

    public void setParentContainer(Pane parentContainer) {
        this.parentContainer = parentContainer;
    }

    private void showError(String message) {
        System.out.println(message);
    }

    private HBox createUserBubble(String message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_RIGHT);
        Label lbl = new Label(message);
        lbl.getStyleClass().add("user-chat-bubble");
        hbox.getChildren().add(lbl);
        return hbox;
    }

    private HBox createBotBubble(String message) {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label(message);
        lbl.getStyleClass().add("bot-chat-bubble");
        hbox.getChildren().add(lbl);
        return hbox;
    }

    private Label generateLoadingLabel() {
        Random rand = new Random();
        String message = ORACLE_MESSAGES[rand.nextInt(ORACLE_MESSAGES.length)];
        Label lbl = new Label(message);
        lbl.getStyleClass().add("bot-chat-bubble");
        lbl.getStyleClass().add("loading");
        return lbl;
    }

    private HBox createLoadingBubble() {
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);
        Label lbl = generateLoadingLabel();
        lbl.getStyleClass().add("bot-chat-bubble");
        lbl.getStyleClass().add("loading");
        hbox.getChildren().add(lbl);
        return hbox;
    }

}
