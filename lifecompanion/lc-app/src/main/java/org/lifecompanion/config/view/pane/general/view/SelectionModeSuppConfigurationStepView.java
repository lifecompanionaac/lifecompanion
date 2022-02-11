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

package org.lifecompanion.config.view.pane.general.view;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStepViewI;
import org.lifecompanion.base.view.reusable.GeneralConfigurationStep;
import org.lifecompanion.config.view.pane.selection.SelectionModeSuppParamView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class SelectionModeSuppConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private SelectionModeSuppParamView selectionModeSuppParamView;

    public SelectionModeSuppConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.selection.mode.supp.configuration.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.SELECTION_MODE_SUPP_CONFIGURATION.name();
    }

    @Override
    public String getMenuStepToSelect() {
        return GeneralConfigurationStep.SELECTION_MODE_MAIN.name();
    }

    @Override
    public String getPreviousStep() {
        return GeneralConfigurationStep.SELECTION_MODE_MAIN.name();
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        selectionModeSuppParamView = new SelectionModeSuppParamView();
        this.setCenter(selectionModeSuppParamView);
    }

    @Override
    public void beforeShow(Object[] stepArgs) {
        SelectionModeEnum selectionMode = (SelectionModeEnum) stepArgs[0];
        selectionModeSuppParamView.setSelectedSelectionMode(selectionMode);
    }

    @Override
    public void afterHide() {
    }

    @Override
    public void saveChanges() {
        selectionModeSuppParamView.saveChanges();
    }

    @Override
    public void cancelChanges() {
    }

    @Override
    public void bind(LCConfigurationI model) {
        selectionModeSuppParamView.modelProperty().set(model);
    }

    @Override
    public void unbind(LCConfigurationI model) {
        selectionModeSuppParamView.modelProperty().set(null);
    }


}
