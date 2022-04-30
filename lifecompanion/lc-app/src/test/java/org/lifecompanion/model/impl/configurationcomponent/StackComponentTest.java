/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StackComponentTest {
    private static final int ROW_COLUMN = 2;

    private LCConfigurationComponent configurationComponent;
    private StackComponent stackComponent;
    private GridPartGridComponent grid1, grid2, grid3;

    @BeforeEach
    public void beforeEach() {
        this.configurationComponent = new LCConfigurationComponent();
        this.stackComponent = new StackComponent();
        configurationComponent.getChildren().add(stackComponent);

        grid1 = createGrid();
        grid2 = createGrid();
        grid3 = createGrid();
    }

    private GridPartGridComponent createGrid() {
        GridPartGridComponent grid = new GridPartGridComponent();
        grid.getGrid().setRow(ROW_COLUMN);
        grid.getGrid().setColumn(ROW_COLUMN);
        return grid;
    }

    @Test
    void testSimpleAddOneGridToStack() {
        stackComponent.getComponentList().add(grid1);

        assertEquals(stackComponent, grid1.stackParentProperty().get());
        assertEquals(configurationComponent, grid1.configurationParentProperty().get());
        assertTrue(grid1.lastStackChildProperty().get());
        assertFalse(stackComponent.nextPossibleProperty().get());
        assertFalse(stackComponent.previousPossibleProperty().get());
        assertEquals(grid1, stackComponent.displayedComponentProperty().get());
    }

    @Test
    void testSimpleAddTwoGridToStack() {
        stackComponent.getComponentList().addAll(grid1, grid2);

        assertEquals(grid1, stackComponent.getComponentList().get(0));
        assertEquals(grid1, stackComponent.getTreeIdentifiableChildren().get(0));
        assertEquals(stackComponent, grid1.stackParentProperty().get());
        assertEquals(configurationComponent, grid1.configurationParentProperty().get());
        assertFalse(grid1.lastStackChildProperty().get());

        assertEquals(grid2, stackComponent.getComponentList().get(1));
        assertEquals(grid2, stackComponent.getTreeIdentifiableChildren().get(1));
        assertEquals(stackComponent, grid2.stackParentProperty().get());
        assertEquals(configurationComponent, grid2.configurationParentProperty().get());
        assertFalse(grid2.lastStackChildProperty().get());

        assertEquals(grid1, stackComponent.displayedComponentProperty().get());
        assertTrue(stackComponent.nextPossibleProperty().get());
        assertFalse(stackComponent.previousPossibleProperty().get());
    }

    @Test
    void testNextPreviousDisplayedComponent() {
        stackComponent.getComponentList().addAll(grid1, grid2);

        assertEquals(grid1, stackComponent.displayedComponentProperty().get());
        assertTrue(stackComponent.nextPossibleProperty().get());
        assertFalse(stackComponent.previousPossibleProperty().get());
        assertTrue(grid1.displayedProperty().get());
        assertFalse(grid2.displayedProperty().get());
        grid2.getGrid().getGridContent().forEach(c -> assertFalse(c.displayedProperty().get()));
        grid1.getGrid().getGridContent().forEach(c -> assertTrue(c.displayedProperty().get()));

        stackComponent.displayNextForEditMode();

        assertTrue(grid2.displayedProperty().get());
        assertFalse(grid1.displayedProperty().get());
        grid1.getGrid().getGridContent().forEach(c -> assertFalse(c.displayedProperty().get()));
        grid2.getGrid().getGridContent().forEach(c -> assertTrue(c.displayedProperty().get()));

        assertEquals(grid2, stackComponent.displayedComponentProperty().get());
        assertFalse(stackComponent.nextPossibleProperty().get());
        assertTrue(stackComponent.previousPossibleProperty().get());
    }

    @Test
    void testShiftUpComponent() {
        stackComponent.getComponentList().addAll(grid1, grid2);

        assertEquals(grid1, stackComponent.getComponentList().get(0));
        assertEquals(grid1, stackComponent.getTreeIdentifiableChildren().get(0));
        assertEquals(stackComponent, grid1.stackParentProperty().get());
        assertEquals(configurationComponent, grid1.configurationParentProperty().get());
        assertFalse(grid1.lastStackChildProperty().get());

        assertEquals(grid2, stackComponent.getComponentList().get(1));
        assertEquals(grid2, stackComponent.getTreeIdentifiableChildren().get(1));
        assertEquals(stackComponent, grid2.stackParentProperty().get());
        assertEquals(configurationComponent, grid2.configurationParentProperty().get());
        assertFalse(grid2.lastStackChildProperty().get());

        stackComponent.shiftUpComponent(grid2);

        assertEquals(grid2, stackComponent.getComponentList().get(0));
        assertEquals(grid2, stackComponent.getTreeIdentifiableChildren().get(0));
        assertEquals(stackComponent, grid2.stackParentProperty().get());
        assertEquals(configurationComponent, grid2.configurationParentProperty().get());
        assertFalse(grid2.lastStackChildProperty().get());

        assertEquals(grid1, stackComponent.getComponentList().get(1));
        assertEquals(grid1, stackComponent.getTreeIdentifiableChildren().get(1));
        assertEquals(stackComponent, grid1.stackParentProperty().get());
        assertEquals(configurationComponent, grid1.configurationParentProperty().get());
        assertFalse(grid1.lastStackChildProperty().get());
    }

}
