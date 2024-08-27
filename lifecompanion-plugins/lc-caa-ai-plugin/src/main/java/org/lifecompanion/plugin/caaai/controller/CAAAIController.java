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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.*;
import org.lifecompanion.plugin.caaai.model.keyoption.AiSuggestionKeyOption;
import org.lifecompanion.plugin.caaai.model.keyoption.SpeechRecordingVolumeIndicatorKeyOption;
import org.lifecompanion.plugin.caaai.service.SpeechToTextService;
import org.lifecompanion.plugin.caaai.service.SuggestionService;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public enum CAAAIController implements ModeListenerI {
    INSTANCE;

    public static final String VAR_LAST_CONVERSATION_MESSAGE_AUTHOR = "CAAAILastConversationMessageAuthor";

    public static final String VAR_LAST_CONVERSATION_MESSAGE_CONTENT = "CAAAILastConversationMessageContent";

    private static final Logger LOGGER = LoggerFactory.getLogger(CAAAIController.class);

    private LCConfigurationI configuration;
    private CAAAIPluginProperties currentCAAAIPluginProperties;

    private SuggestionService suggestionService;
    private SpeechToTextService speechToTextService;

    private final ObservableList<ConversationMessage> conversationMessages;

    private final Set<Consumer<Boolean>> speechRecordingChangeListeners;
    private final Set<Consumer<ConversationMessageAuthor>> conversationAuthorChangeListeners;

    private final ObjectProperty<MoodAiContextValue> moodContextValue;

    private final InvalidationListener speechRecordingChangeListener;
    private final InvalidationListener conversationMessagesChangeListener;
    private final InvalidationListener textChangeListener;
    private final ChangeListener<AiContextValue> contextValueChangeListener;

    private final Map<GridComponentI, List<AiSuggestionKeyOption>> suggestionKeyOptionsByGrid;
    private final List<SpeechRecordingVolumeIndicatorKeyOption> recordedVolumeIndicatorKeys;

    private boolean pauseUpdateSuggestion;

    CAAAIController() {
        suggestionKeyOptionsByGrid = new HashMap<>();
        recordedVolumeIndicatorKeys = new ArrayList<>();

        this.conversationMessages = FXCollections.observableArrayList();

        this.speechRecordingChangeListeners = new HashSet<>();
        this.conversationAuthorChangeListeners = new HashSet<>();

        this.moodContextValue = new SimpleObjectProperty<>();

        this.speechRecordingChangeListener = (inv) -> this.onSpeechRecordingChange();
        this.conversationMessagesChangeListener = (inv) -> this.onConversationChange();
        this.textChangeListener = (inv) -> this.updateSuggestions();
        this.contextValueChangeListener = (obs, prev, next) -> {
            if (next != null) {
                this.suggestionService.handleOwnMessage(next.getTextValue());
                this.updateSuggestions();
            }
        };
    }

    public void setPauseUpdateSuggestion(boolean pauseUpdateSuggestion) {
        this.pauseUpdateSuggestion = pauseUpdateSuggestion;
    }

    // Class part : "Context values"
    //========================================================================

    public void defineMoodContextValue(MoodAiContextValue value) {
        this.moodContextValue.set(value);
    }

    //========================================================================

    // Class part : "Conversations"
    //========================================================================

    public ObservableList<ConversationMessage> conversationMessages() {
        return this.conversationMessages;
    }

    public void addOwnMessage(String content) {
        if (!content.isBlank()) {
            this.conversationMessages.add(new ConversationMessage(ConversationMessageAuthor.ME, content));
            this.suggestionService.handleOwnMessage(content);
            this.updateSuggestions();
            UseVariableController.INSTANCE.requestVariablesUpdate();
        }
    }

    public void replaceInterlocutorMessage(Function<String, String> callback) {
        boolean shouldAppend = false;
        ConversationMessage conversationMessage = this.lastConversationMessage();
        if (conversationMessage == null || conversationMessage.author() != ConversationMessageAuthor.INTERLOCUTOR) {
            conversationMessage = new ConversationMessage(ConversationMessageAuthor.INTERLOCUTOR, "");

            shouldAppend = true;
        }

        String nextContent = callback.apply(conversationMessage.content().get());

        if (!nextContent.isBlank()) {
            conversationMessage.content().set(nextContent);

            if (shouldAppend) {
                this.conversationMessages.add(conversationMessage);
            }
            UseVariableController.INSTANCE.requestVariablesUpdate();
        }
    }

    private ConversationMessage lastConversationMessage() {
        return this.conversationMessages.isEmpty() ? null : this.conversationMessages.getLast();
    }

    public void clearConversation() {
        this.moodContextValue.set(null);
        this.conversationMessages.clear();
        this.suggestionService.clearConversation();
        this.updateSuggestions();
    }

    //========================================================================

    // Class part : "Speech to text"
    //========================================================================

    public void enableSpeechToTextRecognition() {
        if (!this.speechToTextService.recordingProperty().get()) {
            this.startSpeechToTextRecognition();
        }
    }

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
                this.replaceInterlocutorMessage(content -> String.join(" ", content + sentenceDetected).trim());

                LOGGER.info("Speech detected : {}", sentenceDetected);
            }, allSentences -> {
                String interlocutorMessage = String.join("\n", allSentences).trim();
                if (!interlocutorMessage.isBlank()) {
                    this.replaceInterlocutorMessage(content -> interlocutorMessage);

                    LOGGER.info("Speech finished : {}", interlocutorMessage);

                    this.suggestionService.handleInterlocutorMessage(interlocutorMessage);
                    this.updateSuggestions();
                }
            });
        }
    }

    //========================================================================

    // Class part : "Suggestions"
    //========================================================================
    public void retrySuggestions() {
        String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();
        forEachSuggestionKeyOption((index, keyOption) -> keyOption.startLoading());
        this.dispatchSuggestions(this.suggestionService.retrySuggestSentences(textBeforeCaret));
    }

    private void forEachSuggestionKeyOption(BiConsumer<Integer, AiSuggestionKeyOption> action) {
        this.suggestionKeyOptionsByGrid.forEach((grid, keyOptions) -> {
            for (int i = 0; i < keyOptions.size(); i++) {
                final int index = i;
                FXThreadUtils.runOnFXThread(() -> action.accept(index, keyOptions.get(index)));
            }
        });
    }

    private void dispatchSuggestions(List<Suggestion> suggestions) {
        this.forEachSuggestionKeyOption((index, keyOption) -> keyOption.suggestionProperty().set(index < suggestions.size() ? suggestions.get(index).content() : ""));
    }

    private void updateSuggestions() {
        if (!pauseUpdateSuggestion) {
            ThreadUtils.debounce(500, "caa-ai", () -> {
                String textBeforeCaret = WritingStateController.INSTANCE.textBeforeCaretProperty().get();
                forEachSuggestionKeyOption((index, keyOption) -> keyOption.startLoading());
                this.dispatchSuggestions(this.suggestionService.suggestSentences(textBeforeCaret));
            });
        }
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

    public void onConversationChange() {
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
        if (recording) {
            recordedVolumeIndicatorKeys.forEach(k -> k.showVolume(speechToTextService.currentRecordedVolumeProperty()));
        } else {
            recordedVolumeIndicatorKeys.forEach(SpeechRecordingVolumeIndicatorKeyOption::hideVolume);
        }

        this.speechRecordingChangeListeners.forEach(callback -> callback.accept(recording));
    }

    //========================================================================

    // Class part : "Start/stop"
    //========================================================================

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.pauseUpdateSuggestion = false;
        this.moodContextValue.set(null);

        this.configuration = configuration;
        // Get plugin properties for current configuration
        currentCAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        this.speechToTextService = new SpeechToTextService(currentCAAAIPluginProperties.speechToTextJsonConfig().get());

        this.suggestionService = new SuggestionService(
                currentCAAAIPluginProperties.apiEndpointProperty().get(),
                currentCAAAIPluginProperties.apiTokenProperty().get());

        // Find all the keys option by grid that can display the suggestions
        ConfigurationComponentUtils.findKeyOptionsByGrid(AiSuggestionKeyOption.class, configuration, this.suggestionKeyOptionsByGrid, null);

        Map<GridComponentI, List<SpeechRecordingVolumeIndicatorKeyOption>> keys2 = new HashMap<>();
        ConfigurationComponentUtils.findKeyOptionsByGrid(SpeechRecordingVolumeIndicatorKeyOption.class, configuration, keys2, null);
        keys2.values().stream().flatMap(List::stream).distinct().forEach(recordedVolumeIndicatorKeys::add);

        this.suggestionService.initConversation(this.suggestionKeyOptionsByGrid.entrySet().iterator().next().getValue().size());

        // Add listener
        this.speechToTextService.recordingProperty().addListener(this.speechRecordingChangeListener);
        this.conversationMessages.addListener(this.conversationMessagesChangeListener);
        this.moodContextValue.addListener(this.contextValueChangeListener);
        WritingStateController.INSTANCE.textBeforeCaretProperty().addListener(this.textChangeListener);

        this.updateSuggestions();
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.speechToTextService.recordingProperty().removeListener(this.speechRecordingChangeListener);
        this.conversationMessages.removeListener(this.conversationMessagesChangeListener);
        this.moodContextValue.removeListener(this.contextValueChangeListener);
        WritingStateController.INSTANCE.textBeforeCaretProperty().removeListener(this.textChangeListener);

        this.conversationMessages.clear();
        this.speechToTextService.dispose();
        this.speechToTextService = null;

        this.suggestionService.stopConversation();
        this.suggestionService = null;

        this.currentCAAAIPluginProperties = null;
        suggestionKeyOptionsByGrid.clear();
        recordedVolumeIndicatorKeys.clear();
        this.configuration = null;
    }

    //========================================================================

    // Class part : "Use variables"
    //========================================================================

    public List<UseVariableDefinitionI> getDefinedVariables() {
        return List.of(
                new UseVariableDefinition(
                        VAR_LAST_CONVERSATION_MESSAGE_AUTHOR,
                        "caa.ai.plugin.variables.last_conversation_message_author.name",
                        "caa.ai.plugin.variables.last_conversation_message_author.description",
                        "caa.ai.plugin.variables.last_conversation_message_author.values.me",
                        500),
                new UseVariableDefinition(
                        VAR_LAST_CONVERSATION_MESSAGE_CONTENT,
                        "caa.ai.plugin.variables.last_conversation_message_content.name",
                        "caa.ai.plugin.variables.last_conversation_message_content.description",
                        "caa.ai.plugin.variables.last_conversation_message_content.example",
                        500)
        );
    }

    public Function<UseVariableDefinitionI, UseVariableI<?>> getSupplierForUseVariable(String id) {
        return switch (id) {
            case VAR_LAST_CONVERSATION_MESSAGE_AUTHOR ->
                    def -> new StringUseVariable(def, this.generateLastConversationMessageAuthorVariable());
            case VAR_LAST_CONVERSATION_MESSAGE_CONTENT ->
                    def -> new StringUseVariable(def, this.generateLastConversationMessageContentVariable());
            default -> null;
        };
    }

    private String generateLastConversationMessageAuthorVariable() {
        ConversationMessage message = this.lastConversationMessage();

        return message != null ? message.author().getName() : ConversationMessageAuthor.ME.getName();
    }

    private String generateLastConversationMessageContentVariable() {
        ConversationMessage message = this.lastConversationMessage();

        return message != null ? message.content().get() : "";
    }

    //========================================================================
}
