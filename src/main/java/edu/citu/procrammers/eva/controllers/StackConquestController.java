package edu.citu.procrammers.eva.controllers;

import edu.citu.procrammers.eva.Eva;
import edu.citu.procrammers.eva.data.User;
import edu.citu.procrammers.eva.utils.NavService;
import edu.citu.procrammers.eva.utils.SoundManager;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
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
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static edu.citu.procrammers.eva.utils.Constant.Page.*;
import static edu.citu.procrammers.eva.utils.UIElementUtils.setupGlow;

public class StackConquestController {
    public Pane fadePane;
    public ImageView imgBackMenuBtn, imgSettingsBtn;
    public ProgressBar pbUserExpLevel, pbTimeLimit;
    public Label lblUserName, lblUserLevel;
    public Label lblTargetStack, lblCurrentStack;
    public TextField tfSpellCommand;
    public Button btnDispel;
    public TextArea taNarration;
    public HBox hbHealthContainer;
    public Label lblRoundCounter;

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
    
    private Stack<String> targetStack;
    private Stack<String> currentStack;
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
            NavService.previousPage = StackConquest;
        });
        
        btnDispel.setOnMouseClicked(e -> {
            processSpellCommand();
        });
        
        tfSpellCommand.setOnAction(e -> {
            processSpellCommand();
        });

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
            createStack("fire"),
            createStack("fire", "water", "earth")
        ));
        
        // Round 2
        rounds.add(new ConquestRound(
            createStack("potion"),
            createStack("scroll", "potion", "wand")
        ));
        
        // Round 3
        rounds.add(new ConquestRound(
            createStack("dragon", "knight"),
            createStack("wizard", "dragon")
        ));
        
        // Round 4
        rounds.add(new ConquestRound(
            createStack("ruby"),
            createStack("diamond", "emerald", "ruby")
        ));

        // Round 5
        rounds.add(new ConquestRound(
            createStack("shield", "sword"),
            createStack("dagger", "staff", "bow")
        ));
    }
    
    private Stack<String> createStack(String... elements) {
        Stack<String> stack = new Stack<>();
        for (int i = elements.length - 1; i >= 0; i--) {
            stack.push(elements[i]);
        }
        return stack;
    }
    
    private void setupNewRound() {
        spGameStatusPane.setVisible(false);
        
        if (currentIndex < rounds.size()) {
            ConquestRound round = rounds.get(currentIndex);
            currentStack = new Stack<>();
            targetStack = new Stack<>();

            copyStack(round.getStartStack(), currentStack);
            copyStack(round.getTargetStack(), targetStack);
            
            updateStackDisplay();
            startTimer();
            
            taNarration.clear();
            taNarration.appendText("The Spellbinder's scroll glows with unstable magic...\n");
            taNarration.appendText("Transform the spell using stack operations: push(element), pop(), top()");
            
            gameActive = true;
        } else {
            endGame(true);
        }
    }
    
    private void copyStack(Stack<String> source, Stack<String> destination) {
        ArrayList<String> temp = new ArrayList<>();
        while (!source.isEmpty()) {
            temp.add(source.pop());
        }
        for (int i = temp.size() - 1; i >= 0; i--) {
            String element = temp.get(i);
            source.push(element);
            destination.push(element);
        }
    }
    
    private void updateStackDisplay() {
        lblCurrentStack.setText(formatStackDisplay(currentStack));
        lblTargetStack.setText(formatStackDisplay(targetStack));
    }
    
    private String formatStackDisplay(Stack<String> stack) {
        if (stack.isEmpty()) {
            return "[]";
        }

        ArrayList<String> tempList = new ArrayList<>();
        Stack<String> tempStack = new Stack<>();

        while (!stack.isEmpty()) {
            tempStack.push(stack.pop());
        }

        while (!tempStack.isEmpty()) {
            String element = tempStack.pop();
            tempList.add(element);
            stack.push(element);
        }

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
            
            updateStackDisplay();

            if (formatStackDisplay(currentStack).equals(formatStackDisplay(targetStack))) {
                stopTimer();
                gameActive = false;
                
                taNarration.appendText("\nThe corrupted chant dissipates — well done, Spellbreaker!\n");
                
                currentIndex++;
                
                if (currentIndex < MAX_ROUNDS) {
                    currentRound.set(currentRound.get() + 1);
                    taNarration.appendText("Prepare for the next spell...\n");

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
        
        if (command.startsWith("push(") && command.endsWith(")")) {
            String element = command.substring(5, command.length() - 1).replace("\"", "");
            currentStack.push(element);
            return true;
        } else if (command.equals("pop()")) {
            if (currentStack.isEmpty()) return false;
            currentStack.pop();
            return true;
        } else if (command.equals("top()")) {
            if (currentStack.isEmpty()) return false;
            String top = currentStack.peek();
            taNarration.appendText("\nTop element: \"" + top + "\"\n");
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

        spGameStatusPane.setVisible(true);
        
        if (victory) {
            SoundManager.playSFX("sfx/victory.MP3");

            spVictoryContainer.setVisible(true);
            spLoseContainer.setVisible(false);

            applyVictoryAnimations();

            Eva.completedLevels.add(StackConquest);

            taNarration.clear();
            taNarration.appendText("The wizard's staff crumbles, and the skies clear. You've broken the final chant. The Kingdom of EVA is safe... for now.\n");
        } else {
            SoundManager.playSFX("sfx/lose.MP3");

            spLoseContainer.setVisible(true);
            spVictoryContainer.setVisible(false);

            applyLoseAnimations();

            taNarration.clear();
            taNarration.appendText("The Spellbinder's magic overwhelms you. Your wards shatter completely, and darkness falls...\n");
            taNarration.appendText("But do not lose heart, brave one. Return when you've recovered to challenge the duel again.\n");
        }

        Timeline returnDelay = new Timeline(
            new KeyFrame(Duration.seconds(8), event -> {
                if (victory) {
                    FadeTransition fadeOut = new FadeTransition(Duration.seconds(1), fadePane);
                    fadeOut.setFromValue(0);
                    fadeOut.setToValue(1);
                    fadeOut.setOnFinished(e -> {
                        SoundManager.fadeOutMusic(() -> NavService.navigateTo(Conquest));
                    });
                    fadeOut.play();
                }
            })
        );
        returnDelay.play();
    }

    private void applyVictoryAnimations() {
        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(10), imgVictorySunrays);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.LINEAR);
        rotateTransition.play();

        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1.5), imgVictoryTitle);
        scaleTransition.setFromX(0.8);
        scaleTransition.setFromY(0.8);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private void applyLoseAnimations() {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(1), imgLoseTitle);
        scaleTransition.setFromX(0.95);
        scaleTransition.setFromY(0.95);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    private void retryLevel() {
        currentHP.set(MAX_HP);
        updateHealthDisplay();

        currentRound.set(1);

        currentIndex = 0;

        spGameStatusPane.setVisible(false);

        setupNewRound();
    }

    private static class ConquestRound {
        private final Stack<String> startStack;
        private final Stack<String> targetStack;
        
        public ConquestRound(Stack<String> startStack, Stack<String> targetStack) {
            this.startStack = startStack;
            this.targetStack = targetStack;
        }
        
        public Stack<String> getStartStack() {
            return startStack;
        }
        
        public Stack<String> getTargetStack() {
            return targetStack;
        }
    }
} 