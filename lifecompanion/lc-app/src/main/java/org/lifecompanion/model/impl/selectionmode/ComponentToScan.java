/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.model.impl.selectionmode;

import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.api.selectionmode.ComponentToScanI;
import org.lifecompanion.model.impl.configurationcomponent.GridComponentInformation;

import java.util.List;
import java.util.stream.Collectors;

public class ComponentToScan implements ComponentToScanI {
    private final int index;
    private int span;
    private final List<GridComponentInformation> components;
    //Shouldn't be used, only useful when generating component to scan
    private final List<GridPartComponentI> rawComponents;

    public ComponentToScan(final int indexP, final List<GridPartComponentI> components, final int spanP) {
        this.index = indexP;
        rawComponents = components;
        this.components = components.stream().map(GridComponentInformation::create).collect(Collectors.toList());
        this.span = spanP;
    }

    @Override
    public int getIndex() {
        return index;
    }

    @Override
    public List<GridComponentInformation> getComponents() {
        return components;
    }

    @Override
    public void increaseSpan() {
        this.span++;
    }

    @Override
    public GridPartComponentI getPartIn(GridComponentI grid, int secondaryIndex) {
        final GridComponentInformation compInfo = components.get(secondaryIndex);
        return grid.getGrid().getComponent(compInfo.getRow(), compInfo.getColumn());
    }

    @Override
    public int getSpan() {
        return span;
    }

    @Override
    public boolean containsAllComponents(List<GridPartComponentI> components) {
        if (getComponents().size() == components.size()) {
            return rawComponents.equals(components);
        }
        return false;
    }
}
