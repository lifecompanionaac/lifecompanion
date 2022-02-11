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

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.*;

/**
 * A simple class to get a displayable name for a component by its class.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ComponentNameEnum {
    GRID_PART_GRID(GridComponentI.class, "component.type.grid.part"), GRID_PART_KEY(GridPartKeyComponentI.class, "component.type.key"), GRID_STACK(
            StackComponentI.class, "component.type.stack"), TEXT_EDITOR(WriterDisplayerI.class,
            "component.type.text.editor"), CONFIGURATION(LCConfigurationI.class, "component.type.configuration");
    private Class<? extends DisplayableComponentI> component;
    private String componentNameID;

    ComponentNameEnum(final Class<? extends DisplayableComponentI> componentP, final String componentNameIdP) {
        this.component = componentP;
        this.componentNameID = componentNameIdP;
    }

    public Class<? extends DisplayableComponentI> getComponent() {
        return this.component;
    }

    public String getComponentName() {
        return Translation.getText(this.componentNameID);
    }

    public static ComponentNameEnum getBySuperClass(final Class<? extends DisplayableComponentI> componentType) {
        ComponentNameEnum[] values = ComponentNameEnum.values();
        for (ComponentNameEnum current : values) {
            if (current.component.isAssignableFrom(componentType)) {
                return current;
            }
        }
        return null;
    }
}
