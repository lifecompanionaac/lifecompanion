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
import javafx.scene.Node;
import org.lifecompanion.model.api.selectionmode.VirtualCursorSelectionModeI;
import org.lifecompanion.ui.selectionmode.VirtualDirectionalCursorSelectionModeView;
import org.lifecompanion.util.LangUtils;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class VirtualDirectionalCursorSelectionMode extends AbstractDirectSelectionMode<VirtualDirectionalCursorSelectionModeView> implements VirtualCursorSelectionModeI {

    private static final double MOVING_SPEED = 5.0; // FIXME : param

    /**
     * Cursor position in scene (relative to global scene)
     */
    private final DoubleProperty cursorX, cursorY;

    public VirtualDirectionalCursorSelectionMode() {
        super(true);
        this.cursorX = new SimpleDoubleProperty(0.0);
        this.cursorY = new SimpleDoubleProperty(0.0);
        this.view = new VirtualDirectionalCursorSelectionModeView(this);
        this.drawProgress.set(false);
    }

    @Override
    public Node getSelectionView() {
        return this.view;
    }

    public DoubleProperty cursorXProperty() {
        return cursorX;
    }

    public DoubleProperty cursorYProperty() {
        return cursorY;
    }


    @Override
    public void moveRight(Double amount) {
        cursorX.set(cursorX.get() + (amount != null ? amount : MOVING_SPEED));
    }

    @Override
    public void moveLeft(Double amount) {
        cursorX.set(cursorX.get() - (amount != null ? amount : MOVING_SPEED));
    }

    @Override
    public void moveUp(Double amount) {
        cursorY.set(cursorY.get() - (amount != null ? amount : MOVING_SPEED));
    }

    @Override
    public void moveDown(Double amount) {
        cursorY.set(cursorY.get() + (amount != null ? amount : MOVING_SPEED));
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

    public double getSceneWidth() {
        return view.getScene().getWidth();
    }

    public double getSceneHeight() {
        return view.getScene().getHeight();
    }

    @Override
    public double getCursorY() {
        return cursorY.get();
    }
}
