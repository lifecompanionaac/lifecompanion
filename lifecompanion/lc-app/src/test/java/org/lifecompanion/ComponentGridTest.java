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

package org.lifecompanion;

import junit.framework.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.configurationcomponent.ComponentGrid;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

public class ComponentGridTest {
    private GridPartGridComponent gridComponent;
    private ComponentGrid componentGrid;

    @BeforeEach
    public void setUp() {
        this.gridComponent = new GridPartGridComponent();
        this.componentGrid = (ComponentGrid) this.gridComponent.getGrid();
    }

    @Test
    public void testSimpleRowColumnAdd() {
        this.gridComponent.getGrid().setRow(2);
        this.gridComponent.getGrid().setColumn(4);
        //Check count
        assertEquals(2, this.componentGrid.getRow());
        assertEquals(4, this.componentGrid.getColumn());
        assertEquals(8, this.componentGrid.getGridContent().size());

        //Check if adding row shift component
        List<GridPartComponentI> firstLineComp = new ArrayList<>();
        for (int c = 0; c < this.componentGrid.getColumn(); c++) {
            firstLineComp.add(this.componentGrid.getComponent(0, c));
        }
        //add, and check
        this.gridComponent.getGrid().setRow(this.gridComponent.rowCountProperty().get() + 1);
        for (int c = 0; c < this.componentGrid.getColumn(); c++) {
            assertEquals(this.componentGrid.getComponent(0, c), firstLineComp.get(c));
        }
        for (int c = 0; c < this.componentGrid.getColumn(); c++) {
            assertNotSame(this.componentGrid.getComponent(1, c), firstLineComp.get(c));
        }
    }

    @Test
    public void testRowColumnAddSpan() {
        this.gridComponent.getGrid().setRow(3);
        this.gridComponent.getGrid().setColumn(4);
        GridPartComponentI spannedComponent = this.gridComponent.getGrid().getComponent(1, 1);
        spannedComponent.expandRight();
        spannedComponent.expandRight();
        spannedComponent.expandBottom();
        //Check span
        for (int r = 1; r < this.gridComponent.getGrid().getRow(); r++) {
            for (int c = 1; c < this.gridComponent.getGrid().getColumn(); c++) {
                Assert.assertEquals(this.componentGrid.getComponent(r, c), spannedComponent);
            }
        }
        //Add row at index 1
        this.gridComponent.getGrid().addRow(1);
        //on line start, should shift components
        for (int c = 1; c < this.gridComponent.getGrid().getColumn(); c++) {
            assertNotSame(this.componentGrid.getComponent(1, c), spannedComponent);
        }
        //on next line, should still be the same component
        for (int r = 2; r < this.gridComponent.getGrid().getRow(); r++) {
            for (int c = 1; c < this.gridComponent.getGrid().getColumn(); c++) {
                assertEquals(this.componentGrid.getComponent(r, c), spannedComponent);
            }
        }
    }

}
