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

import org.junit.jupiter.api.Test;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Should be moved in individual component tests ? > each component type check its parent binding...
// still better here than nothing...
public class ParentBindingTest {

    @Test
    public void testParentBindingGridPart() {
        //Grid 2 x 2
        GridPartGridComponent gridComponent = new GridPartGridComponent();
        gridComponent.getGrid().setColumn(2);
        gridComponent.getGrid().setRow(2);
        //Get a part
        GridPartComponentI gridPart = gridComponent.getGrid().getComponent(0, 0);

        //Check parent simple properties
        assertEquals(true, gridPart.isParentExist());
        assertEquals(gridComponent, gridPart.gridParentProperty().get());

        //Set configuration parent
        LCConfigurationComponent config = new LCConfigurationComponent();
        //Set stack root parent and grid
        StackComponent stackComponent = new StackComponent();
        stackComponent.getComponentList().add(gridComponent);
        config.getChildren().add(stackComponent);

        //Test configuration parent
        assertEquals(config, gridComponent.configurationParentProperty().get());
        assertEquals(config, gridPart.configurationParentProperty().get());

        //Check on grid
        assertEquals(stackComponent, gridComponent.stackParentProperty().get());
        assertEquals(true, gridComponent.lastStackChildProperty().get());
        assertEquals(stackComponent, gridComponent.rootParentProperty().get());
        assertEquals(stackComponent, gridComponent.stackParentProperty().get());
        assertEquals(true, gridComponent.displayedProperty().get());//First grid is displayed
        assertEquals(true, stackComponent.isDirectStackChild(gridComponent));
        //Check on part
        assertEquals(stackComponent, gridPart.stackParentProperty().get());
        assertEquals(stackComponent, gridPart.rootParentProperty().get());
        assertEquals(true, gridPart.displayedProperty().get());

        //Now detach from configuration
        gridComponent.getGrid().removeComponent(0, 0);
        assertEquals(true, gridPart.removedProperty().get());
        assertEquals(null, gridPart.stackParentProperty().get());
        assertEquals(null, gridPart.rootParentProperty().get());
        assertEquals(null, gridPart.configurationParentProperty().get());
        assertEquals(false, gridPart.displayedProperty().get());
    }

    @Test
    public void testAllComponentBindingSimpleRemove() {
        //Config to test
        LCConfigurationComponent config = new LCConfigurationComponent();

        //Add stack
        StackComponent stackComponent = new StackComponent();
        config.getChildren().add(stackComponent);
        //Add grid to stack
        GridPartGridComponent gridComponent = new GridPartGridComponent();
        gridComponent.getGrid().setRow(2);
        gridComponent.getGrid().setColumn(2);
        stackComponent.getComponentList().add(gridComponent);

        //Check all components contains bases
        assertTrue(config.getAllComponent().containsKey(config.getID()));
        assertTrue(config.getAllComponent().containsKey(stackComponent.getID()));
        assertTrue(config.getAllComponent().containsKey(gridComponent.getID()));
        //Check contains every keys
        for (int c = 0; c < gridComponent.getGrid().getColumn(); c++) {
            for (int r = 0; r < gridComponent.getGrid().getRow(); r++) {
                GridPartComponentI comp = gridComponent.getGrid().getComponent(r, c);
                assertTrue(config.getAllComponent().containsKey(comp.getID()));
            }
        }

        //Now the root component
        config.getChildren().remove(stackComponent);

        //Should not contains any component expect the configuration itself
        assertEquals(1, config.getAllComponent().size());
        assertEquals(true, config.getAllComponent().containsKey(config.getID()));
    }
}