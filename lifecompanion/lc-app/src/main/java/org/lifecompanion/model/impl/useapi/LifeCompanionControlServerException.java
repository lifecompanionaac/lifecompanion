package org.lifecompanion.model.impl.useapi;

import org.lifecompanion.model.impl.useapi.dto.ErrorDto;

public class LifeCompanionControlServerException extends RuntimeException {
    private final String errorId;
    private final String errorMessage;

    public LifeCompanionControlServerException(String errorId, String errorMessage) {
        super(errorMessage);
        this.errorId = errorId;
        this.errorMessage = errorMessage;
    }

    public String getErrorId() {
        return errorId;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public ErrorDto toDto() {
        return new ErrorDto(getErrorId(), getErrorMessage());
    }

    public static ErrorDto toDto(Throwable throwable) {
        return new ErrorDto("error.unknown", throwable.getClass().getSimpleName() + " :\n" + throwable.getMessage());
    }
}
