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
package org.lifecompanion.model.impl.configurationcomponent;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.jdom2.Element;
import org.lifecompanion.controller.io.ConfigurationComponentIOHelper;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.ComponentGridI;
import org.lifecompanion.model.api.configurationcomponent.KeyFactory;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.api.io.IOContextI;
import org.lifecompanion.model.api.io.XMLSerializable;
import org.lifecompanion.framework.commons.fx.io.XMLObjectSerializer;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class represent a component grid.<br>
 * This class is use to manage the add,remove, replace, move of key,grid,column,row...<br>
 * It fire change event when a key is added or removed from the grid.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ComponentGrid implements XMLSerializable<IOContextI>, ComponentGridI {
    private final static Logger LOGGER = LoggerFactory.getLogger(ComponentGrid.class);

    /**
     * Simple enum that give the grow direction for a span change
     */
    private static enum GrowDirection {
        TOP, BOTTOM, LEFT, RIGHT;
    }

    /**
     * The grid that contains every component
     */
    private GridPartComponentI[][] grid;

    /**
     * The size of the grid
     */
    private IntegerProperty row, column;

    /**
     * The key factory to create user keys
     */
    private KeyFactory keyFactory = KeyFactories.DEFAULT;

    /**
     * List to back the list content in a observable list
     */
    private ObservableList<GridPartComponentI> gridContent;

    /**
     * This grid parent
     */
    private GridComponentI parent;

    /**
     * Create a component grid for a given parent.
     *
     * @param parentP the grid parent associated with this grid component.
     */
    public ComponentGrid(final GridComponentI parentP) {
        this.row = new SimpleIntegerProperty(0);
        this.column = new SimpleIntegerProperty(0);
        this.createGrid();
        this.gridContent = FXCollections.observableArrayList();
        this.parent = parentP;
    }

    /**
     * Create the grid for the current count of row and column
     */
    private void createGrid() {
        this.grid = new GridPartComponentI[this.row.get()][this.column.get()];
    }

    // Class part : "Save/restore grid"
    // ========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public GridState saveGrid() {
        // Grid
        GridPartComponentI[][] dst = new GridPartComponentI[this.row.get()][this.column.get()];
        CollectionUtils.clone(this.grid, dst);
        // Span
        HashMap<GridPartComponentI, GridComponentInformation> spans = new HashMap<>(this.row.get() * this.column.get());
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
                GridPartComponentI comp = this.grid[rowIndex][colIndex];
                if (!(comp instanceof ComponentSpan)) {
                    spans.put(comp, GridComponentInformation.create(comp));
                }
            }
        }
        // Create state
        GridState state = new GridState(dst, spans);
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void restoreGrid(final GridState state) {
        // Remove previous component
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
                GridPartComponentI comp = this.grid[rowIndex][colIndex];
                if (!(comp instanceof ComponentSpan)) {
                    this.componentRemoved(comp);
                }
            }
        }
        // Set the new grid
        this.grid = state.getGrid();
        this.row.set(state.getGrid().length);
        this.column.set(state.getGrid()[0].length);
        // Restore grid
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
                GridPartComponentI comp = this.grid[rowIndex][colIndex];
                if (!(comp instanceof ComponentSpan)) {
                    // Restore layout
                    GridComponentInformation spanInfo = state.getSpans().get(comp);
                    comp.rowSpanProperty().set(spanInfo.getRowSpan());
                    comp.columnSpanProperty().set(spanInfo.getColumnSpan());
                    comp.rowProperty().set(spanInfo.getRow());
                    comp.columnProperty().set(spanInfo.getColumn());
                    // Create spans for layout
                    if (comp.rowSpanProperty().get() > 1 || comp.columnSpanProperty().get() > 1) {
                        int endRowIndex = comp.rowProperty().get() + comp.rowSpanProperty().get();
                        int endColumnIndex = comp.columnProperty().get() + comp.columnSpanProperty().get();
                        for (int rowIndexSpan = comp.rowProperty().get(); rowIndexSpan < endRowIndex; rowIndexSpan++) {
                            for (int columnIndexSpan = comp.columnProperty().get(); columnIndexSpan < endColumnIndex; columnIndexSpan++) {
                                if (rowIndexSpan != comp.rowProperty().get() || columnIndexSpan != comp.columnProperty().get()) {
                                    LOGGER.info("Will create a component span on {}x{}", rowIndexSpan, columnIndexSpan);
                                    grid[rowIndexSpan][columnIndexSpan] = createSpan(comp, rowIndexSpan, columnIndexSpan);
                                }
                            }
                        }
                    }
                    // Fire add
                    this.componentAdded(comp);
                }

            }
        }
        // Parent properties
        this.checkGridSpan();
    }

    // ========================================================================

    // Class part : "Row and column change"
    // ========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void setRow(final int rowP) {
        int diff = rowP - this.row.get();
        int count = Math.abs(diff);
        for (int i = 0; i < count; i++) {
            if (diff > 0) {
                this.addRow(this.row.get());
            } else if (diff < 0) {
                this.removeRow(this.row.get() - 1);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setColumn(final int columnP) {
        int diff = columnP - this.column.get();
        int count = Math.abs(diff);
        for (int i = 0; i < count; i++) {
            if (diff > 0) {
                this.addColumn(this.column.get());
            } else if (diff < 0) {
                this.removeColumn(this.column.get() - 1);
            }
        }
    }

    // ========================================================================

    // Class part : "Base getters"
    // ========================================================================
    @Override
    public GridPartComponentI getComponent(final int row, final int column) throws IllegalArgumentException {
        if (row < 0 || row >= this.row.get()) {
            throw new IllegalArgumentException("Given component row must be inside the grid");
        }
        if (column < 0 || column >= this.column.get()) {
            throw new IllegalArgumentException("Given component column must be inside the grid");
        }
        GridPartComponentI part = this.grid[row][column];
        if (part instanceof ComponentSpan) {
            return ((ComponentSpan) part).getExpanded();
        } else {
            return part;
        }
    }

    @Override
    public Set<GridPartKeyComponentI> getKeysBetween(GridPartComponentI part1, GridPartComponentI part2) {
        Set<GridPartKeyComponentI> keys = new HashSet<>();
        int startRow = Math.min(part1.rowProperty().get(), part2.rowProperty().get());
        int endRow = Math.max(part1.rowProperty().get(), part2.rowProperty().get());
        int startColumn = Math.min(part1.columnProperty().get(), part2.columnProperty().get());
        int endColumn = Math.max(part1.columnProperty().get(), part2.columnProperty().get());
        for (int r = startRow; r <= endRow; r++) {
            for (int c = startColumn; c <= endColumn; c++) {
                final GridPartComponentI comp = this.getComponent(r, c);
                if (comp instanceof GridPartKeyComponentI) {
                    keys.add((GridPartKeyComponentI) comp);
                }
            }
        }
        return keys;
    }

    @Override
    public ReadOnlyIntegerProperty rowProperty() {
        return this.row;
    }

    @Override
    public ReadOnlyIntegerProperty columnProperty() {
        return this.column;
    }

    @Override
    public int getRow() {
        return this.row.get();
    }

    @Override
    public int getColumn() {
        return this.column.get();
    }
    // ========================================================================

    // Class part : "Add/remove component into grid"
    // ========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void setComponent(final int row, final int column, final GridPartComponentI component) {
        // Get the real previous component
        GridPartComponentI before = this.grid[row][column];
        if (before instanceof ComponentSpan) {
            before = ((ComponentSpan) before).getExpanded();
        }
        // Replace span expanded component
        // Set the before component properties
        component.gridParentProperty().set(this.parent);
        component.rowProperty().set(before.rowProperty().get());
        component.columnProperty().set(before.columnProperty().get());
        component.columnSpanProperty().set(before.columnSpanProperty().get());
        component.rowSpanProperty().set(before.rowSpanProperty().get());
        this.replaceInGridAndComponentSpan(before.rowProperty().get(), before.columnProperty().get(), before.rowSpanProperty().get(),
                before.columnSpanProperty().get(), component);
        this.componentAdded(component);
        // Remove previous
        this.componentRemoved(before);
        this.checkGridSpan();
    }

    private void replaceInGridAndComponentSpan(final int row, final int column, final int rowSpan, final int columnSpan,
                                               final GridPartComponentI newComponent) {
        this.forEachComponentSpan(row, column, rowSpan, columnSpan, span -> span.setExpanded(newComponent));
        // Replace and add
        this.grid[row][column] = newComponent;
    }

    private void forEachComponentSpan(final int row, final int column, final int rowSpan, final int columnSpan,
                                      final Consumer<ComponentSpan> consumer) {
        int endRowIndex = row + rowSpan;
        int endColumnIndex = column + columnSpan;
        for (int rowIndex = row; rowIndex < endRowIndex; rowIndex++) {
            for (int columnIndex = column; columnIndex < endColumnIndex; columnIndex++) {
                GridPartComponentI comp = this.grid[rowIndex][columnIndex];
                if (comp instanceof ComponentSpan) {
                    consumer.accept((ComponentSpan) comp);
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeComponent(final int row, final int column) {
        // Get the real component and split it
        GridPartComponentI before = this.grid[row][column];
        if (before instanceof ComponentSpan) {
            before = ((ComponentSpan) before).getExpanded();
        }
        // Keep the same span
        int beforeRowSpan = before.rowSpanProperty().get();
        int beforeColumnSpan = before.columnSpanProperty().get();
        this.splitComponent(before);
        // Remove the component
        this.componentRemoved(before);
        // Create the empty key to fill it
        GridPartComponentI createdKey = this.keyFactory.createKey(before.rowProperty().get(), before.columnProperty().get(), 1, 1);
        this.grid[before.rowProperty().get()][before.columnProperty().get()] = createdKey;
        this.componentAdded(createdKey);
        // Span : always by the bottom/right because all component root are on left-top corner
        for (int rowSpan = 1; rowSpan < beforeRowSpan; rowSpan++) {
            createdKey.expandBottom();
        }
        for (int columnSpan = 1; columnSpan < beforeColumnSpan; columnSpan++) {
            createdKey.expandRight();
        }
        this.checkGridSpan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replaceComponent(final GridPartComponentI toReplace, final GridPartComponentI component) {
        int compRow = toReplace.rowProperty().get();
        int compColumn = toReplace.columnProperty().get();
        if (this.grid[compRow][compColumn] == toReplace) {
            this.setComponent(compRow, compColumn, component);
        } else {
            throw new IllegalArgumentException("The given component to replace is not in this grid");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void splitComponentIntoKeys(final GridPartComponentI toSplit) {
        int compRow = toSplit.rowProperty().get();
        int compColumn = toSplit.columnProperty().get();
        if (this.grid[compRow][compColumn] == toSplit) {
            this.splitComponent(toSplit);
            this.checkGridSpan();
        } else {
            throw new IllegalArgumentException("The given component to replace is not in this grid");
        }
    }
    // ========================================================================

    // Class part : "Row and column add"
    // ========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRow(final int wantedIndex) {
        // increase size
        this.growRow();
        // Shift the lines : from bottom take the upper line in the lower one
        HashSet<GridPartComponentI> componentsSpanned = new HashSet<>();
        for (int rowIndex = this.row.get(); rowIndex > wantedIndex; rowIndex--) {
            for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
                this.grid[rowIndex][colIndex] = this.grid[rowIndex - 1][colIndex];
                this.addToValidComponentIfPossible(wantedIndex, componentsSpanned, this.grid[rowIndex][colIndex], GridPartComponentI::rowProperty,
                        GridPartComponentI::rowSpanProperty);
            }
        }
        // On the added index : add a new key if needed, but if it's just a spanned component, just add its span
        for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
            GridPartComponentI spanComponentToReplace = this.getSpanComponentToReplace(this.grid[wantedIndex][colIndex], componentsSpanned);
            if (spanComponentToReplace == null) {
                GridPartComponentI key = this.keyFactory.createKey(wantedIndex, colIndex, 1, 1);
                this.grid[wantedIndex][colIndex] = key;
                this.componentAdded(key);
            } else {
                this.grid[wantedIndex][colIndex] = createSpan(spanComponentToReplace, wantedIndex, colIndex);
            }
        }
        // Add row span on needed
        for (GridPartComponentI componentSpanned : componentsSpanned) {
            componentSpanned.rowSpanProperty().set(componentSpanned.rowSpanProperty().get() + 1);
        }
        this.row.set(this.row.get() + 1);
        this.checkGridSpan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumn(final int wantedIndex) {
        // increase size
        this.growColumn();
        // Shift the columns : from right take the left column in the right one
        HashSet<GridPartComponentI> componentsSpanned = new HashSet<>();
        for (int colIndex = this.column.get(); colIndex > wantedIndex; colIndex--) {
            for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
                this.grid[rowIndex][colIndex] = this.grid[rowIndex][colIndex - 1];
                this.addToValidComponentIfPossible(wantedIndex, componentsSpanned, this.grid[rowIndex][colIndex], GridPartComponentI::columnProperty,
                        GridPartComponentI::columnSpanProperty);
            }
        }
        // On the added index : add a new key if needed, but if it's just a spanned component, just add its span
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            GridPartComponentI spanComponentToReplace = this.getSpanComponentToReplace(this.grid[rowIndex][wantedIndex], componentsSpanned);
            if (spanComponentToReplace == null) {
                GridPartComponentI key = this.keyFactory.createKey(rowIndex, wantedIndex, 1, 1);
                this.grid[rowIndex][wantedIndex] = key;
                this.componentAdded(key);
            } else {
                this.grid[rowIndex][wantedIndex] = createSpan(spanComponentToReplace, rowIndex, wantedIndex);
            }
        }
        // Add row span on needed
        for (GridPartComponentI componentSpanned : componentsSpanned) {
            componentSpanned.columnSpanProperty().set(componentSpanned.columnSpanProperty().get() + 1);
        }
        this.column.set(this.column.get() + 1);
        this.checkGridSpan();
    }

    // ========================================================================

    // Class part : "Row and column remove"
    // ========================================================================

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRow(final int wantedIndex) {
        HashSet<GridPartComponentI> componentsSpanned = new HashSet<>();
        // Remove row
        for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
            GridPartComponentI part = this.grid[wantedIndex][colIndex];
            if (!this.addToValidComponentIfPossibleForRemove(wantedIndex, componentsSpanned, part, GridPartComponentI::rowProperty,
                    GridPartComponentI::rowSpanProperty)) {
                this.componentRemoved(part);
            }
        }
        // Shift
        for (int rowIndex = wantedIndex; rowIndex < this.row.get() - 1; rowIndex++) {
            for (int colIndex = 0; colIndex < this.column.get(); colIndex++) {
                GridPartComponentI spanComponentToReplace = this.getSpanComponentToReplace(this.grid[rowIndex + 1][colIndex], componentsSpanned);
                if (spanComponentToReplace == null) {
                    this.grid[rowIndex][colIndex] = this.grid[rowIndex + 1][colIndex];
                    this.grid[rowIndex][colIndex].rowProperty().set(rowIndex);
                }
            }
        }
        // Reduce span on row
        for (GridPartComponentI componentSpanned : componentsSpanned) {
            componentSpanned.rowSpanProperty().set(componentSpanned.rowSpanProperty().get() - 1);
        }
        // Remove last row
        this.reduceRow();
        this.row.set(this.row.get() - 1);
        this.checkGridSpan();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeColumn(final int wantedIndex) {
        HashSet<GridPartComponentI> componentsSpanned = new HashSet<>();
        // Remove column
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            GridPartComponentI part = this.grid[rowIndex][wantedIndex];
            if (!this.addToValidComponentIfPossibleForRemove(wantedIndex, componentsSpanned, part, GridPartComponentI::columnProperty,
                    GridPartComponentI::columnSpanProperty)) {
                this.componentRemoved(part);
            }
        }
        // Shift
        for (int colIndex = wantedIndex; colIndex < this.column.get() - 1; colIndex++) {
            for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
                GridPartComponentI spanComponentToReplace = this.getSpanComponentToReplace(this.grid[rowIndex][colIndex + 1], componentsSpanned);
                if (spanComponentToReplace == null) {
                    this.grid[rowIndex][colIndex] = this.grid[rowIndex][colIndex + 1];
                    this.grid[rowIndex][colIndex].columnProperty().set(colIndex);
                }
            }
        }
        // Reduce span on column
        for (GridPartComponentI componentSpanned : componentsSpanned) {
            componentSpanned.columnSpanProperty().set(componentSpanned.columnSpanProperty().get() - 1);
        }
        // Remove last
        this.reduceColumn();
        this.column.set(this.column.get() - 1);
        this.checkGridSpan();
    }

    // ========================================================================

    // Class part : "Add/delete rows/columns helpers"
    // ========================================================================
    private boolean addToValidComponentIfPossible(final int wantedIndex, final HashSet<GridPartComponentI> componentsSpanned,
                                                  final GridPartComponentI gridPart, final Function<GridPartComponentI, IntegerProperty> indexGetter,
                                                  final Function<GridPartComponentI, IntegerProperty> spanGetter) {
        GridPartComponentI validComponentSpan = this.getValidComponentSpan(gridPart, wantedIndex, indexGetter, spanGetter, true);
        if (validComponentSpan != null) {
            componentsSpanned.add(validComponentSpan);
            return true;
        }
        return false;
    }

    private boolean addToValidComponentIfPossibleForRemove(final int wantedIndex, final HashSet<GridPartComponentI> componentsSpanned,
                                                           final GridPartComponentI gridPart, final Function<GridPartComponentI, IntegerProperty> indexGetter,
                                                           final Function<GridPartComponentI, IntegerProperty> spanGetter) {
        GridPartComponentI validComponentSpan = this.getValidComponentSpan(gridPart, wantedIndex, indexGetter, spanGetter, false);
        if (validComponentSpan != null) {
            componentsSpanned.add(validComponentSpan);
            return true;
        }
        return false;
    }

    private GridPartComponentI getValidComponentSpan(final GridPartComponentI component, final int addIndex,
                                                     final Function<GridPartComponentI, IntegerProperty> indexGetter, final Function<GridPartComponentI, IntegerProperty> spanGetter,
                                                     final boolean strict) {
        GridPartComponentI foundComp = null;
        if (component instanceof ComponentSpan) {
            foundComp = ((ComponentSpan) component).getExpanded();
        } else {
            foundComp = component;
        }
        if (spanGetter.apply(foundComp).get() > 1) {
            int getterResult = indexGetter.apply(foundComp).get();
            if (strict && getterResult < addIndex || !strict && getterResult <= addIndex) {
                return foundComp;
            }
        }
        return null;
    }

    private GridPartComponentI getSpanComponentToReplace(final GridPartComponentI component, final HashSet<GridPartComponentI> spannedComponent) {
        return spannedComponent.contains(component) ? component
                : component instanceof ComponentSpan && spannedComponent.contains(((ComponentSpan) component).getExpanded())
                ? ((ComponentSpan) component).getExpanded()
                : null;
    }
    // ========================================================================

    // Class part : "Component add/remove"
    // ========================================================================
    private void componentAdded(final GridPartComponentI part) {
        this.componentsAdded(List.of(part));
    }

    private void componentsAdded(final List<GridPartComponentI> parts) {
        List<GridPartComponentI> toAddToContent = new ArrayList<>(parts.size());
        for (GridPartComponentI part : parts) {
            if (!(part instanceof ComponentSpan)) {
                // Property binding
                part.gridParentProperty().set(this.parent);
                part.dispatchRemovedPropertyValue(false);
                part.dispatchDisplayedProperty(this.parent.displayedProperty().get());
                // Add content
                toAddToContent.add(part);
            }
        }
        this.gridContent.addAll(toAddToContent);
    }

    private void componentRemoved(final GridPartComponentI part) {
        if (!(part instanceof ComponentSpan)) {
            this.gridContent.remove(part);
            // Property binding
            part.gridParentProperty().set(null);
            part.dispatchRemovedPropertyValue(true);
            part.dispatchDisplayedProperty(false);
        }
    }

    @Override
    public ObservableList<GridPartComponentI> getGridContent() {
        return this.gridContent;
    }

    // ========================================================================

    // Class part : "Grid grow/reduce"
    // ========================================================================
    private void reduceRow() {
        GridPartComponentI[][] newGrid = new GridPartComponentI[this.row.get() - 1][this.column.get()];
        CollectionUtils.copyReduce(this.grid, newGrid);
        this.grid = newGrid;
    }

    private void reduceColumn() {
        GridPartComponentI[][] newGrid = new GridPartComponentI[this.row.get()][this.column.get() - 1];
        CollectionUtils.copyReduce(this.grid, newGrid);
        this.grid = newGrid;
    }

    private void growRow() {
        GridPartComponentI[][] newGrid = new GridPartComponentI[this.row.get() + 1][this.column.get()];
        CollectionUtils.copyGrow(this.grid, newGrid);
        this.grid = newGrid;
    }

    private void growColumn() {
        GridPartComponentI[][] newGrid = new GridPartComponentI[this.row.get()][this.column.get() + 1];
        CollectionUtils.copyGrow(this.grid, newGrid);
        this.grid = newGrid;
    }
    // ========================================================================

    // Class part : "Component space"
    // ========================================================================

    /**
     * Split a component into individual keys, keep the component on its origin.<br>
     * This will change the component span.
     *
     * @param component the component to split.
     */
    private void splitComponent(final GridPartComponentI component) {
        for (int rowIndex = component.rowProperty().get(); rowIndex < component.rowProperty().get() + component.rowSpanProperty().get(); rowIndex++) {
            for (int colIndex = component.columnProperty().get(); colIndex < component.columnProperty().get()
                    + component.columnSpanProperty().get(); colIndex++) {
                if (rowIndex != component.rowProperty().get() || colIndex != component.columnProperty().get()) {
                    GridPartComponentI created = this.keyFactory.createKey(rowIndex, colIndex, 1, 1);
                    this.grid[rowIndex][colIndex] = created;
                    this.componentAdded(created);
                }
            }
        }
        component.rowSpanProperty().set(1);
        component.columnSpanProperty().set(1);
    }

    /**
     * This method will change the given component to create a individual key from its component if needed.<br>
     * This method will do nothing if not change is needed.<br>
     * This method can result a a full split of the component, if no optimized changes was found.<br>
     * <strong>Dev note : </strong> this is the method to determines the behavior of node expand/collapse.
     *
     * @param needed    the component where we want to create a individual key.
     * @param direction the change direction, to know the possible optimization.
     */
    private void createFreeSpace(final GridPartComponentI needed, final GrowDirection direction) {
        // We only needed to create space when component is not a unique span component, or is a span part
        if (needed instanceof ComponentSpan || needed.columnSpanProperty().get() > 1 || needed.rowSpanProperty().get() > 1) {
            int componentRow = needed.rowProperty().get();
            int componentColumn = needed.columnProperty().get();
            switch (direction) {
                case TOP:
                    if (needed instanceof ComponentSpan) {
                        ComponentSpan part = (ComponentSpan) needed;
                        if (part.getExpanded().rowSpanProperty().get() > 1) {
                            part.getExpanded().collapseBottom();
                        } else if (part.getExpanded().columnSpanProperty().get() > 1) {
                            part.getExpanded().collapseRight();
                        }
                    } else if (needed.columnSpanProperty().get() > 1) {
                        needed.collapseLeft();
                    }
                    break;
                case BOTTOM:
                    if (needed.rowSpanProperty().get() > 1) {
                        needed.collapseTop();
                    } else if (needed.columnSpanProperty().get() > 1) {
                        needed.collapseLeft();
                    } else if (needed instanceof ComponentSpan) {
                        ComponentSpan part = (ComponentSpan) needed;
                        if (part.getExpanded().columnSpanProperty().get() > 1) {
                            part.getExpanded().collapseRight();
                        }
                    }
                    break;
                case LEFT:
                    if (needed instanceof ComponentSpan) {
                        ComponentSpan part = (ComponentSpan) needed;
                        if (part.columnSpanProperty().get() > 1) {
                            part.getExpanded().collapseRight();
                        } else if (part.rowSpanProperty().get() > 1) {
                            part.getExpanded().collapseBottom();
                        }
                    } else if (needed.rowSpanProperty().get() > 1) {
                        needed.collapseTop();
                    }
                    break;
                case RIGHT:
                    if (needed.columnSpanProperty().get() > 1) {
                        needed.collapseLeft();
                    } else if (needed.rowSpanProperty().get() > 1) {
                        needed.collapseTop();
                    } else if (needed instanceof ComponentSpan) {
                        ComponentSpan part = (ComponentSpan) needed;
                        if (part.getExpanded().rowSpanProperty().get() > 1) {
                            part.getExpanded().collapseBottom();
                        }
                    }
                    break;
            }
            // Check if a solution is found
            GridPartComponentI changed = this.grid[componentRow][componentColumn];
            if (changed instanceof ComponentSpan || changed.columnSpanProperty().get() > 1 || changed.rowSpanProperty().get() > 1) {
                if (changed instanceof ComponentSpan) {
                    this.splitComponent(((ComponentSpan) changed).getExpanded());
                } else {
                    this.splitComponent(changed);
                }
            }
        }
    }

    // ========================================================================

    // Class part : "Expand,collapse right/left"
    // ========================================================================
    @Override
    public void expandSpanRight(final GridPartComponentI component) {
        int expandIndex = component.columnProperty().get() + component.columnSpanProperty().get();
        for (int rowIndex = component.rowProperty().get(); rowIndex < component.rowProperty().get() + component.rowSpanProperty().get(); rowIndex++) {
            GridPartComponentI before = this.grid[rowIndex][expandIndex];
            this.createFreeSpace(before, GrowDirection.RIGHT);
            before = this.grid[rowIndex][expandIndex];
            this.componentRemoved(before);
            // Now that everything is cleared, we can expand
            this.grid[rowIndex][expandIndex] = createSpan(component, rowIndex, expandIndex);
        }
        component.columnSpanProperty().set(component.columnSpanProperty().get() + 1);
        this.checkGridSpan();
    }

    @Override
    public void collapseSpanRight(final GridPartComponentI component) {
        int collapseIndex = component.columnProperty().get() + component.columnSpanProperty().get() - 1;
        for (int rowIndex = component.rowProperty().get(); rowIndex < component.rowProperty().get() + component.rowSpanProperty().get(); rowIndex++) {
            // Create a key to replace span
            GridPartComponentI created = this.keyFactory.createKey(rowIndex, collapseIndex, 1, 1);
            this.grid[rowIndex][collapseIndex] = created;
            this.componentAdded(created);
        }
        component.columnSpanProperty().set(component.columnSpanProperty().get() - 1);
        this.checkGridSpan();
    }

    @Override
    public void expandSpanLeft(final GridPartComponentI component) {
        int moveComponentIndex = component.columnProperty().get() - 1;
        int createSpanIndex = component.columnProperty().get();
        for (int rowIndex = component.rowProperty().get(); rowIndex < component.rowProperty().get() + component.rowSpanProperty().get(); rowIndex++) {
            GridPartComponentI before = this.grid[rowIndex][moveComponentIndex];
            this.createFreeSpace(before, GrowDirection.LEFT);
            // Remove
            before = this.grid[rowIndex][moveComponentIndex];
            this.componentRemoved(before);
            // Shift
            this.grid[rowIndex][moveComponentIndex] = this.grid[rowIndex][createSpanIndex];
            this.grid[rowIndex][createSpanIndex] = createSpan(component, rowIndex, createSpanIndex);
        }
        component.columnSpanProperty().set(component.columnSpanProperty().get() + 1);
        component.columnProperty().set(component.columnProperty().get() - 1);
        this.checkGridSpan();
    }

    @Override
    public void collapseSpanLeft(final GridPartComponentI component) {
        int collapseIndex = component.columnProperty().get();
        int newComponentIndex = component.columnProperty().get() + 1;
        for (int rowIndex = component.rowProperty().get(); rowIndex < component.rowProperty().get() + component.rowSpanProperty().get(); rowIndex++) {
            // Shift current component
            this.grid[rowIndex][newComponentIndex] = this.grid[rowIndex][collapseIndex];
            // replace with default key
            GridPartComponentI created = this.keyFactory.createKey(rowIndex, collapseIndex, 1, 1);
            this.grid[rowIndex][collapseIndex] = created;
            this.componentAdded(created);
        }
        component.columnSpanProperty().set(component.columnSpanProperty().get() - 1);
        component.columnProperty().set(component.columnProperty().get() + 1);
        this.checkGridSpan();
    }

    // ========================================================================

    // Class part : "Expand,collapse top/bottom"
    // ========================================================================
    @Override
    public void expandSpanTop(final GridPartComponentI component) {
        int expandIndex = component.rowProperty().get() - 1;
        int createSpanIndex = component.rowProperty().get();
        for (int colIndex = component.columnProperty().get(); colIndex < component.columnProperty().get()
                + component.columnSpanProperty().get(); colIndex++) {
            GridPartComponentI before = this.grid[expandIndex][colIndex];
            this.createFreeSpace(before, GrowDirection.TOP);
            // Remove
            before = this.grid[expandIndex][colIndex];
            this.componentRemoved(before);
            // Shift
            this.grid[expandIndex][colIndex] = this.grid[createSpanIndex][colIndex];
            this.grid[createSpanIndex][colIndex] = createSpan(component, createSpanIndex, colIndex);
        }
        component.rowProperty().set(component.rowProperty().get() - 1);
        component.rowSpanProperty().set(component.rowSpanProperty().get() + 1);
        this.checkGridSpan();
    }

    @Override
    public void collapseSpanTop(final GridPartComponentI component) {
        int collapseIndex = component.rowProperty().get() + 1;
        int previousIndex = component.rowProperty().get();
        for (int colIndex = component.columnProperty().get(); colIndex < component.columnProperty().get()
                + component.columnSpanProperty().get(); colIndex++) {
            // Shift current
            this.grid[collapseIndex][colIndex] = this.grid[previousIndex][colIndex];
            // Replace
            GridPartComponentI key = this.keyFactory.createKey(previousIndex, colIndex, 1, 1);
            this.grid[previousIndex][colIndex] = key;
            this.componentAdded(key);
        }
        component.rowProperty().set(component.rowProperty().get() + 1);
        component.rowSpanProperty().set(component.rowSpanProperty().get() - 1);
        this.checkGridSpan();
    }

    @Override
    public void collapseSpanBottom(final GridPartComponentI component) {
        int collapseIndex = component.rowProperty().get() + component.rowSpanProperty().get() - 1;
        for (int colIndex = component.columnProperty().get(); colIndex < component.columnProperty().get()
                + component.columnSpanProperty().get(); colIndex++) {
            GridPartComponentI created = this.keyFactory.createKey(collapseIndex, colIndex, 1, 1);
            this.grid[collapseIndex][colIndex] = created;
            this.componentAdded(created);
        }
        component.rowSpanProperty().set(component.rowSpanProperty().get() - 1);
        this.checkGridSpan();
    }

    @Override
    public void expandSpanBottom(final GridPartComponentI component) {
        int expandIndex = component.rowProperty().get() + component.rowSpanProperty().get();
        for (int colIndex = component.columnProperty().get(); colIndex < component.columnProperty().get()
                + component.columnSpanProperty().get(); colIndex++) {
            GridPartComponentI before = this.grid[expandIndex][colIndex];
            this.createFreeSpace(before, GrowDirection.BOTTOM);
            before = this.grid[expandIndex][colIndex];
            this.componentRemoved(before);
            // Create
            this.grid[expandIndex][colIndex] = createSpan(component, expandIndex, colIndex);
        }
        component.rowSpanProperty().set(component.rowSpanProperty().get() + 1);
        this.checkGridSpan();
    }

    // ========================================================================

    // Class part : "Component span"
    // ========================================================================

    /**
     * Create a span for the given component.<br>
     * A span is a grid component that is not displayed, and that just fill the empty space created when a component span on more that is base case.
     *
     * @param component the parent component for the span (the component expanded)
     * @param row       the row location of the wanted span
     * @param column    the column location of the wanted span
     * @return the created span
     */
    private static GridPartComponentI createSpan(final GridPartComponentI component, final int row, final int column) {
        ComponentSpan created = new ComponentSpan();
        created.setExpanded(component);
        created.rowProperty().set(row);
        created.columnProperty().set(column);
        return created;

    }

    /**
     * Sometime, while switching component to another location in grid, the column/row attribute of this component can be broken.<br>
     * That's why after each grid structure modification, we check the grid and update attribute when needed.<br>
     * This is also useful because it allows to modify internal grid structure without changing the component index.
     */
    private void checkGridSpan() {
        for (int y = 0; y < this.row.get(); y++) {
            for (int x = 0; x < this.column.get(); x++) {
                GridPartComponentI component = this.grid[y][x];
                if (component.rowProperty().get() != y || component.columnProperty().get() != x) {
                    ComponentGrid.LOGGER.error("Found a component invalid attribute in grid, component is at {}x{} but its attributes were {}x{}", x,
                            y, component.columnProperty().get(), component.rowProperty().get());
                    component.rowProperty().set(y);
                    component.columnProperty().set(x);
                }
            }
        }
    }
    // ========================================================================

    // Class part : "Add key on..."
    // ========================================================================
    @Override
    public void addKeyOnLeft(final GridPartComponentI source) {
        // List each component
        List<GridPartComponentI> components = this.getComponentHorizontal();
        this.shiftComponent(source, components, true);
    }

    @Override
    public void addKeyOnRight(final GridPartComponentI source) {
        List<GridPartComponentI> components = this.getComponentHorizontal();
        this.shiftComponent(source, components, false);
    }

    @Override
    public void addKeyOnTop(final GridPartComponentI source) {
        List<GridPartComponentI> components = this.getComponentVertical();
        this.shiftComponent(source, components, true);
    }

    @Override
    public void addKeyOnBottom(final GridPartComponentI source) {
        List<GridPartComponentI> components = this.getComponentVertical();
        this.shiftComponent(source, components, false);
    }

    private void shiftComponent(final GridPartComponentI source, final List<GridPartComponentI> components, final boolean fromEnd) {
        if (components.size() > 1) {
            int insertIndex = components.indexOf(source);
            GridComponentInformation insertInformations = GridComponentInformation.create(source);
            // For end, shift every component
            GridComponentInformation previousInformations = null;
            for (int i = fromEnd ? components.size() - 1 : 0; fromEnd ? i > insertIndex : i < insertIndex; i += fromEnd ? -1 : 1) {
                GridPartComponentI oldComponent = components.get(i);
                // On first component, there is no informations, and component is removed
                if (previousInformations == null) {
                    this.componentRemoved(oldComponent);
                    previousInformations = GridComponentInformation.create(oldComponent);
                }
                GridPartComponentI replaceComponent = components.get(i + (fromEnd ? -1 : 1));
                // Save informations
                GridComponentInformation oldComponentInfo = previousInformations;
                previousInformations = GridComponentInformation.create(replaceComponent);
                // Replace with previous informations
                replaceComponent.rowProperty().set(oldComponentInfo.getRow());
                replaceComponent.columnProperty().set(oldComponentInfo.getColumn());
                replaceComponent.rowSpanProperty().set(oldComponentInfo.getRowSpan());
                replaceComponent.columnSpanProperty().set(oldComponentInfo.getColumnSpan());
                // Replace in grid and span
                this.replaceInGridAndComponentSpan(replaceComponent.rowProperty().get(), replaceComponent.columnProperty().get(),
                        replaceComponent.rowSpanProperty().get(), replaceComponent.columnSpanProperty().get(), replaceComponent);
            }
            // Create a component on the insert index
            GridPartComponentI addedKey = this.keyFactory.createKey(insertInformations.getRow(), insertInformations.getColumn(),
                    insertInformations.getRowSpan(), insertInformations.getColumnSpan());
            this.replaceInGridAndComponentSpan(addedKey.rowProperty().get(), addedKey.columnProperty().get(), addedKey.rowSpanProperty().get(),
                    addedKey.columnSpanProperty().get(), addedKey);
            this.componentAdded(addedKey);
            // If insert on extreme, previous component need to be removed because it wasn't shifted
            if (!fromEnd && insertIndex == 0 || insertIndex == components.size() - 1 && fromEnd) {
                this.componentRemoved(source);
            }

        }
    }
    // ========================================================================

    // Class part : "List of component"
    // ========================================================================
    @Override
    public List<GridPartComponentI> getComponentHorizontal() {
        int rowCount = this.getRow();
        int columnCount = this.getColumn();
        List<GridPartComponentI> comps = new ArrayList<>(rowCount * columnCount);
        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                GridPartComponentI component = this.getComponent(r, c);
                if (!comps.contains(component)) {
                    comps.add(component);
                }
            }
        }
        return comps;
    }

    @Override
    public List<GridPartComponentI> getComponentVertical() {
        int rowCount = this.getRow();
        int columnCount = this.getColumn();
        List<GridPartComponentI> comps = new ArrayList<>(rowCount * columnCount);
        for (int c = 0; c < columnCount; c++) {
            for (int r = 0; r < rowCount; r++) {
                GridPartComponentI component = this.getComponent(r, c);
                if (!comps.contains(component)) {
                    comps.add(component);
                }
            }
        }
        return comps;
    }
    // ========================================================================

    // Class part : "XML"
    // ========================================================================
    public static final String NODE_GRID = "Grid";

    @Override
    public Element serialize(final IOContextI contextP) {
        Element element = new Element(ComponentGrid.NODE_GRID);
        XMLObjectSerializer.serializeInto(ComponentGrid.class, this, element);
        // For each component
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < this.column.get(); columnIndex++) {
                GridPartComponentI component = this.grid[rowIndex][columnIndex];
                if (component != null) {
                    if (!(component instanceof ComponentSpan)) {
                        element.addContent(component.serialize(contextP));
                    }
                } else {
                    throw new NullPointerException("Grid contains a NULL component!");
                }
            }
        }
        return element;
    }

    @Override
    public void deserialize(final Element nodeP, final IOContextI contextP) throws LCException {
        // Base
        XMLObjectSerializer.deserializeInto(ComponentGrid.class, this, nodeP);
        this.createGrid();
        // Load the children
        List<GridPartComponentI> loadedChildren = new ArrayList<>();
        List<Element> gridComponents = nodeP.getChildren();
        for (Element gridComponent : gridComponents) {
            // Load the child
            Pair<Boolean, XMLSerializable<IOContextI>> gridPartResult = ConfigurationComponentIOHelper.create(gridComponent, contextP, GridPartKeyComponent::new);
            GridPartComponentI gridPart = (GridPartComponentI) gridPartResult.getRight();
            if (!gridPartResult.getLeft()) {
                gridPart.deserialize(gridComponent, contextP);
            }

            // Put into the grid
            this.grid[gridPart.rowProperty().get()][gridPart.columnProperty().get()] = gridPart;

            // Create component span if needed
            if (gridPart.rowSpanProperty().get() > 1 || gridPart.columnSpanProperty().get() > 1) {
                int endRowIndex = gridPart.rowProperty().get() + gridPart.rowSpanProperty().get();
                int endColumnIndex = gridPart.columnProperty().get() + gridPart.columnSpanProperty().get();
                for (int rowIndex = gridPart.rowProperty().get(); rowIndex < endRowIndex; rowIndex++) {
                    for (int columnIndex = gridPart.columnProperty().get(); columnIndex < endColumnIndex; columnIndex++) {
                        if (rowIndex != gridPart.rowProperty().get() || columnIndex != gridPart.columnProperty().get()) {
                            grid[rowIndex][columnIndex] = createSpan(gridPart, rowIndex, columnIndex);
                        }
                    }
                }
            }
            // Fire
            loadedChildren.add(gridPart);
        }
        this.componentsAdded(loadedChildren);
    }
    // ========================================================================

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int rowIndex = 0; rowIndex < this.row.get(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < this.column.get(); columnIndex++) {
                GridPartComponentI component = this.grid[rowIndex][columnIndex];
                if (component instanceof ComponentSpan) {
                    sb.append("Span(").append(rowIndex).append(",").append(columnIndex).append(")[")
                            .append(((ComponentSpan) component).getExpandedId()).append("]  ");
                } else {
                    sb.append("Comp(").append(rowIndex).append(",").append(columnIndex).append(")[").append(component.getID()).append("]  ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();

    }

}
