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
package org.lifecompanion.ui.app.main.ribbon.available;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.ui.app.main.ribbon.AbstractRibbonTabContent;
import org.lifecompanion.ui.app.main.ribbon.available.create.AddGridRibbonPart;
import org.lifecompanion.ui.app.main.ribbon.available.create.AddRootRibbonPart;
import org.lifecompanion.ui.app.main.ribbon.available.create.ChangeKeyRibbonPart;
import org.lifecompanion.ui.app.main.ribbon.available.global.*;
import org.lifecompanion.ui.app.main.ribbon.available.withselection.GridLayoutRibbonPart;

public class RibbonTabCreate extends AbstractRibbonTabContent {

    /**
     * Create a new ribbon tab for home tab
     */
    public RibbonTabCreate() {
        super(//
                RibbonTabPart.create(AddRootRibbonPart.class),//
                RibbonTabPart.create(AddGridRibbonPart.class),//
                RibbonTabPart.create(ChangeKeyRibbonPart.class)//
        );
        this.tabTitle.set(Translation.getText("tabs.title.create"));
    }
}
