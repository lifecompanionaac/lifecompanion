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
package org.lifecompanion.plugin.email.actions.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.categorizedelement.useaction.*;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum EmailActionMainCategory implements UseActionMainCategoryI {
    INSTANCE;
    private static final String ID = "EMAIL_MAIN_CATEGORY";

    private ObservableList<UseActionSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("email.plugin.use.action.main.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("email.plugin.use.action.main.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_main_category_emails.png";
    }

    @Override
    public Color getColor() {
        return Color.FORESTGREEN;
    }

    @Override
    public ObservableList<UseActionSubCategoryI> getSubCategories() {
        return this.subCategories;
    }

    @Override
    public int order() {
        return 1000;//at the end
    }

    @Override
    public String getID() {
        return EmailActionMainCategory.ID;
    }

    @Override
    public String generateID() {
        return EmailActionMainCategory.ID;
    }

}
