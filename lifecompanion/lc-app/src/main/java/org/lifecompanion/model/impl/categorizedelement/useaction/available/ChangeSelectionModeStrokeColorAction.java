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

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ChangeSelectionModeStrokeColorAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	@XMLGenericProperty(Color.class)
	private ObjectProperty<Color> wantedColor;

	public ChangeSelectionModeStrokeColorAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 3;
		this.category = DefaultUseActionSubCategories.SELECTION_MODE_GENERAL;
		this.nameID = "action.change.selection.mode.stroke.color.name";
		this.staticDescriptionID = "action.change.selection.mode.stroke.color.static.description";
		this.configIconPath = "configuration/icon_change_selection_mode_color.png";
		this.parameterizableAction = true;
		this.wantedColor = new SimpleObjectProperty<>(Color.GREEN);
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	public ObjectProperty<Color> wantedColorProperty() {
		return this.wantedColor;
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		SelectionModeController.INSTANCE.changeTempStrokeColor(this.wantedColor.get());
	}

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(ChangeSelectionModeStrokeColorAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(ChangeSelectionModeStrokeColorAction.class, this, nodeP);
	}
	//========================================================================
}
