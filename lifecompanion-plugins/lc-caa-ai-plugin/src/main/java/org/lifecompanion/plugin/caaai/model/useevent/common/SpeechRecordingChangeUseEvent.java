package org.lifecompanion.plugin.caaai.model.useevent.common;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.caaai.controller.CAAAIController;

import java.util.function.Consumer;

public abstract class SpeechRecordingChangeUseEvent extends BaseUseEventGeneratorImpl {
    private final Boolean targetRecording;

    private final Consumer<Boolean> recordingChange;

    public SpeechRecordingChangeUseEvent(Boolean targetRecording) {
        super();

        this.parameterizableAction = false;
        this.targetRecording = targetRecording;

        this.recordingChange = value -> {
            if (value == this.targetRecording) {
                this.useEventListener.fireEvent(this, null, null);
            }
        };
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        CAAAIController.INSTANCE.addSpeechRecordingChangeListener(this.recordingChange);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        CAAAIController.INSTANCE.removeSpeechRecordingChangeListener(this.recordingChange);
    }
}

