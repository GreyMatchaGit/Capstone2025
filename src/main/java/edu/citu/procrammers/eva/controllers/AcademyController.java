package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.data.LessonContent;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.sql.SQLException;
import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;
import static edu.citu.procrammers.eva.utils.UIElementUtils.toggleGlow;

public class AcademyController {
    private ArrayList<LessonContent> lessons = new ArrayList<>();
    private int index = -2;

    public Pane fadePane;
    public AnchorPane apPrefaceP1, apPrefaceP2;
    public StackPane spChatbot;
    public ImageView imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer;
    public ImageView imgToggleChatbotPane;
    public TextArea taDiscussion, taCodeSnippet;
    public Text txtTopicTitle, txtTopicNum;
    public TextField tfPrompt;

    @FXML
    public void initialize() {
        SoundManager.playBackgroundMusic("music/academy_music.MP3", true);

        try {
            lessons =  Database.getInstance().loadLessons();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        setupGlow(imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer, imgToggleChatbotPane);

        imgBackBtn.setVisible(false);
        spChatbot.setVisible(false);

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.fadeOutMusic(() -> NavService.navigateTo(Selection));
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
            return;
        }


        imgBackBtn.setVisible(index - 1 >= 0);
        if (index > 0 && index < lessons.size()) {
            imgNextBtn.setVisible(true);
        }

        loadLessonContents();
    }



    public void toggleChatbotPane() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        spChatbot.setVisible(!spChatbot.isVisible());
    }

    public void handlePrompt() {
        SoundManager.playSFX("sfx/btn_click.MP3");
        String prompt = tfPrompt.getText();
        if (prompt.isEmpty()) {
            return;
        }

        // ToDo: Handle prompt

        tfPrompt.clear();
    }
}
