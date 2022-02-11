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
package org.lifecompanion.ui.app.main.ribbon.available.withselection;

import java.util.function.BiFunction;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.controller.editaction.GridActions.AddColumnGenericAction;
import org.lifecompanion.controller.editaction.GridActions.AddRowGenericAction;
import org.lifecompanion.controller.editaction.GridActions.RemoveColumnGenericAction;
import org.lifecompanion.controller.editaction.GridActions.RemoveRowGenericAction;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * Part that is showed when a grid component is selected
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridLayoutRibbonPart extends RibbonBasePart<DisplayableComponentI> implements LCViewInitHelper {

	/**
	 * Buttons
	 */
	private Button buttonAddRow, buttonRemoveRow, buttonAddColumn, buttonRemoveColumn;

	private ObjectProperty<GridComponentI> selectedGrid;

	public GridLayoutRibbonPart() {
		selectedGrid = new SimpleObjectProperty<>();
		this.initAll();
	}

	@Override
	public void initUI() {

		this.buttonAddRow = UIUtils.createTextButtonWithIcon(null, "actions/icon_add_row.png", "tooltip.add.row.comp");
		this.buttonAddColumn = UIUtils.createTextButtonWithIcon(null, "actions/icon_add_column.png", "tooltip.add.column.comp");
		this.buttonRemoveColumn = UIUtils.createTextButtonWithIcon(null, "actions/icon_remove_column.png", "tooltip.remove.column.comp");
		this.buttonRemoveRow = UIUtils.createTextButtonWithIcon(null, "actions/icon_remove_row.png", "tooltip.remove.row.comp");

		GridPane gridPane = new GridPane();
		gridPane.setHgap(5);
		Label labelAdd = new Label(Translation.getText("grid.add.rows.columns"));
		GridPane.setHalignment(labelAdd, HPos.CENTER);
		gridPane.add(labelAdd, 0, 0, 2, 1);
		gridPane.add(this.buttonAddRow, 0, 1);
		gridPane.add(this.buttonAddColumn, 1, 1);
		Label labelRemove = new Label(Translation.getText("grid.remove.rows.columns"));
		GridPane.setHalignment(labelRemove, HPos.CENTER);
		gridPane.add(labelRemove, 0, 2, 2, 1);
		gridPane.add(this.buttonRemoveRow, 0, 3);
		gridPane.add(this.buttonRemoveColumn, 1, 3);

		this.setContent(gridPane);
		this.setTitle(Translation.getText("part.grid.layout"));
		this.disableAllButtons();
	}

	@Override
	public void initListener() {
		this.createButtonListener(this.buttonAddColumn, AddColumnGenericAction::new);
		this.createButtonListener(this.buttonAddRow, AddRowGenericAction::new);
		this.createButtonListener(this.buttonRemoveRow, RemoveRowGenericAction::new);
		this.createButtonListener(this.buttonRemoveColumn, RemoveColumnGenericAction::new);
	}

	private void createButtonListener(final Button button,
			final BiFunction<GridComponentI, GridPartComponentI, BaseEditActionI> actionConstructor) {
		button.setOnAction((ea) -> {
			final DisplayableComponentI modelValue = this.model.get();
			GridPartComponentI gridPartComp = null;
			if (modelValue instanceof GridPartComponentI) {
				gridPartComp = ((GridPartComponentI) modelValue).isParentExist() ? ((GridPartComponentI) modelValue) : null;
			}
			final GridComponentI gridComp = selectedGrid.get();
			if (gridComp != null || gridPartComp != null) {
				ConfigActionController.INSTANCE.executeAction(actionConstructor.apply(gridComp, gridPartComp));
			}
		});
	}

	@Override
	public void initBinding() {
		this.model.bind(SelectionController.INSTANCE.selectedComponentBothProperty());
		this.selectedGrid.addListener((obs, ov, nv) -> {
			if (nv != null) {
				this.buttonRemoveColumn.disableProperty().bind(nv.columnCountProperty().lessThan(2));
				this.buttonRemoveRow.disableProperty().bind(nv.rowCountProperty().lessThan(2));
				this.buttonAddColumn.setDisable(false);
				this.buttonAddRow.setDisable(false);
			} else {
				this.buttonRemoveColumn.disableProperty().unbind();
				this.buttonRemoveRow.disableProperty().unbind();
				this.disableAllButtons();
			}
		});
	}

	private void disableAllButtons() {
		this.buttonRemoveColumn.disableProperty().unbind();
		this.buttonRemoveRow.disableProperty().unbind();
		this.buttonRemoveColumn.setDisable(true);
		this.buttonRemoveRow.setDisable(true);
		this.buttonAddColumn.setDisable(true);
		this.buttonAddRow.setDisable(true);
	}

	// Class part : "Bind/unbind"
	// ========================================================================
	@Override
	public void bind(DisplayableComponentI model) {
		if (model instanceof StackComponentI) {
			selectedGrid.bind(((StackComponentI) model).displayedComponentProperty());
		} else if (model instanceof GridPartComponentI) {
			GridPartComponentI gridPartComp = (GridPartComponentI) model;
			if (gridPartComp.isParentExist()) {
				selectedGrid.bind(gridPartComp.gridParentProperty());
			} else {
				selectedGrid.set((GridComponentI) model);
			}
		}
	}

	@Override
	public void unbind(DisplayableComponentI model) {
		selectedGrid.unbind();
		selectedGrid.set(null);
		this.disableAllButtons();
	}
	// ========================================================================

}
