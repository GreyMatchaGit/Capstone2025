package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.utils.NavService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.io.IOException;

import static edu.citu.procrammers.eva.utils.Constant.Page.Chatbot;
import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;

public class AcademyController {

    public Pane fadePane;
    public ImageView imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgProfileBtn, imgChatbotBtn;
    public AnchorPane apChat;

    private ChatBotController chatBotController;

    @FXML
    public void initialize() {
        imgBackMenuBtn.setOnMouseClicked(e -> NavService.navigateTo(Selection));
        imgChatbotBtn.setOnMouseClicked(e -> loadChatbot());
    }

    private void loadChatbot() {
        try{
            FXMLLoader loader = new FXMLLoader(Eva.class.getResource(Chatbot));
            BorderPane chatbotUI = loader.load();
            chatBotController = loader.getController();
            apChat.getChildren().setAll(chatbotUI);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
