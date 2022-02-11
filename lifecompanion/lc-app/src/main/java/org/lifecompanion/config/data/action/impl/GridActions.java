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
package org.lifecompanion.config.data.action.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.lifecompanion.model.impl.editaction.BaseGridChangeAction;
import org.lifecompanion.model.impl.editaction.BasePropertyChangeAction;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.configurationcomponent.GridPartKeyComponent;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.ui.editmode.AddTypeEnum;
import org.lifecompanion.model.api.ui.editmode.PossibleAddComponentI;

/**
 * Class for actions on a {@link GridPartGridComponent} like change row/column, add keys, etc...
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridActions {
	private final static Logger LOGGER = LoggerFactory.getLogger(GridActions.class);

	/**
	 * To change row count
	 */
	public static class ChangeRowAction extends BaseGridChangeAction {
		private int rowCount;

		public ChangeRowAction(final ComponentGridI gridP, final int rowCountP) {
			super(gridP);
			this.rowCount = rowCountP;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.setRow(this.rowCount);
		}

		@Override
		public void undoAction() throws LCException {
			super.undoAction();
		}

		@Override
		public String getNameID() {
			return "action.change.row.name";
		}
	}

	/**
	 * To change column count
	 */
	public static class ChangeColumnAction extends BaseGridChangeAction {
		private int columnCount;

		public ChangeColumnAction(final ComponentGridI gridP, final int columnCountP) {
			super(gridP);
			this.columnCount = columnCountP;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.setColumn(this.columnCount);
		}

		@Override
		public String getNameID() {
			return "action.change.column.name";
		}
	}

	/**
	 * Set a component into the grid.
	 */
	public static class SetComponentAction extends BaseGridChangeAction {
		private int rowIndex, columnIndex;
		private GridPartComponentI component;

		public SetComponentAction(final ComponentGridI gridP, final int rowIndexP, final int columnIndexP, final GridPartComponentI componentP) {
			super(gridP);
			this.rowIndex = rowIndexP;
			this.columnIndex = columnIndexP;
			this.component = componentP;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.component.dispatchRemovedPropertyValue(false);
			this.grid.setComponent(this.rowIndex, this.columnIndex, this.component);
		}

		@Override
		public String getNameID() {
			return "action.set.component.in.grid";
		}

	}

	/**
	 * Action to inverse keys in grid.
	 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
	 */
	public static class InverseKeyAction implements UndoRedoActionI {
		private GridPartKeyComponentI source, destination;
		// private ReplaceComponentAction setComponentSource, setComponentDestination, setComponentTemp;
		private List<ReplaceComponentAction> replaceActions;
		private List<ReplaceComponentAction> replaceActionsInverse;

		public InverseKeyAction(final GridPartKeyComponentI source, final GridPartKeyComponentI destination) {
			this.source = source;
			this.destination = destination;
		}

		@Override
		public void doAction() throws LCException {
			ComponentGridI sourceGrid = this.source.gridParentProperty().get().getGrid();
			ComponentGridI destGrid = this.destination.gridParentProperty().get().getGrid();
			GridPartKeyComponent tempKey = new GridPartKeyComponent();
			// Create all actions
			ReplaceComponentAction setComponentTemp = new ReplaceComponentAction(sourceGrid, this.source, tempKey, false);
			ReplaceComponentAction setComponentDestination = new ReplaceComponentAction(destGrid, this.destination, this.source, false);
			ReplaceComponentAction setComponentSource = new ReplaceComponentAction(sourceGrid, tempKey, this.destination, false);
			this.replaceActions = Arrays.asList(setComponentTemp, setComponentDestination, setComponentSource);
			this.replaceActionsInverse = new ArrayList<>(this.replaceActions);
			Collections.reverse(this.replaceActionsInverse);
			// Execute in correct order
			for (ReplaceComponentAction replaceAction : this.replaceActions) {
				replaceAction.doAction();
			}
		}

		@Override
		public void undoAction() throws LCException {
			for (ReplaceComponentAction replaceAction : this.replaceActionsInverse) {
				replaceAction.undoAction();
			}
		}

		@Override
		public void redoAction() throws LCException {
			for (ReplaceComponentAction replaceAction : this.replaceActions) {
				replaceAction.redoAction();
			}
		}

		@Override
		public String getNameID() {
			return "action.inverse.keys.name";
		}

	}

	/**
	 * Action to change the keys option
	 */
	// TODO : Check ChangeMultiKeyOptionAction and merge into this kind of method !
	public static class SetKeyOptionsAction implements UndoRedoActionI {
		private List<GridPartKeyComponentI> keys;
		private PossibleAddComponentI<GridPartKeyComponentI> keyAdder;
		private Map<GridPartKeyComponentI, GridPartKeyComponentI> keyThatReplaces;

		public SetKeyOptionsAction(final List<GridPartKeyComponentI> keys, final PossibleAddComponentI<GridPartKeyComponentI> keyAdderP) {
			this.keys = keys;
			this.keyAdder = keyAdderP;
			this.keyThatReplaces = new HashMap<>();
			// Sort keys by their position in grid
			Collections.sort(this.keys, LCUtils.positionInGridParentIncludingParentComparator());
		}

		@Override
		public void doAction() throws LCException {
			// For each keys, set the option
			for (GridPartKeyComponentI keyComponent : this.keys) {
				GridComponentI gridComponent = keyComponent.gridParentProperty().get();
				GridPartKeyComponentI keyReplace = this.keyAdder.getNewComponent(AddTypeEnum.GRID_PART, gridComponent, keyComponent);
				// Replace key
				this.keyThatReplaces.put(keyComponent, keyReplace);
				gridComponent.getGrid().replaceComponent(keyComponent, keyReplace);
			}
		}

		@Override
		public void undoAction() throws LCException {
			// For each replaced keys, replace the key that replaced
			for (GridPartKeyComponentI keyComponent : this.keys) {
				GridPartKeyComponentI replacingKey = this.keyThatReplaces.get(keyComponent);
				GridComponentI gridComponent = replacingKey.gridParentProperty().get();
				gridComponent.getGrid().replaceComponent(replacingKey, keyComponent);
			}
		}

		@Override
		public void redoAction() throws LCException {
			// For each original keys, replace it again, but without creating new instance
			for (GridPartKeyComponentI keyComponent : this.keys) {
				GridPartKeyComponentI replacingKey = this.keyThatReplaces.get(keyComponent);
				GridComponentI gridComponent = keyComponent.gridParentProperty().get();
				gridComponent.getGrid().replaceComponent(keyComponent, replacingKey);
			}
		}

		@Override
		public String getNameID() {
			return "action.set.keys.option";
		}
	}

	/**
	 * Action to paste a component on different keys
	 */
	public static class ReplaceMultiCompAction implements UndoRedoActionI {
		private List<ReplaceComponentAction> replaceActions;
		private GridPartComponentI toPaste;
		private List<GridPartKeyComponentI> destKeys;

		public ReplaceMultiCompAction(final GridPartComponentI toPaste, final List<GridPartKeyComponentI> destKeys) {
			this.replaceActions = new ArrayList<>();
			this.toPaste = toPaste;
			this.destKeys = destKeys;
		}

		@Override
		public void doAction() throws LCException {
			ArrayList<GridPartComponentI> compToSelect = new ArrayList<>();
			// Create actions
			for (GridPartKeyComponentI targetKey : this.destKeys) {
				GridPartComponentI toPasteComp = (GridPartComponentI) this.toPaste.duplicate(true);
				GridComponentI targetParent = targetKey.gridParentProperty().get();
				compToSelect.add(toPasteComp);
				this.replaceActions.add(new ReplaceComponentAction(targetParent.getGrid(), targetKey, toPasteComp, false));
			}
			for (ReplaceComponentAction action : this.replaceActions) {
				action.doAction();
			}
			// Select pasted
			SelectionController.INSTANCE.setSelectedKeys(compToSelect);
		}

		@Override
		public String getNameID() {
			return "action.replace.multi.key.action";
		}

		@Override
		public void undoAction() throws LCException {
			for (int i = this.replaceActions.size() - 1; i >= 0; i--) {
				this.replaceActions.get(i).undoAction();
			}
		}

		@Override
		public void redoAction() throws LCException {
			for (ReplaceComponentAction action : this.replaceActions) {
				action.redoAction();
			}
		}
	}

	/**
	 * Replace a component into the grid.
	 */
	public static class ReplaceComponentAction extends BaseGridChangeAction {
		private GridPartComponentI toReplace;
		private GridPartComponentI component;
		private boolean select;

		public ReplaceComponentAction(final ComponentGridI gridP, final GridPartComponentI toReplaceP, final GridPartComponentI componentP,
				final boolean selectP) {
			super(gridP);
			this.toReplace = toReplaceP;
			this.component = componentP;
			this.select = selectP;
		}

		@Override
		public void doAction() throws LCException {
			if (this.toReplace.getID().equals(this.component.getID())) {
				GridActions.LOGGER.warn("Component to replace are the same, the replace action will not be executed");
			} else {
				this.stateBeforeDo = this.grid.saveGrid();
				this.component.dispatchRemovedPropertyValue(false);
				this.grid.replaceComponent(this.toReplace, this.component);
				if (this.select) {
					SelectionController.INSTANCE.setSelectedPart(this.component);
				}
			}
		}

		@Override
		public void redoAction() throws LCException {
			if (!this.toReplace.getID().equals(this.component.getID())) {
				this.select = false;
				super.redoAction();
			}
		}

		@Override
		public void undoAction() throws LCException {
			if (!this.toReplace.getID().equals(this.component.getID())) {
				super.undoAction();
			}
		}

		@Override
		public String getNameID() {
			return "action.replace.component.in.grid";
		}
	}

	public static class ChangeGridVGapAction extends BasePropertyChangeAction<Number> {

		public ChangeGridVGapAction(final GridComponentI grid, final Number wantedValueP) {
			super(grid.vGapProperty(), wantedValueP);
		}

		@Override
		public String getNameID() {
			return "action.name.change.grid.vgap";
		}

	}

	public static class ChangeGridHGapAction extends BasePropertyChangeAction<Number> {

		public ChangeGridHGapAction(final GridComponentI grid, final Number wantedValueP) {
			super(grid.hGapProperty(), wantedValueP);
		}

		@Override
		public String getNameID() {
			return "action.name.change.grid.hgap";
		}

	}

	public static class SplitGridPartAction extends BaseGridChangeAction {
		private GridPartComponentI toSplit;

		public SplitGridPartAction(final ComponentGridI gridP, final GridPartComponentI toSplit) {
			super(gridP);
			this.toSplit = toSplit;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.splitComponentIntoKeys(this.toSplit);
		}

		@Override
		public String getNameID() {
			return "action.replace.component.in.grid";
		}
	}

	// Class part : "Generic add/remove column,row -> dispatch to correct action then"
	// ========================================================================
	private static abstract class AbstractGenericAddRemoveCRAction implements UndoRedoActionI {

		private final UndoRedoActionI delegateAction;

		protected AbstractGenericAddRemoveCRAction(GridComponentI grid, GridPartComponentI selectedPart) {
			super();
			if (selectedPart != null) {
				this.delegateAction = getActionForSelectedPartInGrid(selectedPart);
			} else {
				this.delegateAction = getActionForOnlyGridSelected(grid);
			}
		}

		@Override
		public void doAction() throws LCException {
			this.delegateAction.doAction();
		}

		@Override
		public void undoAction() throws LCException {
			this.delegateAction.undoAction();
		}

		@Override
		public void redoAction() throws LCException {
			this.delegateAction.redoAction();
		}

		@Override
		public String getNameID() {
			return this.delegateAction.getNameID();
		}

		protected abstract UndoRedoActionI getActionForSelectedPartInGrid(GridPartComponentI selectedPart);

		protected abstract UndoRedoActionI getActionForOnlyGridSelected(GridComponentI grid);
	}

	public static class AddColumnGenericAction extends AbstractGenericAddRemoveCRAction {

		public AddColumnGenericAction(GridComponentI grid, GridPartComponentI selectedPart) {
			super(grid, selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForSelectedPartInGrid(GridPartComponentI selectedPart) {
			return new AddColumnAtIndexAction(selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForOnlyGridSelected(GridComponentI grid) {
			return new ChangeColumnAction(grid.getGrid(), grid.columnCountProperty().get() + 1);
		}
	}

	public static class AddRowGenericAction extends AbstractGenericAddRemoveCRAction {

		public AddRowGenericAction(GridComponentI grid, GridPartComponentI selectedPart) {
			super(grid, selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForSelectedPartInGrid(GridPartComponentI selectedPart) {
			return new AddRowAtIndexAction(selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForOnlyGridSelected(GridComponentI grid) {
			return new ChangeRowAction(grid.getGrid(), grid.rowCountProperty().get() + 1);
		}
	}

	public static class RemoveRowGenericAction extends AbstractGenericAddRemoveCRAction {

		public RemoveRowGenericAction(GridComponentI grid, GridPartComponentI selectedPart) {
			super(grid, selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForSelectedPartInGrid(GridPartComponentI selectedPart) {
			return new RemoveRowAtIndexAction(selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForOnlyGridSelected(GridComponentI grid) {
			return new ChangeRowAction(grid.getGrid(), grid.rowCountProperty().get() - 1);
		}
	}

	public static class RemoveColumnGenericAction extends AbstractGenericAddRemoveCRAction {

		public RemoveColumnGenericAction(GridComponentI grid, GridPartComponentI selectedPart) {
			super(grid, selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForSelectedPartInGrid(GridPartComponentI selectedPart) {
			return new RemoveColumnAtIndexAction(selectedPart);
		}

		@Override
		protected UndoRedoActionI getActionForOnlyGridSelected(GridComponentI grid) {
			return new ChangeColumnAction(grid.getGrid(), grid.columnCountProperty().get() - 1);
		}
	}

	// ========================================================================

	// Class part : "Add/remove column,row"
	// ========================================================================

	public static class AddColumnAtIndexAction extends BaseGridChangeAction {
		private GridPartComponentI gridPart;

		public AddColumnAtIndexAction(final GridPartComponentI gridPart) {
			super(gridPart.gridParentProperty().get().getGrid());
			this.gridPart = gridPart;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.addColumn(this.gridPart.columnProperty().get());
		}

		@Override
		public String getNameID() {
			return "action.add.column.index.action";
		}
	}

	public static class AddRowAtIndexAction extends BaseGridChangeAction {
		private GridPartComponentI gridPart;

		public AddRowAtIndexAction(final GridPartComponentI gridPart) {
			super(gridPart.gridParentProperty().get().getGrid());
			this.gridPart = gridPart;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.addRow(this.gridPart.rowProperty().get());
		}

		@Override
		public String getNameID() {
			return "action.add.row.index.action";
		}
	}

	public static class RemoveRowAtIndexAction extends BaseGridChangeAction {
		private GridPartComponentI gridPart;

		public RemoveRowAtIndexAction(final GridPartComponentI gridPart) {
			super(gridPart.gridParentProperty().get().getGrid());
			this.gridPart = gridPart;
		}

		@Override
		public void doAction() throws LCException {
			int column = this.gridPart.columnProperty().get();
			int row = this.gridPart.rowProperty().get();
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.removeRow(this.gridPart.rowProperty().get());
			// Try to select the closest part from the removed component
			if (row >= this.grid.getRow()) {
				row = this.grid.getRow() - 1;
			}
			GridPartComponentI toSelect = this.grid.getComponent(row, column);
			SelectionController.INSTANCE.setSelectedPart(toSelect);
		}

		@Override
		public String getNameID() {
			return "action.remove.row.index.action";
		}
	}

	public static class RemoveColumnAtIndexAction extends BaseGridChangeAction {
		private GridPartComponentI gridPart;

		public RemoveColumnAtIndexAction(final GridPartComponentI gridPart) {
			super(gridPart.gridParentProperty().get().getGrid());
			this.gridPart = gridPart;
		}

		@Override
		public void doAction() throws LCException {
			int column = this.gridPart.columnProperty().get();
			int row = this.gridPart.rowProperty().get();
			this.stateBeforeDo = this.grid.saveGrid();
			this.grid.removeColumn(this.gridPart.columnProperty().get());
			// Try to select the closest part from the removed component
			if (column >= this.grid.getColumn()) {
				column = this.grid.getColumn() - 1;
			}
			GridPartComponentI toSelect = this.grid.getComponent(row, column);
			SelectionController.INSTANCE.setSelectedPart(toSelect);
		}

		@Override
		public String getNameID() {
			return "action.remove.column.index.action";
		}
	}
	// ========================================================================

	// Class part : "Add key actions"
	// ========================================================================
	public static class AddKeyOnRightAction extends AbstractAddKeyAction {

		public AddKeyOnRightAction(final GridPartComponentI gridPart) {
			super(gridPart, ComponentGridI::addKeyOnRight);
		}

		@Override
		public String getNameID() {
			return "action.add.key.right.name";
		}
	}

	public static class AddKeyOnLeftAction extends AbstractAddKeyAction {

		public AddKeyOnLeftAction(final GridPartComponentI gridPart) {
			super(gridPart, ComponentGridI::addKeyOnLeft);
		}

		@Override
		public String getNameID() {
			return "action.add.key.left.name";
		}
	}

	public static class AddKeyOnTopAction extends AbstractAddKeyAction {

		public AddKeyOnTopAction(final GridPartComponentI gridPart) {
			super(gridPart, ComponentGridI::addKeyOnTop);
		}

		@Override
		public String getNameID() {
			return "action.add.key.top.name";
		}
	}

	public static class AddKeyOnBottomAction extends AbstractAddKeyAction {

		public AddKeyOnBottomAction(final GridPartComponentI gridPart) {
			super(gridPart, ComponentGridI::addKeyOnBottom);
		}

		@Override
		public String getNameID() {
			return "action.add.key.bottom.name";
		}
	}

	public static abstract class AbstractAddKeyAction extends BaseGridChangeAction {
		private GridPartComponentI gridPart;
		private BiConsumer<ComponentGridI, GridPartComponentI> addKeyMethod;

		public AbstractAddKeyAction(final GridPartComponentI gridPart, final BiConsumer<ComponentGridI, GridPartComponentI> addKeyMethod) {
			super(gridPart.gridParentProperty().get().getGrid());
			this.gridPart = gridPart;
			this.addKeyMethod = addKeyMethod;
		}

		@Override
		public void doAction() throws LCException {
			this.stateBeforeDo = this.grid.saveGrid();
			this.addKeyMethod.accept(this.grid, this.gridPart);
		}
	}
	// ========================================================================
}
