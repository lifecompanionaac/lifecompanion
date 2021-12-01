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

package org.lifecompanion.config.view.component.simple;

import org.lifecompanion.base.view.component.simple.LCConfigurationViewBase;
import org.lifecompanion.config.data.control.SelectionController;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;

/**
 * Displayer for a configuration.<br>
 * Display it in configuration mode.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationViewConfig extends LCConfigurationViewBase {
	private StringProperty styleProperty;

	public LCConfigurationViewConfig() {
		super();
		this.styleProperty = new SimpleStringProperty();
	}

	@Override
	public void initUI() {
		super.initUI();
		this.styleProperty().bind(this.styleProperty);
	}

	@Override
	public void initBinding() {
		super.initBinding();
		this.styleProperty.bind(Bindings.createStringBinding(() -> {
			if (this.model.useGridProperty().get()) {
				return this.configurationCssStyle.get() + "-fx-background-image: url(/icons/background_grid_part.png);-fx-background-size: "
						+ this.model.gridSizeProperty().get() + "px;";
			} else {
				return this.configurationCssStyle.get();
			}
		}, this.model.useGridProperty(), this.model.gridSizeProperty(), this.configurationCssStyle));
	}

	@Override
	public void initListener() {
		super.initListener();
		this.paneForRootComponents.setOnMouseClicked((me) -> {
			//Clear selection, only there is no other component clicked
			Node intersectedNode = me.getPickResult().getIntersectedNode();
			if (intersectedNode == paneForRootComponents) {
				SelectionController.INSTANCE.clearSelection();
			}
		});
	}
}
