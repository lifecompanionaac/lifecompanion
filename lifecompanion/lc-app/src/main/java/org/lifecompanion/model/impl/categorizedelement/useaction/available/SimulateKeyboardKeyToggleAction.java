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

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import org.jdom2.Element;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.style.KeyCompStyleI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.KeyCodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class SimulateKeyboardKeyToggleAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulateKeyboardKeyToggleAction.class);

    @XMLGenericProperty(KeyCode.class)
    private ObjectProperty<KeyCode> keyToToggle;

    public SimulateKeyboardKeyToggleAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 1;
        this.category = DefaultUseActionSubCategories.KEYBOARD;
        this.keyToToggle = new SimpleObjectProperty<>();
        this.nameID = "action.simulate.keyboard.key.toggle.action.name";
        this.staticDescriptionID = "action.simulate.keyboard.key.toggle.action.description.static";
        this.configIconPath = "computeraccess/icon_keyboard_key_toggle.png";
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.simulate.keyboard.key.toggle.action.description.variable",
                        Bindings.createStringBinding(() -> KeyCodeUtils.getTranslatedKeyCodeName(keyToToggle.get(), "no.keyboard.key.selected"), keyToToggle)));
    }

    public ObjectProperty<KeyCode> keyToToggleProperty() {
        return keyToToggle;
    }

    private Color previousBackgroundColor;

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD) && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS)) {
            KeyCode keyCode = keyToToggle.get();
            if (keyCode != null) {
                boolean released = VirtualKeyboardController.INSTANCE.toggleKeyPressRelease(keyCode);
                UseActionTriggerComponentI parentComp = this.parentComponentProperty().get();
                if (parentComp instanceof GridPartKeyComponentI) {
                    KeyCompStyleI keyStyle = ((GridPartKeyComponentI) parentComp).getKeyStyle();
                    Property<Color> forcedBackgroundColor = keyStyle.backgroundColorProperty().forced();
                    if (!forcedBackgroundColor.isBound()) {
                        if (released) {
                            forcedBackgroundColor.setValue(previousBackgroundColor);
                        } else {
                            previousBackgroundColor = forcedBackgroundColor.getValue();
                            Color baseColor = keyStyle.backgroundColorProperty().value().getValue();
                            if (baseColor != null) {
                                forcedBackgroundColor.setValue(baseColor.brighter());
                            }
                        }
                    }
                }
            }
        } else {
            LOGGER.info("Ignored {} action because {} or {} is enabled", this.getClass().getSimpleName(), GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD, GlobalRuntimeConfiguration.DISABLE_EXTERNAL_ACTIONS);
        }
    }

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SimulateKeyboardKeyToggleAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SimulateKeyboardKeyToggleAction.class, this, nodeP);
    }
    //========================================================================

}
