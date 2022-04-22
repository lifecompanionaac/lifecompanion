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
package org.lifecompanion.ui.common.control.specific.componenttree;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.ListView;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.RootGraphicComponentI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.ui.common.pane.generic.BaseConfigurationViewBorderPane;
import org.lifecompanion.ui.common.pane.specific.cell.SelectionItemListCell;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static org.lifecompanion.util.model.ConfigurationComponentUtils.getSimilarityScoreFor;

/**
 * A view to display the selection tree result
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentSelectionSearchList extends BaseConfigurationViewBorderPane<LCConfigurationI> implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentSelectionSearchList.class);
    /**
     * List view that show result list
     */
    private ListView<DisplayableComponentI> componentListView;

    /**
     * Current map change listener (binded to model map)
     */
    private MapChangeListener<String, DisplayableComponentI> currentMapChangeListener;

    /**
     * List to filter results
     */
    private FilteredList<DisplayableComponentI> filteredList;

    /**
     * List to sort filtered results
     */
    private SortedList<DisplayableComponentI> sortedList;

    public ComponentSelectionSearchList() {
        this.initAll();
        this.setPrefSize(0.0, 0.0);
    }

    @Override
    public void initUI() {
        this.componentListView = new ListView<>();
        this.componentListView.setCellFactory((lv) -> new SelectionItemListCell());
        this.setCenter(this.componentListView);
    }

    @Override
    public void initListener() {
        this.componentListView.getSelectionModel().selectedItemProperty().addListener((observableP, oldValueP, newValueP) -> SelectionController.INSTANCE.selectDisplayableComponent(newValueP, true));
    }

    @Override
    public void initBinding() {
        this.model.bind(AppModeController.INSTANCE.getEditModeContext().configurationProperty());
    }

    // Class part : "Override"
    // ========================================================================
    @Override
    protected double computeMinWidth(final double arg0P) {
        return 0.0;
    }

    @Override
    protected double computeMinHeight(final double widthP) {
        return 0.0;
    }

    public void search(final String text) {
        if (StringUtils.isBlank(text)) {
            this.filteredList.setPredicate(null);
            this.sortedList.setComparator(null);
        } else {
            this.filteredList.setPredicate(
                    (c) -> !(c instanceof LCConfigurationI) && getSimilarityScoreFor(text, c) > 0);
            this.sortedList.setComparator((c1, c2) -> Double.compare(getSimilarityScoreFor(text, c2),
                    getSimilarityScoreFor(text, c1)));
        }
    }


    // ========================================================================

    // Class part : "Binding"
    // ========================================================================
    @Override
    public void bind(final LCConfigurationI component) {
        ObservableMap<String, DisplayableComponentI> allComponentMap = component.getAllComponent();
        ObservableList<DisplayableComponentI> listItems = FXCollections.observableArrayList();
        this.currentMapChangeListener = BindingUtils.createBindMapValue(listItems);
        listItems.addAll(allComponentMap.values());
        filteredList = new FilteredList<>(listItems);
        sortedList = new SortedList<>(filteredList);
        this.componentListView.setItems(sortedList);
        allComponentMap.addListener(this.currentMapChangeListener);
    }

    @Override
    public void unbind(final LCConfigurationI component) {
        component.getAllComponent().removeListener(this.currentMapChangeListener);
        this.currentMapChangeListener = null;
        // Issue #191 : fix memory leak in sorted list
        this.filteredList = null;
        this.sortedList = null;
        this.componentListView.setItems(null);
    }
    // ========================================================================
}
