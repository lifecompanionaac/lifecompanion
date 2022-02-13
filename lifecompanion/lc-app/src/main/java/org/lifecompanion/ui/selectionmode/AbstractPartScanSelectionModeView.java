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

package org.lifecompanion.ui.selectionmode;

import javafx.animation.TranslateTransition;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.selectionmode.ScanningDirection;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.model.impl.selectionmode.AbstractPartScanSelectionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractPartScanSelectionModeView extends AbstractSelectionModeView<AbstractPartScanSelectionMode<?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPartScanSelectionModeView.class);

    //Animations
    private final TranslateTransition lineTranslateTransition;

    private Rectangle lineStrokeRectangle;
    private final Rectangle lineProgressRectangle;
    private final Rectangle clipLineProgressRectangle;

    private final BooleanProperty lineProgressRectangleVisible;

    public AbstractPartScanSelectionModeView(final AbstractPartScanSelectionMode<?> selectionModeP, final ScanningDirection direction) {
        super(selectionModeP, direction);
        this.lineProgressRectangleVisible = new SimpleBooleanProperty(false);

        //Rectangles
        this.lineStrokeRectangle = getOtherOptionalStrokeRectangle();
        lineStrokeRectangle.boundsInParentProperty().addListener((obs, ov, nv) -> requestBackgroundReductionUpdate());
        this.lineProgressRectangle = new Rectangle();
        this.clipLineProgressRectangle = new Rectangle();
        this.configureAndAddScanningRectangles(this.lineStrokeRectangle, this.selectionMode.currentPartNotNullProperty().not(),
                this.lineProgressRectangleVisible, this.lineProgressRectangle, this.clipLineProgressRectangle);

        //Animation
        this.lineTranslateTransition = new TranslateTransition(Duration.millis(AbstractSelectionModeView.TIME), this.lineStrokeRectangle);
    }

    private void updateLineMoveAnimation(final int primaryIndex, final int span, final GridComponentI grid, final long progressTime,
                                         final boolean enableAnimation) {
        //Get values
        Pair<Double, Double> pos = this.getPosition(primaryIndex, span, grid);
        Pair<Double, Double> size = this.getSize(primaryIndex, span, grid);
        //Launch
        this.updateMoveAnimation(enableAnimation, null, pos, size,
                LangUtils.nullToZeroDouble(grid.getGridShapeStyle().shapeRadiusProperty().value().getValue()), this.lineTranslateTransition,
                this.lineStrokeRectangle, this.lineProgressRectangle, this.lineProgressRectangleVisible, () -> this.startLineProgressTransition(pos, size, progressTime));
    }

    @Override
    protected Rectangle getOtherOptionalStrokeRectangle() {
        if (lineStrokeRectangle == null) lineStrokeRectangle = new Rectangle();
        return lineStrokeRectangle;
    }

    protected abstract Pair<Double, Double> getPosition(final int primaryIndex, final int span, GridComponentI grid);

    protected abstract Pair<Double, Double> getSize(final int primaryIndex, final int span, GridComponentI grid);

    private void startLineProgressTransition(final Pair<Double, Double> position, final Pair<Double, Double> size, final long progressTime) {
        this.startProgressTransition(false, position, size, progressTime, this.direction.opposite(), this.lineProgressRectangleVisible,
                this.lineProgressRectangle);
    }

    public void moveToPrimaryIndex(final int primaryIndex, final int span, final GridComponentI grid, final long progressTime,
                                   final boolean enableAnimation) {
        FXThreadUtils.runOnFXThread(() -> {
            if (this.selectionMode.currentGridProperty().get() == grid) {
                this.updateLineMoveAnimation(primaryIndex, span, grid, progressTime, enableAnimation);
            } else {
                /*
                 * FIX : on change grid action (not on FX Thread), the call can be delayed so the grid has changed and this runnable is called by FX Thread.
                 * This create visual error, so we have to check that current grid is the same than the one on initial call.
                 */
                AbstractPartScanSelectionModeView.LOGGER.warn(
                        "Grid changed since moveToPrimaryIndex was called, so skip the call to updateLineMoveAnimation(...), changed from {} to {}",
                        grid, this.selectionMode.currentGridProperty().get());
            }
        });
    }
}
