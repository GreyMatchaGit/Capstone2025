package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.ChatBotController;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class ChatService {

    public static JSONObject readJSON(String path) {
        try {
            String content = Files.readString(Paths.get(path));
            return new JSONObject(content);
        } catch (IOException e) {
            return null;
        }
    }

    public static void updateData(JSONObject dataJSON){
        JSONObject prompt = readJSON(PROMPT_PATH);
        if(prompt == null) return;

        fileWriter(dataJSON, DATA_PATH);

        prompt.getJSONArray("messages")
                .getJSONObject(1)
                .put("content", dataJSON.toString());

        fileWriter(prompt, PROMPT_PATH);
    }

    public static void fileWriter(JSONObject object,String path){
        try (FileWriter file = new FileWriter(path)) {
            file.write(object.toString(2));
        } catch (IOException e) {
            System.out.println("Failed to write" + path);
        }
    }

    public static void loadChatbot(ChatBotController controller, Pane pane){
        try{
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource(Chatbot));
            BorderPane chatbotUI = loader.load();
            controller = loader.getController();
            controller.setParentContainer(pane);
            pane.getChildren().setAll(chatbotUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
