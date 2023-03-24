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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolderById;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolder;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

/**
 * Action to go to a direct key in the current configuration.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class MoveToKeyAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	@SuppressWarnings("FieldCanBeLocal")
	private StringProperty targetKeyId;
	private ComponentHolderById<GridPartKeyComponentI> targetKey;

	public MoveToKeyAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 1;
		this.category = DefaultUseActionSubCategories.MOVE_TO_SIMPLE;
		this.nameID = "go.to.key.name";
		this.movingAction = true;
		this.staticDescriptionID = "go.to.key.static.description";
		this.configIconPath = "show/icon_move_to_key.png";
		this.targetKeyId = new SimpleStringProperty();
		this.targetKey = new ComponentHolderById<>(this.targetKeyId, this.parentComponentProperty());
		this.variableDescriptionProperty().bind(TranslationFX.getTextBinding("go.to.key.variable.description", EasyBind.select(this.targetKeyProperty())
				.selectObject(GridPartKeyComponentI::nameProperty).orElse(Translation.getText("key.none.selected"))));

	}

	public ReadOnlyObjectProperty<GridPartKeyComponentI> targetKeyProperty() {
		return this.targetKey.componentProperty();
	}

	public StringProperty targetKeyIdProperty() {
		return this.targetKey.componentIdProperty();
	}

	@Override
	public void idsChanged(final Map<String, String> changes) {
		super.idsChanged(changes);
		this.targetKey.idsChanged(changes);
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		if (this.targetKeyProperty().get() != null) {
			SelectionModeController.INSTANCE.goToGridPart(this.targetKeyProperty().get());
		}
	}

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(MoveToKeyAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(MoveToKeyAction.class, this, nodeP);
	}
	//========================================================================
}
