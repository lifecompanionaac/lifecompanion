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

import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;

/**
 * This interface is use to provide different component representation function of a implementation.<br>
 * Typically, a class that extends a Region can implements this interface and return its instance in {@link #getView()}.<br>
 * For the same {@link #initialize(DisplayableComponentI)} call, the Region returned by {@link #getView()} must be always the same, the view can change on a {@link #initialize(DisplayableComponentI)} call.
 *
 * @param <T> the component that will be displayed in this view.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface ComponentViewI<T extends DisplayableComponentI> {

    /**
     * Must initialize the view to represent the given component.<br>
     *
     * @param component the component that will be displayed.
     */
    void initialize(ViewProviderI viewProvider, boolean useCache, T component);

    void unbindComponentAndChildren();

    /**
     * @return the view to represent the component that was given to {@link #initialize(DisplayableComponentI)}<br>
     * The returned view musn't change until a new call to {@link #initialize(DisplayableComponentI)} is done.
     */
    Region getView();//This must be the only place where model rely on JavaFX API

    /**
     * Must be called by the component when its {@link DisplayableComponentI#showToFront()} method is called.<br>
     * This must show the component on the top when it's possible.
     */
    void showToFront();
}
