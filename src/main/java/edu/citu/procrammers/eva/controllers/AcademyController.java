package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.data.LessonContent;
import edu.citu.procrammers.eva.utils.ChatService;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;
import org.json.JSONObject;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;
import java.sql.SQLException;
import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class AcademyController {
    private ArrayList<LessonContent> lessons = new ArrayList<>();
    private int index = -2;

    public Pane fadePane;
    public AnchorPane apChat;
    public ChatBotController chatBotController;
    public AnchorPane apPrefaceP1, apPrefaceP2;
    public ImageView imgChatbotBtn, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer, imgToggleChatbotPane;
    public StackPane spChatbot;
    public TextArea taDiscussion, taCodeSnippet;
    public Text txtTopicTitle, txtTopicNum;


    @FXML
    public void initialize() {
        SoundManager.playBackgroundMusic("music/academy_music.MP3", true);
        try {
            lessons =  Database.getInstance().loadLessons();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        ChatService.updateData(new JSONObject());

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Selection);
        });

        imgToggleChatbotPane.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            toggleChatpane();
        });

        imgChatbotBtn.setOnMouseClicked(e ->{
            SoundManager.playSFX("sfx/btn_click.MP3");
            ChatService.loadChatbot(chatBotController, apChat, GeneralChatbot);
            toggleChatpane();
        });

        setupGlow(imgChatbotBtn, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer, imgToggleChatbotPane);

        imgBackBtn.setVisible(false);
        spChatbot.setVisible(false);

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.fadeOutMusic(() -> NavService.navigateTo(Selection));

            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> SoundManager.fadeOutMusic());
            fadeOut.play();
        });

        imgSettingsBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.pauseMusic();
            NavService.navigateTo(Settings);
            NavService.previousPage = Academy;
        });
    }

    public void viewVisualizer() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        NavService.navigateTo(lessons.get(index).getVisualizerPath());
    }

    private void loadLessonContents() {
        LessonContent content = lessons.get(index);

        taDiscussion.setText(content.getLessonText());
        taCodeSnippet.setText(content.getCodeSnippet());
        txtTopicTitle.setText(content.getTopicTitle());
        txtTopicNum.setText("0" + (index + 1));
    }

    public void nextLesson() {
        System.out.println("Index: " + index);
        SoundManager.playSFX("sfx/btn_click.MP3");
        index++;

        if (index == -1) {
            imgBackBtn.setVisible(true);
            apPrefaceP1.setVisible(false);
            apPrefaceP1.setTranslateX(1500);
            return;
        } else if (index == 0) {
            apPrefaceP2.setVisible(false);
            apPrefaceP2.setTranslateX(1500);
            imgBackBtn.setVisible(false);
        }

        imgNextBtn.setVisible(index + 1 < lessons.size());
        if (index > 0 && index < lessons.size()) {
            imgBackBtn.setVisible(true);
        }

        loadLessonContents();
    }

    public void prevLesson() {
        System.out.println("Index: " + index);
        SoundManager.playSFX("sfx/btn_click.MP3");
        index--;

        if (index == -2) {
            imgBackBtn.setVisible(false);
            apPrefaceP1.setVisible(true);
            apPrefaceP1.setTranslateX(0);
            return;
        } else if (index == -1) {
            apPrefaceP2.setVisible(true);
            apPrefaceP2.setTranslateX(0);
        }

        loadLessonContents();
    }

    public void toggleChatpane() {
        spChatbot.setVisible(!spChatbot.isVisible());
    }
}
