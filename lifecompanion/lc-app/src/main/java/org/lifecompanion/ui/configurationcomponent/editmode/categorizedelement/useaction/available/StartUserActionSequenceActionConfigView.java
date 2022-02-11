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
package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useaction.available;

import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.StartUserActionSequenceAction;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedListManageView;
import org.lifecompanion.ui.common.control.specific.selector.UserActionSequenceSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;

public class StartUserActionSequenceActionConfigView extends VBox implements UseActionConfigurationViewI<StartUserActionSequenceAction> {

    private UserActionSequenceSelectorControl userActionSequenceSelectorControl;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<StartUserActionSequenceAction> getConfiguredActionType() {
        return StartUserActionSequenceAction.class;
    }

    @Override
    public void initUI() {
        userActionSequenceSelectorControl = new UserActionSequenceSelectorControl(Translation.getText("user.action.sequence.start.field.sequence"));
        userActionSequenceSelectorControl.setMaxWidth(AbstractCategorizedListManageView.STAGE_WIDTH - 20.0);
        this.getChildren().add(userActionSequenceSelectorControl);
    }

    @Override
    public void editStarts(final StartUserActionSequenceAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        this.userActionSequenceSelectorControl.setInputUserActionSequences(configuration.userActionSequencesProperty().get());
        this.userActionSequenceSelectorControl.selectedSequenceId().set(element.sequenceToStartIdProperty().get());
    }

    @Override
    public void editEnds(final StartUserActionSequenceAction element) {
        element.sequenceToStartIdProperty().set(this.userActionSequenceSelectorControl.selectedSequenceId().get());
        this.userActionSequenceSelectorControl.setInputUserActionSequences(null);
    }
}
