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
package org.lifecompanion.controller.categorizedelement.useevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.categorizedelement.CategorizedElementSearchHelper;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.controller.io.ReflectionHelper;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Provide all the existing use event generator.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum AvailableUseEventController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(AvailableUseEventController.class);

    /**
     * List that contains every event generator
     */
    private ObservableList<UseEventGeneratorI> availableEventGenerator;

    /**
     * List that contains every action main category
     */
    private ObservableList<UseEventMainCategoryI> mainCategories;

    /**
     * Private singleton constructor
     */
    private AvailableUseEventController() {
        this.availableEventGenerator = FXCollections.observableArrayList();
        this.mainCategories = FXCollections.observableArrayList();
        this.initActionListListener();
        this.createUseEvents();
        CategorizedElementSearchHelper.sortMainCategoriesAndSubCategories(mainCategories);
    }


    // Class part : "Listener"
    //========================================================================

    /**
     * Create action listener on list
     */
    private void initActionListListener() {
        this.availableEventGenerator.addListener(LCUtils.createListChangeListener(this::addUseEvent, this::removeUseEvent));
    }

    /**
     * Add the action and add the associated category when needed.
     *
     * @param useEventGenerator the event generator to add
     */
    private void addUseEvent(final UseEventGeneratorI useEventGenerator) {
        UseEventSubCategoryI subCategory = useEventGenerator.getCategory();
        UseEventMainCategoryI mainCategory = subCategory.getMainCategory();
        //Add the main category
        if (!this.mainCategories.contains(mainCategory)) {
            this.mainCategories.add(mainCategory);
        }
        //Add the event to its subcategory
        subCategory.getContent().add(useEventGenerator);
    }

    private void removeUseEvent(final UseEventGeneratorI useEventGenerator) {
        //Remove from sub category list
        useEventGenerator.getCategory().getContent().remove(useEventGenerator);
        //Remove sub category if empty
        UseEventMainCategoryI mainCategory = useEventGenerator.getCategory().getMainCategory();
        if (CollectionUtils.isEmpty(useEventGenerator.getCategory().getContent())) {
            mainCategory.getSubCategories().remove(useEventGenerator.getCategory());
        }
        //Remove main category if empty
        if (CollectionUtils.isEmpty(mainCategory.getSubCategories())) {
            this.mainCategories.remove(mainCategory);
        }
    }
    //========================================================================

    // Class part : "Initialization"
    //========================================================================

    /**
     * Add all the default available use events.<br>
     */
    private void createUseEvents() {
        List<Class<? extends UseEventGeneratorI>> implementedActions = ReflectionHelper.findImplementationsInModules(UseEventGeneratorI.class);
        for (Class<? extends UseEventGeneratorI> possibleEvent : implementedActions) {
            this.addEventType(possibleEvent);
        }
        //Added by plugin manager
        PluginController.INSTANCE.getUseEventGenerators().registerListenerAndDrainCache(this::addEventType);
    }

    private void addEventType(final Class<? extends UseEventGeneratorI> possibleEvent) {
        String className = possibleEvent.getName();
        try {
            this.LOGGER.debug("Found a subtype of UseEventGeneratorI : {}", className);
            this.availableEventGenerator.add(possibleEvent.getConstructor().newInstance());
        } catch (Exception e) {
            this.LOGGER.warn("A found use event ({}) couldn't be created", className, e);
        }
    }
    //========================================================================

    // Class part : "Help method"
    //========================================================================
    public ObservableList<UseEventGeneratorI> getAvailableUseEvent() {
        return this.availableEventGenerator;
    }

    public ObservableList<UseEventMainCategoryI> getMainCategories() {
        return this.mainCategories;
    }

    public UseEventGeneratorI createUseEvent(final UseEventGeneratorI action) {
        try {
            return action.getClass().getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            this.LOGGER.error("Can't create use event new instance", e);
        }
        return null;
    }

    public List<UseEventGeneratorI> searchUseEvent(final String terms) {
        return CategorizedElementSearchHelper.searchElement(availableEventGenerator, terms);
    }
    //========================================================================
}
