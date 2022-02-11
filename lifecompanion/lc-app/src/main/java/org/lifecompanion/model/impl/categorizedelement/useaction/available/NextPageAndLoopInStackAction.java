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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.fxmisc.easybind.EasyBind;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.ComponentHolder;
import org.lifecompanion.base.data.control.SelectionModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

/**
 * Action to go to the next page in a selected stack and loop if we are on the last page.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class NextPageAndLoopInStackAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

	private final StringProperty changedPageParentStackId;
	private final ComponentHolder<StackComponentI> changedPageParentStack;

	public NextPageAndLoopInStackAction() {
		super(UseActionTriggerComponentI.class);
		this.category = DefaultUseActionSubCategories.CHANGE_PAGE;
		this.order = 10;
		this.nameID = "next.page.and.loop.in.stack.name";
		this.staticDescriptionID = "next.page.and.loop.in.stack.static.description";
		this.configIconPath = "show/icon_next_and_loop_page_stack.png";
		this.changedPageParentStackId = new SimpleStringProperty();
		this.changedPageParentStack = new ComponentHolder<>(this.changedPageParentStackId, this.parentComponentProperty());
		this.variableDescriptionProperty().bind(
				TranslationFX.getTextBinding("next.page.and.loop.in.stack.variable.description", EasyBind.select(this.changedPageParentStackProperty())
						.selectObject(StackComponentI::nameProperty).orElse(Translation.getText("stack.none.selected"))));

	}

	public ObjectProperty<StackComponentI> changedPageParentStackProperty() {
		return this.changedPageParentStack.componentProperty();
	}

	@Override
	public void idsChanged(final Map<String, String> changes) {
		super.idsChanged(changes);
		this.changedPageParentStack.idsChanged(changes);
	}

	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		StackComponentI stack = this.changedPageParentStackProperty().get();
		if (stack != null) {
			if (stack.nextPossibleProperty().get()) {
				SelectionModeController.INSTANCE.goToGridPart(stack.getNextComponent());
			} else {
				SelectionModeController.INSTANCE.goToGridPart(stack.getComponentList().get(0));
			}
		}
	}

	// Class part : "XML"
	//========================================================================
	@Override
	public Element serialize(final IOContextI contextP) {
		Element elem = super.serialize(contextP);
		XMLObjectSerializer.serializeInto(NextPageAndLoopInStackAction.class, this, elem);
		return elem;
	}

	@Override
	public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
		super.deserialize(nodeP, contextP);
		XMLObjectSerializer.deserializeInto(NextPageAndLoopInStackAction.class, this, nodeP);
	}
	//========================================================================
}
