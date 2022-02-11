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

package org.lifecompanion.config.view.pane.tabs.selected.part;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.config.data.action.impl.GridActions.ChangeColumnAction;
import org.lifecompanion.config.data.action.impl.GridActions.ChangeRowAction;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.model.api.ui.editmode.ConfigurationProfileLevelEnum;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.TilePane;

/**
 * Pane to configure, the row/column on the selected component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RowColumnRibbonPart extends RibbonBasePart<GridComponentI> implements LCViewInitHelper {
	/**
	 * Spinner to configure the row/column on the selected component
	 */
	private Spinner<Integer> spinnerRow, spinnerColumn;

	/**
	 * Change listener for row/column properties
	 */
	private ChangeListener<Number> changeListenerRow, changeListenerColumn;

	/**
	 * Create a row column pane
	 */
	public RowColumnRibbonPart() {
		this.initAll();
	}

	/**
	 * Initialize all ui components
	 */
	@Override
	public void initUI() {
		this.setTitle(Translation.getText("pane.title.rowcolumn.count"));
		TilePane rows = new TilePane();
		rows.setPrefColumns(1);
		this.spinnerRow = UIUtils.createIntSpinner(1, 150, 2, 1, 75.0);
		UIUtils.createAndAttachTooltip(spinnerRow, "tooltip.explain.grid.row.spinner");
		this.spinnerColumn = UIUtils.createIntSpinner(1, 150, 2, 1, 75.0);
		UIUtils.createAndAttachTooltip(spinnerColumn, "tooltip.explain.grid.column.spinner");
		Label labelRow = new Label(Translation.getText("pane.row.count"));
		TilePane.setAlignment(labelRow, Pos.CENTER);
		Label labelColumn = new Label(Translation.getText("pane.column.count"));
		TilePane.setAlignment(labelColumn, Pos.CENTER);
		rows.getChildren().addAll(labelRow, this.spinnerRow, labelColumn, this.spinnerColumn);
		this.setContent(rows);
	}

	/**
	 * Set the listener to controls
	 */
	@Override
	public void initListener() {
		this.changeListenerColumn = LCConfigBindingUtils.createIntegerSpinnerBinding(this.spinnerColumn, this.model,
				GridComponentI::columnCountProperty, (model, nv) -> new ChangeColumnAction(model.getGrid(), nv));
		this.changeListenerRow = LCConfigBindingUtils.createIntegerSpinnerBinding(this.spinnerRow, this.model, GridComponentI::rowCountProperty,
				(model, nv) -> new ChangeRowAction(model.getGrid(), nv));
	}

	/**
	 * Bind the given component to editor
	 * @param component the component to bind
	 */
	@Override
	public void bind(final GridComponentI component) {
		component.rowCountProperty().addListener(this.changeListenerRow);
		component.columnCountProperty().addListener(this.changeListenerColumn);
		this.spinnerRow.getValueFactory().setValue(component.rowCountProperty().get());
		this.spinnerColumn.getValueFactory().setValue(component.columnCountProperty().get());
	}

	/**
	 * Unbind the given component to editor
	 * @param component the component to unbind
	 */
	@Override
	public void unbind(final GridComponentI component) {
		component.rowCountProperty().removeListener(this.changeListenerRow);
		component.columnCountProperty().removeListener(this.changeListenerColumn);
	}

	/**
	 * Initialize model binding
	 */
	@Override
	public void initBinding() {
		SelectionController.INSTANCE.selectedComponentProperty()
				.addListener((ChangeListener<GridPartComponentI>) (observableP, oldValueP, newValueP) -> {
					if (newValueP instanceof GridComponentI) {
						this.model.set((GridComponentI) newValueP);
					} else {
						this.model.set(null);
					}
				});
		ConfigUIUtils.bindShowForLevelFrom(this, ConfigurationProfileLevelEnum.NORMAL);
	}
}
