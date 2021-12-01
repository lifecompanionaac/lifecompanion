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
package org.lifecompanion.base.data.ui;

import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * A base implementation of view provider that initiate type in a given map.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class BaseViewProvider implements ViewProviderI {
    private final static Logger LOGGER = LoggerFactory.getLogger(BaseViewProvider.class);

    protected final Map<Class<? extends DisplayableComponentI>, Class<? extends ComponentViewI<?>>> types;
    protected final Map<DisplayableComponentI, ComponentViewI<?>> generated;

    public BaseViewProvider(final Map<Class<? extends DisplayableComponentI>, Class<? extends ComponentViewI<?>>> typesP) {
        this.types = typesP;
        this.generated = new HashMap<>();
        BaseViewProvider.LOGGER.info("View provider created with {} view types", this.types.size());
        Set<Class<? extends DisplayableComponentI>> keys = this.types.keySet();
        for (Class<? extends DisplayableComponentI> key : keys) {
            BaseViewProvider.LOGGER.debug("View types for {} : {}", key.getSimpleName(), this.types.get(key).getSimpleName());
        }
    }

    // Class part : "Retriever"
    //========================================================================

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public ComponentViewI<?> getViewFor(final DisplayableComponentI component) {
        if (this.generated.containsKey(component)) {
            return this.generated.get(component);
        } else {
            Class<? extends ComponentViewI<?>> componentViewClass = this.types.get(component.getClass());
            if (componentViewClass != null) {
                try {
                    ComponentViewI view = componentViewClass.getConstructor().newInstance();
                    view.initialize(component);
                    this.generated.put(component, view);
                    return view;
                } catch (Exception e) {
                    BaseViewProvider.LOGGER.error("Problem when instantiate a component view for the component type {}",
                            component.getClass().getSimpleName(), e);
                    return null;
                }
            } else {
                BaseViewProvider.LOGGER.error("There is no view class for the given component type {}, may that the view provider was not configured with the good view ?",
                        component.getClass().getSimpleName());
                return null;
            }
        }
    }

    @Override
    public void clearViewCacheForConfiguration(String configurationId) {
        BaseViewProvider.LOGGER.info("Will clear view cache for {} (configuration filter = {})", this.getClass().getSimpleName(), configurationId);
        int removedCount = 0;
        final ArrayList<Map.Entry<DisplayableComponentI, ComponentViewI<?>>> entriesToRemove = new ArrayList<>(this.generated.entrySet());
        for (Map.Entry<DisplayableComponentI, ComponentViewI<?>> entryToRemove : entriesToRemove) {
            if (configurationId == null || (entryToRemove.getKey().configurationParentProperty().get() != null && StringUtils.isEquals(configurationId, entryToRemove.getKey().configurationParentProperty().get().getID()))) {
                generated.remove(entryToRemove.getKey());
                removedCount++;
            }
        }
        BaseViewProvider.LOGGER.info("{} views cleaned for {} (configuration filter = {})", removedCount, this.getClass().getSimpleName(), configurationId);
    }

    @Override
    public void clearAllViewCaches() {
        clearViewCacheForConfiguration(null);
    }
    //========================================================================

}
