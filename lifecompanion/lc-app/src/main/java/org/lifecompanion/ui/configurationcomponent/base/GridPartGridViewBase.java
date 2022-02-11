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
package org.lifecompanion.ui.configurationcomponent.base;

import javafx.collections.ListChangeListener;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.Unbindable;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.style.ShapeStyleBinder;
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
