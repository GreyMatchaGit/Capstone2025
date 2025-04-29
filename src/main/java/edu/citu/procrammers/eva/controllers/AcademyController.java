package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class AcademyController {

    public Pane fadePane;
    public ImageView imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn;

    @FXML
    public void initialize() {

        SoundManager.playBackgroundMusic("music/academy_music.MP3", true);

        setupGlow(imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgSettingsBtn);

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
}
