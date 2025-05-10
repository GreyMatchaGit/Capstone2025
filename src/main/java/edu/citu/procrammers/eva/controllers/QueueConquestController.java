package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class QueueConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgSettingsBtn;
    public ProgressBar pbUserExpLevel, pbTimeLimit;
    public Label lblUserName, lblUserLevel;
    public Label lblTargetQueue, lblCurrentQueue;
    public TextField tfSpellCommand;
    public Button btnDispel;
    public TextArea taNarration;
    public HBox hbHealthContainer;
    public Label lblRoundCounter;
    
    private User currentUser = Eva.currentUser;
    private final int MAX_HP = 4;
    private final int MAX_ROUNDS = 5;
    private SimpleIntegerProperty currentHP = new SimpleIntegerProperty(MAX_HP);
    private SimpleIntegerProperty currentRound = new SimpleIntegerProperty(1);
    private List<ImageView> healthIcons = new ArrayList<>();
    
    private Queue<String> targetQueue;
    private Queue<String> currentQueue;
    private Timeline timerTimeline;
    private boolean gameActive = false;

    // Level/exp handling, to be implemented later (hopefully)
    private final int level = 1;
    private final double exp = 1.0;
    
    // Game data
    private List<ConquestRound> rounds;
    private int currentIndex = 0;

    @FXML
    public void initialize() {
        if (currentUser == null) {
            currentUser = new User(999);
            currentUser.username = "John Doe";
        }

        SoundManager.playBackgroundMusic("music/conquest_battle_music.MP3", true);
        setupGlow(imgBackMenuBtn, imgSettingsBtn, btnDispel);

        lblUserName.setText(currentUser.username);
        lblUserLevel.setText("Level " + level);
        pbUserExpLevel.setProgress(exp / 100);

        setupHealthIcons();

        initializeGameRounds();

        setupNewRound();

        imgBackMenuBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            stopTimer();
            
            FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
            fadeOut.setFromValue(0);
            fadeOut.setToValue(1);
            fadeOut.setOnFinished(event -> {
                SoundManager.fadeOutMusic(() -> NavService.navigateTo(Conquest));
            });
            fadeOut.play();
        });

        imgSettingsBtn.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            SoundManager.pauseMusic();
            stopTimer();
            NavService.navigateTo(Settings);
            NavService.previousPage = QueueConquest;
        });
        
        btnDispel.setOnMouseClicked(e -> {
            processSpellCommand();
        });
        
        tfSpellCommand.setOnAction(e -> {
            processSpellCommand();
        });

        lblRoundCounter.textProperty().bind(new SimpleStringProperty("Round ").concat(currentRound).concat("/").concat(MAX_ROUNDS));
    }
    
    private void setupHealthIcons() {
        for (int i = 0; i < MAX_HP; i++) {
            try {
                ImageView heart = new ImageView(getClass().getResource("/edu/citu/procrammers/eva/media/img_heart.png").toExternalForm());
                heart.setFitHeight(40);
                heart.setFitWidth(40);
                healthIcons.add(heart);
                hbHealthContainer.getChildren().add(heart);
            } catch (Exception e) {
                System.err.println("Error loading heart image: " + e.getMessage());
            }
        }
        
        updateHealthDisplay();
    }
    
    private void updateHealthDisplay() {
        for (int i = 0; i < healthIcons.size(); i++) {
            healthIcons.get(i).setOpacity(i < currentHP.get() ? 1.0 : 0.3);
        }
    }
    
    private void initializeGameRounds() {
        rounds = new ArrayList<>();
        
        // Round 1
        rounds.add(new ConquestRound(
            createQueue("fire"),
            createQueue("fire", "water", "earth")
        ));
        
        // Round 2
        rounds.add(new ConquestRound(
            createQueue("potion"),
            createQueue("potion", "scroll", "wand")
        ));
        
        // Round 3
        rounds.add(new ConquestRound(
            createQueue("dragon", "knight"),
            createQueue("dragon", "knight", "wizard")
        ));
        
        // Round 4
        rounds.add(new ConquestRound(
            createQueue("ruby", "emerald"),
            createQueue("ruby", "emerald", "sapphire")
        ));

        // Round 5
        rounds.add(new ConquestRound(
            createQueue("shield"),
            createQueue("shield", "sword", "dagger")
        ));
    }
    
    private Queue<String> createQueue(String... elements) {
        Queue<String> queue = new LinkedList<>();
        for (String element : elements) {
            queue.add(element);
        }
        return queue;
    }
    
    private void setupNewRound() {
        if (currentIndex < rounds.size()) {
            ConquestRound round = rounds.get(currentIndex);
            currentQueue = new LinkedList<>();
            targetQueue = new LinkedList<>();

            copyQueue(round.getStartQueue(), currentQueue);
            copyQueue(round.getTargetQueue(), targetQueue);
            
            updateQueueDisplay();
            startTimer();
            
            taNarration.clear();
            taNarration.appendText("The Spellbinder's scroll glows with unstable magic...\n");
            taNarration.appendText("Transform the spell using queue operations: enqueue(element), dequeue(), front()");
            
            gameActive = true;
        } else {
            endGame(true);
        }
    }
    
    private void copyQueue(Queue<String> source, Queue<String> destination) {
        ArrayList<String> temp = new ArrayList<>(source);
        source.clear();

        for (String element : temp) {
            source.add(element);
            destination.add(element);
        }
    }
    
    private void updateQueueDisplay() {
        lblCurrentQueue.setText(formatQueueDisplay(currentQueue));
        lblTargetQueue.setText(formatQueueDisplay(targetQueue));
    }
    
    private String formatQueueDisplay(Queue<String> queue) {
        if (queue.isEmpty()) {
            return "[]";
        }

        // Queue to list para sa display
        List<String> tempList = new ArrayList<>(queue);

        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < tempList.size(); i++) {
            sb.append("\"").append(tempList.get(i)).append("\"");
            if (i < tempList.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private void startTimer() {
        stopTimer();
        
        pbTimeLimit.setProgress(1.0);

        int totalFrames = 40 * 25;  // 40 seconds * 25 fps
        double decrementPerFrame = 1.0 / totalFrames;
        
        timerTimeline = new Timeline();
        timerTimeline.setCycleCount(totalFrames);

        KeyFrame keyFrame = new KeyFrame(Duration.millis(40), event -> {
            double newProgress = pbTimeLimit.getProgress() - decrementPerFrame;
            pbTimeLimit.setProgress(newProgress);

            if (newProgress <= 0) {
                timeExpired();
            }
        });
        
        timerTimeline.getKeyFrames().add(keyFrame);
        timerTimeline.setOnFinished(event -> {
            if (gameActive) {
                timeExpired();
            }
        });
        
        timerTimeline.play();
    }
    
    private void stopTimer() {
        if (timerTimeline != null) {
            timerTimeline.stop();
        }
    }
    
    private void timeExpired() {
        if (!gameActive) return;
        
        SoundManager.playSFX("sfx/damage_taken.MP3");
        stopTimer();
        
        taNarration.appendText("\nThe curse mist engulfs you! Your magical shield shatters!\n");
        decreaseHealth();
        
        if (currentHP.get() > 0) {
            taNarration.appendText("Prepare for the next challenge!\n");
            currentIndex++;
            currentRound.set(currentRound.get() + 1);

            Timeline nextRoundDelay = new Timeline(
                new KeyFrame(Duration.seconds(4), event -> {
                    setupNewRound();
                })
            );
            nextRoundDelay.play();
        } else {
            endGame(false);
        }
    }
    
    private void processSpellCommand() {
        if (!gameActive) return;
        
        String command = tfSpellCommand.getText().trim();
        tfSpellCommand.clear();
        
        if (command.isEmpty()) {
            return;
        }
        
        try {
            boolean operationSuccess = executeSpellOperation(command);
            
            if (!operationSuccess) {
                SoundManager.playSFX("sfx/damage_taken.MP3");
                taNarration.appendText("\nInvalid spell operation! Your ward flickers — the spell misfires!\n");
                decreaseHealth();
                
                if (currentHP.get() <= 0) {
                    endGame(false);
                }
                return;
            }

            SoundManager.playSFX("sfx/attack.MP3");
            
            updateQueueDisplay();

            if (formatQueueDisplay(currentQueue).equals(formatQueueDisplay(targetQueue))) {
                stopTimer();
                gameActive = false;
                
                taNarration.appendText("\nThe corrupted chant dissipates — well done, Spellbreaker!\n");
                
                currentIndex++;
                
                if (currentIndex < MAX_ROUNDS) {
                    currentRound.set(currentRound.get() + 1);
                    taNarration.appendText("Prepare for the next spell...\n");

                    Timeline nextRoundDelay = new Timeline(
                        new KeyFrame(Duration.seconds(4), event -> {
                            setupNewRound();
                        })
                    );
                    nextRoundDelay.play();
                } else {
                    endGame(true);
                }
            }
        } catch (Exception e) {
            SoundManager.playSFX("sfx/damage_taken.MP3");
            taNarration.appendText("\nYour ward flickers — the spell misfires!\n");
            decreaseHealth();
            
            if (currentHP.get() <= 0) {
                endGame(false);
            }
        }
    }
    
    private boolean executeSpellOperation(String command) {
        command = command.replaceAll("\\s+", "");
        
        if (command.startsWith("enqueue(") && command.endsWith(")")) {
            String element = command.substring(8, command.length() - 1).replace("\"", "");
            currentQueue.add(element);
            return true;
        } else if (command.equals("dequeue()")) {
            if (currentQueue.isEmpty()) return false;
            currentQueue.poll(); // poll() is the same as dequeue
            return true;
        } else if (command.equals("front()")) {
            if (currentQueue.isEmpty()) return false;
            String front = currentQueue.peek(); // peek() is the same as front
            taNarration.appendText("\nFront element: \"" + front + "\"\n");
            return true;
        }
        
        return false;
    }
    
    private void decreaseHealth() {
        currentHP.set(currentHP.get() - 1);
        updateHealthDisplay();
    }
    
    private void endGame(boolean victory) {
        stopTimer();
        gameActive = false;
        
        if (victory) {
            SoundManager.playSFX("sfx/btn_click.MP3");
            taNarration.clear();
            taNarration.appendText("The wizard's staff crumbles, and the skies clear. You've broken the final chant. The Kingdom of EVA is safe... for now.\n");

            Eva.completedLevels.add(QueueConquest);
        } else {
            SoundManager.playSFX("sfx/btn_click.MP3");
            taNarration.clear();
            taNarration.appendText("The Spellbinder's magic overwhelms you. Your wards shatter completely, and darkness falls...\n");
            taNarration.appendText("But do not lose heart, brave one. Return when you've recovered to challenge the duel again.\n");
        }

        Timeline returnDelay = new Timeline(
            new KeyFrame(Duration.seconds(5), event -> {
                FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
                fadeOut.setFromValue(0);
                fadeOut.setToValue(1);
                fadeOut.setOnFinished(e -> {
                    SoundManager.fadeOutMusic(() -> NavService.navigateTo(Conquest));
                });
                fadeOut.play();
            })
        );
        returnDelay.play();
    }

    private static class ConquestRound {
        private final Queue<String> startQueue;
        private final Queue<String> targetQueue;
        
        public ConquestRound(Queue<String> startQueue, Queue<String> targetQueue) {
            this.startQueue = startQueue;
            this.targetQueue = targetQueue;
        }
        
        public Queue<String> getStartQueue() {
            return startQueue;
        }
        
        public Queue<String> getTargetQueue() {
            return targetQueue;
        }
    }
} 