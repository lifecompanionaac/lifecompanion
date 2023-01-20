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
package org.lifecompanion.plugin.email.actions.view;

import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.email.actions.WriteNewEmailAction;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;

public class WriteNewEmailActionConfigView extends VBox implements UseActionConfigurationViewI<WriteNewEmailAction> {

    private TextField fieldEmailName, fieldEmailAddress;

    public WriteNewEmailActionConfigView() {
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.fieldEmailAddress = new TextField();
        this.fieldEmailAddress.setPromptText("jean.dupont@domaine.com");
        this.fieldEmailName = new TextField();
        this.fieldEmailName.setPromptText("Jean DUPONT");
        this.getChildren().addAll(new Label(Translation.getText("email.plugin.use.action.write.new.email.field.name")), this.fieldEmailName,
                new Label(Translation.getText("email.plugin.use.action.write.new.email.field.address")), this.fieldEmailAddress);
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final WriteNewEmailAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.fieldEmailName.setText(action.emailToNameProperty().get());
        this.fieldEmailAddress.setText(action.emailToAddressProperty().get());
    }

    @Override
    public void editEnds(final WriteNewEmailAction action) {
        action.emailToNameProperty().set(fieldEmailName.getText());
        action.emailToAddressProperty().set(fieldEmailAddress.getText());
    }

    @Override
    public Class<WriteNewEmailAction> getConfiguredActionType() {
        return WriteNewEmailAction.class;
    }

}
