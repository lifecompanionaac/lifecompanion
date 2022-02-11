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
package org.lifecompanion.config.view.pane.compselector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.view.reusable.searchcombobox.SearchComboBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.Collection;

/**
 * A control to select a component.
 *
 * @author Mathieu THEBAUD
 */
public class ComponentSelectorControl<T extends DisplayableComponentI> extends VBox implements LCViewInitHelper {

    /**
     * Type to display/select
     */
    private final Class<T> selectionType;

    /**
     * Label for this control
     */
    private Label label;

    /**
     * Text in label (default for constructor)
     */
    private final String labelText;

    /**
     * Current map change listener (binded to model map)
     */
    private MapChangeListener<String, T> currentMapChangeListener;

    /**
     * Combobox to display/select components
     */
    private SearchComboBox<T> searchComboBox;

    /**
     * Create the component selector control
     *
     * @param selectionType the type we want to select.<br>
     *                      This type can be a super type, class will be done on inheritance
     * @param labelText     the text to set in the label, can be null
     */
    public ComponentSelectorControl(final Class<T> selectionType, final String labelText) {
        this.selectionType = selectionType;
        this.labelText = labelText;
        this.initAll();
    }

    public ComponentSelectorControl(final Class<T> selectionType) {
        this(selectionType, null);
    }

    /**
     * Property that hold the selected component, you can change to select a previous component
     *
     * @return the property that hold the current selected component, null means no selection
     */
    public ObjectProperty<T> selectedComponentProperty() {
        return this.searchComboBox.valueProperty();
    }

    @Override
    public void initUI() {
        //Create label
        if (labelText != null) {
            this.label = new Label(this.labelText);
        }
        // Search combobox
        searchComboBox = new SearchComboBox<>(
                lv -> new DisplayableComponentListCell<>(),
                searchText -> StringUtils.isBlank(searchText) ? this::isValidItem : c -> this.isValidItem(c) && LCUtils.getSimilarityScoreFor(searchText, c) > 0,
                comp -> comp != null ? comp.nameProperty().get() : Translation.getText("component.selector.control.no.selection"),
                searchText -> StringUtils.isBlank(searchText) ? null : (c1, c2) -> Double.compare(
                        LCUtils.getSimilarityScoreFor(searchText, c2),
                        LCUtils.getSimilarityScoreFor(searchText, c1))
        );
        //UIUtils.setFixedWidth(searchComboBox, 250.0);

        this.setSpacing(5.0);
        if (label != null) {
            this.getChildren().add(label);
        }
        this.getChildren().add(searchComboBox);
    }

    public void setTooltipText(String tooltipTextId) {
        Tooltip.install(searchComboBox, UIUtils.createTooltip(tooltipTextId));
    }

    private boolean isValidItem(final DisplayableComponentI item) {
        if (item != null) {
            return this.selectionType.isAssignableFrom(item.getClass());
        } else {
            return false;
        }
    }

    @Override
    public void initBinding() {
        ChangeListener<LCConfigurationI> configurationChangeListener = (obs, ov, nv) -> {
            if (ov != null && currentMapChangeListener != null) {
                ov.getAllComponent().removeListener((MapChangeListener<String, DisplayableComponentI>) this.currentMapChangeListener);
                this.currentMapChangeListener = null;
                this.searchComboBox.setItems(null);
            }
            if (nv != null) {
                ObservableMap<String, T> allComponentMap = (ObservableMap<String, T>) nv.getAllComponent();
                ObservableList<T> items = FXCollections.observableArrayList();
                items.addAll((Collection<T>) nv.getAllComponent().values());
                this.searchComboBox.setItems(items);
                allComponentMap.addListener(this.currentMapChangeListener = LCConfigBindingUtils.createBindMapValue(items));
            }
        };
        configurationChangeListener.changed(null, null, AppModeController.INSTANCE.getEditModeContext().configurationProperty().get());
        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener(configurationChangeListener);
    }

    public void clearSelection() {
        this.selectedComponentProperty().set(null);
    }
}
