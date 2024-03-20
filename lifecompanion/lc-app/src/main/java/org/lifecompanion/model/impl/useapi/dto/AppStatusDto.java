package org.lifecompanion.model.impl.useapi.dto;

public class AppStatusDto {
    private Status status;
    private SelectionModeStatus selectionModeStatus;

    // Todo : current configuration, current profile, session duration ?

    public AppStatusDto(Status status, SelectionModeStatus selectionModeStatus) {
        this.status = status;
        this.selectionModeStatus = selectionModeStatus;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public SelectionModeStatus getSelectionModeStatus() {
        return selectionModeStatus;
    }

    public void setSelectionModeStatus(SelectionModeStatus selectionModeStatus) {
        this.selectionModeStatus = selectionModeStatus;
    }

    public enum Status {
        STARTING,
        IN_USE_MODE,
        IN_EDIT_MODE,
        STOPPING
    }

    public enum SelectionModeStatus {
        PAUSED, PLAYING;

        public static SelectionModeStatus fromPlayingProperty(boolean playing) {
            return playing ? PLAYING : PAUSED;
        }
    }
}
