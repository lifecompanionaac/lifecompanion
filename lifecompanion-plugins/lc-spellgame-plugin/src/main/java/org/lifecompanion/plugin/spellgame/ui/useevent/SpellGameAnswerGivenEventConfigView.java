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

package org.lifecompanion.plugin.spellgame.ui.useevent;

import javafx.collections.FXCollections;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.plugin.spellgame.model.AnswerTypeEnum;
import org.lifecompanion.plugin.spellgame.model.useevent.SpellGameGameAnswerGivenEventGenerator;
import org.lifecompanion.plugin.spellgame.ui.cell.AnswerTypeListCell;

import java.util.Arrays;

public class SpellGameAnswerGivenEventConfigView extends VBox implements UseEventGeneratorConfigurationViewI<SpellGameGameAnswerGivenEventGenerator> {

    private ComboBox<AnswerTypeEnum> comboBoxAnswerFilter;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SpellGameGameAnswerGivenEventGenerator> getConfiguredActionType() {
        return SpellGameGameAnswerGivenEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        Label label = new Label(Translation.getText("spellgame.plugin.config.field.event.answer.given.answer.filter"));
        label.setMaxWidth(Double.MAX_VALUE);

        comboBoxAnswerFilter = new ComboBox<>(FXCollections.observableList(Arrays.asList(AnswerTypeEnum.values())));
        comboBoxAnswerFilter.setButtonCell(new AnswerTypeListCell());
        comboBoxAnswerFilter.setCellFactory(lv -> new AnswerTypeListCell());
        comboBoxAnswerFilter.setMaxWidth(Double.MAX_VALUE);

        this.getChildren().addAll(label, comboBoxAnswerFilter);
    }

    @Override
    public void editStarts(final SpellGameGameAnswerGivenEventGenerator element) {
        comboBoxAnswerFilter.getSelectionModel().select(element.answerFilterProperty().get());
    }

    @Override
    public void editEnds(final SpellGameGameAnswerGivenEventGenerator element) {
        element.answerFilterProperty().set(comboBoxAnswerFilter.getValue());
    }
}
