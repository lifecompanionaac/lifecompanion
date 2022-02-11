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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.controller.categorizedelement.useaction.AvailableUseActionManager;
import org.lifecompanion.ui.app.categorizedelement.AbstractMainCategoriesView;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractMainCategoryItemView;

import java.util.function.Consumer;

/**
 * View to display use action main categories.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UseActionMainCategoriesView extends AbstractMainCategoriesView<UseActionMainCategoryI> {

    public UseActionMainCategoriesView(final Consumer<UseActionMainCategoryI> categorySelectionCallbackP) {
        super(categorySelectionCallbackP);
    }

    @Override
    protected AbstractMainCategoryItemView<UseActionMainCategoryI> createCell(UseActionMainCategoryI element, final Consumer<UseActionMainCategoryI> categorySelectionCallback) {
        return new UseActionMainCategoryItemView(element, categorySelectionCallback);
    }

    @Override
    protected ObservableList<UseActionMainCategoryI> getMainCategories() {
        return AvailableUseActionManager.INSTANCE.getMainCategories();
    }
}
