package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.util.javafx.FXThreadUtils;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.SELECTION_START;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.SELECTION_STOP;
import static spark.Spark.post;

public class SelectionRoutes {
    static void init() {
        post(SELECTION_START.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.startSelectionMode()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Can't start selection mode (already started ?)");
        })));
        post(SELECTION_STOP.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.stopSelectionMode()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Can't stop selection mode (already stopped ?)");
        })));
    }
}
