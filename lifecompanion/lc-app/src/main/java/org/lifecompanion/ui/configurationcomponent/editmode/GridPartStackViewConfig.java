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

package org.lifecompanion.ui.configurationcomponent.editmode;

import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.configurationcomponent.GridPartStackComponent;
import org.lifecompanion.ui.configurationcomponent.base.GridPartStackViewBase;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.ButtonComponentOption;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.SelectableOption;
import org.lifecompanion.ui.configurationcomponent.editmode.componentoption.StackButtonOption;

/**
 * Configuration view for stack component.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartStackViewConfig extends GridPartStackViewBase {
	private SelectableOption<GridPartStackComponent> selectableOption;

	public GridPartStackViewConfig() {}

	@Override
	public void initUI() {
		super.initUI();
		//Button option
		ButtonComponentOption selectOption = new ButtonComponentOption(this.model);
		StackButtonOption stackOption = new StackButtonOption(this.model);
		selectOption.addOption(stackOption);
		//Root component UI
		this.selectableOption = new SelectableOption<>(this.model, false);
		this.selectableOption.bindSize(this);
		this.getChildren().add(this.selectableOption);
		this.selectableOption.getChildren().add(selectOption);
	}

	@Override
	protected void displayedChanged(final GridPartComponentI oldValueP, final GridPartComponentI newValueP) {
		super.displayedChanged(oldValueP, newValueP);
		this.selectableOption.toFront();
	}

	@Override
	public void showToFront() {
		super.showToFront();
		this.selectableOption.toFront();
	}
}
