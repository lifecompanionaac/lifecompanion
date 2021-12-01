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

package org.lifecompanion.base.data.useaction.impl.configuration.frame;

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.api.component.definition.FramePosition;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.stage.Stage;

public class ChangeFramePositionAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	@XMLGenericProperty(FramePosition.class)
	private ObjectProperty<FramePosition> framePosition;

	public ChangeFramePositionAction() {
		super(UseActionTriggerComponentI.class);
		this.order = 0;
		this.category = DefaultUseActionSubCategories.FRAME;
		this.nameID = "action.change.frame.position.name";
		this.staticDescriptionID = "action.change.frame.position.static.description";
		this.configIconPath = "configuration/icon_move_frame.png";
		this.parameterizableAction = true;
		this.framePosition = new SimpleObjectProperty<>();
		this.variableDescriptionProperty()
				.bind(TranslationFX.getTextBinding("action.change.frame.position.variable.description", Bindings.createStringBinding(() -> {
					FramePosition framePositionValue = this.framePosition.get();
					if (framePositionValue != null) {
						return framePositionValue.getText();
					} else {
						return Translation.getText("frame.position.none");
					}
				}, this.framePosition)));
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		if (!AppController.INSTANCE.isOnEmbeddedDevice() && this.framePosition.get() != null) {
			final Stage mainFrame = AppController.INSTANCE.getMainStage();
			LCUtils.runOnFXThread(() -> {
				if (!mainFrame.isFullScreen() && !mainFrame.isMaximized()) {
					AppController.INSTANCE.moveFrameTo(this.framePosition.get());
				}
			});
		}
	}

	public ObjectProperty<FramePosition> framePositionProperty() {
		return this.framePosition;
	}

	@Override
	public Element serialize(final IOContextI contextP) {
		Element element = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(ChangeFramePositionAction.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(ChangeFramePositionAction.class, this, nodeP);
	}
}
