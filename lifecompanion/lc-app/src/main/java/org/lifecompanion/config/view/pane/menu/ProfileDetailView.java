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
package org.lifecompanion.config.view.pane.menu;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.base.data.control.refacto.ProfileController;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.view.pane.profile.ProfileIconView;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.view.common.SystemVirtualKeyboardHelper;
import org.lifecompanion.config.view.pane.config.UserConfigStage;
import org.lifecompanion.config.view.pane.config.UserConfigurationView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

/**
 * View to display the current profile detail, and to allow user to change profile
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileDetailView extends HBox implements LCViewInitHelper {
    private Label labelProfileName;
    private Hyperlink linkManagerProfiles;
    private Hyperlink linkLifeCompanionPreferences;
    private Hyperlink linkShowConfigTips;
    private ProfileIconView profileIconView;
    private UserConfigurationView userConfigurationView;

    public ProfileDetailView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Style
        this.getStyleClass().addAll("main-menu-section", "main-menu-section-top");
        //Children
        this.labelProfileName = new Label();
        this.labelProfileName.getStyleClass().add("import-blue-title");
        this.linkManagerProfiles = new Hyperlink(Translation.getText("profile.menu.link.manage.profiles"));
        linkManagerProfiles.setTooltip(UIUtils.createTooltip(Translation.getText("tooltip.explain.manage.profiles")));
        this.linkLifeCompanionPreferences = new Hyperlink(Translation.getText("profile.menu.link.lifecompanion.preferences"));
        linkLifeCompanionPreferences.setTooltip(UIUtils.createTooltip(Translation.getText("tooltip.explain.open.user.preferences")));
        this.linkShowConfigTips = new Hyperlink(Translation.getText("profile.menu.link.show.config.tips"));
        linkShowConfigTips.setTooltip(UIUtils.createTooltip(Translation.getText("tooltip.explain.profile.menu.link.show.config.tips")));
        this.profileIconView = new ProfileIconView();
        //Box for text and actions
        VBox textActionBox = new VBox();
        textActionBox.getChildren().addAll(this.labelProfileName, this.linkManagerProfiles, this.linkLifeCompanionPreferences/*, linkShowConfigTips*/);
        //Total box
        HBox.setHgrow(textActionBox, Priority.ALWAYS);
        HBox.setMargin(this.profileIconView, new Insets(8));
        HBox.setMargin(textActionBox, new Insets(8));
        this.getChildren().addAll(textActionBox, this.profileIconView);
    }

    @Override
    public void initListener() {
        this.linkManagerProfiles.setOnAction((ea) -> {
            ProfileConfigSelectionController.INSTANCE.setProfileStep(ProfileConfigStep.PROFILE_LIST, null, null);
        });
        this.linkLifeCompanionPreferences.setOnAction((ea) -> {
            this.initUserConfigurationView();
            this.userConfigurationView.showView();
        });
    }

    private void initUserConfigurationView() {
        //Init if needed
        if (this.userConfigurationView == null) {
            Stage userConfigStage = new UserConfigStage(AppModeController.INSTANCE.getEditModeContext().getStage());
            userConfigStage.centerOnScreen();
            this.userConfigurationView = new UserConfigurationView(userConfigStage);
            Scene settingScene = new Scene(this.userConfigurationView);
            SystemVirtualKeyboardHelper.INSTANCE.registerScene(settingScene);
            SessionStatsController.INSTANCE.registerScene(settingScene);
            userConfigStage.setScene(settingScene);
        }
    }

    @Override
    public void initBinding() {
        this.labelProfileName.textProperty().bind(EasyBind.select(ProfileController.INSTANCE.currentProfileProperty()).selectObject(LCProfileI::nameProperty));
        this.profileIconView.profileProperty().bind(ProfileController.INSTANCE.currentProfileProperty());
    }
}
