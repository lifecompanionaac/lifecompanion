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
package org.lifecompanion.model.api.categorizedelement;

import javafx.scene.layout.Region;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Represent the configuration view that is used for a {@link CategorizedElementI}.<br>
 * A configuration view is not mandatory if the {@link CategorizedElementI#isParameterizableElement()} returns false.<br>
 * The subclass should implements as they need the {@link LCViewInitHelper} interface, note that for optimizing the UI loading, the "init*" method of {@link LCViewInitHelper} will be called just before first show of the view.
 *
 * @param <T> the element type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface CategorizedConfigurationViewI<T extends CategorizedElementI<?>> extends LCViewInitHelper {
    /**
     * @return the JavaFX node use to configure this element
     */
    Region getConfigurationView();

    /**
     * @return the type of the configured element
     */
    Class<T> getConfiguredActionType();

    /**
     * When the edit of the element ends, should save any element changes
     *
     * @param element the element that was edited
     */
    void editEnds(T element);

    /**
     * When the edit of the element is cancelled by user, shouldn't save any change.<br>
     * Default implementation is no-op.
     *
     * @param element the element that was edited.
     */
    default void editCancelled(T element) {
    }
}
