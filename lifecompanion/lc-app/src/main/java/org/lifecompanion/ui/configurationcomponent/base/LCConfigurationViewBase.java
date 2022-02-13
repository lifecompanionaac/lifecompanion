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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.api.ui.configurationcomponent.ViewProviderI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

/**
 * Represent the view base for the {@link LCConfigurationI} object.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationViewBase extends Pane implements LCViewInitHelper, ComponentViewI<LCConfigurationI> {
    protected ViewProviderI viewProvider;
    protected boolean useCache;
    protected LCConfigurationI model;

    protected StringProperty configurationCssStyle;

    protected LCConfigurationChildContainerPane paneForRootComponents;

    private ListChangeListener<RootGraphicComponentI> childrenChangeListener;

    public LCConfigurationViewBase() {
    }

    @Override
    public void initialize(ViewProviderI viewProvider, boolean useCache, LCConfigurationI componentP) {
        this.viewProvider = viewProvider;
        this.useCache = useCache;
        this.model = componentP;
        this.configurationCssStyle = new SimpleStringProperty();
        this.initAll();
        ObservableList<RootGraphicComponentI> children = this.model.getChildren();
        for (RootGraphicComponentI comp : children) {
            paneForRootComponents.getChildren().add(comp.getDisplay(viewProvider, useCache).getView());
        }
    }

    @Override
    public Region getView() {
        return this;
    }

    @Override
    public void initUI() {
        paneForRootComponents = new LCConfigurationChildContainerPane(this, model);
        this.configurationCssStyle.bind(Bindings.createStringBinding(
                () -> new StringBuilder(50).append("-fx-background-color:").append(ColorUtils.toCssColor(this.model.backgroundColorProperty().get()))
                        .append(";").toString(), this.model.backgroundColorProperty()));
        this.styleProperty().bind(this.configurationCssStyle);
        //Bind max/min size on configuration, plus a add, to avoid blocking the user actions in bottom/right side
        double SIDE_ADD = UserConfigurationController.INSTANCE.selectionStrokeSizeProperty().get() + LCGraphicStyle.SELECTED_STROKE_GAP
                + UserConfigurationController.INSTANCE.selectionStrokeSizeProperty().get() + 5.0;
        this.minWidthProperty().bind(this.model.computedWidthProperty().add(SIDE_ADD));
        this.maxWidthProperty().bind(this.model.computedWidthProperty().add(SIDE_ADD));
        this.minHeightProperty().bind(this.model.computedHeightProperty().add(SIDE_ADD));
        this.maxHeightProperty().bind(this.model.computedHeightProperty().add(SIDE_ADD));
    }

    @Override
    public void initBinding() {
        //Binding on children, remove or add component when changes happens
        this.model.getChildren().addListener(childrenChangeListener = BindingUtils.createListChangeListener((added) -> {
            paneForRootComponents.getChildren().add(added.getDisplay(viewProvider, useCache).getView());
        }, (removed) -> {
            paneForRootComponents.getChildren().remove(removed.getDisplay(viewProvider, useCache).getView());
        }));
    }

    @Override
    public void unbindComponentAndChildren() {
        this.model.getChildren().removeListener(childrenChangeListener);
        this.configurationCssStyle.unbind();
        this.styleProperty().unbind();
        this.minWidthProperty().unbind();
        this.maxWidthProperty().unbind();
        this.minHeightProperty().unbind();
        this.maxHeightProperty().unbind();
        ConfigurationComponentUtils.exploreComponentViewChildrenToUnbind(this);
        this.model = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }
}
