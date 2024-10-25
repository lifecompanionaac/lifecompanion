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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.UseModeWhiteboardController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WhiteboardSetPencilSizeAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    private final DoubleProperty pencilSize;

    public WhiteboardSetPencilSizeAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.WHITEBOARD;
        this.nameID = "action.whiteboard.set.pencil.size.name";
        this.staticDescriptionID = "action.whiteboard.set.pencil.size.description";
        this.configIconPath = "miscellaneous/icon_whiteboard_pencil_size.png";
        this.parameterizableAction = true;
        this.pencilSize = new SimpleDoubleProperty(20.0);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public DoubleProperty pencilSizeProperty() {
        return pencilSize;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        UseModeWhiteboardController.INSTANCE.pencilSizeProperty().set(this.pencilSize.get());
        UseModeWhiteboardController.INSTANCE.currentToolProperty().set(UseModeWhiteboardController.WhiteboardTool.PENCIL);
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(WhiteboardSetPencilSizeAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(WhiteboardSetPencilSizeAction.class, this, nodeP);
    }
}
