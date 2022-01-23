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

package org.lifecompanion.config.view.pane.general.view.simplercomp;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequencesI;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.config.view.pane.general.view.simplercomp.useractionsequence.UserActionSequencesEditionView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class UserActionSequenceMainConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;
    private UserActionSequencesEditionView userActionSequencesEditionView;

    public UserActionSequenceMainConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.user.action.sequence.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.SEQUENCE_LIST_NODE.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    // UI
    //========================================================================
    @Override
    public void initUI() {
        userActionSequencesEditionView = new UserActionSequencesEditionView();
        this.setCenter(userActionSequencesEditionView);
        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
    }
    //========================================================================

    private UserActionSequencesI editedSequences;

    @Override
    public void saveChanges() {
        this.model.userActionSequencesProperty().set(editedSequences);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.editedSequences = (UserActionSequencesI) model.userActionSequencesProperty().get().duplicate(false);
        this.userActionSequencesEditionView.setUserActionSequences(this.editedSequences.getUserActionSequences());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
        this.editedSequences = null;
        this.userActionSequencesEditionView.setUserActionSequences(null);
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        if (stepArgs != null && stepArgs.length > 0) {
            // TODO
        }
    }
}
