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

package org.lifecompanion.controller.lifecycle;

import javafx.beans.property.*;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.userconfiguration.UserBaseConfiguration;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;

public class EditModeContext extends AbstractModeContext {

    EditModeContext() {
        super();
        this.configuration.addListener((obs, ov, nv) -> {
            if (ov != null) {
                LCUtils.unbindAndSet(configurationUnsavedAction, 0);
                LCUtils.unbindAndSet(configurationScale, 1.0);
            }
            if (nv != null) {
                configurationUnsavedAction.bind(nv.unsavedActionProperty());
                configurationScale.bind(nv.configurationScaleInEditModeProperty());
            }
        });
        this.configurationUnsavedAction.addListener((obs, ov, nv) -> {
            int threshold = UserBaseConfiguration.INSTANCE.unsavedChangeInConfigurationThresholdProperty().get();
            // Value become larger than threshold : show a warning notification that suggest saving
            if (LCUtils.nullToZeroInt(ov) < threshold && LCUtils.nullToZeroInt(nv) >= threshold && AppModeController.INSTANCE.isEditMode()) {
                LCNotificationController.INSTANCE.showNotification(LCNotification.createWarning(Translation.getText("notification.warning.unsaved.changes.configuration.title", nv),
                        "notification.warning.unsaved.changes.action.name", () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.SaveAction(getStage().getScene().getRoot()))));
            }
        });
    }

    @Override
    public void cleanAfterStop() {
        previousConfiguration = configuration.get();
        previousConfigurationDescription = configurationDescription.get();
        this.configurationDescription.set(null);
        this.configuration.set(null);
    }

    // PREVIOUS CONFIGURATION
    //========================================================================
    private LCConfigurationI previousConfiguration;
    private LCConfigurationDescriptionI previousConfigurationDescription;

    public LCConfigurationI getPreviousConfiguration() {
        return previousConfiguration;
    }

    public LCConfigurationDescriptionI getPreviousConfigurationDescription() {
        return previousConfigurationDescription;
    }

    public void clearPreviouslyEditedConfiguration() {
        previousConfiguration = null;
        previousConfigurationDescription = null;
    }
    //========================================================================


    // CONFIGURATION SCALE
    //========================================================================
    private final DoubleProperty configurationScale = new SimpleDoubleProperty(1.0);

    public ReadOnlyDoubleProperty configurationScaleProperty() {
        return this.configurationScale;
    }

    public void zoomIn() {
        setScaleForCurrentConfiguration(configurationScale.get() + LCGraphicStyle.ZOOM_MODIFIER);
    }

    private void setScaleForCurrentConfiguration(double newValue) {
        final LCConfigurationI configurationV = this.configuration.get();
        if (configurationV != null) {
            if (newValue > LCGraphicStyle.MIN_ZOOM_VALUE && newValue < LCGraphicStyle.MAX_ZOOM_VALUE) {
                configurationV.configurationScaleInEditModeProperty().set(newValue);
            }
        }
    }

    public void zoomOut() {
        setScaleForCurrentConfiguration(configurationScale.get() - LCGraphicStyle.ZOOM_MODIFIER);
    }

    public void resetZoom() {
        setScaleForCurrentConfiguration(1.0);
    }
    //========================================================================

    // UNSAVED ACTIONS
    //========================================================================
    private final IntegerProperty configurationUnsavedAction = new SimpleIntegerProperty(0);

    public ReadOnlyIntegerProperty configurationUnsavedActionProperty() {
        return configurationUnsavedAction;
    }

    public int getConfigurationUnsavedAction() {
        return configurationUnsavedAction.get();
    }

    public void increaseUnsavedActionOnCurrentConfiguration() {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().getConfiguration();
        if (configuration != null) {
            LCUtils.runOnFXThread(() -> configuration.unsavedActionProperty().set(configuration.unsavedActionProperty().get() + 1));
        }
    }

    public void resetUnsavedActionOnCurrentConfiguration() {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().getConfiguration();
        if (configuration != null) {
            LCUtils.runOnFXThread(() -> configuration.unsavedActionProperty().set(0));
        }
    }
    //========================================================================

}
