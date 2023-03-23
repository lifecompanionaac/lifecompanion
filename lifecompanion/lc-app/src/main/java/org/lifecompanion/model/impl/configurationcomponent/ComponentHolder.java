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

import java.util.Map;

import org.lifecompanion.model.api.configurationcomponent.ConfigurationChildComponentI;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

/**
 * A object that holds a component that can be added/removed from a configuration.<br>
 * This class is used by use action when they depend on other components (for example to change the current keyboard)
 *
 * @param <T> the holded component type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @deprecated you should now use {@link ComponentHolderById} - this component is kept just for backward compatibilities as plugin could use it
 */
@Deprecated
public class ComponentHolder<T extends DisplayableComponentI> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentHolder.class);
    private String removedId;
    private final StringProperty id;
    private final ObjectProperty<T> component;

    /**
     * Create a new component holder.<br>
     *
     * @param idP             the property that will holds the current component id, it should be settable (e.g. not binded)
     * @param configChildProp the configuration child that may change to the current component, where listener will be added.
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public ComponentHolder(final StringProperty idP, final ObjectProperty<? extends ConfigurationChildComponentI> configChildProp) {
        this.id = idP;
        this.component = new SimpleObjectProperty<>();
        //When component change, change the ID
        this.component.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.id.set(nv.getID());
            } else {
                this.id.set(null);
            }
        });
        //When the id change on loading, search in all component
        this.id.addListener((obs, ov, nv) -> {
            if (nv != null) {
                ConfigurationChildComponentI configurationChildComponentI = configChildProp.get();
                if (configurationChildComponentI != null) {
                    LCConfigurationI config = configurationChildComponentI.configurationParentProperty().get();
                    if (config != null) {
                        DisplayableComponentI comp = config.getAllComponent().get(nv);
                        this.component.set((T) comp);
                    }
                }
            } else {
                this.component.set(null);
            }
        });
        //Configuration binding
        ConfigurationComponentUtils.addComponentCallback(configChildProp, (allComponent) -> {
            //Check if the component is already in the list, and set it
            String idValue = this.id.get();
            if (idValue != null && allComponent.containsKey(idValue)) {
                this.component.set((T) allComponent.get(idValue));
            }
        }, (added) -> {
            String idValue = this.id.get();
            //If the added component is the wanted, or the previously removed component and there is no other component yet
            if (added != null && idValue != null && added.getID().equals(idValue)
                    || this.removedId != null && this.removedId.equals(added.getID()) && this.component.get() == null) {
                this.component.set((T) added);
            }
        }, (removed) -> {
            String idValue = this.id.get();
            if (removed != null && idValue != null && removed.getID().equals(idValue)) {
                this.removedId = idValue;
                this.component.set(null);
            }
        });
    }

    /**
     * The current hold value.<br>
     * This value can be changed, if the value is null, this will set the id to null, and if the value is !=null , this will set the value to the component id
     *
     * @return the property that holds the current value
     */
    @Deprecated
    public ObjectProperty<T> componentProperty() {
        return this.component;
    }

    /**
     * Execute the id change logic : if the id changed for this component, use the new id<br>
     * See {@link DuplicableComponentI#idsChanged(Map)}
     *
     * @param changes the id changes
     */
    @Deprecated
    public void idsChanged(final Map<String, String> changes) {
        if (this.id.get() != null) {
            String idAfterChange = changes.get(this.id.get());
            if (idAfterChange != null) {
                this.id.set(idAfterChange);
            } else {
                ComponentHolder.LOGGER.warn("This id before was {}, but a change happened and there is no new id", this.id.get());
            }
        }
    }
}
