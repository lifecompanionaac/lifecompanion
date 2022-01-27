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
package org.lifecompanion.api.ui;

import org.lifecompanion.api.component.definition.DisplayableComponentI;
import org.lifecompanion.base.data.control.refacto.AppModeV2;

/**
 * Provider to provide a view for each component that is displayable.<br>
 * A view provider instantiate a view when asked for and internally keep the good view for each component type.<br>
 * The view provider must also keep the instantiated component and retrieve them if the same component is asked twice.<br>
 * The view provider is use to allows different view implementation depending on the current mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ViewProviderI {
    /**
     * Create a new view for the given component type.<br>
     * If a view was already asked for this component, must return the previous view.
     *
     * @param component the component class to display.
     * @return a new view instance for the given component, or null if the given type is unknown.
     */
    ComponentViewI<?> getViewFor(DisplayableComponentI component, boolean useCache);

    ViewProviderType getType();

    static ComponentViewI<?> getComponentView(DisplayableComponentI comp, AppModeV2 mode) {
        return comp.getDisplay(mode.getViewProvider(), true);
    }
}
