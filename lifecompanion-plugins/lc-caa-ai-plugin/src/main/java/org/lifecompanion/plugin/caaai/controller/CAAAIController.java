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
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.ConversationMessage;
import org.lifecompanion.plugin.caaai.model.ConversationMessageAuthor;
import org.lifecompanion.plugin.caaai.model.Suggestion;
import org.lifecompanion.plugin.caaai.model.keyoption.RecordedVolumeIndicatorKeyOption;
import org.lifecompanion.plugin.caaai.model.keyoption.SuggestedSentenceKeyOption;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

public enum CAAAIController implements ModeListenerI {
    INSTANCE;

    public static final String VAR_LAST_CONVERSATION_MESSAGE = "CAAAILastConversationMessage";

    private static final Logger LOGGER = LoggerFactory.getLogger(CAAAIController.class);

    private LCConfigurationI configuration;
    private CAAAIPluginProperties currentCAAAIPluginProperties;

    private SuggestionService suggestionService;
    private SpeechToTextService speechToTextService;

    private final ObservableList<ConversationMessage> conversationMessages;

    private final Set<Consumer<Boolean>> speechRecordingChangeListeners;
    private final Set<Consumer<ConversationMessageAuthor>> conversationAuthorChangeListeners;

    private final InvalidationListener speechRecordingChangeListener;
    private final InvalidationListener conversationMessagesChangeListener;
    private final InvalidationListener textChangeListener;

    // TODO : change it for a grid to avoid having different suggestion on each grid
    private final List<SuggestedSentenceKeyOption> suggestedSentenceKeys;
    private final List<RecordedVolumeIndicatorKeyOption> recordedVolumeIndicatorKeys;

    CAAAIController() {
        suggestedSentenceKeys = new ArrayList<>();
        recordedVolumeIndicatorKeys = new ArrayList<>();

        this.conversationMessages = FXCollections.observableArrayList();

        this.speechRecordingChangeListeners = new HashSet<>();
        this.conversationAuthorChangeListeners = new HashSet<>();

        this.speechRecordingChangeListener = (inv) -> this.onSpeechRecordingChange();
        this.conversationMessagesChangeListener = (inv) -> this.onConversationAuthorChange();
        this.textChangeListener = (inv) -> this.debouncedUpdateSuggestions();
    }

    // Class part : "Conversations"
    //========================================================================

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

    //========================================================================

    // Class part : "Speech to text"
    //========================================================================

    public void toggleSpeechToTextRecognition() {
        if (this.speechToTextService.recordingProperty().get()) {
            this.speechToTextService.stopRecording();
        } else {
            this.startSpeechToTextRecognition();
        }
    }

    public void startSpeechToTextRecognition() {
        if (!this.speechToTextService.recordingProperty().get()) {
            this.speechToTextService.startRecording(sentenceDetected -> {
                // TODO : can be displayed to current user to inform him that the speech is currently detecting something
                LOGGER.info("Sentence detected : {}", sentenceDetected);
            }, allSentences -> {
                String interlocutorMessage = String.join("\n", allSentences);
                if (StringUtils.isNotBlank(interlocutorMessage)) {
                    LOGGER.info("Speech finished : {}", interlocutorMessage);
                    addInterlocutorMessage(interlocutorMessage);
                }
            });
        }
    }

    //========================================================================

    // Class part : "Suggestions"
    //========================================================================

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

    public void retrySuggestions() {
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

    //========================================================================

    // Class part : "Listeners"
    //========================================================================

    public void addConversationAuthorChangeListener(Consumer<ConversationMessageAuthor> callback) {
        this.conversationAuthorChangeListeners.add(callback);
    }

    public void removeConversationAuthorChangeListener(Consumer<ConversationMessageAuthor> callback) {
        this.conversationAuthorChangeListeners.remove(callback);
    }

    public void onConversationAuthorChange() {
        ConversationMessage message = this.conversationMessages.isEmpty() ? null : this.conversationMessages.getLast();

        if (message != null) {
            this.conversationAuthorChangeListeners.forEach(callback -> callback.accept(message.author()));
        }
    }

    public void addSpeechRecordingChangeListener(Consumer<Boolean> callback) {
        this.speechRecordingChangeListeners.add(callback);
    }

    public void removeSpeechRecordingChangeListener(Consumer<Boolean> callback) {
        this.speechRecordingChangeListeners.remove(callback);
    }

    public void onSpeechRecordingChange() {
        Boolean recording = this.speechToTextService.recordingProperty().get();
        if(recording){
            recordedVolumeIndicatorKeys.forEach(k -> k.showVolume(speechToTextService.currentRecordedVolumeProperty()));
        }else {
            recordedVolumeIndicatorKeys.forEach(RecordedVolumeIndicatorKeyOption::hideVolume);
        }

        this.speechRecordingChangeListeners.forEach(callback -> callback.accept(recording));
    }

    //========================================================================

    // Class part : "Start/stop"
    //========================================================================

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentCAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        this.speechToTextService = new SpeechToTextService(currentCAAAIPluginProperties.speechToTextJsonConfig().get());

        this.suggestionService = new SuggestionService(
                currentCAAAIPluginProperties.apiEndpointProperty().get(),
                currentCAAAIPluginProperties.apiTokenProperty().get());

        // Find all the keys that can display the current word
        Map<GridComponentI, List<SuggestedSentenceKeyOption>> keys = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SuggestedSentenceKeyOption.class, configuration, keys, null);
        keys.values().stream().flatMap(List::stream).distinct().forEach(suggestedSentenceKeys::add);

        Map<GridComponentI, List<RecordedVolumeIndicatorKeyOption>> keys2 = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(RecordedVolumeIndicatorKeyOption.class, configuration, keys2, null);
        keys2.values().stream().flatMap(List::stream).distinct().forEach(recordedVolumeIndicatorKeys::add);


        this.suggestionService.initConversation(this.suggestedSentenceKeys.size());

        // Add listener
        this.speechToTextService.recordingProperty().addListener(this.speechRecordingChangeListener);
        this.conversationMessages.addListener(this.conversationMessagesChangeListener);
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(this.textChangeListener);

        this.debouncedUpdateSuggestions();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.suggestionService.stopConversation();
        this.suggestionService = null;

        WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(this.textChangeListener);
        this.currentCAAAIPluginProperties = null;
        suggestedSentenceKeys.clear();
        recordedVolumeIndicatorKeys.clear();
        this.configuration = null;

        this.speechToTextService.dispose();
    }

    //========================================================================

    // Class part : "Use variables"
    //========================================================================

    public List<UseVariableDefinitionI> getDefinedVariables() {
        return List.of(
                new UseVariableDefinition(
                        VAR_LAST_CONVERSATION_MESSAGE,
                        "caa.ai.plugin.variables.last_conversation_message.name",
                        "caa.ai.plugin.variables.last_conversation_message.description",
                        "caa.ai.plugin.variables.last_conversation_message.example",
                        500)
        );
    }

    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return switch (id) {
            case VAR_LAST_CONVERSATION_MESSAGE -> def -> new StringUseVariable(def, this.generateLastConversationMessageVariable());
            default -> null;
        };
    }

    private String generateLastConversationMessageVariable() {
        ConversationMessage message = this.conversationMessages.isEmpty() ? null : this.conversationMessages.getLast();

        return message != null ? message.content().get() : "";
    }

    //========================================================================
}
