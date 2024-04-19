package org.lifecompanion.model.impl.useapi.dto;

public class MoveMouseRelativeDto {
    private Integer dx, dy;

    public MoveMouseRelativeDto(Integer dx, Integer dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public Integer getDx() {
        return dx;
    }

    public Integer getDy() {
        return dy;
    }
}
