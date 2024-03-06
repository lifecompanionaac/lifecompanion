package org.lifecompanion.controller.textcomponent;

import org.lifecompanion.model.api.configurationcomponent.WriterEntryI;

import java.util.List;

public class WritingStateEntryContainerState {

    private final List<WriterEntryI> entries;

    private final int caretPosition;


    public WritingStateEntryContainerState(List<WriterEntryI> entries, int caretPosition) {
        this.entries = entries;
        this.caretPosition = caretPosition;
    }

    public List<WriterEntryI> getEntries() {
        return entries;
    }

    public int getCaretPosition() {
        return caretPosition;
    }
}
