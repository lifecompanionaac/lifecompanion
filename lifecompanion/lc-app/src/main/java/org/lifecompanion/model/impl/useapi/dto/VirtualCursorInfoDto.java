package org.lifecompanion.model.impl.useapi.dto;

public class VirtualCursorInfoDto {
    private double selectionZoneWidth, selectionZoneHeight;
    private double cursorX, cursorY;

    public VirtualCursorInfoDto(double selectionZoneWidth, double selectionZoneHeight, double cursorX, double cursorY) {
        this.selectionZoneWidth = selectionZoneWidth;
        this.selectionZoneHeight = selectionZoneHeight;
        this.cursorX = cursorX;
        this.cursorY = cursorY;
    }

    public double getSelectionZoneWidth() {
        return selectionZoneWidth;
    }

    public double getSelectionZoneHeight() {
        return selectionZoneHeight;
    }

    public double getCursorX() {
        return cursorX;
    }

    public double getCursorY() {
        return cursorY;
    }
}
