package org.lifecompanion.model.api.style;

import org.lifecompanion.framework.commons.translation.Translation;

public enum ShapeStyle {
    CLASSIC("key.shape.classic", null, "M 34.393246,89.207481 H 182.71412 V 191.67068 H 34.393246 Z"),
    TP_ANGLE_CUT("key.shape.tp.angle.cut", "M 39.519508,83.345627 H 166.43792 l 9.62655,7.599905 V 181.38441 H 39.519508 Z"),
    BL_ANGLE_ROUND("key.shape.bl.angle.round",
            "m 49.343033,91.866421 c 0,0 -0.401999,46.436939 0,66.136549 0.116313,5.69982 8.019763,10.20394 13.72077,10.21049 37.256467,0.0428 114.911417,0 114.911417,0 V 91.866421 Z");

    private final String nameId, svg, cellSvg;

    ShapeStyle(String nameId, String svg) {
        this(nameId, svg, svg);
    }

    ShapeStyle(String nameId, String svg, String cellSvg) {
        this.nameId = nameId;
        this.svg = svg;
        this.cellSvg = cellSvg;
    }

    public String getName() {
        return Translation.getText(nameId);
    }

    public String getCustomSvg() {
        return svg;
    }

    public String getCellSvg() {
        return cellSvg;
    }
}
