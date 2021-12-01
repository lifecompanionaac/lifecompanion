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
package org.lifecompanion.config.data.component.profile;

import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.lifecompanion.api.component.definition.LCConfigurationDescriptionI;
import org.lifecompanion.api.component.definition.LCProfileI;
import org.lifecompanion.base.data.control.AppController;
import org.lifecompanion.base.data.control.AsyncExecutorController;
import org.lifecompanion.base.data.io.task.LoadAvailableDefaultConfigurationTask;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.profilconfig.ProfileConfigSelectionStage;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Config manager for profile view.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ProfileConfigSelectionController {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileConfigSelectionController.class);

    /**
     * Stage to manage profile and configurations
     */
    private ProfileConfigSelectionStage profileConfigSelectionStage;

    private ProfileConfigStep previousStep;
    private final BooleanProperty enableViewTransition;
    private final ObjectProperty<ProfileConfigStep> currentStep;
    private LCProfileI profileOption;
    private LCConfigurationDescriptionI configurationOption;

    /**
     * List of all available default configuration
     */
    private List<Pair<LCConfigurationDescriptionI, File>> availableDefaultConfiguration;

    /**
     * Private singleton constructor
     */
    ProfileConfigSelectionController() {
        this.currentStep = new SimpleObjectProperty<>();
        this.enableViewTransition = new SimpleBooleanProperty(true);
    }

    // FOR VIEWS
    //========================================================================
    public LCProfileI getProfileOption() {
        return profileOption;
    }

    public LCConfigurationDescriptionI getConfigurationOption() {
        return configurationOption;
    }

    public ProfileConfigStep getPreviousStep() {
        return previousStep;
    }

    public void hideStage() {
        this.profileConfigSelectionStage.hide();
    }

    public void setStage(ProfileConfigSelectionStage profileConfigSelectionStage) {
        this.profileConfigSelectionStage = profileConfigSelectionStage;
        this.profileConfigSelectionStage.setOnHidden(e -> currentStep.set(null));
    }

    public ReadOnlyObjectProperty<ProfileConfigStep> currentStepProperty() {
        return this.currentStep;
    }

    public BooleanProperty enableViewTransition() {
        return this.enableViewTransition;
    }

    public void getDefaultConfiguration(Consumer<List<Pair<LCConfigurationDescriptionI, File>>> callback) {
        if (availableDefaultConfiguration != null) {
            callback.accept(availableDefaultConfiguration);
        } else {
            LoadAvailableDefaultConfigurationTask loadAvailableDefaultConfigurationTask = new LoadAvailableDefaultConfigurationTask();
            loadAvailableDefaultConfigurationTask.setOnSucceeded(e -> {
                availableDefaultConfiguration = loadAvailableDefaultConfigurationTask.getValue();
                callback.accept(availableDefaultConfiguration);
            });
            loadAvailableDefaultConfigurationTask.setOnFailed(event -> {
                LOGGER.error("Couldn't load default configurations", event.getSource().getException());
            });
            AsyncExecutorController.INSTANCE.addAndExecute(true, false, loadAvailableDefaultConfigurationTask);
        }
    }
    //========================================================================

    // UI
    //========================================================================
    public boolean showNoProfileWarning(Node source) {
        if (AppController.INSTANCE.currentProfileProperty().get() == null) {
            Alert dlg = ConfigUIUtils.createDialog(source, Alert.AlertType.CONFIRMATION);
            dlg.setHeaderText(Translation.getText("profile.alert.no.selected.header"));
            dlg.setContentText(Translation.getText("profile.alert.no.selected.message"));
            Optional<ButtonType> result = dlg.showAndWait();
            if (result.get() == ButtonType.OK) {
                ProfileConfigSelectionController.INSTANCE.hideStage();
                Platform.exit();
            } else {
                return true;
            }
        }
        return false;
    }
    //========================================================================

    // SHOW API
    //========================================================================
    public void setProfileStep(final ProfileConfigStep step, ProfileConfigStep previousStep, LCProfileI profileOption) {
        this.profileOption = profileOption;
        this.setStep(step, previousStep);
    }

    public void setConfigStep(final ProfileConfigStep step, ProfileConfigStep previousStep, LCConfigurationDescriptionI configurationOption) {
        this.configurationOption = configurationOption;
        this.setStep(step, previousStep);
    }

    public void setStep(final ProfileConfigStep step, ProfileConfigStep previousStep) {
        this.previousStep = previousStep;
        if (!this.profileConfigSelectionStage.isShowing()) {
            this.enableViewTransition.set(false);
        }
        this.currentStep.set(step);
        if (!this.profileConfigSelectionStage.isShowing()) {
            this.profileConfigSelectionStage.show();
            this.enableViewTransition.set(true);
        }
    }
    //========================================================================

}
