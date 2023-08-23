package org.lifecompanion.model.impl.useapi.dto;

public class AliveDto {
    private Status status;

    public AliveDto(Status status) {
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
