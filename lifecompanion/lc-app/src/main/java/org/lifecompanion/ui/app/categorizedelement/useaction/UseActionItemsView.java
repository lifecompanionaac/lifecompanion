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
package org.lifecompanion.ui.app.categorizedelement.useaction;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedItemsView;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractCategorizedItemView;

import java.util.function.Consumer;

/**
 * Grid view to display and select action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionItemsView extends AbstractCategorizedItemsView<BaseUseActionI<?>> {

    public UseActionItemsView(final ObservableList<BaseUseActionI<?>> itemsP, final Consumer<BaseUseActionI<?>> selectionCallbackP) {
        super(itemsP, selectionCallbackP);
    }

    @Override
    protected AbstractCategorizedItemView<BaseUseActionI<?>> createCell(BaseUseActionI<?> element, final Consumer<BaseUseActionI<?>> selectionCallback) {
        return new UseActionItemView(element, selectionCallback);
    }

}
