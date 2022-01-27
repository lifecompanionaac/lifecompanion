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
package org.lifecompanion.config.view.pane.profilconfig;

import javafx.scene.Scene;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.base.data.control.stats.SessionStatsController;
import org.lifecompanion.base.view.reusable.AnimatedBorderPane;
import org.lifecompanion.config.data.component.profile.ProfileConfigSelectionController;
import org.lifecompanion.config.data.component.profile.ProfileConfigStep;
import org.lifecompanion.config.view.common.SystemVirtualKeyboardHelper;
import org.lifecompanion.config.view.pane.configuration.ConfigAddFromDefaultView;
import org.lifecompanion.config.view.pane.configuration.ConfigAddView;
import org.lifecompanion.config.view.pane.configuration.ConfigurationEditionView;
import org.lifecompanion.config.view.pane.configuration.ConfigurationSelectionView;
import org.lifecompanion.config.view.pane.profile.ProfileAddView;
import org.lifecompanion.config.view.pane.profile.ProfileEditionView;
import org.lifecompanion.config.view.pane.profile.ProfileSelectionView;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Scene that is the content of ToolStage, and that allow user to select or create a profile, select configuration, etc...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ProfileConfigSelectionScene extends Scene implements LCViewInitHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProfileConfigSelectionScene.class);

    /**
     * Border pane, center of the scene
     */
    private final AnimatedBorderPane borderPane;

    /**
     * Currently displayed step
     */
    private ProfileConfigStepViewI currentStepView;
    private final Map<ProfileConfigStep, ProfileConfigStepViewI> stepViews;

    public ProfileConfigSelectionScene() {
        super(new AnimatedBorderPane());
        this.stepViews = new HashMap<>();
        this.borderPane = (AnimatedBorderPane) getRoot();
        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        // DON'T CALL initAll() > it's loaded in background on app startup
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.borderPane.setEnableTransition(false);//Default : no transition before first display
        //Steps for profile
        this.stepViews.put(ProfileConfigStep.PROFILE_LIST, new ProfileSelectionView());
        final ProfileEditionView profileEditionView = new ProfileEditionView();
        this.stepViews.put(ProfileConfigStep.PROFILE_CREATE, profileEditionView);
        this.stepViews.put(ProfileConfigStep.PROFILE_EDIT, profileEditionView);
        this.stepViews.put(ProfileConfigStep.PROFILE_ADD, new ProfileAddView());
        //Steps for configuration
        ConfigurationEditionView configEditionView = new ConfigurationEditionView();
        this.stepViews.put(ProfileConfigStep.CONFIGURATION_LIST, new ConfigurationSelectionView());
        this.stepViews.put(ProfileConfigStep.CONFIGURATION_EDIT, configEditionView);
        this.stepViews.put(ProfileConfigStep.CONFIGURATION_CREATE, configEditionView);
        this.stepViews.put(ProfileConfigStep.CONFIGURATION_ADD, new ConfigAddView());
        this.stepViews.put(ProfileConfigStep.CONFIGURATION_ADD_FROM_DEFAULT, new ConfigAddFromDefaultView());
    }

    @Override
    public void initBinding() {
        ProfileConfigSelectionController.INSTANCE.currentStepProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.displayStepView(nv);
            }
        });
        //Transition
        ProfileConfigSelectionController.INSTANCE.enableViewTransition().addListener((obs, ov, nv) -> {
            this.borderPane.setEnableTransition(nv);
        });
    }

    @Override
    public void initListener() {
        SystemVirtualKeyboardHelper.INSTANCE.registerScene(this);
        SessionStatsController.INSTANCE.registerScene(this);
    }
    //========================================================================

    // Class part : "Step"
    //========================================================================
    private void displayStepView(final ProfileConfigStep step) {
        ProfileConfigStepViewI stepView = this.stepViews.get(step);
        stepView.beforeShow();
        if (stepView.getView() != this.borderPane.getCenter()) {
            this.borderPane.changeCenter(stepView.getView());
            this.currentStepView = stepView;
        }
    }

    public boolean cancelRequest() {
        return this.currentStepView.cancelRequest();
    }
    //========================================================================

}
