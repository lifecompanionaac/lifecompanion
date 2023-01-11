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

package org.lifecompanion.model.impl.categorizedelement.useevent.available;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.MouseEvent;
import org.jdom2.Element;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.selectionmode.MouseButton;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;

public class MouseButtonPressedEventGenerator extends BaseUseEventGeneratorImpl {

    @XMLGenericProperty(MouseButton.class)
    private ObjectProperty<MouseButton> wantedButton;

    public MouseButtonPressedEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 2;
        this.category = DefaultUseEventSubCategories.CLIC;
        this.wantedButton = new SimpleObjectProperty<>(MouseButton.ANY);
        this.nameID = "use.event.clic.mouse.button.pressed.name";
        this.staticDescriptionID = "use.event.clic.mouse.button.pressed.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        this.configIconPath = "control/icon_mouse_button_pressed.png";
    }

    public ObjectProperty<MouseButton> wantedButtonProperty() {
        return this.wantedButton;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        SelectionModeController.INSTANCE.addMouseEventListener((mouseEvent) -> {
            //Filter on event type and button if set
            if (mouseEvent.getEventType() == MouseEvent.MOUSE_PRESSED && (this.wantedButton.get().checkEvent(mouseEvent))) {
                this.useEventListener.fireEvent(this, null, null);
            }
        });
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {

    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(MouseButtonPressedEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(MouseButtonPressedEventGenerator.class, this, node);
    }
}
