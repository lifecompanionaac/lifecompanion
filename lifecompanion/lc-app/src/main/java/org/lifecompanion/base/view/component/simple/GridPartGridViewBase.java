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

import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.api.component.definition.GridPartComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.api.ui.ViewProviderI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.common.Unbindable;
import org.lifecompanion.base.data.component.simple.GridPartGridComponent;
import org.lifecompanion.base.data.style.impl.ShapeStyleBinder;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.ArrayList;

/**
 * Base view for a grid part grid component.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class GridPartGridViewBase extends Pane implements ComponentViewI<GridPartGridComponent>, LCViewInitHelper {
    protected ViewProviderI viewProvider;
    protected boolean useCache;
    protected GridPartGridComponent model;

    private ListChangeListener<GridPartComponentI> gridPartChangeListener;
    private Unbindable shapeStyleBinding;

    public GridPartGridViewBase() {
    }

    @Override
    public void initUI() {
    }

    @Override
    public void initBinding() {
        //Position and size
        this.layoutXProperty().bind(this.model.layoutXProperty());
        this.layoutYProperty().bind(this.model.layoutYProperty());
        this.prefWidthProperty().bind(this.model.layoutWidthProperty());
        this.prefHeightProperty().bind(this.model.layoutHeightProperty());
        //When grid content change (and init with initial content)
        for (GridPartComponentI comp : new ArrayList<>(this.model.getGrid().getGridContent())) {
            GridPartGridViewBase.this.getChildren().add(comp.getDisplay(viewProvider, useCache).getView());
        }
        this.model.getGrid().getGridContent().addListener(gridPartChangeListener = LCUtils.createListChangeListener((added) -> {
            GridPartGridViewBase.this.getChildren().add(added.getDisplay(viewProvider, useCache).getView());
        }, (removed) -> {
            GridPartGridViewBase.this.getChildren().remove(removed.getDisplay(viewProvider, useCache).getView());
        }));
        //Bind style
        shapeStyleBinding = ShapeStyleBinder.bindNode(this, this.model.getGridShapeStyle());
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, GridPartGridComponent componentP) {
        this.viewProvider = viewProvider;
        this.useCache = useCache;
        this.model = componentP;
        this.initAll();
    }

    @Override
    public void unbindComponentAndChildren() {
        this.layoutXProperty().unbind();
        this.layoutYProperty().unbind();
        this.prefWidthProperty().unbind();
        this.prefHeightProperty().unbind();
        this.model.getGrid().getGridContent().removeListener(gridPartChangeListener);
        shapeStyleBinding.unbind();
        this.model = null;
        LCUtils.exploreComponentViewChildrenToUnbind(this);
    }

    @Override
    public Region getView() {
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }
}
