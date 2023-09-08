package org.lifecompanion.controller.training;

import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.api.lifecycle.LCStateListener;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.ui.training.TrainingInformationStage;
import org.lifecompanion.util.ThreadUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;

import static org.lifecompanion.model.impl.constant.LCConstant.TRAINING_DIALOG_SHOW_DELAY;

public enum TrainingController implements LCStateListener {
    INSTANCE;

    private boolean shown;

    @Override
    public void lcStart() {
        if (shouldTrainingInformationBeDisplayed()) {
            AppModeController.INSTANCE.modeProperty().addListener((obs, ov, nv) -> {
                if (nv == AppMode.EDIT) {
                    ThreadUtils.runAfter(TRAINING_DIALOG_SHOW_DELAY, () -> {
                        if (AppModeController.INSTANCE.isEditMode() && !shown && shouldTrainingInformationBeDisplayed()) {
                            shown = true;
                            FXThreadUtils.runOnFXThread(() -> StageUtils.centerOnOwnerOrOnCurrentStageAndShow(new TrainingInformationStage(StageUtils.getOnTopWindowExcludingNotification())));
                        }
                    });
                }
            });
        }
    }

    private static boolean shouldTrainingInformationBeDisplayed() {
        return !LCStateController.INSTANCE.hideTrainingDialogProperty()
                                          .get() && System.currentTimeMillis() - LCStateController.INSTANCE.getLastTrainingDialogShow() >= LCConstant.TRAINING_DIALOG_SHOW_INTERVAL;
    }

    @Override
    public void lcExit() {
    }
}
