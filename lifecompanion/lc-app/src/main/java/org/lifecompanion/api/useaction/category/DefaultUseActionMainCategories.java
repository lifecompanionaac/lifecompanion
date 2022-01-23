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

package org.lifecompanion.api.useaction.category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.api.component.definition.useaction.UseActionMainCategoryI;
import org.lifecompanion.api.component.definition.useaction.UseActionSubCategoryI;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Create all the main action categories.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DefaultUseActionMainCategories implements UseActionMainCategoryI {
	// Class part : "Category definition"
	//========================================================================
	TEXT("use.action.main.category.text.name", "use.action.main.category.text.description", "icon_write.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	SPEAK("use.action.main.category.speak.name", "use.action.main.category.speak.description", "icon_speak.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	SHOW("use.action.main.category.show.name", "use.action.main.category.show.description", "icon_show.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	KEY_LIST("use.action.main.category.key.list.name", "use.action.main.category.show.description", "icon_key_list.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	USER_ACTION_SEQUENCE("use.action.main.category.user.action.sequence.name", "use.action.main.category.user.action.sequence.description", "icon_user_action_sequence.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)),//
	SELECTION("use.action.main.category.selection.name", "use.action.main.category.selection.description", "icon_selection_mode.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	COMPUTER_ACCESS("use.action.main.category.computer.access.name", "use.action.main.category.computer.access.description",
			"icon_computer_access.png", CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	CONFIGURATION("use.action.main.category.configuration.name", "use.action.main.category.configuration.description", "icon_configuration.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	MEDIA("use.action.main.category.media.name", "use.action.main.category.media.description", "icon_media.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	APP("use.action.main.category.app.name", "use.action.main.category.app.description", "icon_show.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class)), //
	MISCELLANEOUS("use.action.main.category.miscellaneous.name", "use.action.main.category.miscellaneous.description", "icon_miscellaneous.png",
			CategoryColors.nextColor(DefaultUseActionMainCategories.class));
	//========================================================================

	// Class part : "Class"
	//========================================================================
	public final static String INT_PATH_USEACTION_MAIN_CATEGORY_ICON_PATH = "use-actions/main-categories/";
	private final String nameID;
	private final String id;
	private final String configIconPath;
	private final String staticDescriptionID;
	private final Color color;
	private final ObservableList<UseActionSubCategoryI> subCategories;

	DefaultUseActionMainCategories(final String nameIDP, final String staticDescriptionIDP, final String configIconPathP,
								   final Color colorP) {
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
	}

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
		return DefaultUseActionMainCategories.INT_PATH_USEACTION_MAIN_CATEGORY_ICON_PATH + this.configIconPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObservableList<UseActionSubCategoryI> getSubCategories() {
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
