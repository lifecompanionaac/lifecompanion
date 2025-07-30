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

import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ScrollDownMouseAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	private DoubleProperty scrollAmount;

	public ScrollDownMouseAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.MOUSE_ACTION;
		this.nameID = "action.mouse.scroll.down.name";
		this.order = 10;
		this.staticDescriptionID = "action.mouse.scroll.down.description";
		this.configIconPath = "computeraccess/icon_mouse_scroll_down.png";
		this.parameterizableAction = true;
		scrollAmount = new SimpleDoubleProperty(5);
		this.variableDescriptionProperty().set(this.getStaticDescription());
		this.allowSystems = SystemType.allExpectMobile();
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		VirtualMouseController.INSTANCE.executeMouseWheelDown((int) scrollAmount.get());
	}
	//========================================================================

	public DoubleProperty scrollAmountProperty() {
		return scrollAmount;
	}

	@Override
	public Element serialize(final IOContextI contextP) {
		Element element = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(ScrollDownMouseAction.class, this, element);
		return element;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(ScrollDownMouseAction.class, this, nodeP);
	}
}
