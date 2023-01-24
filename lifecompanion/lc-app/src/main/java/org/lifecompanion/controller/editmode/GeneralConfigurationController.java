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

package org.lifecompanion.controller.editmode;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStage;

public enum GeneralConfigurationController {
    INSTANCE;

    private final StringProperty currentStep;
    private GeneralConfigurationStage stage;
    private Object[] stepArgs;
    private final BooleanProperty enableTransition;

    GeneralConfigurationController() {
        this.currentStep = new SimpleStringProperty();
        enableTransition = new SimpleBooleanProperty();
    }

    public void initStage(GeneralConfigurationStage stage) {
        this.stage = stage;
    }

    public GeneralConfigurationStage getStage() {
        return stage;
    }

    public Object[] getStepArgs() {
        return stepArgs;
    }

    public BooleanProperty enableTransitionProperty() {
        return enableTransition;
    }

    public StringProperty currentStepProperty() {
        return currentStep;
    }

    /**
     * Will navigate and show to the given step if a step exists for the given ID.<br>
     * If the general config view stage is already showing, just navigate, else show the stage before.
     *
     * @param step the step from {@link GeneralConfigurationStep}, {@link GeneralConfigurationStep#name()} will be used as ID
     * @param args args for the given step (can be empty)
     */
    public void showStep(GeneralConfigurationStep step, Object... args) {
        showStep(step.name(), args);
    }

    /**
     * Will navigate and show to the given step if a step exists for the given ID.<br>
     * If the general config view stage is already showing, just navigate, else show the stage before.
     *
     * @param step the step ID
     * @param args args for the given step (can be empty)
     */
    public void showStep(String step, Object... args) {
        if (!this.stage.isShowing()) {
            enableTransition.set(false);
            this.stage.show();
        }
        this.stepArgs = args;
        this.currentStep.set(step);
        enableTransition.set(true);
    }

    public void clearCurrentStep() {
        this.currentStep.set(null);
        stepArgs = null;
    }
}
