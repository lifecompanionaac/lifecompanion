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

package org.lifecompanion.base.data.useaction.impl.computera.mouseaction;

import java.util.Map;

import org.jdom2.Element;

import org.lifecompanion.base.data.control.virtual.mouse.VirtualMouseController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ScrollUpMouseAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	private DoubleProperty scrollAmount;

	public ScrollUpMouseAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.MOUSE_ACTION;
		this.nameID = "action.mouse.scroll.up.name";
		this.order = 15;
		this.staticDescriptionID = "action.mouse.scroll.up.description";
		this.configIconPath = "computeraccess/icon_mouse_scroll_up.png";
		this.parameterizableAction = true;
		scrollAmount = new SimpleDoubleProperty(5);
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		VirtualMouseController.INSTANCE.executeMouseWheelUp((int) scrollAmount.get());
	}
	//========================================================================

	public DoubleProperty scrollAmountProperty() {
		return scrollAmount;
	}

	@Override
	public Element serialize(final IOContextI contextP) {
		Element element = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(ScrollUpMouseAction.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(ScrollUpMouseAction.class, this, nodeP);
	}
}
