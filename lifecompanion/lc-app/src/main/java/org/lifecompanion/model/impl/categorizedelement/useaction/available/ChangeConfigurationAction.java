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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.jdom2.Element;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Map;

public class ChangeConfigurationAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	private StringProperty configurationName, configurationId;

	public ChangeConfigurationAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 3;
		this.category = DefaultUseActionSubCategories.MOVE_TO_COMPLEX;
		this.nameID = "change.configuration.action.name";
		this.movingAction = true;
		this.staticDescriptionID = "change.configuration.action.description";
		this.configIconPath = "show/icon_change_configuration.png";
		this.configurationName = new SimpleStringProperty(null);
		this.configurationId = new SimpleStringProperty(null);
		this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("change.configuration.action.variable.description", this.configurationName));

	}

	public StringProperty configurationNameProperty() {
		return this.configurationName;
	}

	public StringProperty configurationIdProperty() {
		return this.configurationId;
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		SelectionModeController.INSTANCE.changeConfigurationInUseMode(this.getConfigurationDescription());
	}

	public LCConfigurationDescriptionI getConfigurationDescription() {
		if (!StringUtils.isBlank(this.configurationId.get())) {
			LCProfileI currentProfile = ProfileController.INSTANCE.currentProfileProperty().get();
			if (currentProfile != null) {
				return currentProfile.getConfigurationById(this.configurationId.get());
			}
		}
		return null;
	}

	public void updateConfigurationDescription(final LCConfigurationDescriptionI configDescription) {
		if (configDescription != null) {
			this.configurationName.set(configDescription.configurationNameProperty().get());
			this.configurationId.set(configDescription.getConfigurationId());
		} else {
			this.configurationName.set(null);
			this.configurationId.set(null);
		}
	}

	// Class part : "XML"
	//========================================================================

	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(ChangeConfigurationAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(ChangeConfigurationAction.class, this, nodeP);
	}
	//========================================================================
}
