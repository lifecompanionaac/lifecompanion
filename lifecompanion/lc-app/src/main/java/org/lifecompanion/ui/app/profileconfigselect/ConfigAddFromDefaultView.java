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
package org.lifecompanion.ui.app.profileconfigselect;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.lifecompanion.ui.common.pane.specific.DefaultConfigurationListPane;
import org.lifecompanion.util.model.Triple;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.util.ConfigUIUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class ConfigAddFromDefaultView extends BorderPane implements LCViewInitHelper, ProfileConfigStepViewI {
    private DefaultConfigurationListPane defaultConfigurationListPane;

    public ConfigAddFromDefaultView() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        Triple<HBox, Label, Node> header = ConfigUIUtils.createHeader("config.add.from.default.title.title", e -> ProfileConfigSelectionController.INSTANCE.setConfigStep(ProfileConfigStep.CONFIGURATION_ADD, ProfileConfigStep.PROFILE_LIST, null));
        defaultConfigurationListPane = new DefaultConfigurationListPane(false);
        defaultConfigurationListPane.setPadding(new Insets(10.0));
        this.setTop(header.getLeft());
        this.setCenter(defaultConfigurationListPane);
    }

    @Override
    public void initListener() {
        defaultConfigurationListPane.setOnConfigurationSelected(configAndDir -> {
            ProfileConfigSelectionController.INSTANCE.hideStage();
            ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.AddNewEditFromDefaultAction(defaultConfigurationListPane, configAndDir));
        });
    }
    //========================================================================

    // Class part : "Profile step"
    //========================================================================
    @Override
    public void beforeShow() {
        this.defaultConfigurationListPane.initDefaultConfigurations();
    }

    @Override
    public boolean cancelRequest() {
        return false;
    }


    @Override
    public Node getView() {
        return this;
    }
    //========================================================================

}
