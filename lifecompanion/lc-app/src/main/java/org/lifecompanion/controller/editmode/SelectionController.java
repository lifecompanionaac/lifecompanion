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
package org.lifecompanion.controller.editmode;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.util.binding.BindingUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;

import java.util.*;

/**
 * Manage the selection of components in edit mode.<br>
 * For component, multiple keys can be selected, but only one grid or stack can be selected.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SelectionController {
    INSTANCE;

    /**
     * Currently selected grid or grid part (it can be grid part text editor or grid part stack, etc.) - it cannot be a key (real value)
     */
    private final ObjectProperty<GridPartComponentI> selectedGridPartComponent;

    /**
     * The selected root component (real value)
     */
    private final ObjectProperty<RootGraphicComponentI> selectedRootComponent;

    /**
     * List of all currently selected keys (real value)
     */
    private final ObservableList<GridPartKeyComponentI> selectedKeys;

    // Helpers : store values to make handling current selection easier (but all the stored values are from bindings)
    private final ListProperty<GridPartKeyComponentI> listPropertySelectedKeys;
    private final ObjectProperty<DisplayableComponentI> selectedDisplayableComponentHelper;
    private final ObjectProperty<GridPartComponentI> selectedGridPartOrKeyHelper;
    private final ObjectProperty<GridPartKeyComponentI> selectedKeyHelper;

    private final Map<Object, ChangeListener<Boolean>> removedListeners;

    private final LinkedList<GridPartComponentI> selectedPartHistory;

    SelectionController() {
        this.selectedKeys = FXCollections.observableArrayList();
        this.listPropertySelectedKeys = new SimpleListProperty<>(this.selectedKeys);
        this.selectedGridPartComponent = new SimpleObjectProperty<>();
        this.selectedRootComponent = new SimpleObjectProperty<>();

        this.selectedGridPartOrKeyHelper = new SimpleObjectProperty<>();
        this.selectedKeyHelper = new SimpleObjectProperty<>();
        this.selectedDisplayableComponentHelper = new SimpleObjectProperty<>();

        this.removedListeners = new HashMap<>();

        this.selectedPartHistory = new LinkedList<>();

        initBindings();
    }

    // INTERNAL
    //========================================================================
    private void initBindings() {
        // These are only helpers bindings
        this.selectedDisplayableComponentHelper.bind(Bindings.createObjectBinding(() -> {
            if (selectedRootComponent.get() != null) {
                return selectedRootComponent.get();
            } else if (selectedGridPartComponent.get() != null) {
                return selectedGridPartComponent.get();
            } else if (!selectedKeys.isEmpty()) {
                return selectedKeys.getLast();
            } else return null;
        }, selectedGridPartComponent, selectedKeys, selectedRootComponent));
        selectedKeyHelper.bind(Bindings.createObjectBinding(() -> selectedKeys.isEmpty() ? null : selectedKeys.get(selectedKeys.size() - 1), selectedKeys));
        selectedGridPartOrKeyHelper.bind(Bindings.createObjectBinding(() -> selectedKeyHelper.get() != null ? selectedKeyHelper.get() : selectedGridPartComponent.get(),
                selectedKeyHelper,
                selectedGridPartComponent));

        // These are the real values : when updated, should handle component state
        selectedKeys.addListener(BindingUtils.createListChangeListenerV2(this::handleSelectedComponent, this::handleUnselectedComponent));
        selectedRootComponent.addListener(createChangeListener());
        selectedGridPartComponent.addListener(createChangeListener());

        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> {
            this.selectedPartHistory.clear();
            this.clearSelection();
        });

        this.selectedGridPartComponent.addListener((obs, ov, nv) -> {
            if (nv != null && !nv.equals(selectedPartHistory.peekLast())) {
                selectedPartHistory.add(nv);
            }
        });
    }

    private <T extends SelectableComponentI & DisplayableComponentI> ChangeListener<T> createChangeListener() {
        return (obs, ov, nv) -> {
            if (ov != null) handleUnselectedComponent(ov);
            if (nv != null) {
                handleSelectedComponent(nv);
            }
        };
    }

    private <T extends SelectableComponentI & DisplayableComponentI> void handleUnselectedComponent(T comp) {
        comp.selectedProperty().set(false);
        comp.removedProperty().removeListener(removedListeners.remove(comp));
    }

    private <T extends SelectableComponentI & DisplayableComponentI> void handleSelectedComponent(T comp) {
        ChangeListener<Boolean> removedListener = (obs, ov, nv) -> {
            if (nv && comp.selectedProperty().get()) {
                if (comp instanceof GridPartKeyComponentI) {
                    selectedKeys.remove(comp);
                } else if (comp instanceof GridPartComponentI) {
                    selectedGridPartComponent.set(null);
                } else if (comp instanceof RootGraphicComponentI) {
                    selectedRootComponent.set(null);
                }
            }
        };
        removedListeners.put(comp, removedListener);
        comp.removedProperty().addListener(removedListener);
        comp.selectedProperty().set(true);
        comp.showToFront(AppMode.EDIT.getViewProvider(), true);
    }

    private boolean handleKeyIntervalSelection(GridPartKeyComponentI key) {
        if (!selectedKeys.isEmpty()) {
            GridComponentI grid = key.gridParentProperty().get();
            // Find first key in the same grid
            GridPartKeyComponentI otherKey = selectedKeys.stream().filter(k -> k != key && k.gridParentProperty().get() == grid).findFirst().orElse(null);
            if (otherKey != null) {
                Set<GridPartKeyComponentI> toSelect = grid.getGrid().getKeysBetween(otherKey, key);
                selectedKeys.setAll(toSelect);
                return true;
            }
        }
        return false;
    }
    //========================================================================

    // PUBLIC API
    //========================================================================
    public void selectPreviousComponent() {
        if (this.selectedPartHistory.size() >= 2) {
            this.selectedPartHistory.removeLast();// the last component is the current grid
            DisplayableComponentI lastElement = this.selectedPartHistory.removeLast(); // this is the last selected grid before current
            this.selectDisplayableComponent(lastElement, true);
        } else {
            LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
            if (configuration != null && configuration.firstSelectionPartProperty().get() != null) {
                this.selectDisplayableComponent(configuration.firstSelectionPartProperty().get(), true);
            }
        }
    }

    public void selectDisplayableComponent(DisplayableComponentI displayableComponent, boolean force) {
        if (displayableComponent != null) {
            if (displayableComponent instanceof GridPartKeyComponentI) {
                selectKeyComponent((GridPartKeyComponentI) displayableComponent, force, false, false);
            } else if (displayableComponent instanceof GridPartComponentI) {
                selectGridPartComponent((GridPartComponentI) displayableComponent, force);
            } else if (displayableComponent instanceof RootGraphicComponentI) {
                selectRootComponent((RootGraphicComponentI) displayableComponent, force);
            }
        } else {
            clearSelection();
        }
    }

    public void selectKeyComponent(GridPartKeyComponentI key, boolean force, boolean shortcutDown, boolean shiftDown) {
        selectedRootComponent.set(null);
        selectedGridPartComponent.set(null);
        if (force) {
            if (!selectedKeys.contains(key) || selectedKeys.size() != 1) {
                selectedKeys.setAll(key);
            }
        } else {
            if (shiftDown && handleKeyIntervalSelection(key)) {
                return;
            }
            if (shortcutDown) {
                if (selectedKeys.contains(key)) selectedKeys.remove(key);
                else selectedKeys.add(key);
            } else if (!key.selectedProperty().get()) {
                selectedKeys.setAll(key);
            } else {
                selectedKeys.clear();
            }
        }
    }

    public void selectKeyComponents(Collection<GridPartKeyComponentI> keys) {
        selectedRootComponent.set(null);
        selectedGridPartComponent.set(null);
        selectedKeys.setAll(keys);
    }

    public void clearSelection() {
        selectedRootComponent.set(null);
        selectedGridPartComponent.set(null);
        selectedKeys.clear();
    }

    public void selectNextKeyInCurrentGrid() {
        GridPartKeyComponentI selectedKey = this.selectedKeyHelper.get();
        if (selectedKey != null) {
            GridPartComponentI toSelect = ConfigurationComponentUtils.getNextComponentInGrid(selectedKey, true);
            if (toSelect != null)
                this.selectDisplayableComponent(toSelect, true);
        }
    }

    public void selectPreviousKeyInCurrentGrid() {
        GridPartKeyComponentI selectedKey = this.selectedKeyHelper.get();
        if (selectedKey != null) {
            GridPartComponentI toSelect = ConfigurationComponentUtils.getPreviousComponent(selectedKey, true);
            if (toSelect != null)
                this.selectDisplayableComponent(toSelect, true);
        }
    }

    private void selectRootComponent(RootGraphicComponentI rootComponent, boolean force) {
        if (!force && rootComponent.selectedProperty().get()) {
            selectedRootComponent.set(null);
        } else {
            selectedGridPartComponent.set(null);
            selectedKeys.clear();
            selectedRootComponent.set(rootComponent);
        }
    }

    private void selectGridPartComponent(GridPartComponentI gridPartComponent, boolean force) {
        if (!force && gridPartComponent.selectedProperty().get()) {
            selectedGridPartComponent.set(null);
        } else {
            selectedRootComponent.set(null);
            selectedKeys.clear();
            selectedGridPartComponent.set(gridPartComponent);
        }
    }
    //========================================================================


    // PROPERTIES
    //========================================================================
    public ReadOnlyObjectProperty<DisplayableComponentI> selectedDisplayableComponentHelperProperty() {
        return selectedDisplayableComponentHelper;
    }

    public ReadOnlyObjectProperty<RootGraphicComponentI> selectedRootComponentProperty() {
        return selectedRootComponent;
    }

    public ReadOnlyObjectProperty<GridPartComponentI> selectedGridPartComponentProperty() {
        return selectedGridPartComponent;
    }

    public ObservableList<GridPartKeyComponentI> getSelectedKeys() {
        return selectedKeys;
    }

    public ListProperty<GridPartKeyComponentI> getListPropertySelectedKeys() {
        return listPropertySelectedKeys;
    }

    public ReadOnlyObjectProperty<GridPartComponentI> selectedGridPartOrKeyHelperProperty() {
        return selectedGridPartOrKeyHelper;
    }

    public ReadOnlyObjectProperty<GridPartKeyComponentI> selectedKeyHelperProperty() {
        return selectedKeyHelper;
    }
    //========================================================================
}
