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

package org.lifecompanion.model.impl.selectionmode;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeParameterI;
import org.lifecompanion.model.api.selectionmode.VirtualCursorSelectionModeI;
import org.lifecompanion.ui.selectionmode.VirtualDirectionalCursorSelectionModeView;
import org.lifecompanion.util.LangUtils;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualDirectionalCursorSelectionMode extends AbstractAutoActivationSelectionMode<VirtualDirectionalCursorSelectionModeView> implements VirtualCursorSelectionModeI {

    /**
     * Cursor position in scene (relative to global scene)
     */
    private final DoubleProperty cursorX, cursorY;

    private final DoubleProperty selectionZoneWidth, selectionZoneHeight;

    public VirtualDirectionalCursorSelectionMode() {
        this.cursorX = new SimpleDoubleProperty(0.0);
        this.cursorY = new SimpleDoubleProperty(0.0);
        this.selectionZoneWidth = new SimpleDoubleProperty(0.0);
        this.selectionZoneHeight = new SimpleDoubleProperty(0.0);
        this.view = new VirtualDirectionalCursorSelectionModeView(this);
        this.drawProgress.set(false);
    }

    @Override
    protected boolean shouldExecuteAutoActivation(SelectionModeParameterI parameters) {
        return parameters.enableAutoActivationProperty().get();
    }

    public DoubleProperty cursorXProperty() {
        return cursorX;
    }

    public DoubleProperty cursorYProperty() {
        return cursorY;
    }

    @Override
    public void moveRelative(Integer dx, Integer dy) {
        if (dx != null) {
            cursorX.set(cursorX.get() + dx);
        }
        if (dy != null) {
            cursorY.set(cursorY.get() + dy);
        }
    }

    @Override
    public void moveAbsolute(Integer x, Integer y) {
        cursorX.set(LangUtils.nullToZero(x));
        cursorY.set(LangUtils.nullToZero(y));
    }

    @Override
    public void pressed() {
        this.view.pressed();
    }

    @Override
    public void released() {
        this.view.released();

    }

    @Override
    public double getCursorX() {
        return cursorX.get();
    }

    @Override
    public double getSelectionZoneWidth() {
        return selectionZoneWidth.get();
    }

    @Override
    public double getSelectionZoneHeight() {
        return selectionZoneHeight.get();
    }

    @Override
    public void moveCenter() {
        this.cursorX.set(getSelectionZoneWidth() / 2.0);
        this.cursorY.set(getSelectionZoneHeight() / 2.0);
    }

    @Override
    public double getCursorY() {
        return cursorY.get();
    }

    @Override
    public void init(LCConfigurationI configuration, SelectionModeI previousSelectionMode) {
        super.init(configuration, previousSelectionMode);
        this.selectionZoneWidth.set(configuration.computedWidthProperty().get());
        this.selectionZoneHeight.set(configuration.computedHeightProperty().get());
        moveCenter();
    }
}
