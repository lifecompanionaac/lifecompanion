/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.lifecompanion.plugin.spellgame.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.controller.task.ExportGameResultTask;
import org.lifecompanion.plugin.spellgame.model.*;
import org.lifecompanion.plugin.spellgame.model.keyoption.CurrentWordDisplayKeyOption;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;
import java.util.function.Consumer;

/**
 *
 */
public enum SpellGameController implements ModeListenerI {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellGameController.class);

    public final static int WORD_MAX_SCORE = 10;
    private final static int ERROR_POINT_SUBTRACT = 2, WORD_MIN_SCORE = 1;

    public static final String
            VAR_ID_USER_SCORE = "SpellGameUserScore",
            VAR_ID_WORD_INDEX = "SpellGameWordIndex",
            VAR_ID_WORD_COUNT = "SpellGameWordCount",
            VAR_ID_CURRENT_STEP_INSTRUCTION_WITH_WORD = "SpellGameCurrentStepInstructionWithWord",
            VAR_ID_CURRENT_STEP_INSTRUCTION = "SpellGameCurrentStepInstruction";

    // Event listeners
    private final Set<Runnable> gameEndedListeners, gameStartedListeners;
    private final Set<Consumer<SpellGameStepResult>> answerListeners;

    // Current game
    private SpellGameWordList currentWordList;
    private List<String> words;
    private int currentWordIndex;
    private int userScore;
    private final List<SpellGameStepResult> currentGameAnswers;

    private int currentWordScore;
    private GameStep currentStep;

    private SpellGamePluginProperties currentSpellGamePluginProperties;
    private final List<CurrentWordDisplayKeyOption> wordDisplayKeyOptions;
    private final InvalidationListener textListener;

    private MediaPlayer playerSuccess, playerError, playerStartTyping;
    private LCConfigurationI configuration;

    SpellGameController() {
        wordDisplayKeyOptions = new ArrayList<>();
        currentGameAnswers = new ArrayList<>();
        textListener = inv -> {
            String text = WritingStateController.INSTANCE.currentTextProperty().get();
            if (StringUtils.isNotBlank(text) && text.endsWith("\n")) {
                // This is expected call (and not FXThreadUtils to directly be ran)
                // will avoid nested changes as this is called from a change listener and the called method create new changes.
                Platform.runLater(this::validateCurrentStepAndGoToNext);
            }
        };
        gameEndedListeners = new HashSet<>();
        gameStartedListeners = new HashSet<>();
        answerListeners = new HashSet<>();
    }

    // PROPS
    //========================================================================
    public int getUserScore() {
        return userScore;
    }

    public int getWordIndex() {
        return currentWordIndex + 1;
    }

    public int getWordCount() {
        return words != null ? words.size() : 0;
    }

    public String getCurrentStepInstruction() {
        return currentStep != null ? currentStep.getGeneralInstruction() : "";
    }

    public String getCurrentStepInstructionWithWord() {
        if (currentStep != null && words != null && currentWordIndex >= 0 && currentWordIndex < words.size()) {
            String currentWord = words.get(currentWordIndex);
            return currentStep.getInstruction(currentWord);
        } else return "";
    }
    //========================================================================

    // LISTENERS
    //========================================================================
    public void addGameEndedListener(Runnable listener) {
        this.gameEndedListeners.add(listener);
    }

    public void removeGameEndedListener(Runnable listener) {
        this.gameEndedListeners.remove(listener);
    }

    public void addGameStartedListener(Runnable listener) {
        this.gameStartedListeners.add(listener);
    }

    public void removeGameStartedListener(Runnable listener) {
        this.gameStartedListeners.remove(listener);
    }

    public void addAnswerListener(Consumer<SpellGameStepResult> listener) {
        this.answerListeners.add(listener);
    }

    public void removeAnswerListener(Consumer<SpellGameStepResult> listener) {
        this.answerListeners.remove(listener);
    }
    //========================================================================

    private boolean isGameRunning() {
        return AppModeController.INSTANCE.isUseMode() && currentWordList != null;
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentSpellGamePluginProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);

        // Find all the keys that can display the current word
        Map<GridComponentI, List<CurrentWordDisplayKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(CurrentWordDisplayKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).distinct().forEach(wordDisplayKeyOptions::add);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        endGame();
        this.currentSpellGamePluginProperties = null;
        wordDisplayKeyOptions.clear();
        this.configuration = null;
    }

    public void startGame(String id) {
        if (isGameRunning()) {
            endGame();
        }
        currentWordList = currentSpellGamePluginProperties.getWordListById(id);
        if (currentWordList != null) {
            playerSuccess = initPlayer("answer_good.wav", currentSpellGamePluginProperties.feedbacksVolumeProperty().get());
            playerError = initPlayer("answer_bad.mp3", currentSpellGamePluginProperties.feedbacksVolumeProperty().get());
            playerStartTyping = initPlayer("start_typing.wav", currentSpellGamePluginProperties.feedbacksVolumeProperty().get() * 0.75);
            if (currentSpellGamePluginProperties.validateWithEnterProperty().get()) {
                WritingStateController.INSTANCE.currentTextProperty().addListener(textListener);
            }
            userScore = 0;
            this.words = new ArrayList<>(currentWordList.getWords());
            if (currentWordList.shuffleWordsProperty().get()) {
                Collections.shuffle(words);
            }
            currentWordIndex = 0;
            currentStep = GameStepEnum.values()[0];
            this.currentWordScore = WORD_MAX_SCORE;
            this.currentGameAnswers.clear();
            WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
            gameStartedListeners.forEach(Runnable::run);
            startCurrentStep();
        }
    }


    public void endGame() {
        if (isGameRunning()) {
            WritingStateController.INSTANCE.currentTextProperty().removeListener(textListener);
            if (!CollectionUtils.isEmpty(currentGameAnswers)) {
                Date now = new Date();
                File destinationDirectory = new File(getResultBasePath(configuration) + File.separator + IOHelper.DATE_FORMAT_FILENAME_WITH_TIME_SECOND.format(now));

                ArrayList<SpellGameStepResult> answers = new ArrayList<>(this.currentGameAnswers);
                SpellGameResult spellGameResult = new SpellGameResult(
                        currentWordList.nameProperty().get(),
                        currentWordList.getWords().size(),
                        currentSpellGamePluginProperties.ignoreAccentsProperty().get(),
                        now,
                        answers.stream().mapToLong(SpellGameStepResult::timeSpent).sum(),
                        userScore,
                        currentWordIndex + 1,
                        answers
                );
                AsyncExecutorController.INSTANCE.addAndExecute(false, false,
                        new ExportGameResultTask(destinationDirectory, spellGameResult)
                );
            }
            this.currentWordIndex = 0;
            this.currentStep = null;
            this.words = null;
            this.userScore = 0;
            currentWordList = null;
            stopPlayerAndThen(playerSuccess, () -> playerSuccess = null);
            stopPlayerAndThen(playerError, () -> playerError = null);
            stopPlayerAndThen(playerStartTyping, () -> playerStartTyping = null);
            gameEndedListeners.forEach(Runnable::run);
        }
    }

    private void stopPlayerAndThen(MediaPlayer playerSuccess, Runnable then) {
        if (playerSuccess != null) {
            playerSuccess.stop();
            then.run();
        }
    }

    private void addGameAnswer(SpellGameStepResult spellGameStepResult) {
        currentGameAnswers.add(spellGameStepResult);
        answerListeners.forEach(c -> c.accept(spellGameStepResult));
    }

    public void validateCurrentStepAndGoToNext() {
        if (isGameRunning()) {
            // GameStep step = GameStepEnum.values()[currentStepIndex];
            String input = cleanText(WritingStateController.INSTANCE.currentTextProperty().get());
            String word = cleanText(words.get(currentWordIndex));
            WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);

            // If the user successfully enter the word : set the point and go to next word
            boolean success = currentStep.checkWord(currentSpellGamePluginProperties, removeAccentIfNeeded(word), removeAccentIfNeeded(input));
            addGameAnswer(new SpellGameStepResult(currentStep,
                    success ? SpellGameStepResultStatusEnum.SUCCESS : SpellGameStepResultStatusEnum.FAILED,
                    word,
                    input,
                    System.currentTimeMillis() - currentStepStartedAt));
            if (success) {
                showSuccessFeedback();
                GameStep stepOnSuccess = currentStep.getStepOnSuccess();
                if (stepOnSuccess != null) {
                    currentStep = stepOnSuccess;
                    startCurrentStep();
                } else {
                    endCurrentStepAndGoToNextWord(false);
                }
            }
            // User failed to enter the correct word
            else {
                showFailFeedback();
                currentStep = currentStep.getStepOnFail();
                this.currentWordScore = Math.max(WORD_MIN_SCORE, this.currentWordScore - ERROR_POINT_SUBTRACT);
                startCurrentStep();
            }
        }
    }

    private String removeAccentIfNeeded(String word) {
        return currentSpellGamePluginProperties.ignoreAccentsProperty().get() ? StringUtils.stripAccents(word) : word;
    }

    public void skipWord() {
        if (isGameRunning()) {
            String word = cleanText(words.get(currentWordIndex));
            addGameAnswer(new SpellGameStepResult(currentStep, SpellGameStepResultStatusEnum.UNDONE, word, "", System.currentTimeMillis() - currentStepStartedAt));
            endCurrentStepAndGoToNextWord(true);
        }
    }


    private void showSuccessFeedback() {
        if (currentSpellGamePluginProperties.enableFeedbackSoundProperty().get()) {
            playFromStart(playerSuccess);
        }
        wordDisplayKeyOptions.forEach(k -> k.answerDone(true));
    }

    private void showFailFeedback() {
        if (currentSpellGamePluginProperties.enableFeedbackSoundProperty().get()) {
            playFromStart(playerError);
        }
        wordDisplayKeyOptions.forEach(k -> k.answerDone(false));
    }

    private void endCurrentStepAndGoToNextWord(boolean skipped) {
        this.userScore += skipped ? WORD_MIN_SCORE : currentWordScore;
        this.currentWordScore = WORD_MAX_SCORE;
        UseVariableController.INSTANCE.requestVariablesUpdate();
        currentStep = GameStepEnum.values()[0];
        if (currentWordIndex < words.size() - 1) {
            currentWordIndex++;
            startCurrentStep();
        } else {
            endGame();
        }
    }

    private long currentStepStartedAt;

    private void startCurrentStep() {
        String currentWord = words.get(currentWordIndex);
        GameStep step = currentStep;
        StepDisplayMode displayMode = step.getDisplayMode();

        // Speak instruction and then inform the user that it's possible to type
        VoiceSynthesizerController.INSTANCE.speakAsync(
                step.getInstruction(currentWord),
                AppModeController.INSTANCE.getUseModeContext().configurationProperty().get().getVoiceSynthesizerParameter(),
                () -> {
                    if (isGameRunning() && displayMode != StepDisplayMode.TIMER) playFromStart(playerStartTyping);
                });
        currentStepStartedAt = System.currentTimeMillis();


        // Show word when needed
        if (displayMode == StepDisplayMode.SHOWN || displayMode == StepDisplayMode.TIMER) {
            wordDisplayKeyOptions.forEach(currentWordDisplayKeyOption -> currentWordDisplayKeyOption.showWord(currentWord));
        } else if (displayMode == StepDisplayMode.HIDDEN) {
            wordDisplayKeyOptions.forEach(CurrentWordDisplayKeyOption::hideWord);
        }
        // If the step is a timer step, we should block user input word is hidden
        if (displayMode == StepDisplayMode.TIMER) {
            WritingStateController.INSTANCE.writingDisabledProperty().set(true);
            LCNamedThreadFactory.daemonThreadFactory("SpellGame").newThread(() -> {
                ThreadUtils.safeSleep(currentSpellGamePluginProperties.wordDisplayTimeInMsProperty().get());
                // As this is async, we should ensure that we are still in use mode at the end of the sleep
                if (isGameRunning()) {
                    wordDisplayKeyOptions.forEach(CurrentWordDisplayKeyOption::hideWord);
                    WritingStateController.INSTANCE.writingDisabledProperty().set(false);
                    playFromStart(playerStartTyping);
                    currentStepStartedAt = System.currentTimeMillis();
                }
            }).start();
        }
    }

    // IO
    //========================================================================
    public static File getResultBasePath(LCConfigurationI configuration) {
        return new File(IOHelper.getConfigurationDirectoryPath(
                ProfileController.INSTANCE.currentProfileProperty().get().getID(), configuration.getID())
                + "plugins" + File.separator
                + SpellGamePlugin.ID + File.separator + "results");
    }
    //========================================================================

    // UTILS
    //========================================================================
    private static String cleanText(String text) {
        return StringUtils.toLowerCase(StringUtils.trimToEmpty(text));
    }
    //========================================================================

    // SOUND
    //========================================================================
    public void playFromStart(MediaPlayer mediaPlayer) {
        if (mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED || mediaPlayer.getStatus() == MediaPlayer.Status.READY) {
            mediaPlayer.play();
        } else {
            mediaPlayer.setOnStopped(() -> {
                mediaPlayer.setOnStopped(null);
                mediaPlayer.play();
            });
            mediaPlayer.stop();
        }
    }

    public MediaPlayer initPlayer(String soundName, double volume) {
        File destAlarm = new File(org.lifecompanion.util.IOUtils.getTempDir("sound") + File.separator + soundName);
        IOUtils.createParentDirectoryIfNeeded(destAlarm);
        try (FileOutputStream fos = new FileOutputStream(destAlarm)) {
            try (InputStream is = ResourceHelper.getInputStreamForPath("/sounds/" + soundName)) {
                IOUtils.copyStream(is, fos);
                Media media = new Media(destAlarm.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(volume);
                return mediaPlayer;
            }
        } catch (Exception e) {
            LOGGER.error("Problem load sound", e);
            return null;
        }
    }


    //========================================================================


}
