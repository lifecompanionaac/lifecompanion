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
package org.lifecompanion.config.view.pane.tabs.api;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.pane.tabs.selected.part.ComponentSelectionPopTrigger;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * Abstract ribbon that changes when selected component type change.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public abstract class AbstractSelectionChangeRibbonTab extends AbstractTabContent implements LCViewInitHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSelectionChangeRibbonTab.class);

    /**
     * Map that contains view that should be displayed on a subtype selection
     */
    private Map<Class<?>, AbstractRibbonTabContent> selectionTabContent;

    /**
     * Left part to trigger selection tree pop up
     */
    private ComponentSelectionPopTrigger selectionTrigger;

    /**
     * Label when no component is selected, or multi component is selected
     */
    private Label labelNoSelection, labelMultiSelection;

    /**
     * If {@link #selectionTrigger} is showed
     */
    private boolean selectionPaneEnable;

    /**
     * If the tab title should vary with the component
     */
    private boolean enableTitleComponentName;

    public AbstractSelectionChangeRibbonTab(final boolean selectionPaneEnableP, final boolean enableTitleComponentNameP) {
        this.selectionPaneEnable = selectionPaneEnableP;
        this.enableTitleComponentName = enableTitleComponentNameP;
        this.selectionTabContent = this.getSelectionComponent();
        this.initAll();
    }

    /**
     * @return the map that contains the tab content to show for each component type.<br>
     * Component type are typically all subclass of {@link DisplayableComponentI} but they can also be subclass of null {@link Void} to indicate that
     * the null selection also have a part.
     */
    protected abstract Map<Class<?>, AbstractRibbonTabContent> getSelectionComponent();

    /**
     * @param displayableComponent the component to test
     * @return true if the given component is valid, and should be displayed, if return false, will disable the tab and show the "no component selected tab"
     */
    protected abstract boolean isValidAsSingleComponent(DisplayableComponentI displayableComponent);

    /**
     * @return if the tab should be disabled when selection is null
     */
    protected abstract boolean disableOnNullSelection();

    /**
     * @return true if this tab should be disabled on multiselection
     */
    protected abstract boolean disableOnMultiSelection();

    /**
     * @return return the tab title that should be use when there is no current valid selection.<br>
     * The default implementation return the current title, subclass can override to return a specific title.
     */
    public String getNoSelectionTabTitle() {
        return this.tabTitle.get();
    }

    /**
     * @return return the tab title that should be use when there is a multi selection.<br>
     * The default implementation return the current title, subclass can override to return a specific title.
     */
    public String getMultiSelectionTabTitle() {
        return this.tabTitle.get();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //When there is no selected component
        this.labelNoSelection = new Label(Translation.getText("pane.selected.no.component.selected"));//TODO : allow subclass to change this text
        this.labelMultiSelection = new Label(Translation.getText("pane.selected.multi.component.selected"));
        //Selection
        if (this.selectionPaneEnable) {
            this.selectionTrigger = new ComponentSelectionPopTrigger();
            this.setLeft(this.selectionTrigger);
        }
        //Default : no selection
        this.updateSelection();
    }

    @Override
    public void initBinding() {
        InvalidationListener updateSelectionInvListener = inv -> updateSelection();
        SelectionController.INSTANCE.getSelectedKeys().addListener(updateSelectionInvListener);
        SelectionController.INSTANCE.selectedComponentBothProperty().addListener(updateSelectionInvListener);
    }

    private void updateSelection() {
        // Get select both and selected keys
        DisplayableComponentI selectedBoth = SelectionController.INSTANCE.selectedComponentBothProperty().get();
        ObservableList<GridPartKeyComponentI> selectedKeys = SelectionController.INSTANCE.getSelectedKeys();

        // Multi selection enabled
        Node nodeCenter = updateAndGetNodeFor(selectedBoth, selectedKeys);
        this.setCenter(nodeCenter);
    }

    private Node updateAndGetNodeFor(DisplayableComponentI selectedBoth, ObservableList<GridPartKeyComponentI> selectedKeys) {
        this.disableTab.set(false);
        // Multi selection possible
        boolean multiSelectedKeys = selectedKeys.size() > 1;
        if (this.selectionTabContent.containsKey(MultiSelection.class) && multiSelectedKeys) {
            return this.selectionTabContent.get(MultiSelection.class);
        }
        // Single selection or multiple selection disabled
        else {
            // Multi selection disabled
            if (multiSelectedKeys) {
                this.disableTab.set(this.disableOnMultiSelection());
                this.tabTitle.set(this.getMultiSelectionTabTitle());
                return this.labelMultiSelection;
            }
            // Single selection
            else if (selectedBoth != null && this.isValidAsSingleComponent(selectedBoth)) {
                return this.selectionTabContent.get(selectedBoth.getClass());
            }
            // No selection
            else {
                this.tabTitle.set(this.getNoSelectionTabTitle());
                this.disableTab.set(this.disableOnNullSelection());
                if (this.selectionTabContent.containsKey(Void.class)) {
                    return selectionTabContent.get(Void.class);
                } else {
                    return this.labelNoSelection;
                }
            }
        }
    }

    /**
     * Class to add a multiselection panel
     */
    public static class MultiSelection {
    }
    //========================================================================

}
