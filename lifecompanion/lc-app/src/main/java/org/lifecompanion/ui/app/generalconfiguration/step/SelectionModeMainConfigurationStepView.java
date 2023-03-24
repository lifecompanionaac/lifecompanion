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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.common.pane.specific.selectionmode.SelectionModeMainParamView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class SelectionModeMainConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private SelectionModeMainParamView selectionModeMainParamView;

    public SelectionModeMainConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.selection.mode.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.SELECTION_MODE_MAIN.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        selectionModeMainParamView = new SelectionModeMainParamView(
                e -> GeneralConfigurationController.INSTANCE.showStep(GeneralConfigurationStep.SELECTION_MODE_SUPP_CONFIGURATION, selectionModeMainParamView.getSelectedSelectionMode())
        );
        this.setCenter(selectionModeMainParamView);
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
    }

    @Override
    public void afterHide() {
    }


    @Override
    public void saveChanges() {
        selectionModeMainParamView.saveChanges();
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {
        selectionModeMainParamView.modelProperty().set(model);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        selectionModeMainParamView.modelProperty().set(null);
    }

    public SelectionModeMainParamView getSelectionModeMainParamView() {
        return selectionModeMainParamView;
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        return selectionModeMainParamView.isDirty();
    }
}
