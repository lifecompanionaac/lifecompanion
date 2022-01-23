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

package org.lifecompanion.base.view.reusable.impl;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.base.view.reusable.BaseConfigurationViewI;

/**
 * Base implementation for {@link BaseConfigurationViewI} that use a {@link BorderPane} as view type.
 *
 * @param <T> the type of component that this configuration view configures.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class BaseConfigurationViewBorderPane<T> extends BorderPane implements BaseConfigurationViewI<T> {
    /**
     * Model use in this configuration view
     */
    protected ObjectProperty<T> model;

    public BaseConfigurationViewBorderPane() {
        this.model = new SimpleObjectProperty<>(this, "model");
        this.model.addListener((obs, ov, nv) -> {
            if (ov != null) {
                this.unbind(ov);
            }
            if (nv != null) {
                this.bind(nv);
            } else {
                this.clearFieldsAfterUnbind();
            }
        });
    }

    protected void clearFieldsAfterUnbind() {
    }
}
