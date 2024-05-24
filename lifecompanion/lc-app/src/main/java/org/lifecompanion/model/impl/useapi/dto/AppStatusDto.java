package org.lifecompanion.model.impl.useapi.dto;

public class AppStatusDto {
    private Status status;
    private SelectionModeStatus selectionModeStatus;
    private GridDto mainCurrentGrid;
    private GridPartDto currentOverPart;

    // Todo : current configuration, current profile, session duration ?

    public AppStatusDto(Status status, SelectionModeStatus selectionModeStatus, GridDto mainCurrentGrid, GridPartDto currentOverPart) {
        this.status = status;
        this.selectionModeStatus = selectionModeStatus;
        this.mainCurrentGrid = mainCurrentGrid;
        this.currentOverPart = currentOverPart;
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

    public GridDto getMainCurrentGrid() {
        return mainCurrentGrid;
    }

    public GridPartDto getCurrentOverPart() {
        return currentOverPart;
    }

    public void setCurrentOverPart(GridPartDto currentOverPart) {
        this.currentOverPart = currentOverPart;
    }

    public void setMainCurrentGrid(GridDto mainCurrentGrid) {
        this.mainCurrentGrid = mainCurrentGrid;
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
