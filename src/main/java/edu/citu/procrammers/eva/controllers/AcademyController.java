package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static edu.citu.procrammers.eva.utils.Constant.Page.Chatbot;
import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;

public class AcademyController {

    public Pane fadePane;
    public ImageView imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgProfileBtn, imgChatbotBtn;

    @FXML
    public void initialize() {
        imgBackMenuBtn.setOnMouseClicked(e -> NavService.navigateTo(Selection));

        imgChatbotBtn.setOnMouseClicked(e -> NavService.navigateTo(Chatbot));
    }
}
