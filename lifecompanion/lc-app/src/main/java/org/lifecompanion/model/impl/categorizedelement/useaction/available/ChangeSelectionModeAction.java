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

import javafx.beans.property.*;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.controller.textcomponent.WritingStateController;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.io.XMLUtils;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.selectionmode.DirectSelectionModeI;
import org.lifecompanion.model.api.selectionmode.SelectionModeI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.configurationcomponent.WriterEntry;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.selectionmode.DirectActivationSelectionMode;
import org.lifecompanion.model.impl.selectionmode.SelectionModeParameter;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ChangeSelectionModeAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeSelectionModeAction.class);

    private final ObjectProperty<Class<? extends SelectionModeI>> selectionModeType;

    public ChangeSelectionModeAction() {
        super(UseActionTriggerComponentI.class);
        this.category = DefaultUseActionSubCategories.SELECTION_MODE_GENERAL;
        this.order = 1;
        this.selectionModeType = new SimpleObjectProperty<>(DirectActivationSelectionMode.class);
        this.nameID = "action.simple.change.selection.mode.name";
        this.staticDescriptionID = "action.simple.change.selection.mode.description";
        this.configIconPath = "selection/icon_change_selection_mode.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty().set(getStaticDescription());
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if (this.selectionModeType.get() != null) {
            SelectionModeController.INSTANCE.changeUseModeSelectionModeTo(selectionModeType.get());
        }
    }

    public ObjectProperty<Class<? extends SelectionModeI>> selectionModeTypeProperty() {
        return selectionModeType;
    }

    private static final String ATB_SELECTION_MODE_TYPE = "selectionModeType";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element node = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeSelectionModeAction.class, this, node);
        XMLUtils.write(this.selectionModeType.get().getSimpleName(), ATB_SELECTION_MODE_TYPE, node);
        return node;
    }

    @Override
    public void deserialize(final Element node, final IOContextI contextP) throws LCException {
        super.deserialize(node, contextP);
        XMLObjectSerializer.deserializeInto(ChangeSelectionModeAction.class, this, node);
        String selectModeName = XMLUtils.readString(ATB_SELECTION_MODE_TYPE, node);
        if (selectModeName != null) {
            try {
                this.selectionModeType.set(ConfigurationComponentIOHelper.getClassForName(selectModeName));
            } catch (ClassNotFoundException e) {
                LOGGER.warn("Couldn't load the select mode class", e);
            }
        }
    }
}
