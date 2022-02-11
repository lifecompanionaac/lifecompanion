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
package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent;

import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.controller.io.ReflectionHelper;
import org.lifecompanion.controller.plugin.PluginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum UseEventConfigurationViewProvider {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(UseEventConfigurationViewProvider.class);

    /**
     * Map that keep every type
     */
    private Map<Class<? extends UseEventGeneratorI>, UseEventGeneratorConfigurationViewI<?>> viewTypes;

    private UseEventConfigurationViewProvider() {
        this.viewTypes = new HashMap<>();
        this.initializeConfigurations();
        this.LOGGER.info("Singleton {} initialized", this.getClass().getSimpleName());
    }

    // Class part : "Init"
    //========================================================================
    @SuppressWarnings({"rawtypes"})
    private void initializeConfigurations() {
        List<Class<? extends UseEventGeneratorConfigurationViewI>> implementations = ReflectionHelper.findImplementationsInModules(UseEventGeneratorConfigurationViewI.class);
        for (Class<? extends UseEventGeneratorConfigurationViewI> element : implementations) {
            this.addConfigurationViewClass(element);
        }
        //Added by plugin manager
        PluginController.INSTANCE.getUseEventGeneratorConfigViews().registerListenerAndDrainCache(this::addConfigurationViewClass);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addConfigurationViewClass(final Class<? extends UseEventGeneratorConfigurationViewI> configViewSubType) {
        String className = configViewSubType.getName();
        try {
            this.LOGGER.info("Found a subtype of use action configuration view : {}", className);
            UseEventGeneratorConfigurationViewI configurationView = configViewSubType.getConstructor().newInstance();
            Class<? extends UseEventGeneratorI> actionType = configurationView.getConfiguredActionType();
            this.viewTypes.put(actionType, configurationView);
            this.LOGGER.info("Associate the configuration view {} to {}", className, actionType);
        } catch (Exception e) {
            this.LOGGER.warn("A found use action configuration ({}) couldn't be created", className, e);
        }
    }
    //========================================================================

    // Class part : "Create view"
    //========================================================================
    @SuppressWarnings("unchecked")
    public UseEventGeneratorConfigurationViewI<UseEventGeneratorI> getConfigurationViewFor(final UseEventGeneratorI useAction) {
        if (this.viewTypes.containsKey(useAction.getClass())) {
            try {
                //Create a new instance and init its view content
                UseEventGeneratorConfigurationViewI<UseEventGeneratorI> configView = (UseEventGeneratorConfigurationViewI<UseEventGeneratorI>) this.viewTypes
                        .get(useAction.getClass());
                UseEventGeneratorConfigurationViewI<UseEventGeneratorI> viewInstance = configView.getClass().getConstructor().newInstance();
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
