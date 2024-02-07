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

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.configurationcomponent.UseTimerController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.Map;

/**
 * @author Oscar PAVOINE
 */
public class TimerStartAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final IntegerProperty durationInMs;

    public TimerStartAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.TIMER;
        this.durationInMs = new SimpleIntegerProperty(60_000);
        this.nameID = "action.start.timer.name";
        this.staticDescriptionID = "action.start.timer.static.description";
        this.configIconPath = "miscellaneous/icon_timer_start_action.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.start.timer.variable.description", BindingUtils.createDivide1000Binding(this.durationInMs)));
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        UseTimerController.INSTANCE.startTimer(this.durationInMs.get());
    }

    public IntegerProperty durationInMsProperty() {
        return this.durationInMs;
    }

    // Class part : "IO"
    // ========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(TimerStartAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(TimerStartAction.class, this, nodeP);
    }
    // ========================================================================

}
