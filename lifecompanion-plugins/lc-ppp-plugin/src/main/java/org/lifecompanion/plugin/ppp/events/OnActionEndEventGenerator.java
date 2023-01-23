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
package org.lifecompanion.plugin.ppp.events;

import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;
import org.lifecompanion.plugin.ppp.events.categories.PPPEventSubCategories;
import org.lifecompanion.plugin.ppp.model.ActionRecord;
import org.lifecompanion.plugin.ppp.services.ActionService;

import java.util.function.Consumer;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnActionEndEventGenerator extends BaseUseEventGeneratorImpl {
    private final Consumer<ActionRecord> actionEndCallback;

    public OnActionEndEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 20;
        this.category = PPPEventSubCategories.ASSESSMENT;
        this.nameID = "ppp.plugin.events.actions.end.name";
        this.staticDescriptionID = "ppp.plugin.events.actions.end.description";
        this.variableDescriptionProperty().set(this.getStaticDescription());

        this.actionEndCallback = (action) -> this.useEventListener.fireEvent(this, null, null);
    }

    @Override
    public String getConfigIconPath() {
        return "events/icon_action_start.png";
    }

    // Class part : "Mode start/stop"
    //========================================================================
    @Override
    public void modeStart(final LCConfigurationI configuration) {
        ActionService.INSTANCE.addActionEndListener(this.actionEndCallback);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        ActionService.INSTANCE.removeActionEndListener(this.actionEndCallback);
    }
    //========================================================================
}
