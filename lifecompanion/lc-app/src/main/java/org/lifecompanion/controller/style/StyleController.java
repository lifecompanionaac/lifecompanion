/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.controller.style;

import javafx.scene.control.ContentDisplay;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.lifecompanion.model.api.style.*;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.style.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum StyleController {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(StyleController.class);

    private static final String DEFAULT_FONT = "Deja Vu Sans";

    private final GridCompStyleI defaultShapeStyleForGrid;
    private final ShapeCompStyleI defaultShapeStyleForTextEditor;
    private final KeyCompStyleI defaultKeyStyle;
    private final TextCompStyleI defaultTextStyleForKey;
    private final TextCompStyleI defaultTextStyleForTextEditor;


    StyleController() {
        this.defaultShapeStyleForGrid = new GridCompStyle() {
            {
                this.backgroundColorProperty().selected().setValue(LCGraphicStyle.LC_VERY_LIGHT_GRAY);
                this.strokeSizeProperty().selected().setValue(0);
                this.strokeColorProperty().selected().setValue(Color.TRANSPARENT);
                this.shapeRadiusProperty().selected().setValue(0);
                this.vGapProperty().selected().setValue(5);
                this.hGapProperty().selected().setValue(5);
            }
        };
        this.defaultShapeStyleForTextEditor = new TextDisplayerShapeCompStyle() {
            {
                this.backgroundColorProperty().selected().setValue(Color.WHITE);
                this.strokeSizeProperty().selected().setValue(1);
                this.strokeColorProperty().selected().setValue(LCGraphicStyle.LC_GRAY);
                this.shapeRadiusProperty().selected().setValue(0);
            }
        };
        this.defaultKeyStyle = new KeyCompStyle() {
            {
                this.backgroundColorProperty().selected().setValue(Color.WHITE);
                this.strokeSizeProperty().selected().setValue(1);
                this.strokeColorProperty().selected().setValue(LCGraphicStyle.LC_GRAY);
                this.shapeRadiusProperty().selected().setValue(0);
                this.autoFontSizeProperty().selected().setValue(false);
                this.textPositionProperty().selected().setValue(TextPosition.CENTER);
                this.shapeStyleProperty().selected().setValue(ShapeStyle.CLASSIC);

            }
        };
        this.defaultTextStyleForKey = new KeyTextCompStyle() {
            {
                this.fontFamilyProperty().selected().setValue(DEFAULT_FONT);
                this.fontSizeProperty().selected().setValue(16);
                this.colorProperty().selected().setValue(LCGraphicStyle.LC_BLACK);
                this.boldProperty().selected().setValue(true);
                this.italicProperty().selected().setValue(false);
                this.underlineProperty().selected().setValue(false);
                this.upperCaseProperty().selected().setValue(false);
                this.textAlignmentProperty().selected().setValue(TextAlignment.CENTER);
            }
        };
        this.defaultTextStyleForTextEditor = new TextDisplayerTextCompStyle() {
            {
                this.fontFamilyProperty().selected().setValue(DEFAULT_FONT);
                this.fontSizeProperty().selected().setValue(24);
                this.colorProperty().selected().setValue(LCGraphicStyle.LC_BLACK);
                this.boldProperty().selected().setValue(false);
                this.italicProperty().selected().setValue(false);
                this.underlineProperty().selected().setValue(false);
                this.upperCaseProperty().selected().setValue(false);
                this.textAlignmentProperty().selected().setValue(TextAlignment.LEFT);
            }
        };
    }


    // Class part : "Default styles"
    //========================================================================
    public GridCompStyleI getDefaultShapeStyleForGrid() {
        return this.defaultShapeStyleForGrid;
    }

    public ShapeCompStyleI getDefaultShapeStyleForTextEditor() {
        return this.defaultShapeStyleForTextEditor;
    }

    public KeyCompStyleI getDefaultKeyStyle() {
        return this.defaultKeyStyle;
    }

    public TextCompStyleI getDefaultTextStyleForKey() {
        return this.defaultTextStyleForKey;
    }

    public TextCompStyleI getDefaultTextStyleForTextEditor() {
        return this.defaultTextStyleForTextEditor;
    }
    //========================================================================

}
