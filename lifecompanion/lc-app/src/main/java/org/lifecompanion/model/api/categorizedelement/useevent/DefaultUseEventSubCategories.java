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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;

public enum DefaultUseEventSubCategories implements UseEventSubCategoryI {

	// Class part : "Category definition"
	//========================================================================
	//TIME
	CYCLIC("use.event.sub.category.cyclic.name", DefaultUseEventMainCategories.TIME, CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.TIME)), //
	PERIODIC("use.event.sub.category.periodic.name", DefaultUseEventMainCategories.TIME, CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.TIME)), //

	//CONFIGURATION
	STATUS("use.event.sub.category.status.name", DefaultUseEventMainCategories.CONFIGURATION,
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.CONFIGURATION)), //
	VARIABLE("use.event.sub.category.variable.name", DefaultUseEventMainCategories.CONFIGURATION,
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.CONFIGURATION)), //

	// KEYLIST
	KEYLIST_MOVES("use.event.sub.category.keylist.moves.name", DefaultUseEventMainCategories.KEY_LIST,
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.KEY_LIST)), //

	// SEQUENCE
	UA_SEQUENCE_GENERAL("use.event.sub.category.sequence.general.name", DefaultUseEventMainCategories.UA_SEQUENCE,
			CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.UA_SEQUENCE)), //

	//CONTROL
	CLIC("use.event.sub.category.clic.name", DefaultUseEventMainCategories.CONTROL, CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.CONTROL)), //
	KEYS("use.event.sub.category.keys.name", DefaultUseEventMainCategories.CONTROL, CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.CONTROL)),//

	//SCANNING
	END_CURSOR_STRIP("use.event.sub.category.scanning.name", DefaultUseEventMainCategories.CURSOR_STRIP, CategorizedElementColorProvider.nextColor(DefaultUseEventMainCategories.CURSOR_STRIP)),//
	;
	//========================================================================

	// Class part : "Class part"
	//========================================================================
	private String nameID;
	private String id;
	private UseEventMainCategoryI mainCategory;
	private Color color;
	private ObservableList<UseEventGeneratorI> events;

	private DefaultUseEventSubCategories(final String nameIDP, final UseEventMainCategoryI mainCategoryP, final Color colorP) {
		this.nameID = nameIDP;
		this.id = this.name();
		this.mainCategory = mainCategoryP;
		this.color = colorP;
		this.events = FXCollections.observableArrayList();
		mainCategoryP.getSubCategories().add(this);
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public String generateID() {
		return this.id;
	};

	@Override
	public String getName() {
		return Translation.getText(this.nameID);
	}

	@Override
	public UseEventMainCategoryI getMainCategory() {
		return this.mainCategory;
	}

	@Override
	public ObservableList<UseEventGeneratorI> getContent() {
		return this.events;
	}

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
