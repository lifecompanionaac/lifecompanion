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
package org.lifecompanion.ui.app.main.mainmenu;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.fxmisc.easybind.EasyBind;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.ui.common.pane.specific.ProfileIconView;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigSelectionController;
import org.lifecompanion.controller.profileconfigselect.ProfileConfigStep;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.ui.app.userconfiguration.UserConfigStage;
import org.lifecompanion.ui.app.userconfiguration.UserConfigurationView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

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

    public ProfileDetailView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        //Style
        this.getStyleClass().addAll("main-menu-section", "main-menu-section-top");
        //Children
        this.labelProfileName = new Label();
        this.labelProfileName.getStyleClass().addAll("text-fill-primary-dark", "text-font-size-150", "padding-t10");
        this.linkManagerProfiles = new Hyperlink(Translation.getText("profile.menu.link.manage.profiles"));
        linkManagerProfiles.setTooltip(FXControlUtils.createTooltip(Translation.getText("tooltip.explain.manage.profiles")));
        this.linkLifeCompanionPreferences = new Hyperlink(Translation.getText("profile.menu.link.lifecompanion.preferences"));
        linkLifeCompanionPreferences.setTooltip(FXControlUtils.createTooltip(Translation.getText("tooltip.explain.open.user.preferences")));
        this.linkShowConfigTips = new Hyperlink(Translation.getText("profile.menu.link.show.config.tips"));
        linkShowConfigTips.setTooltip(FXControlUtils.createTooltip(Translation.getText("tooltip.explain.profile.menu.link.show.config.tips")));
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
        this.linkLifeCompanionPreferences.setOnAction((ea) -> UserConfigurationController.INSTANCE.getUserConfigurationView().showView());
    }

    @Override
    public void initBinding() {
        this.labelProfileName.textProperty().bind(EasyBind.select(ProfileController.INSTANCE.currentProfileProperty()).selectObject(LCProfileI::nameProperty));
        this.profileIconView.profileProperty().bind(ProfileController.INSTANCE.currentProfileProperty());
    }
}
