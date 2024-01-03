package org.lifecompanion.model.api.style;

import org.lifecompanion.framework.commons.translation.Translation;

public enum ShapeStyle {
    CLASSIC("key.shape.classic",
            false,
            (width, height, radius) -> "m 0 0 h " + width + " a " + radius + " " + radius + " 0 0 1 " + radius + " " + radius +
                    " v " + height + " a " + radius + "," + radius + " 0 0 1 -" + radius + " " + radius +
                    " h -" + width + " a " + radius + "," + radius + " 0 0 1 -" + radius + " -" + radius +
                    " v -" + height + " a " + radius + "," + radius + " 0 0 1 " + radius + " -" + radius + " z"),//
    TP_ANGLE_CUT(
            "key.shape.tp.angle.cut",
            true,
            (width, height, radius) -> {
                int cut = Math.max(8, radius);
                return "m 0 0 h " + (width - cut) + " l " + cut + " " + cut +
                        " v " + (height - cut) + " a " + radius + " " + radius + " 0 0 1 -" + radius + " " + radius +
                        " h -" + width + " a " + radius + " " + radius + " 0 0 1 -" + radius + " -" + radius +
                        " v -" + (height - radius) + " a " + radius + " " + radius + " 0 0 1 " + radius + " -" + radius + " z";
            }),//
    BL_ANGLE_ROUND("key.shape.bl.angle.round",
            true,
            (width, height, radius) -> {
                int cut = Math.max(6, radius);
                return "m 0 0 h " + width +
                        " v " + height +
                        " h -" + (width - cut) + " a " + cut + " " + cut + " 0 0 1 -" + cut + " -" + cut +
                        " v -" + (height - cut) + " z";
            });//


    private final String nameId;
    private final boolean useForShape;
    private final DynamicSvgPath dynamicSvgPath;

    ShapeStyle(String nameId, boolean useForShape, DynamicSvgPath dynamicSvgPath) {
        this.nameId = nameId;
        this.useForShape = useForShape;
        this.dynamicSvgPath = dynamicSvgPath;
    }

    public String getName() {
        return Translation.getText(nameId);
    }

    public String getSvgPathFor(int angle) {
        return useForShape ? dynamicSvgPath.draw(100, 65, angle) : null;
    }

    public String getCellSvg() {
        return dynamicSvgPath.draw(100, 65, 6);
    }

    interface DynamicSvgPath {
        String draw(int width, int height, int radius);
    }
}
