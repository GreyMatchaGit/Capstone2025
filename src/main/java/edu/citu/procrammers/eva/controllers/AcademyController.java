package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class AcademyController {

    public Pane fadePane;
    public ImageView imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgProfileBtn;

    @FXML
    public void initialize() {

        setupGlow(imgLessonsBkmrk, imgTutorBkmrk, imgBackMenuBtn, imgProfileBtn);

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Selection);
        });

    }
}
