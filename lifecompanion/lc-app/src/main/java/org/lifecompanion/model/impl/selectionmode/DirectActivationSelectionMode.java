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

package org.lifecompanion.model.impl.selectionmode;

import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.ui.selectionmode.SimpleClicSelectionModeView;
import javafx.scene.Node;

/**
 * Simplest selection mode in LifeCompanion.<br>
 * Will select key by a clic/press on it (depending if the mode is used on touch platform)
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DirectActivationSelectionMode extends AbstractDirectSelectionMode<SimpleClicSelectionModeView> implements DirectSelectionModeI {

	public DirectActivationSelectionMode() {
		super(true);
		this.view = new SimpleClicSelectionModeView(this);
		this.drawProgress.set(false);
	}

	@Override
	public Node getSelectionView() {
		return this.view;
	}
}
