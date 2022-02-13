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

import java.util.List;

import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.configurationcomponent.GridComponentInformation;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.ui.selectionmode.DirectKeyScanSelectionModeView;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.util.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract selection mode for mode that scan keys with a linear ways (vertical, horizontal, etc...)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractDirectKeyScanSelectionMode extends AbstractScanningSelectionMode<DirectKeyScanSelectionModeView>
		implements DrawSelectionModeI {
	private final static Logger LOGGER = LoggerFactory.getLogger(AbstractDirectKeyScanSelectionMode.class);

	/**
	 * Issue #195 : first selection is buggy with char prediction because it don't wait for key changes.</br>
	 * This is a dirty fix before finding a better solution.
	 */
	private static final long DELAY_BEFORE_RESTART = 300;
	
	/**
	 * All components that are currently scanned
	 */
	private List<GridComponentInformation> currentScanList;

	/**
	 * The current component index in the scanning list
	 */
	private int currentIndex = 0;

	public AbstractDirectKeyScanSelectionMode() {
		super();
	}

	// Class part : "Implementation"
	//========================================================================
	@Override
	public void goToGridPart(final GridPartComponentI part) {
		super.goToGridPart(part);
		int partIndex = this.indexOfComponent(part);
		this.currentIndex = partIndex;
		this.updateCurrentComponent(true);
	}

	private int indexOfComponent(GridPartComponentI part) {
		int index = 0;
		for (GridComponentInformation compInfo : this.currentScanList) {
			if (compInfo.getRow() == part.rowProperty().get() && compInfo.getColumn() == part.columnProperty().get()) {
				return index;
			}
			index++;
		}
		return index;
	}

	@Override
	public void restart() {
		//If we scanned to much times this grid
		if (this.checkRestartAndMaxScanValid()) {
			// Issue #195 : dirty fix...
			ThreadUtils.safeSleep(DELAY_BEFORE_RESTART);
			//Create the component to scan list
			this.currentIndex = 0;
			this.updateCurrentComponent(true);
		}
	}

	@Override
	public void executeNext() {
		final GridComponentInformation nextCompInfo = this.currentScanList.get(++this.currentIndex);
		this.currentPart.set(this.currentGrid.get().getGrid().getComponent(nextCompInfo.getRow(), nextCompInfo.getColumn()));
	}

	@Override
	public boolean isScanningNextPossible() {
		return this.currentIndex < this.currentScanList.size() - 1;
	}

	@Override
	public void updateCurrentComponent(final boolean firstPart) {
		final GridComponentInformation currentComponentInfo = this.currentScanList.get(this.currentIndex);
		this.updateCurrentPart(this.currentGrid.get().getGrid().getComponent(currentComponentInfo.getRow(), currentComponentInfo.getColumn()),
				firstPart);
		this.view.moveToPart(this.currentPart.get(), this.getProgressTime(firstPart), this.isMoveAnimationEnabled(firstPart));
	}

	@Override
	protected void generateScannedComponents() {
		this.currentScanList = this.generateComponentToScan(this.currentGrid.get(), false);
		if (CollectionUtils.isEmpty(this.currentScanList)) {
			AbstractDirectKeyScanSelectionMode.LOGGER.warn("No component to scan found for {}, so the empty check will be bypassed",
					this.getClass().getSimpleName());
			this.currentScanList = this.generateComponentToScan(this.currentGrid.get(), true);
		}
	}

	@Override
	public void init(SelectionModeI previousSelectionMode) {
		super.init(previousSelectionMode);
		this.currentIndex = 0;
		this.currentPart.set(null);
	}
	//========================================================================

	// Class part : "Subclass"
	//========================================================================
	/**
	 * Method that generate the component that will be scanned.<br>
	 * This method should care about this mode parameters (skip empty keys, etc...)
	 * @param componentGrid the grid to scan
	 * @param byPassEmptyCheck if this parameter is true, you shouldn't check if the keys are empty
	 * @return the list of all components to scan, with the wanted scanning order
	 */
	protected abstract List<GridComponentInformation> generateComponentToScan(final GridComponentI componentGrid, boolean byPassEmptyCheck);

	/**
	 * Helper method to add a grid component (add only if the component is not in the current list and valid (not empty when needed...))
	 * @param components the component list
	 * @param component the component to add
	 * @param byPassEmptyCheck if this parameter is true, the empty check will not be done
	 */
	protected void addGridComponentToList(final List<GridPartComponentI> components, final GridPartComponentI component,
			final boolean byPassEmptyCheck) {
		if (byPassEmptyCheck || !this.isPartEmpty(component)) {
			components.add(component);
		}
	}

	/**
	 * Helper method to add a grid component (add only if the component is not in the current list and valid (not empty when needed...))
	 * @param components the component list
	 * @param component the component to add
	 * @param byPassEmptyCheck if this parameter is true, the empty check will not be done
	 */
	protected void addGridComponentToList2(final List<GridComponentInformation> components, final GridPartComponentI component,
			final boolean byPassEmptyCheck) {
		if (byPassEmptyCheck || !this.isPartEmpty(component)) {
			components.add(GridComponentInformation.create(component));
		}
	}
	//========================================================================

}
