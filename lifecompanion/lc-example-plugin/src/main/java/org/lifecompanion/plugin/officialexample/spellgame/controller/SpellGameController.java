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

import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.officialexample.ExamplePluginOfficial;
import org.lifecompanion.plugin.officialexample.ExamplePluginProperties;
import org.lifecompanion.plugin.officialexample.spellgame.keyoption.CurrentWordDisplayKeyOption;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;

public enum SpellGameController implements ModeListenerI {
    INSTANCE;

    private List<String> inputList = Arrays.asList("vélo", "maison", "papa", "maman");

    private List<String> currentWordList;
    private int currentWordIndex;
    private int currentListSize;

    private ExamplePluginProperties currentExamplePluginProperties;

    private final List<CurrentWordDisplayKeyOption> wordDisplayKeyOptions;

    private SpellGameController() {
        currentWordIndex = -1;
        currentListSize = -1;
        wordDisplayKeyOptions = new ArrayList<>();
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
        this.currentExamplePluginProperties = null;
        wordDisplayKeyOptions.clear();
    }

    public void startGame() {
        currentWordList = inputList;//FIXME : shuffle
        currentWordIndex = 0;
        stepStartWithCurrentWord();
    }

    public void endGame() {

    }

    public void finishStep() {
        // FIXME : check if not currently writing before finished step
        currentWordIndex++;
        stepStartWithCurrentWord();
    }

    private void stepStartWithCurrentWord() {
        WritingStateController.INSTANCE.writingDisabledProperty().set(true);
        String currentWord = currentWordList.get(currentWordIndex);
        wordDisplayKeyOptions.forEach(currentWordDisplayKeyOption -> currentWordDisplayKeyOption.showWord(currentWord));
        LCNamedThreadFactory.daemonThreadFactory("SpellGame").newThread(() -> {
            ThreadUtils.safeSleep(currentExamplePluginProperties.wordDisplayTimeInMsProperty().get());
            wordDisplayKeyOptions.forEach(CurrentWordDisplayKeyOption::hideWord);
            WritingStateController.INSTANCE.writingDisabledProperty().set(false);
        }).start();
    }
}
