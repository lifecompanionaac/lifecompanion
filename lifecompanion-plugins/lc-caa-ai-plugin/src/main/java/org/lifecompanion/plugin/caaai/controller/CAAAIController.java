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

package org.lifecompanion.plugin.caaai.controller;

import javafx.beans.InvalidationListener;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.keyoption.SuggestedSentenceKeyOption;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum CAAAIController implements ModeListenerI {
    INSTANCE;
    private static final Logger LOGGER = LoggerFactory.getLogger(CAAAIController.class);
    private LCConfigurationI configuration;
    private CAAAIPluginProperties currentCAAAIPluginProperties;
    private final List<SuggestedSentenceKeyOption> suggestedSentenceKeys;
    private final InvalidationListener textChangedListener;


    CAAAIController() {
        suggestedSentenceKeys = new ArrayList<>();
        this.textChangedListener = (inv) -> {
            this.launchSuggestion();
        };
    }

    private void launchSuggestion() {
        ThreadUtils.debounce(500, "caa-ai", () -> {
            // Make a call to get suggestions
            String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();

            // Get the suggestions
            List<String> suggestions = SuggestionService.INSTANCE.getSuggestions(textBeforeCaret, suggestedSentenceKeys.size());

            // Dispatch in keys
            for (int i = 0; i < suggestedSentenceKeys.size(); i++) {
                final int index = i;
                FXThreadUtils.runOnFXThread(() -> suggestedSentenceKeys.get(index).suggestionUpdated(index < suggestions.size() ? suggestions.get(index) : null));
            }
        });
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentCAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        // Find all the keys that can display the current word
        Map<GridComponentI, List<SuggestedSentenceKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SuggestedSentenceKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).distinct().forEach(suggestedSentenceKeys::add);

        // Add listener
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(this.textChangedListener);
        this.launchSuggestion();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(this.textChangedListener);
        this.currentCAAAIPluginProperties = null;
        suggestedSentenceKeys.clear();
        this.configuration = null;
    }
}
