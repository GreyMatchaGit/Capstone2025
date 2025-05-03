package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.data.LessonContent;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.util.ArrayList;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class AcademyController {
    private ArrayList<LessonContent> lessons = new ArrayList<>();
    private int index = 0;

    public Pane fadePane;
    public ImageView imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer;
    public TextArea taDiscussion, taCodeSnippet;
    public Text txtToppicTitle, txtTopicNum;

    @FXML
    public void initialize() {

        SoundManager.playBackgroundMusic("music/academy_music.MP3", true);

        setupGlow(imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn, imgBackBtn, imgNextBtn, imgViewVisualizer);

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
        NavService.navigateTo(lessons.get(index).getVisualizerPath());
    }

    private void loadLesson() {
        LessonContent content = lessons.get(index);
        taDiscussion.setText(content.getLessonText());
        taCodeSnippet.setText(content.getCodeSnippet());
        txtToppicTitle.setText(content.getTopicTitle());
        txtTopicNum.setText("0" + (index + 1));
    }

}
