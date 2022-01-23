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

package org.lifecompanion.config.data.eventaction;

import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.eventaction.CategorizedElementI;
import org.lifecompanion.api.component.definition.eventaction.CategoryElementI;
import org.lifecompanion.api.component.definition.eventaction.MainCategoryI;
import org.lifecompanion.api.component.definition.eventaction.SubCategoryI;
import org.lifecompanion.api.component.definition.useaction.BaseUseActionI;
import org.lifecompanion.api.component.definition.useaction.UseActionMainCategoryI;
import org.lifecompanion.api.component.definition.useaction.UseActionSubCategoryI;
import org.lifecompanion.api.component.definition.useaction.UseActionTriggerComponentI;
import org.lifecompanion.api.component.definition.useevent.UseEventMainCategoryI;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class EventActionManagerHelper {

    public static <T extends SubCategoryI<?, ?>> void sortMainCategoriesAndSubCategories(ObservableList<? extends MainCategoryI<? extends T>> mainCategories) {
        if (!AppController.INSTANCE.isUseModeOnly()) {
            Collections.sort(mainCategories, Comparator.comparingInt(CategoryElementI::order));
            for (MainCategoryI<? extends T> category : mainCategories) {
                Collections.sort(category.getSubCategories(), Comparator.comparingInt(CategoryElementI::order));
                for (T subCategory : category.getSubCategories()) {
                    Collections.sort(subCategory.getContent(), Comparator.comparingInt(CategorizedElementI::order));
                }
            }
        }
    }

    public static <T extends CategorizedElementI<?>> List<T> searchElement(ObservableList<T> allElements, final String terms) {
        if (terms == null || terms.length() < 3) {
            return Arrays.asList();
        } else {
            String[] termArray = terms.split(" ");
            List<String> termList = Arrays.stream(termArray).filter(s -> s.length() > 2).collect(Collectors.toList());

            Map<T, Integer> scores = new HashMap<>();
            //On name
            for (T useAction : allElements) {
                int value = 0;
                value += StringUtils.countContainsIgnoreCase(useAction.getName(), termList) * 2;
                value += StringUtils.countContainsIgnoreCase(useAction.getStaticDescription(), termList);
                scores.put(useAction, value);
            }
            //Generate result
            return scores.entrySet().stream().filter(e -> e.getValue() > 0)
                    .sorted(Map.Entry.<T, Integer>comparingByValue().reversed()).limit(6).map(e -> e.getKey())
                    .collect(Collectors.toList());
        }
    }
}
