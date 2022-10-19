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
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.controller.task.ExportGameResultTask;
import org.lifecompanion.plugin.spellgame.model.GameStep;
import org.lifecompanion.plugin.spellgame.model.GameStepEnum;
import org.lifecompanion.plugin.spellgame.model.SpellGameStepResult;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.model.keyoption.CurrentWordDisplayKeyOption;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.*;

public enum SpellGameController implements ModeListenerI {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(SpellGameController.class);


    private List<String> inputList = Arrays.asList("vélo", "maison", "papa", "maman");

    public static final String
            VAR_ID_USER_SCORE = "SpellGameUserScore",
            VAR_ID_WORD_INDEX = "SpellGameWordIndex",
            VAR_ID_WORD_COUNT = "SpellGameWordCount";

    private List<String> currentWordList;
    private int currentWordIndex;
    private int currentStepIndex;
    private int userScore;

    private SpellGamePluginProperties currentSpellGamePluginProperties;
    private final List<CurrentWordDisplayKeyOption> wordDisplayKeyOptions;
    private final InvalidationListener textListener;
    private final List<SpellGameStepResult> currentGameAnswers;

    private MediaPlayer playerSuccess, playerError;

    // FIXME : boolean running game...

    SpellGameController() {
        currentWordIndex = -1;
        currentStepIndex = -1;
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
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
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

    }

    public void startGame(String id) {
        playerSuccess = initPlayer("valid.wav");
        playerError = initPlayer("error.mp3");
        SpellGameWordList currentWordList = currentSpellGamePluginProperties.getWordListById(id);
        if (currentSpellGamePluginProperties.validateWithEnterProperty().get()) {
            WritingStateController.INSTANCE.currentTextProperty().addListener(textListener);
        }
        userScore = 0;
        this.currentWordList = new ArrayList<>(currentWordList.getWords());
        currentWordIndex = 0;
        currentStepIndex = 0;
        this.currentGameAnswers.clear();
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
        startCurrentStep();
    }

    public void endGame() {
        this.currentGameAnswers.forEach(System.out::println);
        WritingStateController.INSTANCE.currentTextProperty().removeListener(textListener);
        this.currentWordList = null;
        if (playerSuccess != null) {
            playerSuccess.stop();
            playerSuccess = null;
        }
        if (playerError != null) {
            playerError.stop();
            playerError = null;
        }
    }

    public void validateCurrentStepAndGoToNext() {
        GameStep step = GameStepEnum.values()[currentStepIndex];
        String input = cleanText(WritingStateController.INSTANCE.currentTextProperty().get());
        String word = cleanText(currentWordList.get(currentWordIndex));
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
        traceAnswer(step, word, input);

        // If the user successfully enter the word : set the point and go to next word
        if (step.checkWord(word, input)) {
            if (currentSpellGamePluginProperties.enableFeedbackSoundProperty().get()) {
                playerSuccess.stop();
                playerSuccess.play();
            }
            endCurrentStepAndGoToNextWord();
        }
        // User failed to enter the correct word
        else {
            if (currentSpellGamePluginProperties.enableFeedbackSoundProperty().get()) {
                playerError.stop();
                playerError.play();
            }
            if (currentStepIndex < GameStepEnum.values().length - 1) {
                currentStepIndex++;
                startCurrentStep();
            } else {
                endCurrentStepAndGoToNextWord();
            }
        }
    }


    private void traceAnswer(GameStep step, String word, String input) {
        currentGameAnswers.add(new SpellGameStepResult(step, word, input, System.currentTimeMillis() - currentStepStartedAt));
    }

    private void endCurrentStepAndGoToNextWord() {
        userScore += GameStepEnum.values().length - GameStepEnum.values()[currentStepIndex].ordinal();
        UseVariableController.INSTANCE.requestVariablesUpdate();
        currentStepIndex = 0;
        if (currentWordIndex < currentWordList.size() - 1) {
            currentWordIndex++;
            startCurrentStep();
        } else {
            endGame();
        }
    }

    private long currentStepStartedAt;

    private void startCurrentStep() {
        String currentWord = currentWordList.get(currentWordIndex);
        GameStepEnum step = GameStepEnum.values()[currentStepIndex];
        VoiceSynthesizerController.INSTANCE.speakAsync(
                step.getInstruction(currentWord),
                AppModeController.INSTANCE.getUseModeContext().configurationProperty().get().getVoiceSynthesizerParameter(),
                null);
        currentStepStartedAt = System.currentTimeMillis();
        if (step.isWordDisplayOnStep()) {
            WritingStateController.INSTANCE.writingDisabledProperty().set(true);
            wordDisplayKeyOptions.forEach(currentWordDisplayKeyOption -> currentWordDisplayKeyOption.showWord(currentWord));
            LCNamedThreadFactory.daemonThreadFactory("SpellGame").newThread(() -> {
                ThreadUtils.safeSleep(currentSpellGamePluginProperties.wordDisplayTimeInMsProperty().get());
                wordDisplayKeyOptions.forEach(CurrentWordDisplayKeyOption::hideWord);
                WritingStateController.INSTANCE.writingDisabledProperty().set(false);
                currentStepStartedAt = System.currentTimeMillis();
            }).start();
        }
    }

    // UTILS
    //========================================================================
    private static String cleanText(String text) {
        return StringUtils.toLowerCase(StringUtils.trimToEmpty(text));
    }

    public int getUserScore() {
        return userScore;
    }
    //========================================================================

    // SOUND
    //========================================================================
    public MediaPlayer initPlayer(String soundName) {
        File destAlarm = new File(org.lifecompanion.util.IOUtils.getTempDir("sound") + File.separator + soundName);
        IOUtils.createParentDirectoryIfNeeded(destAlarm);
        try (FileOutputStream fos = new FileOutputStream(destAlarm)) {
            try (InputStream is = ResourceHelper.getInputStreamForPath("/sounds/" + soundName)) {
                IOUtils.copyStream(is, fos);
                Media media = new Media(destAlarm.toURI().toString());
                return new MediaPlayer(media);
            }
        } catch (Exception e) {
            LOGGER.error("Problem load sound", e);
            return null;
        }
    }
    //========================================================================


}
