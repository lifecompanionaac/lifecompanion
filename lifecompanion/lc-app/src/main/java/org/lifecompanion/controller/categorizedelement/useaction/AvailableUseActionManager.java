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
package org.lifecompanion.controller.categorizedelement.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.categorizedelement.CategorizedElementSearchHelper;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.io.ReflectionHelper;
import org.lifecompanion.controller.plugin.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Class that provide useful method to list every available use action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum AvailableUseActionManager {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(AvailableUseActionManager.class);

    /**
     * List that contains every action
     */
    private ObservableList<BaseUseActionI<? extends UseActionTriggerComponentI>> availableAction;

    /**
     * List that contains every action main category
     */
    private ObservableList<UseActionMainCategoryI> mainCategories;

    /**
     * Private singleton constructor
     */
    AvailableUseActionManager() {
        this.availableAction = FXCollections.observableArrayList();
        this.mainCategories = FXCollections.observableArrayList();
        this.initActionListListener();
        this.createActions();
        CategorizedElementSearchHelper.sortMainCategoriesAndSubCategories(mainCategories);
    }


    // Class part : "Listener"
    //========================================================================

    /**
     * Create action listener on list
     */
    private void initActionListListener() {
        this.availableAction.addListener(LCUtils.createListChangeListener(this::addAction, null));
        PluginManager.INSTANCE.getUseActions().registerListenerAndDrainCache(this::addActionType);
    }

    /**
     * Add the action and add the associated category when needed.
     *
     * @param action the action to add
     */
    private void addAction(final BaseUseActionI<? extends UseActionTriggerComponentI> action) {
        UseActionSubCategoryI subCategory = action.getCategory();
        UseActionMainCategoryI mainCategory = subCategory.getMainCategory();
        //Add the main category
        if (!this.mainCategories.contains(mainCategory)) {
            this.mainCategories.add(mainCategory);
        }
        //Add the action to its subcategory
        subCategory.getContent().add(action);
    }
    //========================================================================

    // Class part : "Initialization"
    //========================================================================

    /**
     * Add all the default available actions.<br>
     */
    @SuppressWarnings({"rawtypes"})
    private void createActions() {
        List<Class<? extends BaseUseActionI>> implementedActions = ReflectionHelper.findImplementationsInModules(BaseUseActionI.class);
        for (Class<? extends BaseUseActionI> possibleAction : implementedActions) {
            this.addActionType(possibleAction);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void addActionType(final Class<? extends BaseUseActionI> possibleAction) {
        String className = possibleAction.getName();
        try {
            this.availableAction.add(possibleAction.getConstructor().newInstance());
        } catch (Exception e) {
            this.LOGGER.warn("A found use action ({}) couldn't be created", className, e);
        }
    }
    //========================================================================

    // Class part : "Help method"
    //========================================================================

    public ObservableList<BaseUseActionI<? extends UseActionTriggerComponentI>> getAvailableAction() {
        return this.availableAction;
    }

    public ObservableList<UseActionMainCategoryI> getMainCategories() {
        return this.mainCategories;
    }

    public BaseUseActionI<?> createNewAction(final BaseUseActionI<?> action) {
        try {
            return action.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            this.LOGGER.error("Can't create action new instance", e);
        }
        return null;
    }

    public List<BaseUseActionI<?>> searchAction(final String terms) {
        return CategorizedElementSearchHelper.searchElement(availableAction, terms);
    }
    //========================================================================

}
