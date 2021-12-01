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

package org.lifecompanion.base.data.component.keyoption.simplercomp;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

public class RectangleOnKeyForKeyViewAdded extends Pane {
    private static final int STRIKE_OUT_WIDTH = 2;
    private static final double STRIKE_OUT_BACKGROUND_REDUCTION = 0.3;
    public static final Color STRIKE_OUT_COLOR = Color.DARKRED, CURRENT_COLOR = Color.DARKORANGE;


    private final Rectangle borderAndFillRectangle;
    private final Line strikeOutLine;

    public RectangleOnKeyForKeyViewAdded() {
        borderAndFillRectangle = new Rectangle(0, 0);
        borderAndFillRectangle.widthProperty().bind(widthProperty());
        borderAndFillRectangle.heightProperty().bind(heightProperty());
        borderAndFillRectangle.setFill(null);
        borderAndFillRectangle.setStrokeWidth(STRIKE_OUT_WIDTH);
        getChildren().add(borderAndFillRectangle);

        strikeOutLine = new Line();
        strikeOutLine.endXProperty().bind(widthProperty());
        strikeOutLine.endYProperty().bind(heightProperty());
        strikeOutLine.setStrokeWidth(STRIKE_OUT_WIDTH);
        strikeOutLine.setStrokeLineCap(StrokeLineCap.ROUND);
        strikeOutLine.setVisible(false);
        getChildren().add(strikeOutLine);
    }

    public RectangleOnKeyForKeyViewAdded withStrokeColor(Color color) {
        borderAndFillRectangle.setStroke(color);
        return this;
    }

    public RectangleOnKeyForKeyViewAdded withBackgroundReduction() {
        borderAndFillRectangle.setFill(Color.BLACK.deriveColor(0.0, 1.0, 1.0, STRIKE_OUT_BACKGROUND_REDUCTION));
        return this;
    }

    public RectangleOnKeyForKeyViewAdded withStrikeout() {
        strikeOutLine.setVisible(true);
        strikeOutLine.setStroke(borderAndFillRectangle.getStroke());
        return this;
    }
}
