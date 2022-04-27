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
import org.lifecompanion.controller.plugin.PluginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public enum AddComponentProvider {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(AddComponentProvider.class);

    private final Map<AddComponentCategoryEnum, ObservableList<AddComponentI>> available;

    AddComponentProvider() {
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
        addComp(new AddComponents.AddGridInStackCopy());
        addComp(new AddComponents.AddUserModelGridInStack());
        addComp(new AddComponents.ChangeKeyToStack());
        addComp(new AddComponents.ChangeKeyToGrid());
        addComp(new AddComponents.ChangeKeyToTextEditor());
        addComp(new AddComponents.AddUserModelKey());

        //Plugin FIXME
//        PluginController.INSTANCE.getPossibleAddComponents().registerListenerAndDrainCache(addCompType -> {
//            try {
//                addPossibleComp(addCompType.getConstructor().newInstance());
//            } catch (Exception e) {
//                LOGGER.error("Can't create possible add component {} from plugin", addCompType, e);
//            }
//        });
    }
}
