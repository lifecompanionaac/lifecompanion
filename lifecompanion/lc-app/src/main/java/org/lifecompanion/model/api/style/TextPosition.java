package org.lifecompanion.model.api.style;

import org.lifecompanion.framework.commons.translation.Translation;

public enum TextPosition {
    CENTER("text.location.center", "text-position/icon_text_position_center.png", "tooltip.text.position.center"),
    BOTTOM("text.location.bottom", "text-position/icon_text_position_bottom.png", "tooltip.text.position.bottom"),
    TOP("text.location.top", "text-position/icon_text_position_top.png", "tooltip.text.position.top"),
    LEFT("text.location.left", "text-position/icon_text_position_left.png", "tooltip.text.position.left"),
    RIGHT("text.location.right", "text-position/icon_text_position_right.png", "tooltip.text.position.right");

    private final String nameId, iconUrl, tooltipId;

    TextPosition(String nameId, String iconUrl, String tooltipId) {
        this.nameId = nameId;
        this.iconUrl = iconUrl;
        this.tooltipId = tooltipId;
    }

    public String getName() {
        return Translation.getText(nameId);
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getTooltipId() {
        return Translation.getText(tooltipId);
    }
}
