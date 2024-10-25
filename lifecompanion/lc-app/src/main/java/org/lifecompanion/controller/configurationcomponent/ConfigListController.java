/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.configurationcomponent;

import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.ConfigListKeyOption;

import java.util.List;
import java.util.stream.Collectors;

public enum ConfigListController implements ModeListenerI {
    INSTANCE;

    private final DynamicListHelper<ConfigListKeyOption, LCConfigurationDescriptionI> dynamicListHelper;

    ConfigListController() {
        dynamicListHelper = new DynamicListHelper<>(ConfigListKeyOption.class) {
            @Override
            protected List<LCConfigurationDescriptionI> getItemsFromConfiguration(LCConfigurationI configuration) {
                LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
                return profile != null ? profile.getConfiguration().stream().filter(cd -> StringUtils.isDifferent(configuration.getID(), cd.getConfigurationId())).collect(Collectors.toList()) : null;
            }

            @Override
            protected void updateKeyOption(ConfigListKeyOption keyOption, LCConfigurationDescriptionI item) {
                keyOption.updateConfiguration(item);
            }
        };
    }

    public void nextPage() {
        this.dynamicListHelper.nextPageWithoutLoop();
    }

    public void previousPage() {
        this.dynamicListHelper.previousPageWithoutLoop();
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        this.dynamicListHelper.modeStart(configuration);
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.dynamicListHelper.modeStop(configuration);
    }
}
