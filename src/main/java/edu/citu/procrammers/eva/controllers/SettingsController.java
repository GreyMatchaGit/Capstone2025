package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import static edu.citu.procrammers.eva.utils.Constant.Page.Selection;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class SettingsController {
    public Slider sldrMusicVolume, sldrSfxVolume;
    public ImageView imgBackMenuBtn;

    @FXML
    public void initialize() {

        setupGlow(imgBackMenuBtn);

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            NavService.navigateTo(Selection);
        });

    }

}
