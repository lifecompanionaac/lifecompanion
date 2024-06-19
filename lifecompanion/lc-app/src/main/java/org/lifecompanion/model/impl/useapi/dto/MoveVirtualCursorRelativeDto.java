package org.lifecompanion.model.impl.useapi.dto;

public class MoveVirtualCursorRelativeDto {
    private Integer dx, dy;

    public MoveVirtualCursorRelativeDto(Integer dx, Integer dy) {
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
