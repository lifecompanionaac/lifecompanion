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
package org.lifecompanion.base.data.definition.selection.impl;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.grid.GridComponentInformation;
import org.lifecompanion.api.definition.selection.SelectionModeI;
import org.lifecompanion.base.data.definition.selection.AbstractScanningSelectionMode;
import org.lifecompanion.base.data.definition.selection.view.AbstractPartScanSelectionModeView;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


/**
 * Abstract selection mode for selection mode that scan grids by creating part of, with grid part inside these parts.<br>
 * For example it will create part with each line, and each line contains the line's keys.
 *
 * @param <T> the selection mode view
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractPartScanSelectionMode<T extends AbstractPartScanSelectionModeView> extends AbstractScanningSelectionMode<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(AbstractPartScanSelectionMode.class);

    /**
     * All determined part to scan
     */
    private List<ComponentToScan> components;

    /**
     * The selected part, if the part is selected, we are currently components inside this part with secondary index.
     */
    private ComponentToScan selectedComponentToScan;

    /**
     * Current part we are over, even if it is not selected
     */
    private ComponentToScan currentComponentToScan;

    /**
     * The scan part index
     */
    private int primaryIndex;

    /**
     * If a part is selected, the index inside the part's components
     */
    private int secondaryIndex;

    /**
     * Deselect current part on next play
     */
    private boolean deselectAndUpdateCurrentPartOnNextPlay = false;

    // Class part : "Implementations"
    //========================================================================
    @Override
    public void executeNext() {
        //Line selected
        if (this.selectedComponentToScan != null) {
            //Correct column
            if (this.secondaryIndex < this.selectedComponentToScan.components.size() - 1) {
                this.currentPart.set(this.selectedComponentToScan.getPartIn(this.currentGrid.get(), ++this.secondaryIndex));
            } else {
                this.secondaryIndex = 0;
                this.timesInSamePart++;
                if (this.isTimesInPartSupMax()) {
                    this.deselectCurrentPart();
                }
            }
        } else {
            this.primaryIndex++;
            this.currentComponentToScan = this.components.get(this.primaryIndex);
        }
    }

    @Override
    protected void scannedGridChanged(final GridComponentI gridP) {
        super.scannedGridChanged(gridP);
        this.selectedComponentToScan = null;
    }

    @Override
    protected void generateScannedComponents() {
        this.components = this.generateLineToScan(this.currentGrid.get(), false);
        if (CollectionUtils.isEmpty(this.components)) {
            AbstractPartScanSelectionMode.LOGGER.warn("No component to scan found for {}, so the empty check will be bypassed",
                    this.getClass().getSimpleName());
            this.components = this.generateLineToScan(this.currentGrid.get(), true);
        }
    }

    @Override
    public boolean isScanningNextPossible() {
        return this.selectedComponentToScan != null || this.primaryIndex < this.components.size() - 1;
    }

    @Override
    public void updateCurrentComponent(final boolean firstPart) {
        if (this.selectedComponentToScan != null && !this.selectedComponentToScan.components.isEmpty()) {
            this.updateCurrentPart(this.selectedComponentToScan.getPartIn(this.currentGrid.get(), secondaryIndex), firstPart);
            this.view.moveToPart(this.currentPart.get(), this.getProgressTime(firstPart), this.isMoveAnimationEnabled(firstPart));
        } else {
            this.updateCurrentPart(null, firstPart);
            this.view.moveToPrimaryIndex(this.currentComponentToScan.index, this.currentComponentToScan.span, this.currentGrid.get(),
                    this.getProgressTime(firstPart), this.isMoveAnimationEnabled(firstPart));
        }
    }

    @Override
    public void restart() {
        if (this.checkRestartAndMaxScanValid()) {
            this.primaryIndex = 0;
            this.secondaryIndex = 0;
            this.currentComponentToScan = this.components.get(this.primaryIndex);
            this.selectedComponentToScan = null;
            this.updateCurrentComponent(true);
        }
    }

    @Override
    public void init(SelectionModeI previousSelectionMode) {
        super.init(previousSelectionMode);
        this.reinitParts();
    }

    private void reinitParts() {
        this.primaryIndex = 0;
        this.secondaryIndex = 0;
        this.currentComponentToScan = null;
        this.selectedComponentToScan = null;
        this.deselectAndUpdateCurrentPartOnNextPlay = false;
    }
    //========================================================================

    // Class part : "Internal API"
    //========================================================================

    /**
     * If there is a currently selected part, unselect it
     */
    private void deselectCurrentPart() {
        if (this.selectedComponentToScan != null) {
            this.selectedComponentToScan = null;
            this.secondaryIndex = 0;
            this.timesInSamePart = 0;
            this.currentPart.set(null);
        }
    }
    //========================================================================

    // Class part : "Override of parent implementation"
    //========================================================================
    @Override
    protected boolean fireActionNoCurrentPart() {
        AbstractPartScanSelectionMode.LOGGER.debug("Current selected line {}", this.selectedComponentToScan);
        if (this.selectedComponentToScan == null) {
            this.selectedComponentToScan = this.components.get(this.primaryIndex);
            this.secondaryIndex = 0;
            this.timesInSamePart = 0;
            //If the current line contains only one element, select the first element and return true because
            //actions should be executed
            if (this.selectedComponentToScan.components.size() == 1) {
                AbstractPartScanSelectionMode.LOGGER.info("Selected component to scan is size one");
                this.secondaryIndex = 0;
                GridPartComponentI uniqueCompInside = this.selectedComponentToScan.getPartIn(this.currentGrid.get(), secondaryIndex);
                this.currentPart.set(uniqueCompInside);
                // Issue : #136, view was not correctly updated when there was only one part in row/column scanning
                this.view.moveToPart(this.currentPart.get(), this.getProgressTime(true), this.isMoveAnimationEnabled(true));
                //If the next component is a action trigger component, we should deselect the line on next play call, because
                // it will fire action and it should never select a line with just one component
                this.deselectAndUpdateCurrentPartOnNextPlay = uniqueCompInside instanceof GridPartKeyComponentI;
                return true;
            } else {
                //This select the first part of the current line, so scanning time should restart
                this.updateCurrentComponent(true);
            }
        }
        return false;
    }

    @Override
    public void goToGridPart(final GridPartComponentI part) {
        super.goToGridPart(part);
        this.reinitParts();
        //Search for the component in the grid
        boolean found = false;
        for (int i = 0; i < this.components.size() && !found; i++) {
            ComponentToScan componentsInside = this.components.get(i);
            for (int j = 0; j < componentsInside.components.size() && !found; j++) {
                final GridComponentInformation componentInfo = componentsInside.components.get(j);
                if (componentInfo.getRow() == part.rowProperty().get() && componentInfo.getColumn() == part.columnProperty().get()) {
                    found = true;
                    //Select parts to scan
                    this.primaryIndex = i;
                    this.currentComponentToScan = componentsInside;
                    //Select part inside if needed
                    if (componentsInside.components.size() > 1) {
                        this.selectedComponentToScan = componentsInside;
                        this.currentPart.set(part);
                        this.secondaryIndex = j;
                    }
                }
            }
        }
        AbstractPartScanSelectionMode.LOGGER.info("Go to grid part {} in {}", part, this.getClass().getSimpleName());
        this.updateCurrentComponent(true);
    }

    @Override
    public void play() {
        // Sometimes needed because the selection of a part will directly fire the action
        // of the unique part inside (e.g. line with only one key inside)
        if (this.deselectAndUpdateCurrentPartOnNextPlay) {
            this.deselectCurrentPart();
            this.updateCurrentComponent(true);
            this.deselectAndUpdateCurrentPartOnNextPlay = false;
        }
        super.play();
    }
    //========================================================================

    // Class part : "Subclasses"
    //========================================================================

    /**
     * Should generate the list of part for a given grid
     *
     * @param grid             the grid to scan
     * @param byPassEmptyCheck if the empty check should be bypassed for keys
     * @return the list of all parts.
     */
    protected abstract List<ComponentToScan> generateLineToScan(final GridComponentI grid, boolean byPassEmptyCheck);

    /**
     * Useful method to check if a part contains all components
     *
     * @param part       the line to check
     * @param components the components to check
     * @return true if it contains all elements
     */
    protected boolean containsAllComponents(final ComponentToScan part, final List<GridPartComponentI> components) {
        if (part.components.size() == components.size()) {
            return part.cachedRawComponents.equals(components);
        }
        return false;
    }

    /**
     * To generate a list of component to scan from a double list of part.
     *
     * @param components       the list of components (raw)
     * @param byPassEmptyCheck if this parameter is true, the empty check will not be done
     * @return the list of component to scan, generated with the needed parameters.
     */
    protected List<ComponentToScan> generateComponentToScan(final List<List<GridPartComponentI>> components, final boolean byPassEmptyCheck) {
        HashSet<GridPartComponentI> scannedSet = new HashSet<>();
        ArrayList<ComponentToScan> groupsToScan = new ArrayList<>();
        for (int i = 0; i < components.size(); i++) {
            List<GridPartComponentI> currentPart = components.get(i);
            ArrayList<GridPartComponentI> rowsComponents = new ArrayList<>();
            scannedSet.clear();//Unique just on the same line
            for (int j = 0; j < currentPart.size(); j++) {
                GridPartComponentI current = currentPart.get(j);
                if ((byPassEmptyCheck || !this.isPartEmpty(current)) && !scannedSet.contains(current)) {
                    rowsComponents.add(current);
                    scannedSet.add(current);
                }
            }
            //Check if previous line contains exactly the same component
            if (!groupsToScan.isEmpty() && this.containsAllComponents(groupsToScan.get(groupsToScan.size() - 1), rowsComponents)) {
                ComponentToScan previousColumn = groupsToScan.get(groupsToScan.size() - 1);
                previousColumn.increaseSpan();
                //LOGGER.info("Previous row {} contains the same element than row {}", previousLine.index, row);
            } else if (!rowsComponents.isEmpty()) {
                groupsToScan.add(new ComponentToScan(i, rowsComponents, 1));
            }
        }
        return groupsToScan;
    }
    //========================================================================

    // Class part : "Internal class"
    //========================================================================

    /**
     * Class to store the scanning parts.
     *
     * @author Mathieu THEBAUD <math.thebaud@gmail.com>
     */
    protected static class ComponentToScan {
        private final int index;
        private int span;
        private final List<GridComponentInformation> components;
        //Shouldn't be used, only useful when generating component to scan
        private final List<GridPartComponentI> cachedRawComponents;

        public ComponentToScan(final int indexP, final List<GridPartComponentI> components, final int spanP) {
            this.index = indexP;
            cachedRawComponents = components;
            this.components = components.stream().map(GridComponentInformation::create).collect(Collectors.toList());
            this.span = spanP;
        }

        public void increaseSpan() {
            this.span++;
        }

        public GridPartComponentI getPartIn(GridComponentI grid, int secondaryIndex) {
            final GridComponentInformation compInfo = components.get(secondaryIndex);
            return grid.getGrid().getComponent(compInfo.getRow(), compInfo.getColumn());
        }

    }
    //========================================================================
}
