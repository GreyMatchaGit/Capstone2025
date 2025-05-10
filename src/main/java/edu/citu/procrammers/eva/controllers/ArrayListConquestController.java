package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class ArrayListConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgSettingsBtn;
    public ProgressBar pbUserExpLevel, pbTimeLimit;
    public Label lblUserName, lblUserLevel;
    public Label lblTargetArrayList, lblCurrentArrayList;
    public TextField tfSpellCommand;
    public Button btnDispel;
    public TextArea taNarration;
    public HBox hbHealthContainer;
    public Label lblRoundCounter;
    
    // New UI elements for victory/lose screen
    public StackPane spGameStatusPane;
    public StackPane spVictoryContainer;
    public StackPane spLoseContainer;
    public ImageView imgVictorySunrays;
    public ImageView imgVictoryTitle;
    public ImageView imgLoseTitle;
    public Button btnRetry;
    
    private User currentUser = Eva.currentUser;
    private final int MAX_HP = 4;
    private final int MAX_ROUNDS = 5;
    private SimpleIntegerProperty currentHP = new SimpleIntegerProperty(MAX_HP);
    private SimpleIntegerProperty currentRound = new SimpleIntegerProperty(1);
    private List<ImageView> healthIcons = new ArrayList<>();
    
    private ArrayList<String> targetList;
    private ArrayList<String> currentList;
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
        setupGlow(imgBackMenuBtn, imgSettingsBtn, btnDispel, btnRetry);

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
            NavService.previousPage = ArrayListConquest;
        });
        
        btnDispel.setOnMouseClicked(e -> {
            processSpellCommand();
        });
        
        tfSpellCommand.setOnAction(e -> {
            processSpellCommand();
        });
        
        // Set up retry button
        btnRetry.setOnMouseClicked(e -> {
            SoundManager.playSFX("sfx/btn_click.MP3");
            retryLevel();
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
            new ArrayList<>(java.util.Arrays.asList("fire", "water")),
            new ArrayList<>(java.util.Arrays.asList("fire", "water", "earth", "air", "light"))
        ));
        
        // Round 2
        rounds.add(new ConquestRound(
            new ArrayList<>(java.util.Arrays.asList("potion", "scroll")),
            new ArrayList<>(java.util.Arrays.asList("wand", "potion", "scroll", "rune"))
        ));
        
        // Round 3
        rounds.add(new ConquestRound(
            new ArrayList<>(java.util.Arrays.asList("dragon", "knight")),
            new ArrayList<>(java.util.Arrays.asList("knight", "wizard", "dragon"))
        ));
        
        // Round 4
        rounds.add(new ConquestRound(
            new ArrayList<>(java.util.Arrays.asList("ruby", "emerald", "diamond")),
            new ArrayList<>(java.util.Arrays.asList("sapphire", "ruby", "emerald"))
        ));

        // Round 5
        rounds.add(new ConquestRound(
            new ArrayList<>(java.util.Arrays.asList("shield", "sword")),
            new ArrayList<>(java.util.Arrays.asList("bow", "shield", "sword", "dagger", "staff"))
        ));
    }
    
    private void setupNewRound() {
        // Hide the game status pane if it's visible
        spGameStatusPane.setVisible(false);
        
        if (currentIndex < rounds.size()) {
            ConquestRound round = rounds.get(currentIndex);
            currentList = new ArrayList<>(round.getStartList());
            targetList = new ArrayList<>(round.getTargetList());
            
            updateListDisplay();
            startTimer();
            
            taNarration.clear();
            taNarration.appendText("The Spellbinder's scroll glows with unstable magic...\n");
            taNarration.appendText("Transform the spell using operations: addFirst(element), addLast(element), removeFirst(), removeLast(), get(index), search(element)");
            
            gameActive = true;
        } else {
            endGame(true);
        }
    }
    
    private void updateListDisplay() {
        lblCurrentArrayList.setText(formatArrayListDisplay(currentList));
        lblTargetArrayList.setText(formatArrayListDisplay(targetList));
    }
    
    private String formatArrayListDisplay(ArrayList<String> list) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
    
    private void startTimer() {
        stopTimer();
        
        pbTimeLimit.setProgress(1.0);
        
        // Calculate number of frames for smooth animation (25 frames per second)
        int totalFrames = 40 * 25;  // 40 seconds * 25 fps
        double decrementPerFrame = 1.0 / totalFrames;
        
        timerTimeline = new Timeline();
        timerTimeline.setCycleCount(totalFrames);
        
        // Add frame for decrementing progress
        KeyFrame keyFrame = new KeyFrame(Duration.millis(40), event -> {
            double newProgress = pbTimeLimit.getProgress() - decrementPerFrame;
            pbTimeLimit.setProgress(newProgress);
            
            // Check if we've reached the end
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
            
            updateListDisplay();

            if (currentList.equals(targetList)) {
                stopTimer();
                gameActive = false;
                
                taNarration.appendText("\nThe corrupted chant dissipates — well done, Spellbreaker!\n");
                
                currentIndex++;
                
                if (currentIndex < MAX_ROUNDS) {
                    currentRound.set(currentRound.get() + 1);
                    taNarration.appendText("Prepare for the next spell...\n");

                    // Play next round sound effect instead of btn_click
                    SoundManager.playSFX("sfx/next-round.MP3");
                    
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
        
        if (command.startsWith("addFirst(") && command.endsWith(")")) {
            String element = command.substring(9, command.length() - 1).replace("\"", "");
            currentList.add(0, element);
            return true;
        } else if (command.startsWith("addLast(") && command.endsWith(")")) {
            String element = command.substring(8, command.length() - 1).replace("\"", "");
            currentList.add(element);
            return true;
        } else if (command.equals("removeFirst()")) {
            if (currentList.isEmpty()) return false;
            currentList.remove(0);
            return true;
        } else if (command.equals("removeLast()")) {
            if (currentList.isEmpty()) return false;
            currentList.remove(currentList.size() - 1);
            return true;
        } else if (command.startsWith("get(") && command.endsWith(")")) {
            try {
                int index = Integer.parseInt(command.substring(4, command.length() - 1));
                if (index < 0 || index >= currentList.size()) return false;
                taNarration.appendText("\nElement at index " + index + ": " + currentList.get(index) + "\n");
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        } else if (command.startsWith("search(") && command.endsWith(")")) {
            String element = command.substring(7, command.length() - 1).replace("\"", "");
            int index = currentList.indexOf(element);
            taNarration.appendText("\nElement \"" + element + "\" found at index: " + (index == -1 ? "not found" : index) + "\n");
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
        
        // Show game status pane
        spGameStatusPane.setVisible(true);
        
        if (victory) {
            // Play victory sound effect
            SoundManager.playSFX("sfx/victory.MP3");
            
            // Show victory container and hide lose container
            spVictoryContainer.setVisible(true);
            spLoseContainer.setVisible(false);
            
            // Apply victory animations
            applyVictoryAnimations();
            
            // Mark level as completed
            Eva.completedLevels.add(ArrayListConquest);
            
            // Clear narration and add victory message
            taNarration.clear();
            taNarration.appendText("The wizard's staff crumbles, and the skies clear. You've broken the final chant. The Kingdom of EVA is safe... for now.\n");
        } else {
            // Play lose sound effect
            SoundManager.playSFX("sfx/lose.MP3");
            
            // Show lose container and hide victory container
            spLoseContainer.setVisible(true);
            spVictoryContainer.setVisible(false);
            
            // Apply lose animations
            applyLoseAnimations();
            
            // Clear narration and add defeat message
            taNarration.clear();
            taNarration.appendText("The Spellbinder's magic overwhelms you. Your wards shatter completely, and darkness falls...\n");
            taNarration.appendText("But do not lose heart, brave one. Return when you've recovered to challenge the duel again.\n");
        }

        // Delay before returning to conquest screen
        Timeline returnDelay = new Timeline(
            new KeyFrame(Duration.seconds(8), event -> {
                if (victory) {
                    // Return to conquest view after showing victory screen
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
                    fadeOut.setFromValue(0);
                    fadeOut.setToValue(1);
                    fadeOut.setOnFinished(e -> {
                        SoundManager.fadeOutMusic(() -> NavService.navigateTo(Conquest));
                    });
                    fadeOut.play();
                }
                // For defeat, don't automatically return - let player click retry or back button
            })
        );
        returnDelay.play();
    }
    
    /**
     * Apply animations to victory screen elements
     */
    private void applyVictoryAnimations() {
        // Create rotation animation for sunrays image
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(10), imgVictorySunrays);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotateTransition.play();
        
        // Create scale animation for victory title
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1.5), imgVictoryTitle);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
    
    /**
     * Apply animations to lose screen elements
     */
    private void applyLoseAnimations() {
        // Create subtle scale animation for lose title
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), imgLoseTitle);
        scaleTransition.setFromX(0.95);
        scaleTransition.setFromY(0.95);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }
    
    /**
     * Retry the current level
     */
    private void retryLevel() {
        // Reset health
        currentHP.set(MAX_HP);
        updateHealthDisplay();
        
        // Reset round counter
        currentRound.set(1);
        
        // Reset game index
        currentIndex = 0;
        
        // Hide game status pane
        spGameStatusPane.setVisible(false);
        
        // Setup new round
        setupNewRound();
    }

    private static class ConquestRound {
        private final ArrayList<String> startList;
        private final ArrayList<String> targetList;
        
        public ConquestRound(ArrayList<String> startList, ArrayList<String> targetList) {
            this.startList = startList;
            this.targetList = targetList;
        }
        
        public ArrayList<String> getStartList() {
            return new ArrayList<>(startList);
        }
        
        public ArrayList<String> getTargetList() {
            return new ArrayList<>(targetList);
        }
    }
} 