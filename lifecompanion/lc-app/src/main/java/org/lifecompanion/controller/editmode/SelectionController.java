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

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.impl.configurationcomponent.RootGraphicComponentBaseImpl;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.*;

/**
 * Class that manage the selection of component.<br>
 * For component, multiple keys can be selected, but only one Grid or GridStack can be selected.<br>
 * Every base selection is done on key, and the selection is given to parent when it's possible.<br>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum SelectionController {
    INSTANCE;

    /**
     * Map that contains every selection for every component level
     */
    //    private final Map<Integer, ObservableList<GridPartComponentI>> selection;


    /**
     * Listener on selected component selection : to remove the component when it's selected
     */
    //    private ChangeListener<DisplayableComponentI> selectedComponentListener;

    /**
     * All stored listener, to remove after a component unselection
     */
    //    private final Map<ConfigurationChildComponentI, ChangeListener<? super Boolean>> removeListeners;

    /**
     * The currently selected component (only for
     */
    //    private final ObjectProperty<GridPartComponentI> selectedComponent;

    // V2 HERE
    /**
     * Property that keep the selected component, the component can a root component or a grid part component
     */
    private final ObjectProperty<DisplayableComponentI> selectedComponentBoth;

    /**
     * The selected root object
     */
    private final ObjectProperty<RootGraphicComponentI> selectedRoot;

    /**
     * Currently selected grid or grid part (it can be grid part text editor or grid part stack, etc.)
     */
    private final ObjectProperty<GridPartComponentI> selectedGridPart;

    /**
     * List of all currently selected keys
     */
    private final ObservableList<GridPartKeyComponentI> selectedKeys;

    /**
     * List property for selected keys
     */
    private final ListProperty<GridPartKeyComponentI> listPropertySelectedKeys;

    // Helper property
    private final ObjectProperty<GridPartComponentI> selectedGridPartOrKey;
    private final ObjectProperty<GridPartKeyComponentI> selectedKey;
    // TODO  : helper for GridComponentI

    SelectionController() {
        //        this.selection = new HashMap<>();
        this.selectedKeys = FXCollections.observableArrayList();
        this.listPropertySelectedKeys = new SimpleListProperty<>(this.selectedKeys);
        selectedGridPart = new SimpleObjectProperty<>();
        this.selectedRoot = new SimpleObjectProperty<>();

        selectedGridPartOrKey = new SimpleObjectProperty<>();
        selectedKey = new SimpleObjectProperty<>();


        //        this.selectedComponent = new SimpleObjectProperty<>(this, "selectedComponent");//FIXME : should disappears

        this.selectedComponentBoth = new SimpleObjectProperty<>(this, "selectedComponentBoth");
        //        this.removeListeners = new HashMap<>();
        //this.initListener();
        initBinding();
    }

    // V2
    //========================================================================

    /*
     Things to think about
     - if a component is already selected on select, unselect it
     - binding on each property (root, grid part, keylist) should update the selection property of component
     - a selected component should be unselected on delete/remove
     - provide a method to detect the correct method to call (e.g. we should not call selectpart if we want to select keys...)
     */

    private void initBinding() {
        this.selectedComponentBoth.bind(Bindings.createObjectBinding(() -> {
            System.out.println("Selected component both changed !");
            if (selectedRoot.get() != null) {
                return selectedRoot.get();
            } else if (selectedGridPart.get() != null) {
                return selectedGridPart.get();
            } else if (!selectedKeys.isEmpty()) {
                return selectedKeys.get(selectedKeys.size() - 1);
            } else return null;
        }, selectedGridPart, selectedKeys, selectedRoot));

        selectedKey.bind(Bindings.createObjectBinding(() -> selectedKeys.isEmpty() ? null : selectedKeys.get(selectedKeys.size() - 1), selectedKeys));
        selectedGridPartOrKey.bind(Bindings.createObjectBinding(() -> selectedKey.get() != null ? selectedKey.get() : selectedGridPart.get(), selectedKey, selectedGridPart));

        selectedKeys.addListener(BindingUtils.createListChangeListenerV2(added -> {
            added.selectedProperty().set(true);
            added.showToFront(AppMode.EDIT.getViewProvider(), true);
        }, removed -> removed.selectedProperty().set(false)));

        // FIXME : common listener ?
        selectedRoot.addListener((obs, ov, nv) -> {
            if (ov != null) ov.selectedProperty().set(false);
            if (nv != null) {
                nv.selectedProperty().set(true);
                nv.showToFront(AppMode.EDIT.getViewProvider(), true);
            }
        });
        selectedGridPart.addListener((obs, ov, nv) -> {
            if (ov != null) ov.selectedProperty().set(false);
            if (nv != null) {
                nv.selectedProperty().set(true);
                nv.showToFront(AppMode.EDIT.getViewProvider(), true);
            }
        });
    }

    public void selectKey(GridPartKeyComponentI key, boolean force, boolean shortcutDown, boolean shiftDown) {
        selectedRoot.set(null);
        selectedGridPart.set(null);
        if (force) {
            selectedKeys.setAll(key);
        } else if (shortcutDown) {
            if (selectedKeys.contains(key)) selectedKeys.remove(key);
            else selectedKeys.add(key);
        } else if (!key.selectedProperty().get()) {
            selectedKeys.setAll(key);
        } else {
            selectedKeys.clear();
        }
    }

    public void selectKeys(GridPartKeyComponentI... keys) {
        selectedRoot.set(null);
        selectedGridPart.set(null);
        selectedKeys.setAll(keys);
    }

    public void selectRootComponent(RootGraphicComponentI rootComponent, boolean force) {
        if (!force && rootComponent.selectedProperty().get()) {
            selectedRoot.set(null);
        } else {
            selectedGridPart.set(null);
            selectedKeys.clear();
            selectedRoot.set(rootComponent);
        }
    }

    public void selectGridPart(GridPartComponentI gridPartComponent, boolean force) {
        if (!force && gridPartComponent.selectedProperty().get()) {
            selectedGridPart.set(null);
        } else {
            selectedRoot.set(null);
            selectedKeys.clear();
            selectedGridPart.set(gridPartComponent);
        }
    }


    public ReadOnlyObjectProperty<DisplayableComponentI> selectedComponentBothProperty() {
        return selectedComponentBoth;
    }


    public ReadOnlyObjectProperty<RootGraphicComponentI> selectedRootProperty() {
        return selectedRoot;
    }

    public ReadOnlyObjectProperty<GridPartComponentI> selectedGridPartProperty() {
        return selectedGridPart;
    }

    public ObservableList<GridPartKeyComponentI> getSelectedKeys() {
        return selectedKeys;
    }

    public ListProperty<GridPartKeyComponentI> getListPropertySelectedKeys() {
        return listPropertySelectedKeys;
    }

    public ReadOnlyObjectProperty<GridPartComponentI> selectedGridPartOrKeyProperty() {
        return selectedGridPartOrKey;
    }

    public ReadOnlyObjectProperty<GridPartKeyComponentI> selectedKeyProperty() {
        return selectedKey;
    }

    public void clearSelection() {
        selectedRoot.set(null);
        selectedGridPart.set(null);
        selectedKeys.clear();
    }

    public void selectNextGridPartInCurrentGrid() {
        // FIXME SELECTION
    }

    public void selectPreviousGridPartInCurrentGrid() {
        // FIXME SELECTION
    }

    //========================================================================


    //
    //    // Class part : "Listener"
    //    //========================================================================
    //    private void initListener() {
    //        //When selected component change, add a listener on configuration change listener
    //        this.selectedComponentListener = (obs, ov, nv) -> {
    //            if (ov != null) {
    //                ov.removedProperty().removeListener(this.removeListeners.remove(ov));
    //            }
    //            if (nv != null) {
    //                //Listen for remove
    //                ChangeListener<? super Boolean> removedChangeListener = (bobs, bov, bnv) -> {
    //                    if (bnv) {
    //                        if (nv instanceof GridPartComponentI && nv == this.selectedComponent.get()) {
    //                            this.setSelectedPart(null);
    //                        }
    //                        if (nv instanceof RootGraphicComponentI && nv == this.selectedRoot.get()) {
    //                            this.setSelectedRoot(null);
    //                        }
    //                        if (nv == this.selectedComponentBoth.get()) {
    //                            this.selectedComponentBoth.set(null);
    //                        }
    //                    }
    //                };
    //                this.removeListeners.put(nv, removedChangeListener);
    //                nv.removedProperty().addListener(removedChangeListener);
    //            }
    //            //List for both selection, if the previous selected component is the current selection, set it
    //            if (this.selectedComponentBoth.get() == ov) {
    //                if (nv == null || nv.removedProperty().get()) {
    //                    this.selectedComponentBoth.set(null);
    //                } else {
    //                    this.selectedComponentBoth.set(nv);
    //                }
    //            }
    //        };
    //        this.selectedComponent.addListener(this.selectedComponentListener);
    //        this.selectedRoot.addListener(this.selectedComponentListener);
    //
    //        //Clear selection on configuration change
    //        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> this.configurationChanged());
    //    }
    //    //========================================================================
    //
    //    private void configurationChanged() {
    //        this.clearAllGridPartSelection();
    //        this.clearSelection();
    //    }
    //
    //    // Class part : "Getters"
    //    //========================================================================
    //    public ReadOnlyObjectProperty<RootGraphicComponentI> selectedRootProperty() {
    //        return this.selectedRoot;
    //    }
    //
    //    public ReadOnlyObjectProperty<GridPartComponentI> selectedComponentProperty() {
    //        return this.selectedComponent;
    //    }
    //
    //    public ReadOnlyObjectProperty<DisplayableComponentI> selectedComponentBothProperty() {
    //        return this.selectedComponentBoth;
    //    }
    //
    //    public ObservableList<GridPartKeyComponentI> getSelectedKeys() {
    //        return this.selectedKeys;
    //    }
    //
    //    public ListProperty<GridPartKeyComponentI> getListPropertySelectedKeys() {
    //        return this.listPropertySelectedKeys;
    //    }
    //    //========================================================================
    //
    //    // Class part : "Public selection method"
    //    //========================================================================
    //
    //    /**
    //     * Set the current root component selected
    //     *
    //     * @param rootP the currently selected root component
    //     */
    //    public void setSelectedRoot(final RootGraphicComponentI rootP) {
    //        RootGraphicComponentI previous = this.selectedRoot.get();
    //        if (previous != rootP) {
    //            this.clearAllGridPartSelection();
    //            //Previous
    //            if (previous != null) {
    //                previous.showSelectedProperty().set(false);
    //                previous.selectedProperty().set(false);
    //            }
    //            //New
    //            this.selectedRoot.set(rootP);
    //            if (rootP != null) {
    //                rootP.selectedProperty().set(true);
    //                rootP.showSelectedProperty().set(true);
    //                rootP.showToFront(AppMode.EDIT.getViewProvider(), true);
    //            }
    //        }
    //    }
    //
    //    /**
    //     * To select a single component, this must be use only in a tree or a list selection, because it will not check parent etc...<br>
    //     * To make a mouse selection, use #selected(GridPartComponentI, boolean) instead
    //     *
    //     * @param part the part to select
    //     */
    //    public void setSelectedPart(final GridPartComponentI part) {
    //        this.setSelectedPart(part, true);
    //    }
    //
    //    private void setSelectedPart(final GridPartComponentI part, final boolean clearSelection) {
    //        //If this is not the same as the current component, clear the selection
    //        if (this.selectedComponent.get() != part && clearSelection) {
    //            //Remove previous selection
    //            this.setSelectedRoot(null);
    //            this.clearSelectionFrom(0);
    //        }
    //        //Select or remove selection
    //        if (this.selectedComponent.get() != part && part != null) {
    //            //Select parents
    //            GridComponentI parent = part.gridParentProperty().get();
    //            //FIX : better selection of parent, if not, the parent could never be unselected and the selection was stick to this level
    //            while (parent != null && !parent.selectedProperty().get() && parent != part) {
    //                this.addSelection(parent);
    //            }
    //            //Select
    //            this.addSelection(part);
    //            part.showSelectedProperty().set(true);
    //        } else if (part == null) {
    //            this.selectedComponent.set(null);
    //        }
    //    }
    //
    //    /**
    //     * Inform the selection controller that a component is selected
    //     *
    //     * @param part        the component selected
    //     * @param controlDown if control is down
    //     * @param shiftDown   if shift is down
    //     */
    //    public void selected(final GridPartComponentI part, final boolean controlDown, final boolean shiftDown) {
    //        this.setSelectedRoot(null);
    //        //A selected component must have a parent
    //        if (part.isParentExist()) {
    //            //If the component is already selected
    //            if (part.selectedProperty().get()) {
    //                //Remove only this part
    //                if (controlDown) {
    //                    this.removeSelection(part);
    //                }
    //                //Clear selection
    //                else {
    //                    this.clearAllGridPartSelection();
    //                }
    //            }
    //            //If the component is not selected, try to get its closest unselected parent
    //            else {
    //                GridPartComponentI parent = this.getFirstUnselectedParent(part);
    //                //Clear selection at its level, when its not a add
    //                if (!controlDown) {
    //                    this.clearSelectionFrom(parent.getLevel());
    //                }
    //                //Select it : if shift is not handled
    //                if (!shiftDown || !handleShiftDownOnPart(part)) {
    //                    this.addSelection(parent);
    //                    parent.showSelectedProperty().set(true);
    //                }
    //            }
    //        }
    //    }
    //
    //    /**
    //     * Related to issue #161.</br>
    //     * Handle the shift down on selection.</br>
    //     * If the currently selected component is a key, and the new selected component is a key in the same grid, this will try to select all the part in the grid between the two keys.</br>
    //     * This should allow the user to quickly select a whole grid.
    //     *
    //     * @param part the new selected part
    //     * @return if shift was handled, and the classic behavior can be skipped.
    //     */
    //    private boolean handleShiftDownOnPart(final GridPartComponentI part) {
    //        final GridPartComponentI alreadySelected = selectedComponent.get();
    //        if (alreadySelected != null && alreadySelected instanceof GridPartKeyComponentI) {
    //            GridPartKeyComponentI alreadySelectedKey = (GridPartKeyComponentI) alreadySelected;
    //            if (alreadySelectedKey.gridParentProperty().get() == part.gridParentProperty().get()) {
    //                final Set<GridPartKeyComponentI> keys = alreadySelectedKey.gridParentProperty().get().getGrid().getKeysBetween(alreadySelected, part);
    //                this.setSelectedKeys(keys);
    //                return true;
    //            }
    //        }
    //        return false;
    //    }
    //
    //    /**
    //     * To call when we want to unselect a component
    //     *
    //     * @param part the component to unselect
    //     */
    //    public void unselected(final GridPartComponentI part) {
    //        this.removeSelection(part);
    //    }
    //
    //    /**
    //     * Set the selected keys
    //     *
    //     * @param parts keys to select
    //     */
    //    public void setSelectedKeys(final Collection<? extends GridPartComponentI> parts) {
    //        this.clearSelection();
    //        //Select each key
    //        for (GridPartComponentI part : parts) {
    //            this.setSelectedPart(part, false);
    //        }
    //    }
    //
    //    /**
    //     * Clear all the current selection of grid component.
    //     */
    //    public void clearAllGridPartSelection() {
    //        this.clearSelectionFrom(0);
    //        this.selectedComponent.set(null);
    //    }
    //
    //    /**
    //     * Useful method to unselect the given component.<br>
    //     * This method will call the good method {@link #setSelectedPart(GridPartComponentI)} or {@link #setSelectedRoot(RootGraphicComponentI)} with the given component type
    //     *
    //     * @param component the component we want to unselect, if the given component is null, this will do nothing
    //     */
    //    public void unselect(final SelectableComponentI component) {
    //        if (component != null) {
    //            if (component instanceof RootGraphicComponentBaseImpl) {
    //                SelectionController.INSTANCE.setSelectedRoot(null);
    //            } else if (component instanceof GridPartComponentI) {
    //                SelectionController.INSTANCE.setSelectedPart(null);
    //            }
    //        }
    //    }
    //
    //    /**
    //     * Clear the current selection.
    //     */
    //    public void clearSelection() {
    //        DisplayableComponentI selected = this.selectedComponentBoth.get();
    //        this.unselect((SelectableComponentI) selected);
    //    }
    //
    //    /**
    //     * @param part the part we want to get the first unselected parent
    //     * @return the first unselected parent, can return the part if not parent is found
    //     */
    //    public GridPartComponentI getFirstUnselectedParent(final GridPartComponentI part) {
    //        GridComponentI parent = part.gridParentProperty().get();
    //        //No parent
    //        if (parent == null) {
    //            return part;
    //        }
    //        //Parent exist
    //        else {
    //            if (parent.selectedProperty().get()) {
    //                return part;
    //            } else {
    //                return this.getFirstUnselectedParent(parent);
    //            }
    //        }
    //    }
    //
    //    /**
    //     * To method will try to select the next gridpart in the current selected gridpart grid parent.<br>
    //     * This action does nothing if there is no selected gridpart.<br>
    //     * A simple use case of this action : press tab to traverse grid element
    //     */
    //    public void selectNextGridPartInCurrentGrid() {
    //        GridPartComponentI selectedGridPart = this.selectedComponent.get();
    //        if (selectedGridPart != null) {
    //            GridComponentI gridParent = selectedGridPart.gridParentProperty().get();
    //            if (gridParent != null) {
    //                GridPartComponentI nextSelected;
    //                // next column ?
    //                int nextColumn = selectedGridPart.columnProperty().get() + selectedGridPart.columnSpanProperty().get();
    //                int nextRow = selectedGridPart.rowProperty().get() + 1;
    //                if (nextColumn < gridParent.columnCountProperty().get()) {
    //                    nextSelected = gridParent.getGrid().getComponent(selectedGridPart.rowProperty().get(), nextColumn);
    //                }
    //                // next line ?
    //                else if (nextRow < gridParent.rowCountProperty().get()) {
    //                    nextSelected = gridParent.getGrid().getComponent(nextRow, 0);
    //                }
    //                // first grid child
    //                else {
    //                    nextSelected = gridParent.getGrid().getComponent(0, 0);
    //                }
    //                this.setSelectedPart(nextSelected);
    //            }
    //        }
    //    }
    //
    //    public void selectPreviousGridPartInCurrentGrid() {
    //        GridPartComponentI selectedGridPart = this.selectedComponent.get();
    //        if (selectedGridPart != null) {
    //            GridComponentI gridParent = selectedGridPart.gridParentProperty().get();
    //            if (gridParent != null) {
    //                GridPartComponentI nextSelected;
    //                // previous column ?
    //                int previousColumn = selectedGridPart.columnProperty().get() - selectedGridPart.columnSpanProperty().get();
    //                int previousRow = selectedGridPart.rowProperty().get() - 1;
    //                if (previousColumn >= 0) {
    //                    nextSelected = gridParent.getGrid().getComponent(selectedGridPart.rowProperty().get(), previousColumn);
    //                }
    //                // next line ?
    //                else if (previousRow >= 0) {
    //                    nextSelected = gridParent.getGrid().getComponent(previousRow, gridParent.getGrid().getColumn() - 1);
    //                }
    //                // first grid child
    //                else {
    //                    nextSelected = gridParent.getGrid().getComponent(gridParent.getGrid().getRow() - 1, gridParent.getGrid().getColumn() - 1);
    //                }
    //                this.setSelectedPart(nextSelected);
    //            }
    //        }
    //    }
    //    //========================================================================
    //
    //    // Class part : "Internal selection method"
    //    //========================================================================
    //    private List<GridPartComponentI> getSelection(final int level) {
    //        if (!this.selection.containsKey(level)) {
    //            ObservableList<GridPartComponentI> compList = FXCollections.observableArrayList();
    //            this.createSelectionChangeListener(level, compList);
    //            this.selection.put(level, compList);
    //        }
    //        return this.selection.get(level);
    //    }
    //
    //    private void createSelectionChangeListener(final int level, final ObservableList<GridPartComponentI> componentList) {
    //        componentList.addListener(BindingUtils.createListChangeListener((added) -> {
    //            if (added instanceof GridPartKeyComponentI) {
    //                this.selectedKeys.add((GridPartKeyComponentI) added);
    //            }
    //        }, (removed) -> {
    //            if (removed instanceof GridPartKeyComponentI) {
    //                this.selectedKeys.remove(removed);
    //            }
    //        }));
    //    }
    //
    //    /**
    //     * Clear all the selected component from a level
    //     *
    //     * @param rootLevel start selection level (inclusive)
    //     */
    //    private void clearSelectionFrom(final int rootLevel) {
    //        Set<Integer> levels = this.selection.keySet();
    //        for (Integer level : levels) {
    //            if (level >= rootLevel) {
    //                List<GridPartComponentI> list = this.selection.get(level);
    //                if (list != null) {
    //                    for (GridPartComponentI comp : list) {
    //                        this.unselect(comp);
    //                    }
    //                    list.clear();
    //                }
    //            }
    //        }
    //    }
    //
    //    private void addSelection(final GridPartComponentI part) {
    //        this.select(part);
    //        part.showToFront(AppMode.EDIT.getViewProvider(), true);
    //        List<GridPartComponentI> selectionList = this.getSelection(part.getLevel());
    //        selectionList.add(part);
    //    }
    //
    //    private void removeSelection(final GridPartComponentI part) {
    //        this.unselect(part);
    //        List<GridPartComponentI> selectionList = this.getSelection(part.getLevel());
    //        if (selectionList != null) {
    //            selectionList.remove(part);
    //        }
    //        if (this.selectedComponent.get() == part) {
    //            // If the selected remove part is a key and keys are still selected (in list) > shift the selection on keys
    //            // this avoid having a "no selection" while the selected keys list is not empty
    //            this.selectedComponent.set(part instanceof GridPartKeyComponentI && !selectedKeys.isEmpty() ? selectedKeys.get(0) : null);
    //        }
    //    }
    //
    //    private void select(final GridPartComponentI part) {
    //        part.selectedProperty().set(true);
    //        this.selectedComponent.set(part);
    //        part.showPossibleSelectedProperty().set(false);
    //        if (part.isParentExist()) {
    //            part.gridParentProperty().get().showSelectedProperty().set(false);
    //            part.gridParentProperty().get().showPossibleSelectedProperty().set(false);
    //        }
    //    }
    //
    //    private void unselect(final GridPartComponentI part) {
    //        part.selectedProperty().set(false);
    //        part.showSelectedProperty().set(false);
    //        part.showPossibleSelectedProperty().set(false);
    //    }
    //    //========================================================================

}
