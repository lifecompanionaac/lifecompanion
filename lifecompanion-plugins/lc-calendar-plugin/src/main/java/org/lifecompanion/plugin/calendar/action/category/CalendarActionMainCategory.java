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

package org.lifecompanion.plugin.calendar.action.category;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum CalendarActionMainCategory implements UseActionMainCategoryI {
    INSTANCE;

    private static final String ID = "CALENDAR_ACTION_MAIN_CATEGORY";

    private final ObservableList<UseActionSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("calendar.plugin.calendar.action.main.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("calendar.plugin.calendar.action.main.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "calendar_main_category.png";
    }

    @Override
    public Color getColor() {
        return CategorizedElementColorProvider.nextColor(ID);
    }

    @Override
    public ObservableList<UseActionSubCategoryI> getSubCategories() {
        return this.subCategories;
    }

    @Override
    public int order() {
        return 2000;//at the end
    }

    @Override
    public String getID() {
        return CalendarActionMainCategory.ID;
    }

    @Override
    public String generateID() {
        return CalendarActionMainCategory.ID;
    }
}
