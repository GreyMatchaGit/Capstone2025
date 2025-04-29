package edu.citu.procrammers.eva;

import edu.citu.procrammers.eva.data.AudioSettings;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.stage.Stage;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;

public class Eva extends Application {

    public static void main(String[] args) {
        AudioSettings settings = SoundManager.loadAudioSettings();
        SoundManager.setMusicVolume(settings.musicVolume);
        SoundManager.setSfxVolume(settings.sfxVolume);
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        new NavService(primaryStage);
//        NavService.navigateTo(Splash);
        NavService.navigateTo(Selection);
        NavService.setFullScreen(true);
        primaryStage.setMinWidth(640);
        primaryStage.setMinHeight(360);
//        lockAspectRatio(primaryStage);
        primaryStage.show();
    }

    private ChangeListener<? super Number> widthListener;
    private ChangeListener<? super Number> heightListener;

    private void lockAspectRatio(Stage primaryStage) {

        widthListener = (observable, oldValue, newValue) -> {
            primaryStage.heightProperty().removeListener(heightListener);
            primaryStage.setHeight(newValue.doubleValue() / 16.0 * 9.0);
            primaryStage.heightProperty().addListener(heightListener);
        };

        heightListener = (observable, oldValue, newValue) -> {
            primaryStage.widthProperty().removeListener(widthListener);
            primaryStage.setWidth(newValue.doubleValue() / 9.0 * 16.0);
            primaryStage.widthProperty().addListener(widthListener);
        };

        primaryStage.widthProperty().addListener(widthListener);
        primaryStage.heightProperty().addListener(heightListener);
    }
}