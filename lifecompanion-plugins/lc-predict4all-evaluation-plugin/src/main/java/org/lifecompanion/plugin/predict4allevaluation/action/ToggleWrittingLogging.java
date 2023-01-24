/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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
package org.lifecompanion.plugin.predict4allevaluation.action;

import java.util.Map;
import java.util.Optional;

import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.lifecycle.UseModeContext;
import org.lifecompanion.plugin.predict4allevaluation.action.categories.Predict4AllActionSubCategories;
import org.lifecompanion.plugin.predict4allevaluation.clinicalstudy.ClinicalStudyTestInformationDialog;
import org.lifecompanion.plugin.predict4allevaluation.clinicalstudy.ClinicalStudyTestInformationDto;
import org.lifecompanion.plugin.predict4allevaluation.clinicalstudy.Predict4AllClinicalStudyManager;
import javafx.application.Platform;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;

public class ToggleWrittingLogging extends SimpleUseActionImpl<UseActionTriggerComponentI> {

    public ToggleWrittingLogging() {
        super(UseActionTriggerComponentI.class);
        this.nameID = "predict4all.action.toggle.clinical.study.name";
        this.staticDescriptionID = "predict4all.action.toggle.clinical.study.description";
        this.category = Predict4AllActionSubCategories.CLINICAL_STUDY;
        this.order = 10;
        this.parameterizableAction = false;
        this.variableDescriptionProperty().set(this.getStaticDescription());
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/icon_p4a.png";
    }

    @Override
    public void execute(final UseActionEvent event, final Map<String, UseVariableI<?>> variables) {
        if (Predict4AllClinicalStudyManager.INSTANCE.isLogging()) {
            Predict4AllClinicalStudyManager.INSTANCE.stopLogging();
        } else {
            Platform.runLater(() -> {
                ClinicalStudyTestInformationDialog infoDialog = new ClinicalStudyTestInformationDialog();
                infoDialog.initOwner(AppModeController.INSTANCE.getUseModeContext().getStage());
                Optional<ClinicalStudyTestInformationDto> resp = infoDialog.showAndWait();
                if (resp.isPresent()) {
                    Predict4AllClinicalStudyManager.INSTANCE.startLoging(resp.get());
                }
            });
        }
    }

}
