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
package org.lifecompanion.config.view.useaction;

import org.lifecompanion.api.component.definition.useaction.BaseUseActionI;
import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.base.data.io.ReflectionHelper;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to provide the needed configuration view for each action type.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseActionConfigurationViewProvider {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(UseActionConfigurationViewProvider.class);

    /**
     * Map that keep every type
     */
    private Map<Class<? extends BaseUseActionI<?>>, UseActionConfigurationViewI<?>> viewTypes;

    UseActionConfigurationViewProvider() {
        this.viewTypes = new HashMap<>();
        this.initializeConfigurations();
        PluginManager.INSTANCE.getUseActionConfigViews().registerListenerAndDrainCache(this::addConfigurationViewClass);
    }

    // Class part : "Init"
    //========================================================================
    @SuppressWarnings({"rawtypes"})
    private void initializeConfigurations() {
        List<Class<? extends UseActionConfigurationViewI>> implementations = ReflectionHelper.findImplementationsInModules(UseActionConfigurationViewI.class);
        for (Class<? extends UseActionConfigurationViewI> element : implementations) {
            this.addConfigurationViewClass(element);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addConfigurationViewClass(final Class<? extends UseActionConfigurationViewI> configViewSubType) {
        if (!Modifier.isAbstract(configViewSubType.getModifiers())) {
            String className = configViewSubType.getName();
            try {
                this.LOGGER.debug("Found a subtype of use action configuration view : {}", className);
                UseActionConfigurationViewI configurationView = configViewSubType.getConstructor().newInstance();
                Class<? extends BaseUseActionI<?>> actionType = configurationView.getConfiguredActionType();
                this.viewTypes.put(actionType, configurationView);
                this.LOGGER.debug("Associate the configuration view {} to {}", className, actionType);
            } catch (Exception e) {
                this.LOGGER.warn("A found use action configuration ({}) couldn't be created", className, e);
            }
        } else {
            this.LOGGER.warn("Found a configuration view type {} but it's an abstract class", configViewSubType);
        }
    }
    //========================================================================

    // Class part : "Create view"
    //========================================================================
    @SuppressWarnings("unchecked")
    public UseActionConfigurationViewI<BaseUseActionI<?>> getConfigurationViewFor(final BaseUseActionI<?> useAction) {
        if (this.viewTypes.containsKey(useAction.getClass())) {
            try {
                //Create a new instance and init its view content
                UseActionConfigurationViewI<BaseUseActionI<?>> configView = (UseActionConfigurationViewI<BaseUseActionI<?>>) this.viewTypes
                        .get(useAction.getClass());
                UseActionConfigurationViewI<BaseUseActionI<?>> viewInstance = configView.getClass().getConstructor().newInstance();
                viewInstance.initAll();
                return viewInstance;
            } catch (Exception e) {
                this.LOGGER.warn("Couldn't get the configuration view for type {}", useAction.getClass(), e);
                return null;
            }
        } else {
            this.LOGGER.warn("Ask a for a use action configuration view for the type {} but didn't found any matching type", useAction.getClass());
            return null;
        }
    }
    //========================================================================
}
