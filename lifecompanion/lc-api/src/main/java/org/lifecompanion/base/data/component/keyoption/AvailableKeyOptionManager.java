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
package org.lifecompanion.base.data.component.keyoption;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.keyoption.KeyOptionI;
import org.lifecompanion.base.data.component.keyoption.simplercomp.KeyListNodeKeyOption;
import org.lifecompanion.base.data.component.keyoption.note.NoteKeyOption;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceCurrentKeyOption;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceItemKeyOption;
import org.lifecompanion.base.data.plugins.PluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

public enum AvailableKeyOptionManager {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(AvailableKeyOptionManager.class);

    private ObservableList<KeyOptionI> keyOptions;

    AvailableKeyOptionManager() {
        this.keyOptions = FXCollections.observableArrayList();
        this.initOptions();
    }

    // Class part : "Public"
    //========================================================================
    public ObservableList<KeyOptionI> getKeyOptions() {
        return this.keyOptions;
    }

    public KeyOptionI getKeyOptionFor(final KeyOptionI selected) {
        if (selected != null) {
            for (KeyOptionI keyOptionI : this.keyOptions) {
                if (keyOptionI.getClass().equals(selected.getClass())) {
                    return keyOptionI;
                }
            }
        }
        return null;
    }
    //========================================================================

    // Class part : "Init"
    //========================================================================
    private void initOptions() {
        this.keyOptions.addAll(
                Arrays.asList(
                        new BasicKeyOption(),
                        new QuickComKeyOption(),
                        new WordPredictionKeyOption(),
                        new AutoCharKeyOption(),
                        new KeyListNodeKeyOption(),
                        new CustomCharKeyOption(),
                        new NoteKeyOption(),
                        new UserActionSequenceItemKeyOption(),
                        new UserActionSequenceCurrentKeyOption(),
                        new ProgressDisplayKeyOption(),
                        new VariableInformationKeyOption())
        );
        Consumer<Class<? extends KeyOptionI>> addConsumer = this::addKeyOptionType;
        PluginManager.INSTANCE.getKeyOptions().registerListenerAndDrainCache(addConsumer);
    }

    private void addKeyOptionType(final Class<? extends KeyOptionI> possibleKeyOption) {
        String className = possibleKeyOption.getName();
        try {
            KeyOptionI newInstance = possibleKeyOption.getConstructor().newInstance();
            this.LOGGER.debug("Found a subtype of KeyOptionI : {}", className);
            this.keyOptions.add(newInstance);
        } catch (Throwable e) {
            LOGGER.warn("A found keyoption ({}) couldn't be created", className, e);
        }
    }
    //========================================================================
}
