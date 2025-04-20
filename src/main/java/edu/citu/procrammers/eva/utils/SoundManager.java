package edu.citu.procrammers.eva.utils;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.net.URL;

public class SoundManager {
    private static MediaPlayer musicPlayer;
    private static double musicVolume = 1;

    public static void playBackgroundMusic(String musicFile, Boolean loop) {
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }

            URL resource = SoundManager.class.getResource("/edu/citu/procrammers/eva/media/sfx/" + musicFile);
            if (resource == null) {
                System.err.println("Music file not found: " + musicFile);
                return;
            }

            Media media = new Media(resource.toString());
            musicPlayer = new MediaPlayer(media);

            musicPlayer.setVolume(musicVolume);
            if (loop) {
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            }
            musicPlayer.play();

        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void setMusicVolume(double volume) {
        musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (musicPlayer != null) {
            musicPlayer.setVolume(musicVolume);
        }
    }

    public static void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
        }
    }

    public static void pauseMusic() {
        if (musicPlayer != null) {
            musicPlayer.pause();
        }
    }

    public static void resumeMusic() {
        if (musicPlayer != null) {
            musicPlayer.play();
        }
    }
}