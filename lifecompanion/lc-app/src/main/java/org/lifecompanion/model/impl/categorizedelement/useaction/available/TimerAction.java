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

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.controller.configurationcomponent.UseModeProgressDisplayerController;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TimerAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final IntegerProperty time;
    private final DoubleProperty progressProperty = new SimpleDoubleProperty();
    private final UseModeProgressDisplayerController progressController = UseModeProgressDisplayerController.INSTANCE;

    public TimerAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.SCRIPT;
        this.time = new SimpleIntegerProperty(1000);
        this.nameID = "action.timer.name";
        this.staticDescriptionID = "action.timer.static.description";
        this.configIconPath = "miscellaneous/icon_pause_action.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.timer.variable.description", BindingUtils.createDivide1000Binding(this.time)));
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        long startTime = System.currentTimeMillis();
        long durationInMillis = this.time.get();
        long endTime = startTime + durationInMillis;
        progressController.launchTimer(durationInMillis, () -> {});

        while (System.currentTimeMillis() < endTime) {
            double progress = (System.currentTimeMillis() - startTime) / (double) durationInMillis;
            progressProperty.set(progress);
        }

        progressProperty.set(1.0);
        Platform.runLater(() -> progressController.hideAllProgress());
    }

    public IntegerProperty timerProperty() {
        return this.time;
    }

    // Class part : "IO"
    // ========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(TimerAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(TimerAction.class, this, nodeP);
    }
    // ========================================================================

}
