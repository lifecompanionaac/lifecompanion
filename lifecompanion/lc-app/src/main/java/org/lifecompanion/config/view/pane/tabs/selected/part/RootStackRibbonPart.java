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

import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.config.data.control.SelectionController;

public class RootStackRibbonPart extends AbstractStackPartRibbonPart {

	@Override
	public void initModelBinding() {
		//Selection binding
		SelectionController.INSTANCE.selectedRootProperty().addListener((o, oldV, newV) -> {
			if (newV instanceof StackComponentI) {
				this.model.set((StackComponentI) newV);
			} else {
				this.model.set(null);
			}
		});
	}
}
