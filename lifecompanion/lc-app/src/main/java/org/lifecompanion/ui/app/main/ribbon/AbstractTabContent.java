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

package org.lifecompanion.ui.app.main.ribbon;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.layout.BorderPane;

public abstract class AbstractTabContent extends BorderPane {
	/**
	 * To enable/disable this tab
	 */
	protected BooleanProperty disableTab;

	/**
	 * Tab title
	 */
	protected StringProperty tabTitle;

	protected AbstractTabContent() {
		this.disableTab = new SimpleBooleanProperty(false);
		this.tabTitle = new SimpleStringProperty("");
	}

	public BooleanProperty disableTabProperty() {
		return this.disableTab;
	}

	public StringProperty tabTitleProperty() {
		return this.tabTitle;
	}
}
