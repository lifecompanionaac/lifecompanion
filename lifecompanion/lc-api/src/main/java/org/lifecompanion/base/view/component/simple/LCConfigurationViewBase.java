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

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.RootGraphicComponentI;
import org.lifecompanion.api.ui.ComponentViewI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * Represent the view base for the {@link LCConfigurationI} object.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class LCConfigurationViewBase extends Pane implements LCViewInitHelper, ComponentViewI<LCConfigurationI> {
    /**
     * Currently represented component
     */
    protected LCConfigurationI model;

    protected StringProperty configurationCssStyle;

    protected LCConfigurationChildContainerPane paneForRootComponents;

    public LCConfigurationViewBase() {
    }

    @Override
    public void initialize(final LCConfigurationI componentP) {
        this.model = componentP;
        this.configurationCssStyle = new SimpleStringProperty();
        this.initAll();
        ObservableList<RootGraphicComponentI> children = this.model.getChildren();
        for (RootGraphicComponentI comp : children) {
            paneForRootComponents.getChildren().add(comp.getDisplay().getView());
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
                () -> new StringBuilder(50).append("-fx-background-color:").append(LCUtils.toCssColor(this.model.backgroundColorProperty().get()))
                        .append(";").toString(), this.model.backgroundColorProperty()));
        this.styleProperty().bind(this.configurationCssStyle);
        //Bind max/min size on configuration, plus a add, to avoid blocking the user actions in bottom/right side
        double SIDE_ADD = UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty().get() + LCGraphicStyle.SELECTED_STROKE_GAP
                + UserBaseConfiguration.INSTANCE.selectionStrokeSizeProperty().get() + 5.0;
        this.minWidthProperty().bind(this.model.computedWidthProperty().add(SIDE_ADD));
        this.maxWidthProperty().bind(this.model.computedWidthProperty().add(SIDE_ADD));
        this.minHeightProperty().bind(this.model.computedHeightProperty().add(SIDE_ADD));
        this.maxHeightProperty().bind(this.model.computedHeightProperty().add(SIDE_ADD));
    }

    @Override
    public void initBinding() {
        //Binding on children, remove or add component when changes happens
        this.model.getChildren().addListener(LCUtils.createListChangeListener((added) -> {
            paneForRootComponents.getChildren().add(added.getDisplay().getView());
        }, (removed) -> {
            paneForRootComponents.getChildren().remove(removed.getDisplay().getView());
        }));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void showToFront() {
        this.toFront();
    }
}
