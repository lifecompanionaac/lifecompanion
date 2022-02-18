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
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.api.configurationcomponent.FramePosition;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.util.javafx.StageUtils;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLGenericProperty;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Map;

public class ChangeFramePositionAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {

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
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        if ( this.framePosition.get() != null) {
            final Stage stage = AppModeController.INSTANCE.getUseModeContext().stageProperty().get();
            FXThreadUtils.runOnFXThread(() -> {
                if (!stage.isFullScreen() && !stage.isMaximized()) {
                    StageUtils.moveStageTo(AppModeController.INSTANCE.getEditModeContext().getStage(), this.framePosition.get());
                    VirtualMouseController.INSTANCE.centerMouseOnStage();
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
