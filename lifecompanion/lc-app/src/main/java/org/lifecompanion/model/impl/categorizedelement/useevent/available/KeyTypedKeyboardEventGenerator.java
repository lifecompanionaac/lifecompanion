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

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.GlobalKeyEventController;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.usevariable.StringUseVariable;
import org.lifecompanion.model.impl.usevariable.UseVariableDefinition;
import org.lifecompanion.util.javafx.KeyCodeUtils;

import java.util.List;

public class KeyTypedKeyboardEventGenerator extends BaseUseEventGeneratorImpl {

    private final UseVariableDefinitionI keyNameDefinition;

    @XMLGenericProperty(KeyCode.class)
    private final ObjectProperty<KeyCode> keyPressed;

    private final BooleanProperty blockKey;

    public KeyTypedKeyboardEventGenerator() {
        super();
        this.parameterizableAction = true;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.KEYS;
        this.keyPressed = new SimpleObjectProperty<>();
        this.blockKey = new SimpleBooleanProperty(false);
        this.nameID = "use.event.keyboard.key.typed.name";
        this.staticDescriptionID = "use.event.keyboard.key.typed.static.description";
        this.configIconPath = "control/icon_keyboard_key_pressed.png";
        this.keyNameDefinition = new UseVariableDefinition("KeyPressedKeyName", "use.variable.key.pressed.name",
                "use.variable.key.pressed.description", "use.variable.key.pressed.example");
        this.generatedVariables.add(this.keyNameDefinition);
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("use.event.keyboard.key.typed.variable.description",
                        Bindings.createStringBinding(() -> KeyCodeUtils.getTranslatedKeyCodeName(this.keyPressed.get(), "no.keyboard.key.selected"),
                                this.keyPressed)));
    }

    public ObjectProperty<KeyCode> keyPressedProperty() {
        return this.keyPressed;
    }

    public BooleanProperty blockKeyProperty() {
        return blockKey;
    }

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        // Listener clear on modeStop
        GlobalKeyEventController.INSTANCE.addKeyEventListenerForCurrentUseMode((keyEvent) -> {
            if (keyEvent.getEventType() == GlobalKeyEventController.LCKeyEventType.PRESSED && (this.keyPressed.get() == null || keyEvent.getKeyCode() == this.keyPressed.get())) {
                List<UseVariableI<?>> variables = List.of(new StringUseVariable(this.keyNameDefinition, KeyCodeUtils.getTranslatedKeyCodeName(keyEvent.getKeyCode(), "keyboard.key.undefined")));
                this.useEventListener.fireEvent(this, variables, null);
            }
        });
        if (this.keyPressed.get() == null) {
            GlobalKeyEventController.INSTANCE.activateListenToAllKeys();
        }
        if (this.blockKey.get()) {
            if (keyPressed.get() != null) {
                GlobalKeyEventController.INSTANCE.addKeyCodeToBlockForCurrentUseMode(this.keyPressed.get());
            } else {
                for (KeyCode keyToBlock : KeyCode.values()) {
                    GlobalKeyEventController.INSTANCE.addKeyCodeToBlockForCurrentUseMode(keyToBlock);
                }
            }
        }
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {

    }

    @Override
    public Element serialize(final IOContextI context) {
        final Element element = super.serialize(context);
        XMLObjectSerializer.serializeInto(KeyTypedKeyboardEventGenerator.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element node, final IOContextI context) throws LCException {
        super.deserialize(node, context);
        XMLObjectSerializer.deserializeInto(KeyTypedKeyboardEventGenerator.class, this, node);
        // Backward compatibility : if the "block key" config wasn't set, will block key if a key is set (previous behavior)
        if (node.getAttribute("blockKey") == null) {
            if (keyPressed.get() != null) {
                blockKey.set(true);
            } else {
                blockKey.set(false);
            }
        }
    }
}
