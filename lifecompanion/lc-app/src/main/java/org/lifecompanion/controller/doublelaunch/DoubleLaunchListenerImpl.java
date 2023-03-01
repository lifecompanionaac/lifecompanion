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
package org.lifecompanion.controller.doublelaunch;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.LifeCompanion;
import org.lifecompanion.controller.editaction.LCConfigurationActions;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.doublelaunch.DoubleLaunchListener;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Implementation for {@link DoubleLaunchListener}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DoubleLaunchListenerImpl implements DoubleLaunchListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(LifeCompanion.class);


    @Override
    public void launched(boolean notify, String[] args) throws RemoteException {
        if (notify) {
            FXThreadUtils.runOnFXThread(() -> {
                LOGGER.info("Double launch will be notified, args is param is = {}", Arrays.toString(args));

                //Show main frame
                Stage stage = StageUtils.getEditOrUseStageVisible();
                stage.setIconified(false);
                stage.show();
                stage.requestFocus();

                // Time left before hiding the dialog (to avoid block)
                IntegerProperty timeLeft = new SimpleIntegerProperty(LCConstant.DOUBLE_LAUNCH_DISPLAY_DELAY);
                Timeline timeLineAutoHide = new Timeline(new KeyFrame(Duration.seconds(1), (e) -> timeLeft.set(timeLeft.get() - 1)));
                timeLineAutoHide.setCycleCount(LCConstant.DOUBLE_LAUNCH_DISPLAY_DELAY);

                //When a double run is detected, show a dialog to user
                final Alert dialog = DialogUtils.alertWithSourceAndType(StageUtils.getOnTopWindowExcludingNotification(), AlertType.ERROR)
                        .withContentText(Translation.getText("double.run.detected.message"))
                        .build();
                dialog.headerTextProperty().bind(TranslationFX.getTextBinding("double.run.detected.header", timeLeft));
                timeLineAutoHide.setOnFinished(e -> {
                    if (dialog.isShowing()) {
                        dialog.hide();
                    }
                });
                timeLineAutoHide.play();

                // On dialog hidden, execute the action
                dialog.setOnHidden(e -> {
                    if (args != null && AppModeController.INSTANCE.isEditMode()) {
                        Platform.runLater(() -> { // Should be explicitly delayed as this can be called on an Animation
                            ArrayList<String> argCollection = new ArrayList<>(Arrays.asList(args));
                            File configurationFile = IOHelper.getFirstConfigurationFile(argCollection);
                            if (configurationFile != null) {
                                LCConfigurationActions.ImportOpenEditAction importOpenConfig = new LCConfigurationActions.ImportOpenEditAction(configurationFile);
                                ConfigActionController.INSTANCE.executeAction(importOpenConfig);
                            }
                        });
                    }
                });

                dialog.show();
            });
        } else {
            LOGGER.info("Double launch detected but not notified to user");
        }
    }
}
