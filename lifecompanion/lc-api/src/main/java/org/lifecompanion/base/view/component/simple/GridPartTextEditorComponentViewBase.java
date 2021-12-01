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
package org.lifecompanion.base.view.component.simple;

import javafx.beans.property.DoubleProperty;
import org.lifecompanion.base.data.component.simple.GridPartTextEditorComponent;
import org.lifecompanion.base.view.component.text.TextDisplayerBaseImplView;

/**
 * Display for text editor in grid.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartTextEditorComponentViewBase extends TextDisplayerBaseImplView<GridPartTextEditorComponent> {

    public GridPartTextEditorComponentViewBase() {
    }

    @Override
    public void initBinding() {
        super.initBinding();
        //Position and size binding
        this.prefWidthProperty().bind(this.model.layoutWidthProperty());
        this.prefHeightProperty().bind(this.model.layoutHeightProperty());
        this.layoutXProperty().bind(this.model.layoutXProperty());
        this.layoutYProperty().bind(this.model.layoutYProperty());
    }

    @Override
    public void initialize(final GridPartTextEditorComponent componentP) {
        this.model = componentP;
        this.initAll();
    }

    @Override
    protected DoubleProperty modelWidthProperty() {
        return this.model.layoutWidthProperty();
    }

    @Override
    protected DoubleProperty modelHeightProperty() {
        return this.model.layoutHeightProperty();
    }
}
