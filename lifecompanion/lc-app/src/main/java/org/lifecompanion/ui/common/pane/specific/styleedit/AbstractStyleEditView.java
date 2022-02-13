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
package org.lifecompanion.ui.common.pane.specific.styleedit;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.style.StyleI;
import org.lifecompanion.model.api.style.StylePropertyI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.controller.editaction.StyleActions.ChangeStylePropAction;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class AbstractStyleEditView<T extends StyleI<?>> extends BaseConfigurationViewBorderPane<T> implements LCViewInitHelper {
    protected GridPane fieldGrid;
    private Tooltip tooltipModificationIndicator;
    private final Map<Node, Function<T, StylePropertyI<?>>> modifiedIndicators;
    protected final boolean bindOnModel;

    public AbstractStyleEditView(boolean bindOnModel) {
        this.bindOnModel = bindOnModel;
        this.modifiedIndicators = new HashMap<>(5);
        this.initAll();
    }

    public ObjectProperty<T> modelProperty() {
        return this.model;
    }

    @Override
    public void initUI() {
        //Tooltip for modified
        this.tooltipModificationIndicator = FXControlUtils.createTooltip(Translation.getText("tooltip.different.style.property.text"));

        //Layout fields
        this.fieldGrid = new GridPane();
        this.fieldGrid.setVgap(3.0);
        this.fieldGrid.setHgap(5.0);
        this.setCenter(fieldGrid);
    }

    protected Node createModifiedIndicator(final Function<T, StylePropertyI<?>> propertyGetter, final Node fieldNode) {
        return this.createModifiedIndicator(propertyGetter, fieldNode, false);
    }

    protected Node createModifiedIndicator(final Function<T, StylePropertyI<?>> propertyGetter, final Node fieldNode, final boolean bindManaged) {
        return this.createModifiedIndicator(propertyGetter, fieldNode.disableProperty(), bindManaged);
    }

    protected Node createModifiedIndicator(final Function<T, StylePropertyI<?>> propertyGetter, final BooleanProperty disableProperty, final boolean bindManaged) {
        Text textModified = new Text("*");
        textModified.setFill(LCGraphicStyle.MAIN_DARK);
        textModified.getStyleClass().add("modified-text-indicator");
        if (bindManaged) {
            textModified.managedProperty().bind(textModified.visibleProperty());
        }
        textModified.setVisible(false);
        Tooltip.install(textModified, this.tooltipModificationIndicator);
        this.modifiedIndicators.put(textModified, propertyGetter);
        if (bindOnModel) {
            textModified.setOnMouseClicked(me -> {
                ConfigActionController.INSTANCE.executeAction(new ChangeStylePropAction(propertyGetter.apply(this.model.get()), null));
            });
        }
        return textModified;
    }

    @Override
    public void bind(T model) {
        if (bindOnModel) {
            Set<Node> indicators = this.modifiedIndicators.keySet();
            for (Node indicator : indicators) {
                indicator.visibleProperty().bind(this.modifiedIndicators.get(indicator).apply(model).isSelectedNotNull());
            }
        }
    }

    @Override
    public void unbind(T model) {
        if (bindOnModel) {
            Set<Node> indicators = this.modifiedIndicators.keySet();
            for (Node indicator : indicators) {
                indicator.visibleProperty().unbind();
            }
        }
    }

    protected <P> BaseEditActionI createChangePropAction(final StylePropertyI<P> model, final P value) {
        return new ChangeStylePropAction<>(model, value);
    }
}
