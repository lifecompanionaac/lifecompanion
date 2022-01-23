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

package org.lifecompanion.base.data.plugins;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.api.component.definition.DuplicableComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.plugins.PluginConfigPropertiesI;
import org.lifecompanion.base.data.common.CopyUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Map;

public abstract class AbstractPluginConfigProperties implements PluginConfigPropertiesI {
    private String id;
    protected final transient BooleanProperty removed;
    protected final ObjectProperty<LCConfigurationI> configurationParent;

    protected AbstractPluginConfigProperties(ObjectProperty<LCConfigurationI> parentConfiguration) {
        this.generateID();
        this.removed = new SimpleBooleanProperty(this, "removed", false);
        this.configurationParent = parentConfiguration;
    }

    @Override
    public final ObjectProperty<LCConfigurationI> configurationParentProperty() {
        return configurationParent;
    }

    @Override
    public final BooleanProperty removedProperty() {
        return removed;
    }

    @Override
    public void dispatchRemovedPropertyValue(boolean value) {
        this.removed.set(value);
    }

    @Override
    public DuplicableComponentI duplicate(boolean changeID) {
        return CopyUtils.createDeepCopyViaXMLSerialization(this, changeID);
    }

    @Override
    public void idsChanged(Map<String, String> changes) {
    }

    @Override
    public final String getID() {
        return id;
    }

    @Override
    public final String generateID() {
        this.id = StringUtils.getNewID();
        return this.id;
    }
}
