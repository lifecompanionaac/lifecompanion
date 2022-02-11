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
package org.lifecompanion.model.api.categorizedelement;

import org.lifecompanion.model.api.configurationcomponent.IdentifiableComponentI;
import javafx.collections.ObservableList;

/**
 * Represent common information on a main category
 *
 * @param <T> subcategory type
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface MainCategoryI<T extends SubCategoryI<? extends MainCategoryI, ?>> extends IdentifiableComponentI, CategoryElementI {
    /**
     * @return the static description of this category.
     */
    public String getStaticDescription();

    /**
     * @return the icon path of the command
     */
    public String getConfigIconPath();

    /**
     * @return list that contains every sub categories of this main category
     */
    public ObservableList<T> getSubCategories();
}
