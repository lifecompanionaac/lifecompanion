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

import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.base.data.component.simple.StackComponent;
import org.lifecompanion.base.view.component.simple.StackViewBase;
import org.lifecompanion.config.view.component.option.ButtonComponentOption;
import org.lifecompanion.config.view.component.option.MoveButtonOption;
import org.lifecompanion.config.view.component.option.RootComponentOption;
import org.lifecompanion.config.view.component.option.StackButtonOption;

/**
 * Node that display a {@link StackComponent}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class StackViewConfig extends StackViewBase {
	private RootComponentOption rootComponentOption;

	private ButtonComponentOption selectOption;

	public StackViewConfig() {}

	@Override
	public void initUI() {
		super.initUI();
		//Button option
		this.selectOption = new ButtonComponentOption(this.model);
		MoveButtonOption<StackComponent> moveOption = new MoveButtonOption<>(this.model);
		StackButtonOption stackOption = new StackButtonOption(this.model);
		this.selectOption.addOption(stackOption);
		this.selectOption.addOption(moveOption);
		//Root component UI
		this.rootComponentOption = new RootComponentOption(this.model);
		this.rootComponentOption.bindSize(this);
		this.getChildren().add(this.rootComponentOption);
		this.rootComponentOption.getChildren().add(this.selectOption);
		UIUtils.applyPerformanceConfiguration(this);
	}

	@Override
	protected void displayedChanged(final GridPartComponentI oldValueP, final GridPartComponentI newValueP) {
		super.displayedChanged(oldValueP, newValueP);
		this.rootComponentOption.toFront();
	}

	@Override
	public void showToFront() {
		super.showToFront();
		this.rootComponentOption.toFront();
	}
}
