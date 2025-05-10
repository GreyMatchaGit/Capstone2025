package edu.citu.procrammers.eva.utils;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.controllers.ChatBotController;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class ChatService {


    /**
     * Reads and parses a JSON file form the given file path.
     *
     * This function attempts to read the contents of the specified file
     * and convert it into a JSONObject
     *
     * @param path The file path to read the JSON from.
     * @return A JSONObject containing the file content, or null if the file could not be read or parsed.
     */
    public static JSONObject readJSON(String path) {
        try {
            String content = Files.readString(Paths.get(path));
            return new JSONObject(content);
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Updates the chat prompt file with data content from the visualizers.
     *
     * This function reads the existing prompt JSON and inserts the provided data JSON
     * as the user message.
     *
     * It also updates the data file with the provided data JSON, and updates the prompt
     * file with the read prompt JSON.
     *
     * @param dataJSON a JSONObject containing the data content from the visualizers to
     *                 include in the conversation context.
     */
    public static void updateData(JSONObject dataJSON){
        JSONObject prompt = readJSON(PROMPT_PATH);
        if(prompt == null) return;

        fileWriter(dataJSON, DATA_PATH);

        prompt.getJSONArray("messages")
                .getJSONObject(1)
                .put("content", dataJSON.toString());

        fileWriter(prompt, PROMPT_PATH);
    }

    /**
     * Updates the chat prompt file with a new/overwritten summary message.
     *
     * This function reads the existing prompt JSON and inserts the provided summary
     * as the system's summary message.
     *
     * It is used to maintain a summary of the conversation a context for future interactions.
     *
     * @param summary A String summary to write in the assistant system message for context
     *                retention.
     */
    public static void updateSummary(String summary){
        JSONObject prompt = readJSON(PROMPT_PATH);
        if(prompt == null) return;

        prompt.getJSONArray("messages")
                .getJSONObject(3)
                .put("content", summary);

        fileWriter(prompt, PROMPT_PATH);
    }


    /**
     * Write a JSONObject to a specified file path.
     *
     * This function serializes the given JSONObject with indentation
     * and writes it to a file.
     *
     * @param object A JSONObject to write to the path.
     * @param path A String representing the file path where the JSON will be written.
     */
    public static void fileWriter(JSONObject object,String path){
        try (FileWriter file = new FileWriter(path)) {
            file.write(object.toString(2));
        } catch (IOException e) {
            System.out.println("Failed to write" + path);
        }
    }

    /**
     * Load the chatbot interface onto a specified pane.
     *
     * This function loads the chatbot-view.fxml and sets the controller's parent container
     * to the provided pane.
     *
     * @param controller An instance of ChatBotController to initialize with the pane.
     * @param pane A Pane where the chatbotUI will be loaded.
     * @throws IOException if Chatbot FXML doesn't exist.
     */
    public static void loadChatbot(ChatBotController controller, Pane pane){
        try{
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource(Chatbot));
            AnchorPane chatbotUI = loader.load();
            controller = loader.getController();
            controller.setParentContainer(pane);
            pane.getChildren().setAll(chatbotUI);
        } catch (IOException e) {
            e.printStackTrace();
        }

        pane.setVisible(false);
    }

    /**
     * Loads a chatbot interface onto a specified FXML file onto a given pane.
     *
     * This function loads the chatbot-view.fxml and sets the controller's parent container
     * to the provided pane.
     *
     * @param controller An instance of ChatBotController to initialize with the pane.
     * @param pane A Pane where the chatbotUI will be loaded.
     * @param chatbot A String path to a FXML resource.
     * @throws IOException if Chatbot FXML doesn't exist.
     */
    public static void loadChatbot(ChatBotController controller, Pane pane, String chatbot){
        try{
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource(chatbot));
            AnchorPane chatbotUI = loader.load();
            controller = loader.getController();
            controller.setParentContainer(pane);
            pane.getChildren().setAll(chatbotUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
