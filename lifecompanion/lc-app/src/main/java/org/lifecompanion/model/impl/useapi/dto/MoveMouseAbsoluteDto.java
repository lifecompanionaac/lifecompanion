package org.lifecompanion.model.impl.useapi.dto;

public class MoveMouseAbsoluteDto {
    private Integer x, y;

    public MoveMouseAbsoluteDto(Integer x, Integer y) {
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
