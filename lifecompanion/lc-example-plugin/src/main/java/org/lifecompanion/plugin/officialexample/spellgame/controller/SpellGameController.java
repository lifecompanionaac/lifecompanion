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

package org.lifecompanion.plugin.officialexample.spellgame.controller;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.plugin.officialexample.ExamplePluginOfficial;
import org.lifecompanion.plugin.officialexample.ExamplePluginProperties;
import org.lifecompanion.plugin.officialexample.spellgame.model.keyoption.CurrentWordDisplayKeyOption;
import org.lifecompanion.plugin.officialexample.spellgame.model.GameStep;
import org.lifecompanion.plugin.officialexample.spellgame.model.GameStepEnum;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;

public enum SpellGameController implements ModeListenerI {
    INSTANCE;

    private List<String> inputList = Arrays.asList("vélo", "maison", "papa", "maman");

    public static final String
            VAR_ID_USER_SCORE = "SpellGameUserScore",
            VAR_ID_WORD_INDEX = "SpellGameWordIndex",
            VAR_ID_WORD_COUNT = "SpellGameWordCount";

    private List<String> currentWordList;
    private int currentWordIndex;
    private int currentStepIndex;
    private int userScore;

    private ExamplePluginProperties currentExamplePluginProperties;
    private final List<CurrentWordDisplayKeyOption> wordDisplayKeyOptions;
    private final InvalidationListener textListener;

    // FIXME : boolean running game...

    SpellGameController() {
        currentWordIndex = -1;
        currentStepIndex = -1;
        wordDisplayKeyOptions = new ArrayList<>();
        textListener = inv -> {
            String text = WritingStateController.INSTANCE.currentTextProperty().get();
            if (StringUtils.isNotBlank(text) && text.endsWith("\n")) {
                // This is expected call (and not FXThreadUtils to directly be ran)
                // > will avoid nested changes as this is called from a change listener and the called method create new changes.
                Platform.runLater(this::validateCurrentStepAndGoToNext);
            }
        };
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        // Get plugin properties for current configuration
        currentExamplePluginProperties = configuration.getPluginConfigProperties(ExamplePluginOfficial.ID, ExamplePluginProperties.class);


        // Find all the keys that can display the current word
        Map<GridComponentI, List<CurrentWordDisplayKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(CurrentWordDisplayKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).distinct().forEach(wordDisplayKeyOptions::add);

        // FIXME : delete this
        new Thread(() -> {
            ThreadUtils.safeSleep(3000);
            this.startGame();
        }).start();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        endGame();
        this.currentExamplePluginProperties = null;
        wordDisplayKeyOptions.clear();
    }

    public void startGame() {
        if (currentExamplePluginProperties.validateWithEnterProperty().get()) {
            WritingStateController.INSTANCE.currentTextProperty().addListener(textListener);
        }
        userScore = 0;
        currentWordList = inputList;//FIXME : shuffle and remove empty words
        currentWordIndex = 0;
        currentStepIndex = 0;
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
        startCurrentStep();
    }

    public void endGame() {
        // FIXME
        WritingStateController.INSTANCE.currentTextProperty().removeListener(textListener);
        System.out.println("== END GAME ==");
    }

    public void validateCurrentStepAndGoToNext() {
        // FIXME : check validation relative to current step
        GameStep step = GameStepEnum.values()[currentStepIndex];
        String input = WritingStateController.INSTANCE.currentTextProperty().get();
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);

        // If the user successfully enter the word : set the point and go to next word
        if (step.checkWord(
                cleanText(currentWordList.get(currentWordIndex)),
                cleanText(input)
        )) {
            // SoundPlayerController.INSTANCE.playSoundAsync(new File("confirmation_002.wav"), true);
            endCurrentStepAndGoToNextWord();
        }
        // User failed to enter the correct word
        else {
            // SoundPlayerController.INSTANCE.playSoundAsync(new File("bong.wav"), true);
            if (currentStepIndex < GameStepEnum.values().length - 1) {
                currentStepIndex++;
                startCurrentStep();
            } else {
                endCurrentStepAndGoToNextWord();
            }
        }

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

    private void startCurrentStep() {
        String currentWord = currentWordList.get(currentWordIndex);
        GameStepEnum step = GameStepEnum.values()[currentStepIndex];
        VoiceSynthesizerController.INSTANCE.speakAsync(
                step.getInstruction(currentWord),
                AppModeController.INSTANCE.getUseModeContext().configurationProperty().get().getVoiceSynthesizerParameter(),
                null);
        if (step.isWordDisplayOnStep()) {
            WritingStateController.INSTANCE.writingDisabledProperty().set(true);
            wordDisplayKeyOptions.forEach(currentWordDisplayKeyOption -> currentWordDisplayKeyOption.showWord(currentWord));
            LCNamedThreadFactory.daemonThreadFactory("SpellGame").newThread(() -> {
                ThreadUtils.safeSleep(currentExamplePluginProperties.wordDisplayTimeInMsProperty().get());
                wordDisplayKeyOptions.forEach(CurrentWordDisplayKeyOption::hideWord);
                WritingStateController.INSTANCE.writingDisabledProperty().set(false);
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


}
