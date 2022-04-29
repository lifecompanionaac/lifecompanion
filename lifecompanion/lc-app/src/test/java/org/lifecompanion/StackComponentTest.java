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

import org.junit.jupiter.api.Test;
import org.lifecompanion.model.impl.configurationcomponent.GridPartGridComponent;
import org.lifecompanion.model.impl.configurationcomponent.LCConfigurationComponent;
import org.lifecompanion.model.impl.configurationcomponent.StackComponent;

import static org.junit.jupiter.api.Assertions.*;

public class StackComponentTest {

    private final StackComponent stackComponent = new StackComponent();

    @Test
    void testAdd() {
        LCConfigurationComponent parent = new LCConfigurationComponent();
        parent.getChildren().add(stackComponent);

        GridPartGridComponent added = new GridPartGridComponent();
        stackComponent.getComponentList().add(added);

        assertEquals(stackComponent, added.stackParentProperty().get());
        assertEquals(parent, added.configurationParentProperty().get());
        assertTrue(added.lastStackChildProperty().get());
        assertFalse(stackComponent.nextPossibleProperty().get());
        assertFalse(stackComponent.previousPossibleProperty().get());
    }

    void testSwap() {
        // TODO
    }

}
