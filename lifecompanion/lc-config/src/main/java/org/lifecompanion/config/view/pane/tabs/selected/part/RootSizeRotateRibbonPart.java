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

import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.reusable.ribbonmenu.RibbonBasePart;
import javafx.scene.control.Slider;

/**
 * Part that modify rotation and base parameter for root component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RootSizeRotateRibbonPart extends RibbonBasePart<RootGraphicComponentI> implements LCViewInitHelper {
	private Slider slider;

	public RootSizeRotateRibbonPart() {
		this.initAll();
	}

	@Override
	public void initUI() {
		this.slider = new Slider(0, 360, 0);
		this.setTitle("Stack base");
		this.setContent(this.slider);
	}

	@Override
	public void initListener() {}

	@Override
	public void initBinding() {
		this.model.bind(SelectionController.INSTANCE.selectedRootProperty());
	}

	@Override
	public void bind(final RootGraphicComponentI component) {
		this.slider.valueProperty().bindBidirectional(component.rotateProperty());
	}

	@Override
	public void unbind(final RootGraphicComponentI modelP) {
		this.slider.valueProperty().unbindBidirectional(modelP.rotateProperty());
	}

}
