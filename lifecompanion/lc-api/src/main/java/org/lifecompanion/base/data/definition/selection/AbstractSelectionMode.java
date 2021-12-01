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

package org.lifecompanion.base.data.definition.selection;

import javafx.beans.property.*;
import javafx.scene.paint.Color;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.WriterDisplayerI;
import org.lifecompanion.api.definition.selection.ScanningMode;
import org.lifecompanion.api.definition.selection.SelectionModeI;
import org.lifecompanion.api.definition.selection.SelectionModeParameterI;
import org.lifecompanion.base.data.definition.selection.impl.DrawSelectionModeI;
import org.lifecompanion.base.data.definition.selection.view.AbstractSelectionModeView;

/**
 * Abstract implementation that just take care of parameters holding.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractSelectionMode<T extends AbstractSelectionModeView<?>> implements SelectionModeI, DrawSelectionModeI {

    /**
     * The mode parameters.
     */
    protected SelectionModeParameterI parameters;

    /**
     * Scanned grid
     */
    protected final ObjectProperty<GridComponentI> currentGrid;

    /**
     * To change the activation color
     */
    protected final ObjectProperty<Color> strokeColor, progressColor;
    protected final BooleanProperty drawProgress;
    protected final BooleanProperty playingProperty;
    protected final BooleanProperty backgroundReductionEnabled;
    protected final DoubleProperty backgroundReductionLevel;

    /**
     * Selection mode view
     */
    protected T view;

    /**
     * Current time in millis when selection press start
     */
    private long selectionPressTime;

    /**
     * Current time in millis when activation was activated
     */
    private long lastActivationTime;

    AbstractSelectionMode() {
        this.currentGrid = new SimpleObjectProperty<>();
        this.strokeColor = new SimpleObjectProperty<>();
        this.progressColor = new SimpleObjectProperty<>();
        this.drawProgress = new SimpleBooleanProperty();
        this.backgroundReductionLevel = new SimpleDoubleProperty();
        this.backgroundReductionEnabled = new SimpleBooleanProperty();
        this.playingProperty = new SimpleBooleanProperty(false);
        //Currently scanned grid
        this.currentGrid.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.scannedGridChanged(nv);
            }
        });
    }

    protected void scannedGridChanged(final GridComponentI gridP) {
    }

    /**
     * To check if a grid part is empty.<br>
     * Subclass that want to skip empty part should call this method to do the part checking on scanning generation.<br>
     * This will check is the part is empty only if the "skip empty element" parameter is on true.
     *
     * @param part the part to test
     * @return true if the part is considered as empty
     */
    protected boolean isPartEmpty(final GridPartComponentI part) {
        if (!this.parameters.skipEmptyComponentProperty().get()) {
            return false;
        }
        if (part instanceof GridPartKeyComponentI) {
            GridPartKeyComponentI key = (GridPartKeyComponentI) part;
            boolean empty = true;
            empty &= key.textContentProperty().get() == null || key.textContentProperty().get().isEmpty();
            empty &= key.imageVTwoProperty().get() == null;
            empty &= key.getActionManager().countAllActions() <= 0;
            empty |= key.keyOptionProperty().get().considerKeyEmptyProperty().get();
            return empty;
        }
        if (part instanceof WriterDisplayerI) {
            return true;
        }
        return false;
    }

    // Class part : "Get/set"
    //========================================================================
    @Override
    public void setParameters(final SelectionModeParameterI parametersP) {
        this.parameters = parametersP;
        this.setDefaultColors();
        this.drawProgress.set(this.parameters.drawProgressProperty().get() && this.parameters.scanningModeProperty().get() != ScanningMode.MANUAL);
        this.backgroundReductionEnabled.set(this.parameters.backgroundReductionEnabledProperty().get());
        this.backgroundReductionLevel.set(this.parameters.backgroundReductionLevelProperty().get());
        this.parameterChanged(parametersP);
    }

    @Override
    public SelectionModeParameterI getParameters() {
        return this.parameters;
    }

    @Override
    public void goToGridPart(final GridPartComponentI part) {
    }

    @Override
    public ObjectProperty<GridComponentI> currentGridProperty() {
        return this.currentGrid;
    }

    @Override
    public boolean isTimeBeforeRepeatCorrect() {
        return System.currentTimeMillis() - this.lastActivationTime >= this.parameters.timeBeforeRepeatProperty().get();
    }

    @Override
    public void activationDone() {
        this.lastActivationTime = System.currentTimeMillis();
    }

    protected boolean isTimeToActivationCorrect() {
        return System.currentTimeMillis() - this.selectionPressTime >= this.parameters.timeToFireActionProperty().get();
    }

    protected void selectionStarted() {
        this.selectionPressTime = System.currentTimeMillis();
    }

    protected void setDefaultColors() {
        this.strokeColor.set(this.parameters.selectionViewColorProperty().get());
        this.progressColor.set(this.parameters.progressViewColorProperty().get());
    }
    //========================================================================

    // Class part : "Subclass impl"
    //========================================================================

    /**
     * Method called when current parameters changes.<br>
     * This should update the selection mode with the new parameters, even if the selection mode is running.
     *
     * @param parameters the new parameters.
     */
    protected abstract void parameterChanged(SelectionModeParameterI parameters);

    @Override
    public void init(SelectionModeI previousSelectionMode) {
        //Fix Issue #177 : when a selection mode change, the last activation time should be copied to be able to eliminate new invalid selection
        if (previousSelectionMode instanceof AbstractSelectionMode<?>) {
            this.lastActivationTime = ((AbstractSelectionMode<?>) previousSelectionMode).lastActivationTime;
        }
    }
    //========================================================================

    // DRAW
    //========================================================================
    @Override
    public ReadOnlyBooleanProperty playingProperty() {
        return this.playingProperty;
    }

    @Override
    public ObjectProperty<Color> strokeFillProperty() {
        return this.strokeColor;
    }

    @Override
    public ObjectProperty<Color> progressFillProperty() {
        return this.progressColor;
    }

    @Override
    public ReadOnlyBooleanProperty drawProgressProperty() {
        return this.drawProgress;
    }

    @Override
    public ReadOnlyBooleanProperty backgroundReductionEnabledProperty() {
        return backgroundReductionEnabled;
    }

    @Override
    public ReadOnlyDoubleProperty backgroundReductionLevelProperty() {
        return backgroundReductionLevel;
    }
    //========================================================================

}
