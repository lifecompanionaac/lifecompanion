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
package org.lifecompanion.ui.configurationcomponent.editmode.keyoption;

import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.BasicKeyOption;
import org.lifecompanion.controller.io.ReflectionHelper;
import org.lifecompanion.controller.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to provide the needed configuration view for each key option.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum KeyOptionViewProvider {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(KeyOptionViewProvider.class);

    /**
     * Map that keep every type
     */
    private final Map<Class<? extends KeyOptionI>, KeyOptionConfigurationViewI<?>> viewTypes;

    KeyOptionViewProvider() {
        this.viewTypes = new HashMap<>();
        this.initializeConfigurations();
    }

    // Class part : "Init"
    //========================================================================
    @SuppressWarnings("rawtypes")
    private void initializeConfigurations() {
        List<Class<? extends KeyOptionConfigurationViewI>> implementedActions = ReflectionHelper.findImplementationsInModules(KeyOptionConfigurationViewI.class);
        for (Class<? extends KeyOptionConfigurationViewI> configViewSubType : implementedActions) {
            this.addConfigurationViewType(configViewSubType);
        }
        //Plugin
        PluginManager.INSTANCE.getKeyOptionConfigViews().registerListenerAndDrainCache(this::addConfigurationViewType);
    }

    @SuppressWarnings("rawtypes")
    private void addConfigurationViewType(final Class<? extends KeyOptionConfigurationViewI> configViewSubType) {
        String className = configViewSubType.getName();
        try {
            this.LOGGER.debug("Found a subtype of key option configuration view : {}", className);
            KeyOptionConfigurationViewI<?> configurationView = configViewSubType.getConstructor().newInstance();
            Class<? extends KeyOptionI> keyOptionType = configurationView.getConfiguredKeyOptionType();
            this.viewTypes.put(keyOptionType, configurationView);
            this.LOGGER.debug("Associate the key option configuration view {} to {}", className, keyOptionType);
        } catch (Exception e) {
            this.LOGGER.warn("A found key option configuration ({}) couldn't be created", className, e);
        }
    }
    //========================================================================

    // Class part : "Create view"
    //========================================================================
    @SuppressWarnings("unchecked")
    public KeyOptionConfigurationViewI<KeyOptionI> getConfigurationViewFor(final Class<? extends KeyOptionI> keyOptionType) {
        if (this.viewTypes.containsKey(keyOptionType)) {
            try {
                //Create a new instance and init its view content
                KeyOptionConfigurationViewI<KeyOptionI> configView = (KeyOptionConfigurationViewI<KeyOptionI>) this.viewTypes.get(keyOptionType);
                KeyOptionConfigurationViewI<KeyOptionI> viewInstance = configView.getClass().getConstructor().newInstance();
                viewInstance.initAll();
                return viewInstance;
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't get the configuration view for key option type {}", keyOptionType, e);
                return null;
            }
        } else {
            if (!(BasicKeyOption.class.equals(keyOptionType))) {
                this.LOGGER.warn("Ask a for a keyoption configuration view for the type {} but didn't found any matching type", keyOptionType);
            }
            return null;
        }
    }
    //========================================================================
}
