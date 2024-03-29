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

public class LCConfigurationComponentTest {
    private LCConfigurationComponent configurationComponent;

    @BeforeEach
    public void beforeEach() {
        this.configurationComponent = new LCConfigurationComponent();
    }

    @Test
    void testAddChild() {
        StackComponent stackComponent = new StackComponent();
        this.configurationComponent.getChildren().add(stackComponent);
        this.configurationComponent.dispatchDisplayedProperty(true);

        assertTrue(configurationComponent.getAllComponent().containsKey(stackComponent.getID()));
        assertEquals(stackComponent, configurationComponent.getAllComponent().get(stackComponent.getID()));
        assertEquals(configurationComponent, stackComponent.configurationParentProperty().get());
        assertFalse(stackComponent.removedProperty().get());
        assertTrue(stackComponent.displayedProperty().get());
    }
}
