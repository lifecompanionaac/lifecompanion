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

package org.lifecompanion.base.data.control.refacto;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.config.UserBaseConfiguration;
import org.lifecompanion.config.data.action.impl.LCConfigurationActions;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;

public class EditModeContext extends AbstractModeContext {
    private final ChangeListener<Number> unsavedNotificationListener = (obs, ov, nv) -> {
        int threshold = UserBaseConfiguration.INSTANCE.unsavedChangeInConfigurationThresholdProperty().get();
        // Value become larger than threshold : show a warning notification that suggest saving
        if (LCUtils.nullToZeroInt(ov) < threshold && LCUtils.nullToZeroInt(nv) >= threshold && AppModeController.INSTANCE.isEditMode()) {
            LCNotificationController.INSTANCE.showNotification(LCNotification.createWarning(Translation.getText("notification.warning.unsaved.changes.configuration.title", nv),
                    "notification.warning.unsaved.changes.action.name", () -> ConfigActionController.INSTANCE.executeAction(new LCConfigurationActions.SaveAction(getStage().getScene().getRoot()))));
        }
    };

    private final DoubleProperty configurationScale;

    EditModeContext() {
        super();
        configurationScale = new SimpleDoubleProperty(1.0);
        this.configuration.addListener((obs, ov, nv) -> {
            if (ov != null) {
                ov.unsavedActionProperty().removeListener(unsavedNotificationListener);
                LCUtils.unbindAndSet(configurationScale, 1.0);
            }
            if (nv != null) {
                unsavedNotificationListener.changed(null, Integer.MAX_VALUE, nv.unsavedActionProperty().get());
                nv.unsavedActionProperty().addListener(unsavedNotificationListener);
                configurationScale.bind(nv.configurationScaleInEditModeProperty());
            }
        });
    }

    // PROPS
    //========================================================================
    @Override
    public void cleanAfterStop() {
        this.configurationDescription.set(null);
        this.configuration.set(null);
    }

    public DoubleProperty configurationScaleProperty() {
        return this.configurationScale;
    }
    //========================================================================


    // CONFIGURATION SCALE
    //========================================================================
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

    // UNSAVED
    //========================================================================
    public void increaseUnsavedActionOnCurrentConfiguration() {
        LCConfigurationI currentConfiguration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        currentConfiguration.unsavedActionProperty().set(currentConfiguration.unsavedActionProperty().get() + 1);
    }
    //========================================================================

}
