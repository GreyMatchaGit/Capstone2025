package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
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
            NavService.previousPage = ArrayListConquest;
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
        timerTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, e -> {}),
            new KeyFrame(Duration.seconds(40), e -> {
                if (pbTimeLimit.getProgress() <= 0) {
                    timeExpired();
                }
            })
        );
        
        timerTimeline.setCycleCount(Timeline.INDEFINITE);
        timerTimeline.getKeyFrames().add(
            new KeyFrame(Duration.millis(200), event -> {
                pbTimeLimit.setProgress(pbTimeLimit.getProgress() - 0.005);
            })
        );
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
        
        if (victory) {
            SoundManager.playSFX("sfx/btn_click.MP3");
            taNarration.clear();
            taNarration.appendText("The wizard's staff crumbles, and the skies clear. You've broken the final chant. The Kingdom of EVA is safe... for now.\n");

            // TODO: Award experience, save progress
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