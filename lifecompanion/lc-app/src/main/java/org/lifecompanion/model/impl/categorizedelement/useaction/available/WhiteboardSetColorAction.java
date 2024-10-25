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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.UseModeWhiteboardController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
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
public class WhiteboardSetColorAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    @XMLGenericProperty(Color.class)
    private final ObjectProperty<Color> drawingColor;

    public WhiteboardSetColorAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.WHITEBOARD;
        this.nameID = "action.whiteboard.set.color.name";
        this.staticDescriptionID = "action.whiteboard.set.color.description";
        this.configIconPath = "miscellaneous/icon_whiteboard_set_color.png";
        this.parameterizableAction = true;
        this.drawingColor = new SimpleObjectProperty<>(Color.RED);
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    public ObjectProperty<Color> drawingColorProperty() {
        return drawingColor;
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        UseModeWhiteboardController.INSTANCE.drawingColorProperty().set(this.drawingColor.get());
        UseModeWhiteboardController.INSTANCE.currentToolProperty().set(UseModeWhiteboardController.WhiteboardTool.PENCIL);
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(WhiteboardSetColorAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(WhiteboardSetColorAction.class, this, nodeP);
    }
}
