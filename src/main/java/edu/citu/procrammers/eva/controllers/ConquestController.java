package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class ConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgProfileBtn;

    @FXML
    public void initialize() {

        SoundManager.playBackgroundMusic("music/conquest_music.MP3", true);

        setupGlow(imgBackMenuBtn, imgProfileBtn);

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.fadeOutMusic(() -> NavService.navigateTo(Selection));
        });
    }
}
