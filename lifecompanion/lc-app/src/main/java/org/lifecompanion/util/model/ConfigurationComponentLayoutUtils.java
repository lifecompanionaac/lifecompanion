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

package org.lifecompanion.util.model;

import javafx.scene.shape.Rectangle;
import javafx.util.Pair;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;

public class ConfigurationComponentLayoutUtils {

    public static Pair<Double, Double> getConfigurationPosition(final GridPartComponentI component) {
        Pair<Double, Double> position = new Pair<>(component.layoutXProperty().get(), component.layoutYProperty().get());
        if (component.gridParentProperty().get() != null
                || component.stackParentProperty().get() != null && component.stackParentProperty().get() instanceof GridPartComponentI) {
            GridPartComponentI parent = component.gridParentProperty().get() != null ? component.gridParentProperty().get()
                    : (GridPartComponentI) component.stackParentProperty().get();
            Pair<Double, Double> parentPos = getConfigurationPosition(parent);
            position = new Pair<>(position.getKey() + parentPos.getKey(), position.getValue() + parentPos.getValue());
        } else {
            RootGraphicComponentI root = component.rootParentProperty().get();
            position = new Pair<>(root.xProperty().get() + position.getKey(), root.yProperty().get() + position.getValue());
        }
        return position;
    }

    public static Pair<Double, Double> getLinePosition(final GridComponentI grid, final int lineIndex) {
        Pair<Double, Double> basePos = getConfigurationPosition(grid);
        double x = basePos.getKey() + grid.hGapProperty().get();
        double y = basePos.getValue() + grid.caseHeightProperty().get() * lineIndex + (lineIndex + 1) * grid.vGapProperty().get();
        return new Pair<>(x, y);
    }

    public static Pair<Double, Double> getLineSize(final GridComponentI grid, final int lineIndex, final int lineSpan) {
        double h = grid.caseHeightProperty().get() * lineSpan + (lineSpan - 1) * grid.vGapProperty().get();
        double w = grid.caseWidthProperty().get() * grid.columnCountProperty().get()
                + (grid.columnCountProperty().get() - 1) * grid.hGapProperty().get();
        return new Pair<>(w, h);
    }

    public static Pair<Double, Double> getColumnPosition(final GridComponentI grid, final int columnIndex) {
        Pair<Double, Double> basePos = getConfigurationPosition(grid);
        double x = basePos.getKey() + grid.caseWidthProperty().get() * columnIndex + (columnIndex + 1) * grid.hGapProperty().get();
        double y = basePos.getValue() + grid.vGapProperty().get();
        return new Pair<>(x, y);
    }

    public static Pair<Double, Double> getColumnSize(final GridComponentI grid, final int columnIndex, final int columnSpan) {
        double w = grid.caseWidthProperty().get() * columnSpan + (columnSpan - 1) * grid.hGapProperty().get();
        double h = grid.caseHeightProperty().get() * grid.rowCountProperty().get() + (grid.rowCountProperty().get() - 1) * grid.vGapProperty().get();
        return new Pair<>(w, h);
    }

    public static void computeArcAndStrokeFor(final Rectangle rectangle, final double wantedArc, final double width, final double height,
                                              final double strokeSize) {
        rectangle.setStrokeWidth(strokeSize);
        double arcSize = computeArcAndStroke(wantedArc, width, height, strokeSize);
        rectangle.setArcWidth(arcSize);
        rectangle.setArcHeight(arcSize);
    }

    public static double computeArcAndStroke(final double wantedArc, final double width, final double height, final double strokeSize) {
        return Math.min(Math.min(height / 2.0, width / 2.0), wantedArc) * 2.0;
    }
}
