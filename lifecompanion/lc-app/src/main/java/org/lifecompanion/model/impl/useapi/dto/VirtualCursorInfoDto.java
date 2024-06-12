package org.lifecompanion.model.impl.useapi.dto;

public class VirtualCursorInfoDto {
    private double sceneWidth, sceneHeight;
    private double cursorX, cursorY;

    public VirtualCursorInfoDto(double sceneWidth, double sceneHeight, double cursorX, double cursorY) {
        this.sceneWidth = sceneWidth;
        this.sceneHeight = sceneHeight;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    public double getSceneWidth() {
        return sceneWidth;
    }

    public double getSceneHeight() {
        return sceneHeight;
    }

    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }
}
