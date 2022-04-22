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
package org.lifecompanion.model.impl.ui.editmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.ui.editmode.AddComponentCategoryEnum;
import org.lifecompanion.model.api.ui.editmode.AddComponentI;
import org.lifecompanion.model.api.ui.editmode.PossibleAddComponentCategoryI;
import org.lifecompanion.model.api.ui.editmode.PossibleAddComponentI;
import org.lifecompanion.controller.plugin.PluginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum AddComponentProvider {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddComponentProvider.class);


    private ObservableList<PossibleAddComponentCategoryI> categories;

    private final Map<AddComponentCategoryEnum, ObservableList<AddComponentI>> available;

    AddComponentProvider() {
        this.categories = FXCollections.observableArrayList();
        available = new HashMap<>();
        this.initComponents();
    }

    public Map<AddComponentCategoryEnum, ObservableList<AddComponentI>> getAvailable() {
        return available;
    }

    private void addComp(AddComponentI addComponent) {
        available.computeIfAbsent(addComponent.getCategory(), k -> FXCollections.observableArrayList()).add(addComponent);
    }

    private void initComponents() {
        addComp(new AddComponents.AddStack());
        addComp(new AddComponents.AddTextEditor());
        addComp(new AddComponents.AddUserModelRoot());
        addComp(new AddComponents.AddGridInStack());


        //Default component : base
        this.addPossibleComp(PossibleAddComponents.AddStack.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddTextEditor.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddGridInKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddGridInStack.INSTANCE);
        //Default component : keys
        this.addPossibleComp(PossibleAddComponents.AddBasicKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddQuickCommunicationKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddWordPredictionKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddCharPredictionKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddCharKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddNoteKey.INSTANCE);
        this.addPossibleComp(PossibleAddComponents.AddVariableInformationKey.INSTANCE);
        //Plugin
        PluginController.INSTANCE.getPossibleAddComponents().registerListenerAndDrainCache(addCompType -> {
            try {
                addPossibleComp(addCompType.getConstructor().newInstance());
            } catch (Exception e) {
                LOGGER.error("Can't create possible add component {} from plugin", addCompType, e);
            }
        });
    }

    private void addPossibleComp(final PossibleAddComponentI<?> addComp) {
        if (!this.categories.contains(addComp.getCategory())) {
            this.categories.add(addComp.getCategory());
        }
        addComp.getCategory().getPossibleAddList().add(addComp);
    }

    public ObservableList<PossibleAddComponentCategoryI> getCategories() {
        return this.categories;
    }
}
