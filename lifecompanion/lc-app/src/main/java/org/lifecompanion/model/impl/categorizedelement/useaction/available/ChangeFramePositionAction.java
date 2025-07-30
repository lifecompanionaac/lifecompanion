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
import javafx.stage.Stage;
import org.jdom2.Element;
import org.lifecompanion.controller.useapi.GlobalRuntimeConfigurationController;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class ChangeFramePositionAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFramePositionAction.class);


    @XMLGenericProperty(FramePosition.class)
    private ObjectProperty<FramePosition> framePosition;

    public ChangeFramePositionAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.FRAME;
        this.nameID = "action.change.frame.position.name";
        this.staticDescriptionID = "action.change.frame.position.static.description";
        this.configIconPath = "configuration/icon_move_frame.png";
        this.parameterizableAction = true;
        this.framePosition = new SimpleObjectProperty<>();
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.change.frame.position.variable.description", Bindings.createStringBinding(() -> {
                    FramePosition framePositionValue = this.framePosition.get();
                    if (framePositionValue != null) {
                        return framePositionValue.getText();
                    } else {
                        return Translation.getText("frame.position.none");
                    }
                }, this.framePosition)));
        this.allowSystems = SystemType.allExpectMobile();
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        List<GlobalRuntimeConfiguration> shouldBeNotActivated = List.of(GlobalRuntimeConfiguration.FORCE_WINDOW_LOCATION,
                GlobalRuntimeConfiguration.FORCE_WINDOW_SIZE,
                GlobalRuntimeConfiguration.DISABLE_WINDOW_FULLSCREEN);
        if (shouldBeNotActivated.stream().anyMatch(GlobalRuntimeConfigurationController.INSTANCE::isPresent)) {
            LOGGER.info("ChangeFramePositionAction action ignored because one of the following configuration {} is enabled", shouldBeNotActivated);
        } else if (this.framePosition.get() != null) {
            FXThreadUtils.runOnFXThread(() -> {
                final Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
                if (stage != null) {
                    if (!stage.isFullScreen() && !stage.isMaximized()) {
                        StageUtils.moveStageTo(stage, this.framePosition.get());
                        VirtualMouseController.INSTANCE.centerMouseOnStage();
                    }
                }
            });
        }
    }

    public ObjectProperty<FramePosition> framePositionProperty() {
        return this.framePosition;
    }

    @Override
    public Element serialize(final IOContextI contextP) {
        Element element = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(ChangeFramePositionAction.class, this, element);
        return element;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(ChangeFramePositionAction.class, this, nodeP);
    }
}
