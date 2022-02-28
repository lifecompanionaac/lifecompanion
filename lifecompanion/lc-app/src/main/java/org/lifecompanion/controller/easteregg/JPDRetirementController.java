/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2022 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.easteregg;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.utils.LCNamedThreadFactory;
import org.lifecompanion.ui.easteregg.JPDRetirementView;
import org.lifecompanion.util.javafx.FXThreadUtils;

public enum JPDRetirementController {
    INSTANCE;

    private final BooleanProperty showView;
    private JPDRetirementView currentView;

    JPDRetirementController() {
        showView = new SimpleBooleanProperty();
    }

    public ReadOnlyBooleanProperty showViewProperty() {
        return showView;
    }

    public void startJPDRetirementJourney() {
        if (UserConfigurationController.INSTANCE.enableJPDRetirementEasterEggProperty().get()) {
            if (!this.showView.get()) {
                LCNamedThreadFactory.daemonThreadFactory("JPDRetirementController").newThread(() -> {
                    // Show view
                    FXThreadUtils.runOnFXThread(() -> this.showView.set(true));

                    // Launch loading
                    this.currentView.launchFirstStep();
                }).start();
            }
        }
    }

    public void setCurrentView(JPDRetirementView jpdRetirementView) {
        this.currentView = jpdRetirementView;
    }
}
