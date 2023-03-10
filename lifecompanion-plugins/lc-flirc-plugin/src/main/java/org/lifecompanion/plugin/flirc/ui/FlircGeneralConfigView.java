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

package org.lifecompanion.plugin.flirc.ui;

import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;

public class FlircGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "FlircGeneralConfigView";

    public FlircGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "todo";
    }

    @Override
    public String getStep() {
        return STEP_ID;
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
    }

    @Override
    public void initListener() {

    }

    @Override
    public void initBinding() {

    }

    @Override
    public void saveChanges() {

    }

    @Override
    public void bind(LCConfigurationI model) {

    }

    @Override
    public void unbind(LCConfigurationI model) {
    }
}
