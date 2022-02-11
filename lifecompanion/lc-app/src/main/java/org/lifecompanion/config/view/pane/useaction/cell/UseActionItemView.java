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
package org.lifecompanion.config.view.pane.useaction.cell;

import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.config.data.eventaction.AvailableUseActionManager;
import org.lifecompanion.config.view.pane.categorized.cell.AbstractCategorizedItemView;

import java.util.function.Consumer;

/**
 * Grid cell to display a use action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionItemView extends AbstractCategorizedItemView<BaseUseActionI<?>> {

    public UseActionItemView(BaseUseActionI<?> item, final Consumer<BaseUseActionI<?>> selectionCallbackP) {
        super(item, selectionCallbackP, AvailableUseActionManager.INSTANCE::createNewAction);
    }
}
