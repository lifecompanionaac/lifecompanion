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
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.input.KeyCode;
import org.jdom2.Element;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.controller.virtualkeyboard.VirtualKeyboardController;
import org.lifecompanion.model.impl.categorizedelement.useaction.RepeatActionBaseImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class SimulateKeyboardUntilReleaseAction extends RepeatActionBaseImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(SimulateKeyboardUntilReleaseAction.class);


    @XMLGenericProperty(KeyCode.class)
    private ObjectProperty<KeyCode> keyPressed1, keyPressed2, keyPressed3;

    public SimulateKeyboardUntilReleaseAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 5;
        this.category = DefaultUseActionSubCategories.KEYBOARD;
        this.keyPressed1 = new SimpleObjectProperty<>();
        this.keyPressed2 = new SimpleObjectProperty<>();
        this.keyPressed3 = new SimpleObjectProperty<>();
        this.nameID = "action.simulate.keyboard.key.until.release.action.name";
        this.staticDescriptionID = "action.simulate.keyboard.key.until.release.action.description";
        this.configIconPath = "computeraccess/icon_keyboard_key_until_repeat.png";
        this.variableDescriptionProperty().bind(
                TranslationFX.getTextBinding("action.simulate.keyboard.key.until.release.action.description.variable", Bindings.createStringBinding(() -> {
                    return this.getKeyText(this.keyPressed1, false, false) + this.getKeyText(this.keyPressed2, true, true)
                            + this.getKeyText(this.keyPressed3, true, true);
                }, this.keyPressed1, this.keyPressed2, this.keyPressed3)));
    }

    private String getKeyText(final ObjectProperty<KeyCode> keyProp, final boolean empty, final boolean comma) {
        return keyProp.get() != null ? (comma ? ", " : "") + keyProp.get().getName() : empty ? "" : Translation.getText("no.keyboard.key.selected");
    }

    public ObjectProperty<KeyCode> keyPressed1Property() {
        return this.keyPressed1;
    }

    public ObjectProperty<KeyCode> keyPressed2Property() {
        return this.keyPressed2;
    }

    public ObjectProperty<KeyCode> keyPressed3Property() {
        return this.keyPressed3;
    }

    /**
     * Cached keys code to repeat
     */
    private KeyCode[] keyCodes;

    @Override
    protected void executeFirstBeforeRepeat(final UseActionEvent eventType) {
        this.keyCodes = this.createKeyCodeList();
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD) && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLED_EXTERNAL_ACTIONS)) {
            if (this.keyCodes.length > 0) {
                VirtualKeyboardController.INSTANCE.keyPressThenRelease(this.keyCodes);
            }
        } else {
            LOGGER.info("Ignored {} action because {} or {} is enabled", this.getClass().getSimpleName(), GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD, GlobalRuntimeConfiguration.DISABLED_EXTERNAL_ACTIONS);
        }
    }

    @Override
    protected void executeOnRepeat(final UseActionEvent eventType) {
        if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD) && !GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLED_EXTERNAL_ACTIONS)) {
            if (this.keyCodes.length > 0) {
                VirtualKeyboardController.INSTANCE.keyPressThenRelease(this.keyCodes);
            }
        } else {
            LOGGER.info("Ignored {} action because {} or {} is enabled", this.getClass().getSimpleName(), GlobalRuntimeConfiguration.DISABLE_VIRTUAL_KEYBOARD, GlobalRuntimeConfiguration.DISABLED_EXTERNAL_ACTIONS);
        }
    }

    @Override
    protected void repeatEnded(final UseActionEvent eventType) {
//		KeyCode[] keyCodes = this.createKeyCodeList();
        //		if (keyCodes.length > 0) {
        //			VirtualKeyboardController.INSTANCE.keyPressThenRelease(keyCodes);
        //		}
        this.keyCodes = null;
    }

    @Override
    protected long getDelayBeforeRepeatStartMillis() {
        return VirtualKeyboardController.DELAY_BEFORE_REPEAT_KEY_START;
    }

    @Override
    protected long getDelayBetweenEachRepeatMillis() {
        return VirtualKeyboardController.DELAY_REPEAT_KEY_HOLD;
    }

    private KeyCode[] createKeyCodeList() {
        List<KeyCode> keyCodes = new ArrayList<>(3);
        if (this.keyPressed1.get() != null) {
            keyCodes.add(this.keyPressed1.get());
        }
        if (this.keyPressed2.get() != null) {
            keyCodes.add(this.keyPressed2.get());
        }
        if (this.keyPressed3.get() != null) {
            keyCodes.add(this.keyPressed3.get());
        }
        return keyCodes.toArray(new KeyCode[keyCodes.size()]);
    }

    // Class part : "XML"
    //========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(SimulateKeyboardUntilReleaseAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(SimulateKeyboardUntilReleaseAction.class, this, nodeP);
    }
    //========================================================================

}
