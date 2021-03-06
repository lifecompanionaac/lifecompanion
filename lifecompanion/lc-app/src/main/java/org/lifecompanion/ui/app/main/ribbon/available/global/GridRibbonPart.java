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

package org.lifecompanion.ui.app.main.ribbon.available.global;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.controller.editaction.LCConfigurationComponentActions.ChangeGridSizeConfigurationAction;
import org.lifecompanion.controller.editaction.LCConfigurationComponentActions.DisableGridOnConfigurationAction;
import org.lifecompanion.controller.editaction.LCConfigurationComponentActions.EnableGridOnConfigurationAction;
import org.lifecompanion.util.binding.EditActionUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available.RibbonBasePart;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.lifecompanion.util.javafx.FXControlUtils;

/**
 * Grid part to enable and parameter the grid in the current configuration.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridRibbonPart extends RibbonBasePart<LCConfigurationI> implements LCViewInitHelper {

	/**
	 * Icon to show with the grid state
	 */
	private final static char DISABLE_ICON = '\uE3EB', ENABLE_ICON = '\uE3EC';

	/**
	 * Slider for the grid size
	 */
	private Slider sliderGridSize;

	/**
	 * Button to disbale/enable grid use
	 */
	private ToggleButton buttonEnableGrid, buttonDisableGrid;

	/**
	 * Button group for enable/disable
	 */
	private ToggleGroup gridEnabledGroup;

	/**
	 * Change listener for model grid size change
	 */
	private ChangeListener<Number> changeListenerGridSize;

	/**
	 * Change listener for model use grid
	 */
	private ChangeListener<Boolean> changeListenerUseGrid;

	/**
	 * Create the grid ribbon part
	 */
	public GridRibbonPart() {
		this.initAll();
	}

	@Override
	public void initUI() {
		this.setTitle(Translation.getText("pane.title.config.grid"));
		this.sliderGridSize = FXControlUtils.createBaseSlider(5.0, 30.0, 10.0);
		this.sliderGridSize.setShowTickLabels(true);
		this.sliderGridSize.setMajorTickUnit(5.0);
		this.sliderGridSize.setMinorTickCount(4);
		FXControlUtils.createAndAttachTooltip(sliderGridSize, "tooltip.explain.grid.size");
		this.gridEnabledGroup = FXControlUtils.createAlwaysSelectedToggleGroup();
		Label labelGridSize = new Label(Translation.getText("label.grid.size"));
		this.buttonDisableGrid = this.createRadioButton(Translation.getText("label.use.grid.disable"), GridRibbonPart.DISABLE_ICON,
				LCGraphicStyle.SECOND_PRIMARY, "tooltip.disable.configuration.grid");
		this.buttonEnableGrid = this.createRadioButton(Translation.getText("label.use.grid.enable"), GridRibbonPart.ENABLE_ICON,
				LCGraphicStyle.MAIN_PRIMARY, "tooltip.enable.configuration.grid");
		HBox boxRadioButton = new HBox(1, this.buttonDisableGrid, this.buttonEnableGrid);
		boxRadioButton.setAlignment(Pos.CENTER);
		//Add content
		VBox rows = new VBox(3.0);
		rows.setAlignment(Pos.CENTER);
		rows.getChildren().addAll(boxRadioButton, labelGridSize, this.sliderGridSize);
		this.setContent(rows);
	}

	private ToggleButton createRadioButton(final String text, final char iconChar, final Color color, final String tooltipTranslationID) {
		ToggleButton btn = FXControlUtils.createGraphicsToggleButton(text, GlyphFontHelper.FONT_MATERIAL.create(iconChar).size(24).color(color),
				tooltipTranslationID);
		this.gridEnabledGroup.getToggles().add(btn);
		return btn;
	}

	// Class part : "Listener"
	//========================================================================
	@Override
	public void initListener() {
		this.disableProperty().bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty().isNull());
		this.buttonEnableGrid.setOnAction((ea) -> {
			if (this.model.get() != null && !this.model.get().useGridProperty().get()) {
				EnableGridOnConfigurationAction action = new EnableGridOnConfigurationAction(this.model.get());
				ConfigActionController.INSTANCE.executeAction(action);
			}
		});
		this.buttonDisableGrid.setOnAction((ea) -> {
			if (this.model.get() != null && this.model.get().useGridProperty().get()) {
				DisableGridOnConfigurationAction action = new DisableGridOnConfigurationAction(this.model.get());
				ConfigActionController.INSTANCE.executeAction(action);
			}
		});
		//To change with keyboard change
		this.changeListenerGridSize = EditActionUtils.createSliderBindingWithScale(1, this.sliderGridSize, this.model,
				model -> model.gridSizeProperty(), (model, nv) -> new ChangeGridSizeConfigurationAction(model, nv.intValue()));
		this.changeListenerUseGrid = (obs, ov, nv) -> {
			if (nv) {
				this.gridEnabledGroup.selectToggle(this.buttonEnableGrid);
			} else {
				this.gridEnabledGroup.selectToggle(this.buttonDisableGrid);
			}
		};
	}
	//========================================================================

	// Class part : "Bindings"
	//========================================================================
	@Override
	public void initBinding() {
		this.model.bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty());
		this.sliderGridSize.disableProperty().bind(this.buttonDisableGrid.selectedProperty());
	}

	@Override
	public void bind(final LCConfigurationI modelP) {
		modelP.gridSizeProperty().addListener(this.changeListenerGridSize);
		modelP.useGridProperty().addListener(this.changeListenerUseGrid);
		if (modelP.useGridProperty().get()) {
			this.gridEnabledGroup.selectToggle(this.buttonEnableGrid);
		} else {
			this.gridEnabledGroup.selectToggle(this.buttonDisableGrid);
		}
		this.sliderGridSize.adjustValue(modelP.gridSizeProperty().get());
	}

	@Override
	public void unbind(final LCConfigurationI modelP) {
		modelP.gridSizeProperty().removeListener(this.changeListenerGridSize);
		modelP.useGridProperty().removeListener(this.changeListenerUseGrid);
	}
	//========================================================================

}
