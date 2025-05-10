package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.Database;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.application.Platform;

import java.awt.*;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class ConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgSettingsBtn;
    public ProgressBar pbUserExpLevel;
    public Label lblUserName, lblUserLevel;
    public ImageView imgLvl1, imgLvl2, imgLvl3, imgLvl4, imgLvl5, imgLvl6;

    private User currentUser = Eva.currentUser;

    // Level/exp handling and tracking to be implemented
    // Temporary variables for now
    private final int level = 1;
    private final double exp = 32.0;

    @FXML
    public void initialize() {

        // For testing purposes
        if (currentUser == null) {
            currentUser = new User(999);
            currentUser.username = "John Doe";
        }

        SoundManager.playBackgroundMusic("music/conquest_music.MP3", true);
        setupGlow(imgBackMenuBtn, imgSettingsBtn, imgLvl1, imgLvl2, imgLvl3);

        lblUserName.setText(currentUser.username);
        lblUserLevel.setText("Level " + level);
        pbUserExpLevel.setProgress(exp / 100);

        updateLevelCompletionStatus();

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic(() -> NavService.navigateTo(Selection));
            });
            fadeOut.play();
        });

        imgSettingsBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.pauseMusic();
            NavService.navigateTo(Settings);
            NavService.previousPage = Conquest;
        });
        
        // Arraylist
        imgLvl1.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic(() -> NavService.navigateTo(ArrayListConquest));
            });
            fadeOut.play();
        });
        
        // Stack
        imgLvl2.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic(() -> NavService.navigateTo(StackConquest));
            });
            fadeOut.play();
        });
        
        // Queue
        imgLvl3.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic(() -> NavService.navigateTo(QueueConquest));
            });
            fadeOut.play();
        });
    }

    private void updateLevelCompletionStatus() {
        if (Eva.completedLevels == null) {
            Eva.completedLevels = new java.util.HashSet<>();
        }

        Image finishedImage = new Image(getClass().getResource("/edu/citu/procrammers/eva/media/img_finished_level.png").toExternalForm());
        Image unfinishedImage = new Image(getClass().getResource("/edu/citu/procrammers/eva/media/img_unfinished_level.png").toExternalForm());

        if (Eva.completedLevels.contains(ArrayListConquest)) {
            imgLvl1.setImage(finishedImage);
        } else {
            imgLvl1.setImage(unfinishedImage);
        }

        if (Eva.completedLevels.contains(StackConquest)) {
            imgLvl2.setImage(finishedImage);
        } else {
            imgLvl2.setImage(unfinishedImage);
        }

        if (Eva.completedLevels.contains(QueueConquest)) {
            imgLvl3.setImage(finishedImage);
        } else {
            imgLvl3.setImage(unfinishedImage);
        }
    }
}
