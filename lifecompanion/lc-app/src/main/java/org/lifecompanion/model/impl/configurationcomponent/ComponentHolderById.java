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

package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.ConfigurationChildComponentI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * An object that holds a component that can be added/removed from a configuration.<br>
 * This class is used by use action when they depend on other components (for example to change the current keyboard)
 *
 * @param <T> the held component type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentHolderById<T extends DisplayableComponentI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentHolderById.class);
    private final StringProperty componentId;
    private final ObjectProperty<T> component;
    private final ObjectProperty<? extends ConfigurationChildComponentI> configChildProperty;

    /**
     * Create a new component holder.<br>
     *
     * @param idP             the property that will hold the current component id, it should be settable (e.g. not bound)
     * @param configChildProp the configuration child that may change to the current component, where listener will be added.
     */
    public ComponentHolderById(final StringProperty idP, final ObjectProperty<? extends ConfigurationChildComponentI> configChildProp) {
        this.componentId = idP;
        this.component = new SimpleObjectProperty<>();
        this.configChildProperty = configChildProp;

        InvalidationListener updateComponentInvalidationListener = i -> this.updateComponent();
        this.componentId.addListener(updateComponentInvalidationListener);

        ChangeListener<LCConfigurationI> configChangeListener = (obs, ov, nv) -> {
            if (ov != null) {
                ov.getAllComponent().removeListener(updateComponentInvalidationListener);
            }
            if (nv != null) {
                nv.getAllComponent().addListener(updateComponentInvalidationListener);
            }
            this.updateComponent();
        };
        ChangeListener<ConfigurationChildComponentI> configChildChangeListener = (obs, ov, nv) -> {
            if (ov != null) {
                ov.configurationParentProperty().removeListener(configChangeListener);
            }           if (nv != null) {
                LCConfigurationI configurationParentValue = nv.configurationParentProperty().get();
                if (configurationParentValue != null) {
                    configChangeListener.changed(nv.configurationParentProperty(), null, configurationParentValue);
                }
                nv.configurationParentProperty().addListener(configChangeListener);
            }
            updateComponent();
        };
        ConfigurationChildComponentI configurationChildComponent = configChildProperty.get();
        if (configurationChildComponent != null) {
            configChildChangeListener.changed(configChildProp, null, configurationChildComponent);
        }
        configChildProperty.addListener(configChildChangeListener);
    }


    @SuppressWarnings("unchecked")
    private void updateComponent() {
        if (StringUtils.isNotBlank(componentId.get())) {
            ConfigurationChildComponentI configurationChildComponentI = this.configChildProperty.get();
            if (configurationChildComponentI != null) {
                LCConfigurationI configuration = configurationChildComponentI.configurationParentProperty().get();
                if (configuration != null) {
                    DisplayableComponentI comp = configuration.getAllComponent().get(componentId.get());
                    this.component.set((T) comp);
                    return;
                }
            }
        }
        this.component.set(null);
    }

    /**
     * The current hold value.<br>
     * This value can be changed, if the value is null, this will set the id to null, and if the value is !=null , this will set the value to the component id
     *
     * @return the property that holds the current value
     */
    public ReadOnlyObjectProperty<T> componentProperty() {
        return this.component;
    }

    public StringProperty componentIdProperty() {
        return componentId;
    }

    /**
     * Execute the id change logic : if the id changed for this component, use the new id<br>
     * See {@link DuplicableComponentI#idsChanged(Map)}
     *
     * @param changes the id changes
     */
    public void idsChanged(final Map<String, String> changes) {
        if (this.componentId.get() != null) {
            String idAfterChange = changes.get(this.componentId.get());
            if (idAfterChange != null) {
                this.componentId.set(idAfterChange);
            } else {
                ComponentHolderById.LOGGER.warn("This id before was {}, but a change happened and there is no new id", this.componentId.get());
            }
        }
    }
}
