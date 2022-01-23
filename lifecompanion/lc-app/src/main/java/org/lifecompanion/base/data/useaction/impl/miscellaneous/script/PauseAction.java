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
package org.lifecompanion.base.data.useaction.impl.miscellaneous.script;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.jdom2.Element;
import org.lifecompanion.api.component.definition.useaction.UseActionEvent;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseVariableI;
import org.lifecompanion.api.exception.LCException;
import org.lifecompanion.api.io.IOContextI;
import org.lifecompanion.api.mode.AppMode;
import org.lifecompanion.api.useaction.category.DefaultUseActionSubCategories;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.useaction.baseimpl.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;

import java.util.Map;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PauseAction extends SimpleUseActionImpl<UseActionTriggerComponentI> {
    private final static long CHECK_PAUSE_TIMING = 1000;
    private final IntegerProperty pause;

    public PauseAction() {
        super(UseActionTriggerComponentI.class);
        this.order = 0;
        this.category = DefaultUseActionSubCategories.SCRIPT;
        this.pause = new SimpleIntegerProperty(1000);
        this.nameID = "action.pause.name";
        this.staticDescriptionID = "action.pause.static.description";
        this.configIconPath = "miscellaneous/icon_pause_action.png";
        this.parameterizableAction = true;
        this.variableDescriptionProperty()
                .bind(TranslationFX.getTextBinding("action.pause.variable.description", LCUtils.createDivide1000Binding(this.pause)));
    }

    @Override
    public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
        // Pause with a busy wait
        final long startTime = System.currentTimeMillis();
        final int waitingTime = this.pause.get();
        while ((System.currentTimeMillis() - startTime) <= waitingTime && AppController.INSTANCE.currentModeProperty().get() == AppMode.USE) {
            LCUtils.safeSleep(Math.min(CHECK_PAUSE_TIMING, Math.max(waitingTime - (System.currentTimeMillis() - startTime), 0)));
        }
    }

    public IntegerProperty pauseProperty() {
        return this.pause;
    }

    // Class part : "IO"
    // ========================================================================
    @Override
    public Element serialize(final IOContextI contextP) {
        Element elem = super.serialize(contextP);
        XMLObjectSerializer.serializeInto(PauseAction.class, this, elem);
        return elem;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        super.deserialize(nodeP, contextP);
        XMLObjectSerializer.deserializeInto(PauseAction.class, this, nodeP);
    }
    // ========================================================================

}
