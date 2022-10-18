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

package org.lifecompanion.plugin.spellgame.ui.useaction;

import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.model.useaction.StartSpellGameAction;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameWordListListCell;

public class StartSpellGameActionConfigView extends HBox implements UseActionConfigurationViewI<StartSpellGameAction> {

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<StartSpellGameAction> getConfiguredActionType() {
        return StartSpellGameAction.class;
    }

    private ComboBox<SpellGameWordList> comboBoxWordList;

    @Override
    public void initUI() {
        this.comboBoxWordList = new ComboBox<>();
        this.comboBoxWordList.setCellFactory(lv -> new SpellGameWordListListCell());
        this.comboBoxWordList.setButtonCell(new SpellGameWordListListCell());
        comboBoxWordList.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(comboBoxWordList, Priority.ALWAYS);
        Label labelField = new Label(Translation.getText("spellgame.plugin.config.field.word.list.to.start"));
        labelField.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelField, Priority.ALWAYS);
        this.setSpacing(10.0);
        this.getChildren().addAll(labelField, this.comboBoxWordList);
    }

    @Override
    public void editStarts(final StartSpellGameAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        SpellGamePluginProperties spellGamePluginProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
        ObservableList<SpellGameWordList> wordLists = spellGamePluginProperties.getWordLists();
        this.comboBoxWordList.setItems(wordLists);
        SpellGameWordList toSelect = spellGamePluginProperties.getWordListById(element.getWordListId());
        if (toSelect != null) {
            this.comboBoxWordList.valueProperty().set(toSelect);
        }
    }

    @Override
    public void editEnds(final StartSpellGameAction element) {
        SpellGameWordList selectedItem = this.comboBoxWordList.getSelectionModel().getSelectedItem();
        element.updateSelectedWordList(selectedItem);
        this.comboBoxWordList.setItems(null);
    }

    @Override
    public void editCancelled(StartSpellGameAction element) {
        this.comboBoxWordList.setItems(null);
    }
}
