/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
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
package org.lifecompanion.controller.textcomponent;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.*;
import org.lifecompanion.model.api.textprediction.WordPredictionI;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.model.impl.textcomponent.WritingControllerState;
import org.lifecompanion.model.impl.textcomponent.WritingEvent;
import org.lifecompanion.controller.textprediction.WordPredictionController;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.model.impl.imagedictionary.StaticImageElement;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum WritingStateController implements ModeListenerI, WritingStateControllerI {
    INSTANCE;

    private final Logger LOGGER = LoggerFactory.getLogger(WritingStateController.class);

    // ATTRIBUTES
    //========================================================================
    private final List<WritingDeviceI> writingDevices;
    private final WritingStateEntryContainer writingStateEntryContainer;
    private final Set<Consumer<WritingEventI>> writingEventListeners;
    private final BooleanProperty writingDisabled;

    /**
     * Contains the whole text.
     */
    private final StringBuilder totalTextBuilder;
    private String lastAddedTextToTotalText;
    //========================================================================

    WritingStateController() {
        this.writingDevices = new ArrayList<>();
        // Default writing state controller : manage entries
        this.writingDevices.add(writingStateEntryContainer = new WritingStateEntryContainer());

        this.totalTextBuilder = new StringBuilder();

        this.writingEventListeners = new HashSet<>(2);

        this.writingDisabled = new SimpleBooleanProperty();

        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener(inv -> initExampleEntriesIfNeeded());
    }
    //========================================================================

    // PROPERTIES
    //========================================================================
    private void disableCapitalizeNext() {
        this.writingStateEntryContainer.disableCapitalizeNext();
    }

    @Override
    public ReadOnlyBooleanProperty upperCaseProperty() {
        return this.writingStateEntryContainer.upperCaseProperty();
    }

    @Override
    public ReadOnlyBooleanProperty capitalizeNextProperty() {
        return this.writingStateEntryContainer.capitalizeNextProperty();
    }

    @Override
    public ReadOnlyStringProperty currentCharProperty() {
        return writingStateEntryContainer.currentCharProperty();
    }

    @Override
    public ReadOnlyStringProperty currentWordProperty() {
        return writingStateEntryContainer.currentWordProperty();
    }

    @Override
    public ReadOnlyStringProperty lastCompleteWordProperty() {
        return writingStateEntryContainer.lastCompleteWordProperty();
    }

    @Override
    public ObservableList<WriterEntryI> getWriterEntries() {
        return writingStateEntryContainer.getWriterEntries();
    }

    @Override
    public ReadOnlyStringProperty currentTextProperty() {
        return writingStateEntryContainer.currentTextProperty();
    }

    @Override
    public ReadOnlyStringProperty textBeforeCaretProperty() {
        return writingStateEntryContainer.textBeforeCaretProperty();
    }

    @Override
    public ReadOnlyStringProperty textAfterCaretProperty() {
        return writingStateEntryContainer.textAfterCaretProperty();
    }

    @Override
    public ReadOnlyIntegerProperty caretPosition() {
        return writingStateEntryContainer.caretPosition();
    }

    /**
     * @return a property that can be changed in use mode to disable event on writing devices.
     * This can be useful to block text entry with API.
     */
    public BooleanProperty writingDisabledProperty() {
        return writingDisabled;
    }
    //========================================================================


    // GETTERS
    //========================================================================
    @Override
    public WriterEntryI getEntryBeforeCaretPosition(final int caretPosition) {
        return writingStateEntryContainer.getEntryBeforeCaretPosition(caretPosition);
    }

    @Override
    public WriterEntryI getEntryAfterCaretPosition(final int caretPosition) {
        return writingStateEntryContainer.getEntryAfterCaretPosition(caretPosition);
    }

    @Override
    public WriterEntryI getLastEntry() {
        return writingStateEntryContainer.getLastEntry();
    }

    @Override
    public String getLastWord() {
        return writingStateEntryContainer.getLastWord();
    }

    @Override
    public String getLastCompleteWord() {
        return writingStateEntryContainer.getLastCompleteWord();
    }

    @Override
    public String getLastSentence() {
        return writingStateEntryContainer.getLastSentence();
    }


    @Override
    public int getTextLengthBefore(final WriterEntryI targetEntry) {
        return writingStateEntryContainer.getTextLengthBefore(targetEntry);
    }

    @Override
    public WriterEntryI getEntryAtCaretPosition(final int position) {
        return writingStateEntryContainer.getEntryAtCaretPosition(position);
    }

    @Override
    public int getCaretEntryIndex(final int caretPosition) {
        return writingStateEntryContainer.getCaretEntryIndex(caretPosition);
    }

    //========================================================================
    // BASICS ACTIONS
    //========================================================================
    @Override
    public void newLine(WritingEventSource src) {
        this.executeEvent(d -> d.newLine(src), src, WritingEventType.INSERTION_SIMPLE, FluentHashMap.mapStrObj("text", "\n"), this::disableCapitalizeNext);
    }

    @Override
    public void tab(WritingEventSource src) {
        this.executeEvent(d -> d.tab(src), src, WritingEventType.INSERTION_SIMPLE, FluentHashMap.mapStrObj("text", "\t"), this::disableCapitalizeNext);
    }

    @Override
    public void space(WritingEventSource src) {
        this.executeEvent(d -> d.space(src), src, WritingEventType.INSERTION_SIMPLE, FluentHashMap.mapStrObj("text", " "), this::disableCapitalizeNext);
    }

    @Override
    public void moveCaretForward(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretForward(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.FORWARD));
    }

    @Override
    public void moveCaretToStart(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretToStart(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.START));
    }

    @Override
    public void moveCaretToEnd(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretToEnd(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.END));
    }

    @Override
    public void moveCaretBackward(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretBackward(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.BACKWARD));
    }

    @Override
    public void switchUpperCase(WritingEventSource src) {
        this.writingStateEntryContainer.switchUpperCase(src);
    }

    @Override
    public void moveCaretUp(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretUp(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.UP));
    }

    @Override
    public void moveCaretDown(WritingEventSource src) {
        this.executeEvent(d -> d.moveCaretDown(src), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.DOWN));
    }

    @Override
    public void moveCaretToPosition(WritingEventSource src, WriterDisplayerI displayer, double xInEditor, double yInEditor) {
        this.executeEvent(d -> d.moveCaretToPosition(src, displayer, xInEditor, yInEditor), src, WritingEventType.CARET_MOVE, FluentHashMap.mapStrObj("direction", CaretMoveTypes.TO_MOUSE));
    }

    @Override
    public void switchCapitalizeNext(WritingEventSource src) {
        this.writingStateEntryContainer.switchCapitalizeNext(src);
    }

    @Override
    public void enableAutoSavedStateCleaning() {
        this.writingStateEntryContainer.enableAutoSavedStateCleaning();
    }

    @Override
    public void disableAutoSavedStateCleaning() {
        this.writingStateEntryContainer.disableAutoSavedStateCleaning();
    }

    @Override
    public void restoreState() {
        this.writingStateEntryContainer.restoreState();
    }

    @Override
    public void saveState() {
        this.writingStateEntryContainer.saveState();
    }

    @Override
    public void insertWordPrediction(WritingEventSource src, String toInsert, WordPredictionI originalPrediction) {
        if (toInsert != null) {
            this.executeEvent(d -> d.insertText(src, toInsert), src, WritingEventType.INSERTION_WORD_PREDICTION, FluentHashMap.mapStrObj("text", toInsert).with("prediction", originalPrediction));
        }
    }

    @Override
    public void insertCharPrediction(WritingEventSource src, String toInsert) {
        if (toInsert != null) {
            this.executeEvent(d -> d.insertText(src, toInsert), src, WritingEventType.INSERTION_CHAR_PREDICTION, FluentHashMap.mapStrObj("text", toInsert).with("prediction", toInsert));
        }
    }

    @Override
    public void removeLastChar(WritingEventSource src) {
        this.executeEvent(d -> d.removeLastChar(src), src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.LAST_CHAR));
    }

    @Override
    public void removeNextChars(WritingEventSource src, final int n) {
        this.executeEvent(d -> d.removeNextChars(src, n), src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.NEXT_CHARS));
    }

    @Override
    public void removeLastChars(WritingEventSource src, final int n) {
        this.executeEvent(d -> d.removeLastChars(src, n), src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.LAST_CHARS));
    }

    @Override
    public void removeNextChar(WritingEventSource src) {
        this.executeEvent(d -> d.removeNextChar(src), src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.NEXT_CHAR));
    }

    public void insert(WritingEventSource src, final WriterEntryI entryP) {
        this.insert(src, entryP, null);
    }

    @Override
    public boolean isExternalWritingDevice() {
        return false;
    }

    @Override
    public void insert(WritingEventSource src, final WriterEntryI entryP, final WriteSpecialChar specialChar) {
        if ((entryP != null && entryP.isValid()) || specialChar != null) {
            this.executeEvent(d -> d.insert(src, entryP, specialChar),
                    src,
                    WritingEventType.INSERTION_SIMPLE,
                    FluentHashMap.mapStrObj("text", entryP.entryTextProperty().get()),
                    this::disableCapitalizeNext);
        }
    }

    @Override
    public void insertText(WritingEventSource src, String text) {
        if (text != null) {
            this.executeEvent(d -> d.insertText(src, text), src, WritingEventType.INSERTION_SIMPLE, FluentHashMap.mapStrObj("text", text), this::disableCapitalizeNext);
        }
    }

    @Override
    public void removeLastEntry(WritingEventSource src) {
        final WriterEntryI lastEntry = this.getLastEntry();
        if (lastEntry != null) {
            this.executeEvent(d -> {
                if (d.isExternalWritingDevice()) {
                    // don't call it on VKB
                } else {
                    d.removeLastEntry(src);
                }
            }, src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.LAST_ENTRY));
        }
    }

    @Override
    public void removeLastWord(WritingEventSource src) {
        final int lastWordAndStopCharCount = writingStateEntryContainer.getLastWordAndStopCharCount();
        this.executeEvent(d -> {
            if (d.isExternalWritingDevice()) {
                d.removeLastChars(src, lastWordAndStopCharCount);
            } else {
                d.removeLastWord(src);
            }
        }, src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.LAST_WORD));
    }

    @Override
    public void removeLastWordPrediction(WritingEventSource src) {
        this.executeEvent(d -> {
            d.removeLastWordPrediction(src);
        }, src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.LAST_WORD));
    }

    @Override
    public void removeAll(WritingEventSource src) {
        this.updateTotalTextWithCurrentText();
        this.executeEvent(d -> {
            if (d.isExternalWritingDevice()) {
                // don't call it on VKB
            } else {
                d.removeAll(src);
            }
        }, src, WritingEventType.DELETION, FluentHashMap.mapStrObj("type", DeletionTypes.ALL));
    }
    //========================================================================

    // EVENTS
    //========================================================================
    public void addWritingEventListener(Consumer<WritingEventI> eventListener) {
        this.writingEventListeners.add(eventListener);
    }

    public void removeWritingEventListener(Consumer<WritingEventI> eventListener) {
        this.writingEventListeners.remove(eventListener);
    }

    private void executeEvent(Consumer<WritingDeviceI> exe, WritingEventSource src, WritingEventType type, Map<String, Object> values) {
        executeEvent(exe, src, type, values, null);
    }

    private void executeEvent(Consumer<WritingDeviceI> exe, WritingEventSource src, WritingEventType type, Map<String, Object> values, Runnable postExeAction) {
        if (!writingDisabled.get()) {
            if (!writingEventListeners.isEmpty()) {
                WritingControllerStateI stateBeforeEvent = getCurrentState();
                runOnWritingDeviceAndDoPostAction(exe, postExeAction);
                // TODO : performance problem caused by listeners on FX thread ?
                // Implementation should make operation on event async (e.g. writing the log a file)

                // TODO : this cause the after event result to be incorrect about prediction because prediction is not instant...
                FXThreadUtils.runOnFXThread(() -> {
                    WritingControllerStateI stateAfterEvent = getCurrentState();
                    for (Consumer<WritingEventI> listener : writingEventListeners) {
                        listener.accept(new WritingEvent(stateBeforeEvent, stateAfterEvent, src, type, values));
                    }
                });
            } else {
                runOnWritingDeviceAndDoPostAction(exe, postExeAction);
            }
        }
    }

    private void runOnWritingDeviceAndDoPostAction(Consumer<WritingDeviceI> exe, Runnable postExeAction) {
        for (WritingDeviceI device : this.writingDevices) {
            exe.accept(device);
        }
        if (postExeAction != null) {
            postExeAction.run();
        }
        FXThreadUtils.runOnFXThread(() -> this.writingStateEntryContainer.evaluateAutoUpperCase());
    }

    private WritingControllerStateI getCurrentState() {
        return new WritingControllerState(textBeforeCaretProperty().get(), textAfterCaretProperty().get(), WordPredictionController.INSTANCE.getLastPredictionResult());
    }

    private enum DeletionTypes {
        LAST_ENTRY, LAST_CHAR, LAST_CHARS, NEXT_CHAR, NEXT_CHARS, ALL, LAST_WORD
    }

    private enum CaretMoveTypes {
        FORWARD, BACKWARD, END, START, UP, DOWN, TO_MOUSE
    }
    //========================================================================

    /**
     * Try to append the current editor text to the user message bank to train a
     * dynamic model.
     */
    private void updateTotalTextWithCurrentText() {
        final String textToAppend = this.currentTextProperty().get();
        if (!StringUtils.isBlank(textToAppend)) {
            // TODO : could learn twice the same text when the user close the configuration,
            // launch it again with the same message but append a new message.
            // text is different but the first part of the text will be learned twice.
            // TODO : could launch dynamic training here

            // Append only if changed
            if (!StringUtils.isEquals(textToAppend, lastAddedTextToTotalText)) {
                this.totalTextBuilder.append(textToAppend).append("\n");
                this.lastAddedTextToTotalText = textToAppend;
                // TODO : save in a file for debug purposes...
            } else {
                LOGGER.warn("Didn't add text \"{}\" to total text because it's the same than a previous added text", textToAppend);
            }
        }
    }

    // USE MODE
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        WriterDisplayerI referenceWriterDisplayer = null;

        final Collection<DisplayableComponentI> allComponent = configuration.getAllComponent().values();
        // Try to find the first root WriterDisplayerI as reference editor
        for (DisplayableComponentI comp : allComponent) {
            if (comp instanceof WriterDisplayerI && comp instanceof RootGraphicComponentI) {
                referenceWriterDisplayer = (WriterDisplayerI) comp;
                break;
            }
        }
        // Accept other writer displayer
        if (referenceWriterDisplayer == null) {
            for (DisplayableComponentI comp : allComponent) {
                if (comp instanceof WriterDisplayerI) {
                    referenceWriterDisplayer = (WriterDisplayerI) comp;
                    break;
                }
            }
        }
        this.writingStateEntryContainer.setCurrentDisplayerAndBindChangeListenerCaret(referenceWriterDisplayer);
        this.writingStateEntryContainer.setWriterEntries(configuration.getUseModeWriterEntries());

        if (configuration.virtualKeyboardProperty().get()) {
            if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD)) {
                this.writingDevices.add(VirtualKeyboardController.INSTANCE);
            } else {
                LOGGER.info("Didn't add VirtualKeyboardController to writing device as {} is enabled", GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD);
            }
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        writingDisabled.set(false);
        this.writingDevices.remove(VirtualKeyboardController.INSTANCE);
        this.updateTotalTextWithCurrentText();
        configuration.getUseModeWriterEntries().clear();
        configuration.getUseModeWriterEntries().addAll(writingStateEntryContainer.getWriterEntries());
        this.writingStateEntryContainer.clearCurrentDisplayerAndUnbindCaret();
        this.removeAll(WritingEventSource.SYSTEM);
    }

    public String getTotalTextFromStart() {
        return totalTextBuilder.toString();
    }

    public WriterDisplayerI getReferencedTextEditor() {
        return this.writingStateEntryContainer.getCurrentDisplayer();
    }

    public void initExampleEntriesIfNeeded() {
        if (getWriterEntries().isEmpty() && !AppModeController.INSTANCE.isUseMode()) {
            this.getWriterEntries().add(new WriterEntry(Translation.getText("text.editor.example.line1.first") + " ", false));
            WriterEntry imageEntry = new WriterEntry(Translation.getText("text.editor.example.line1.second") + " ", false);
            imageEntry.imageProperty().set(new StaticImageElement(IconHelper.get("example_image_entry.png")));
            this.getWriterEntries().add(imageEntry);
            this.getWriterEntries().add(new WriterEntry(Translation.getText("text.editor.example.line2"), false));
            this.moveCaretForward(WritingEventSource.SYSTEM);
            this.moveCaretForward(WritingEventSource.SYSTEM);
            this.moveCaretForward(WritingEventSource.SYSTEM);
        }
    }
    //========================================================================
}
