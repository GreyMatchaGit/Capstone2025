package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;

import static edu.citu.procrammers.eva.utils.Constant.Sound.SFX_BUTTON_CLICK;
import static edu.citu.procrammers.eva.utils.UIElementUtils.*;

public class SettingsController {
    public Slider sldrMusicVolume, sldrSfxVolume;
    public ImageView imgBackMenuBtn;

    @FXML
    public void initialize() {

        setupGlow(imgBackMenuBtn);

        Platform.runLater(() -> {
            setupSliderUI(sldrMusicVolume, sldrSfxVolume);

            sldrMusicVolume.setValue(SoundManager.musicVolume);
            sldrSfxVolume.setValue(SoundManager.sfxVolume);
        });

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX(SFX_BUTTON_CLICK);
            NavService.navigateTo(NavService.previousPage);
            SoundManager.saveAudioSettings(SoundManager.musicVolume, SoundManager.sfxVolume);
        });

        sldrMusicVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setMusicVolume(newVal.doubleValue());
        });

        sldrSfxVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSfxVolume(newVal.doubleValue());
        });
    }



}