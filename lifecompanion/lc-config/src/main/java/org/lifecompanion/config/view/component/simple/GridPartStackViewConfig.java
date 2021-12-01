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

import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.base.data.component.simple.GridPartStackComponent;
import org.lifecompanion.base.view.component.simple.GridPartStackViewBase;
import org.lifecompanion.config.view.component.option.ButtonComponentOption;
import org.lifecompanion.config.view.component.option.SelectableOption;
import org.lifecompanion.config.view.component.option.StackButtonOption;

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
