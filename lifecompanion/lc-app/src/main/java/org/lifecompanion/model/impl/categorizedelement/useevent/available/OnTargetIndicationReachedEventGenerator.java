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

import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.framework.commons.SystemType;
import org.lifecompanion.model.api.categorizedelement.useevent.DefaultUseEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;

public class OnTargetIndicationReachedEventGenerator extends BaseUseEventGeneratorImpl {

    public OnTargetIndicationReachedEventGenerator() {
        super();
        this.parameterizableAction = false;
        this.order = 0;
        this.category = DefaultUseEventSubCategories.MISCELLANEOUS;
        this.nameID = "use.event.on.target.indication.reached.name";
        this.staticDescriptionID = "use.event.on.target.indication.reached.description";
        this.configIconPath = "miscellaneous/icon_on_target_reached.png";
        this.variableDescriptionProperty().set(this.getStaticDescription());
        this.allowSystems = SystemType.allExpectMobile();
    }

    private Runnable listener;

    @Override
    public void modeStart(final LCConfigurationI configuration) {
        this.listener = () -> this.useEventListener.fireEvent(this, null, null);
        IndicationController.INSTANCE.getOnTargetKeyReachedListeners().add(this.listener);
    }

    @Override
    public void modeStop(final LCConfigurationI configuration) {
        IndicationController.INSTANCE.getOnTargetKeyReachedListeners().remove(this.listener);
        this.listener = null;
    }
}
