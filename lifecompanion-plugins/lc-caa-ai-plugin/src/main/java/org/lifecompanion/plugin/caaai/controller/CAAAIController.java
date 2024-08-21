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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.ConversationMessage;
import org.lifecompanion.plugin.caaai.model.ConversationMessageAuthor;
import org.lifecompanion.plugin.caaai.model.Suggestion;
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
    private SuggestionService suggestionService;

    // TODO : change it for a grid to avoid having different suggestion on each grid
    private final List<SuggestedSentenceKeyOption> suggestedSentenceKeys;
    private final InvalidationListener textChangedListener;

    private final ObservableList<ConversationMessage> conversationMessages;

    CAAAIController() {
        suggestedSentenceKeys = new ArrayList<>();

        this.conversationMessages = FXCollections.observableArrayList();

        this.textChangedListener = (inv) -> this.debouncedUpdateSuggestions();
    }

    public ObservableList<ConversationMessage> conversationMessages() {
        return this.conversationMessages;
    }

    public void addOwnMessage(String content) {
        this.conversationMessages.add(new ConversationMessage(ConversationMessageAuthor.ME, content));
        this.suggestionService.handleOwnMessage(content);
        this.debouncedUpdateSuggestions();
    }

    public void addInterlocutorMessage(String content) {
        this.conversationMessages.add(new ConversationMessage(ConversationMessageAuthor.INTERLOCUTOR, content));
        this.suggestionService.handleInterlocutorMessage(content);
        this.debouncedUpdateSuggestions();
    }

    public void updateSuggestions() {
        // Make a call to get suggestions
        String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();

        // Get the suggestions
        List<Suggestion> suggestions = this.suggestionService.suggestSentences(textBeforeCaret);

        // Dispatch in keys
        for (int i = 0; i < suggestedSentenceKeys.size(); i++) {
            final int index = i;
            FXThreadUtils.runOnFXThread(() -> suggestedSentenceKeys.get(index).suggestionProperty().set(index < suggestions.size() ? suggestions.get(index).content() : ""));
        }
    }

    public void retrySuggestions(){
        // Make a call to get suggestions
        String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();

        List<Suggestion> suggestions = this.suggestionService.retrySuggestSentences(textBeforeCaret);

        // Dispatch in keys
        for (int i = 0; i < suggestedSentenceKeys.size(); i++) {
            final int index = i;
            FXThreadUtils.runOnFXThread(() -> suggestedSentenceKeys.get(index).suggestionProperty().set(index < suggestions.size() ? suggestions.get(index).content() : ""));
        }
    }

    private void debouncedUpdateSuggestions() {
        ThreadUtils.debounce(500, "caa-ai", this::updateSuggestions);
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentCAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        this.suggestionService = new SuggestionService(
                currentCAAAIPluginProperties.apiEndpointProperty().get(),
                currentCAAAIPluginProperties.apiTokenProperty().get());

        // Find all the keys that can display the current word
        Map<GridComponentI, List<SuggestedSentenceKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SuggestedSentenceKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).distinct().forEach(suggestedSentenceKeys::add);

        this.suggestionService.initConversation(this.suggestedSentenceKeys.size());

        // Add listener
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(this.textChangedListener);
        this.debouncedUpdateSuggestions();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.suggestionService.stopConversation();
        this.suggestionService = null;

        WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(this.textChangedListener);
        this.currentCAAAIPluginProperties = null;
        suggestedSentenceKeys.clear();
        this.configuration = null;
    }
}
