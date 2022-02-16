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
package org.lifecompanion.model.api.ui.configurationcomponent;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.controller.lifecycle.AppMode;

public interface ViewProviderI {
    /**
     * Create a new view for the given component type
     *
     * @param component the component class to display.
     * @return a new view instance for the given component, or null if the given type is unknown.
     */
    ComponentViewI<?> createComponentViewFor(DisplayableComponentI component, boolean useCache);

    ViewProviderType getType();

    static ComponentViewI<?> getOrCreateViewComponentFor(DisplayableComponentI comp, AppMode mode) {
        return comp.getDisplay(mode.getViewProvider(), true);
    }
}
