package org.lifecompanion.model.impl.useapi.dto;

public class MouseInfoDto {
    private double screenWidth, screenHeight;
    private double mouseX, mouseY;

    public MouseInfoDto(double screenWidth, double screenHeight, double mouseX, double mouseY) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public double getScreenWidth() {
        return screenWidth;
    }

    public double getScreenHeight() {
        return screenHeight;
    }

    public double getMouseX() {
        return mouseX;
    }

    public double getMouseY() {
        return mouseY;
    }
}
