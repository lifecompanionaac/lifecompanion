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

package org.lifecompanion.model.api.categorizedelement.useevent;

import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionMainCategories;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;

public enum DefaultUseEventMainCategories implements UseEventMainCategoryI {

	// Class part : "Category definition"
	//========================================================================
	TIME("use.event.main.category.time.name", "use.event.main.category.time.description", "icon_time.png",
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.class)), //
	CONTROL("use.event.main.category.control.name", "use.event.main.category.control.description", "icon_control.png",
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.class)), //
	CONFIGURATION("use.event.main.category.configuration.name", "use.event.main.category.configuration.description", "icon_configuration.png",
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.class)),//
	KEY_LIST("use.event.main.category.key.list.name", "use.event.main.category.show.description", "icon_key_list.png",
			CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.class)), //
	UA_SEQUENCE("use.event.main.category.sequence.name", "use.event.main.category.sequence.description", "icon_user_action_sequence.png",
			CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.class)), //
	MISCELLANEOUS("use.event.main.category.divers.name", "use.event.main.category.divers.description", "icon_miscellaneous.png",
			CategorizedElementColorProvider.nextColor(DefaultUseActionMainCategories.class)), //
	;
	//========================================================================

	// Class part : "Class"
	//========================================================================
	public final static String INT_PATH_USEEVENT_MAIN_CATEGORY_ICON_PATH = "use-events/main-categories/";
	private String nameID;
	private String id;
	private String configIconPath;
	private String staticDescriptionID;
	private Color color;
	private ObservableList<UseEventSubCategoryI> subCategories;

	private DefaultUseEventMainCategories(final String nameIDP, final String staticDescriptionIDP, final String configIconPathP, final Color colorP) {
		this.id = this.name();
		this.nameID = nameIDP;
		this.configIconPath = configIconPathP;
		this.staticDescriptionID = staticDescriptionIDP;
		this.subCategories = FXCollections.observableArrayList();
		this.color = colorP;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getID() {
		return this.id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateID() {
		return this.id;
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStaticDescription() {
		return Translation.getText(this.staticDescriptionID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return Translation.getText(this.nameID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getConfigIconPath() {
		return DefaultUseEventMainCategories.INT_PATH_USEEVENT_MAIN_CATEGORY_ICON_PATH + this.configIconPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObservableList<UseEventSubCategoryI> getSubCategories() {
		return this.subCategories;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getColor() {
		return this.color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int order() {
		return this.ordinal();
	}
	//========================================================================
}
