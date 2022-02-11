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
package org.lifecompanion.app.instance;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.base.data.config.LCConstant;
import org.lifecompanion.util.StageUtils;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Implementation for {@link DoubleLaunchListener}
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DoubleLaunchListenerImpl implements DoubleLaunchListener {

    @Override
    public void doubleRunDetected() {
        LCUtils.runOnFXThread(() -> {
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
            Alert dialog = ConfigUIUtils.createAlert(stage.getScene().getRoot(), AlertType.ERROR);
            dialog.headerTextProperty().bind(TranslationFX.getTextBinding("double.run.detected.header", timeLeft));
            dialog.setContentText(Translation.getText("double.run.detected.message"));
            timeLineAutoHide.setOnFinished(e -> dialog.hide());
            timeLineAutoHide.play();
            dialog.show();
        });
    }
}
