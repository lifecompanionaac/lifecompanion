package org.lifecompanion.model.impl.useapi.dto;

public class ActionConfirmationDto {
    private boolean done;
    private String message;

    public ActionConfirmationDto(boolean done, String message) {
        this.done = done;
        this.message = message;
    }

    public static ActionConfirmationDto ok() {
        return new ActionConfirmationDto(true, "OK");
    }

    public static ActionConfirmationDto nok(String message) {
        return new ActionConfirmationDto(false, message);
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
