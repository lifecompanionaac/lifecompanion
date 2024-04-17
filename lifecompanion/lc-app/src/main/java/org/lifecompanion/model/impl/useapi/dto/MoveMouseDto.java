package org.lifecompanion.model.impl.useapi.dto;

public class MoveMouseDto {
    private Integer x, y;

    public MoveMouseDto(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
    }
}
