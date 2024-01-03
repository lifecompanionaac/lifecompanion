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

import javafx.beans.property.SimpleIntegerProperty;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.ConfigListKeyOption;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public enum ConfigListController implements ModeListenerI {
    INSTANCE;

    private final List<ConfigListKeyOption> configKeyOptions;
    private List<LCConfigurationDescriptionI> configurations;
    private final SimpleIntegerProperty pageIndex;


    ConfigListController() {
        this.configKeyOptions = new ArrayList<>();
        this.pageIndex = new SimpleIntegerProperty(-1);
        this.pageIndex.addListener(inv -> refreshConfigurationList());
    }

    public void nextPage() {
        int pageSize = configKeyOptions.size();
        int maxPageCount = (int) Math.ceil((1.0 * configurations.size()) / (1.0 * pageSize));
        if (pageIndex.get() + 1 < maxPageCount) {
            pageIndex.set(pageIndex.get() + 1);
        }
    }

    public void previousPage() {
        if (pageIndex.get() - 1 >= 0) {
            pageIndex.set(pageIndex.get() - 1);
        }
    }

    private void refreshConfigurationList() {
        int pageIndexV = pageIndex.get();
        for (int i = 0; i < configKeyOptions.size(); i++) {
            int index = pageIndexV * configKeyOptions.size() + i;
            ConfigListKeyOption keyOption = configKeyOptions.get(i);
            FXThreadUtils.runOnFXThread(() -> keyOption.updateConfiguration(index >= 0 && index < configurations.size() ? configurations.get(index) : null));
        }
    }

    @Override
    public void modeStart(LCConfigurationI configuration) {
        LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
        if (profile != null) {
            configurations = profile.getConfiguration().stream().filter(cd -> StringUtils.isDifferent(configuration.getID(), cd.getConfigurationId())).collect(Collectors.toList());

            Map<GridComponentI, List<ConfigListKeyOption>> groupKeysMap = new HashMap<>();
            ConfigurationComponentUtils.findKeyOptionsByGrid(ConfigListKeyOption.class, configuration, groupKeysMap, null);
            groupKeysMap.values().stream().flatMap(List::stream).distinct().forEach(configKeyOptions::add);

            this.pageIndex.set(0);
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.configurations = null;
        this.configKeyOptions.clear();
        this.pageIndex.set(-1);
    }
}
