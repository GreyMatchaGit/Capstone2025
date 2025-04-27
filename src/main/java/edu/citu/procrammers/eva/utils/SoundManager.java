package edu.citu.procrammers.eva.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoundManager {
    private static MediaPlayer musicPlayer;
    private static double musicVolume = 1;

    private static final Map<String, Media> sfxCache = new HashMap<>();
    private static final List<MediaPlayer> activeSfxPlayers = new ArrayList<>();
    private static double sfxVolume = 1;
    private static final int MAX_CONCURRENT_SFX = 8;

    public static void playBackgroundMusic(String musicFile, Boolean loop) {
        try {
            if (musicPlayer != null) {
                musicPlayer.stop();
            }

            URL resource = SoundManager.class.getResource("/edu/citu/procrammers/eva/media/" + musicFile);
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

    public static void playSFX(String soundFile) {
        try {
            Media sound = getSoundFromCache(soundFile);
            if (sound == null) return;

            cleanupFinishedPlayers();

            if (activeSfxPlayers.size() >= MAX_CONCURRENT_SFX) {
                MediaPlayer oldest = activeSfxPlayers.remove(0);
                oldest.stop();
                oldest.dispose();
            }

            MediaPlayer sfxPlayer = new MediaPlayer(sound);
            sfxPlayer.setVolume(sfxVolume);

            sfxPlayer.setOnEndOfMedia(() -> {
                sfxPlayer.dispose();
                activeSfxPlayers.remove(sfxPlayer);
            });

            activeSfxPlayers.add(sfxPlayer);
            sfxPlayer.play();

        } catch (Exception e) {
            System.err.println("Error playing sound effect: " + e.getMessage());
        }
    }

    private static Media getSoundFromCache(String soundFile) {
        if (sfxCache.containsKey(soundFile)) {
            return sfxCache.get(soundFile);
        }

        URL resource = SoundManager.class.getResource("/edu/citu/procrammers/eva/media/" + soundFile);
        if (resource == null) {
            System.err.println("Sound file not found: " + soundFile);
            return null;
        }

        Media media = new Media(resource.toString());
        sfxCache.put(soundFile, media);
        return media;
    }

    private static void cleanupFinishedPlayers() {
        List<MediaPlayer> toRemove = new ArrayList<>();
        for (MediaPlayer player : activeSfxPlayers) {
            if (player.getStatus() == MediaPlayer.Status.STOPPED ||
                    player.getStatus() == MediaPlayer.Status.DISPOSED) {
                toRemove.add(player);
            }
        }

        for (MediaPlayer player : toRemove) {
            player.dispose();
        }
        activeSfxPlayers.removeAll(toRemove);
    }

    public static void setMusicVolume(double volume) {
        musicVolume = Math.max(0.0, Math.min(1.0, volume));
        if (musicPlayer != null) {
            musicPlayer.setVolume(musicVolume);
        }
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = Math.max(0.0, Math.min(1.0, volume));
        for (MediaPlayer player : activeSfxPlayers) {
            player.setVolume(sfxVolume);
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

    public static void shutdown() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
        }

        for (MediaPlayer player : activeSfxPlayers) {
            player.stop();
            player.dispose();
        }
        activeSfxPlayers.clear();
        sfxCache.clear();
    }

    public static void fadeOutMusic(Runnable onFinished) {
        if (musicPlayer == null || musicPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
            if (onFinished != null) {
                onFinished.run();
            }
            return;
        }

        final double startVolume = musicPlayer.getVolume();

        Timeline fadeOutTimeline = new Timeline();

        KeyFrame startFrame = new KeyFrame(Duration.ZERO,
                new KeyValue(musicPlayer.volumeProperty(), startVolume)
        );

        KeyFrame endFrame = new KeyFrame(Duration.millis(800),
                new KeyValue(musicPlayer.volumeProperty(), 0)
        );

        fadeOutTimeline.getKeyFrames().addAll(startFrame, endFrame);

        fadeOutTimeline.setOnFinished(event -> {
            musicPlayer.stop();
            musicPlayer.setVolume(musicVolume);
            if (onFinished != null) {
                onFinished.run();
            }
        });

        fadeOutTimeline.play();
    }

    public static void fadeOutMusic() {
        fadeOutMusic(null);
    }
}