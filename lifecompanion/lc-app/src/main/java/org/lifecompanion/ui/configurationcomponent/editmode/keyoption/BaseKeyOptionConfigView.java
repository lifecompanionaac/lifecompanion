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

package org.lifecompanion.ui.configurationcomponent.editmode.keyoption;

import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewVBox;
import javafx.beans.property.ObjectProperty;
import javafx.scene.layout.Region;

/**
 * Base configuration view for key option
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @param <T> the key option to configuration
 */
public abstract class BaseKeyOptionConfigView<T extends KeyOptionI> extends BaseConfigurationViewVBox<T> implements KeyOptionConfigurationViewI<T> {

	@Override
	public void initUI() {
		this.setSpacing(3.0);
	}

	@Override
	public ObjectProperty<T> optionProperty() {
		return this.model;
	}

	@Override
	public Region getConfigurationView() {
		return this;
	}
}
