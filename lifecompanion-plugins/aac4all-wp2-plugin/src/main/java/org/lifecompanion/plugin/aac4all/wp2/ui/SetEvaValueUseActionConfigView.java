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

package org.lifecompanion.plugin.aac4all.wp2.ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.aac4all.wp2.model.useaction.EvaCategoryType;
import org.lifecompanion.plugin.aac4all.wp2.model.useaction.EvaScoreType;
import org.lifecompanion.plugin.aac4all.wp2.model.useaction.SetEvaValueUseAction;
import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;

import java.util.Arrays;

public class SetEvaValueUseActionConfigView extends VBox implements UseActionConfigurationViewI<SetEvaValueUseAction> {
    private ComboBox<EvaCategoryType> comboBoxEvaCategoryType;
    private ComboBox<EvaScoreType> comboBoxEvaScoreType;

    public SetEvaValueUseActionConfigView() {
    }


    public void initUI() {
        this.setSpacing(4.0);
        this.setPadding(new Insets(10.0));

        this.comboBoxEvaScoreType = new ComboBox<>(FXCollections.observableList(Arrays.stream(EvaScoreType.values()).toList()));
        this.getChildren().addAll(new Label(Translation.getText("use.action.eva.score")), this.comboBoxEvaScoreType);

        this.comboBoxEvaCategoryType = new ComboBox<>(FXCollections.observableList(Arrays.stream(EvaCategoryType.values()).toList()));
        this.getChildren().addAll(new Label(Translation.getText("use.action.eva.categorie")), this.comboBoxEvaCategoryType);


    }


    public Region getConfigurationView() {
        return this;
    }

    public void editStarts(final SetEvaValueUseAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.comboBoxEvaScoreType.setValue(action.getEvaScoreType());
        this.comboBoxEvaCategoryType.setValue(action.getEvaCategoryType());
    }

    public void editEnds(final SetEvaValueUseAction action) {
        action.evaScoreTypeProperty().set(this.comboBoxEvaScoreType.getValue());
        action.evaCategoryTypeProperty().set(this.comboBoxEvaCategoryType.getValue());

    }

    @Override
    public Class<SetEvaValueUseAction> getConfiguredActionType() {
        return SetEvaValueUseAction.class;
    }

}
