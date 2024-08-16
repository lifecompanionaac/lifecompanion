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

package org.lifecompanion.plugin.caaai.ui.useaction;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.plugin.caaai.CAAAIPlugin;
import org.lifecompanion.plugin.caaai.CAAAIPluginProperties;
import org.lifecompanion.plugin.caaai.model.useaction.ChangeSuggestedSentencesAction;

public class ChangeSuggestedSentencesActionConfigView extends VBox implements UseActionConfigurationViewI<ChangeSuggestedSentencesAction> {

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<ChangeSuggestedSentencesAction> getConfiguredActionType() {
        return ChangeSuggestedSentencesAction.class;
    }

    @Override
    public void initUI() {
        // TODO : create UI
        this.getChildren().add(new Label("DEMO"));
    }

    @Override
    public void editStarts(final ChangeSuggestedSentencesAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        // If needed ?
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        CAAAIPluginProperties CAAAIPluginProperties = configuration.getPluginConfigProperties(CAAAIPlugin.ID, CAAAIPluginProperties.class);

        // TODO : model to view
    }

    @Override
    public void editEnds(final ChangeSuggestedSentencesAction element) {
        // TODO : view to model
    }

    @Override
    public void editCancelled(ChangeSuggestedSentencesAction element) {
        // Clear if needed
    }
}
