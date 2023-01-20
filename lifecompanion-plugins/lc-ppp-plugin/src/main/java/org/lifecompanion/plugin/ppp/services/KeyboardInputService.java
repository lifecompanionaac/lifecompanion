package org.lifecompanion.plugin.ppp.services;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;

import java.util.function.Consumer;

public enum KeyboardInputService implements ModeListenerI {
    INSTANCE;

    private Consumer<String> internalKeyboardInputCallback;
    private Runnable cancelKeyboardInputCallback;
    private String keyboardContentBackup;
    private final StringProperty keyboardInputRequest;

    KeyboardInputService() {
        this.keyboardInputRequest = new SimpleStringProperty();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.internalKeyboardInputCallback = null;
        this.keyboardContentBackup = null;
        this.keyboardInputRequest.set(null);
    }

    public void startInput(Consumer<String> keyboardInputCallback, String keyboardInputRequest, Runnable cancelCallback) {
        this.internalKeyboardInputCallback = keyboardInputCallback;
        this.keyboardInputRequest.set(keyboardInputRequest);
        this.cancelKeyboardInputCallback = cancelCallback;

        this.keyboardContentBackup = WritingStateController.INSTANCE.currentTextProperty().get();
        WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);

        NavigationService.INSTANCE.moveToKeyboardGrid();
    }

    public void finishInput() {
        String keyboardInput = StringUtils.stripToEmpty(WritingStateController.INSTANCE.currentTextProperty().get());
        if (this.keyboardContentBackup != null) {
            WritingStateController.INSTANCE.removeAll(WritingEventSource.SYSTEM);
            // Content backup is skipped because of visual text glitching when restoring if containing content.
            // WritingStateController.INSTANCE.insertText(WritingEventSource.SYSTEM, this.keyboardContentBackup);
            this.keyboardContentBackup = null;
        }

        Consumer<String> keyboardInputCallback = this.internalKeyboardInputCallback;
        this.internalKeyboardInputCallback = null;
        if (keyboardInputCallback != null) {
            this.keyboardInputRequest.set(null);
            keyboardInputCallback.accept(keyboardInput.length() == 0 ? null : keyboardInput);
        }
    }

    public void cancelInput() {
        if (this.cancelKeyboardInputCallback != null) {
            Runnable cancelKeyboardInputCallbackBackup = this.cancelKeyboardInputCallback;
            this.cancelKeyboardInputCallback = null;
            cancelKeyboardInputCallbackBackup.run();
        }
    }

    public StringProperty keyboardInputRequestProperty() {
        return keyboardInputRequest;
    }
}
