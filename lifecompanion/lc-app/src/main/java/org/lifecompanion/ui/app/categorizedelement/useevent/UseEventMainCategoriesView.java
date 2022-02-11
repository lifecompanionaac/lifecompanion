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
package org.lifecompanion.ui.app.categorizedelement.useevent;

import javafx.collections.ObservableList;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.controller.categorizedelement.useevent.AvailableUseEventManager;
import org.lifecompanion.ui.app.categorizedelement.AbstractMainCategoriesView;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractMainCategoryItemView;

import java.util.function.Consumer;

public class UseEventMainCategoriesView extends AbstractMainCategoriesView<UseEventMainCategoryI> {

    public UseEventMainCategoriesView(final Consumer<UseEventMainCategoryI> categorySelectionCallbackP) {
        super(categorySelectionCallbackP);
    }

    @Override
    protected AbstractMainCategoryItemView<UseEventMainCategoryI> createCell(UseEventMainCategoryI element, final Consumer<UseEventMainCategoryI> categorySelectionCallback) {
        return new UseEventMainCategoryItemView(element, categorySelectionCallback);
    }

    @Override
    protected ObservableList<UseEventMainCategoryI> getMainCategories() {
        return AvailableUseEventManager.INSTANCE.getMainCategories();
    }
}
