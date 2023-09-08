package org.lifecompanion.model.impl.useapi.dto;

public class AppStatusDto {
    private Status status;

    // Todo : current configuration, current profile, session duration ?

    public AppStatusDto(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public enum Status {
        STARTING,
        IN_USE_MODE,
        IN_EDIT_MODE,
        STOPPING
    }
}
