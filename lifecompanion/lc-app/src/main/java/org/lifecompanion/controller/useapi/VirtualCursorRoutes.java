package org.lifecompanion.controller.useapi;

import javafx.util.Pair;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.VirtualCursorInfoDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.VIRTUAL_CURSOR_INFO;
import static spark.Spark.get;

public class VirtualCursorRoutes {
    static void init() {
        get(VIRTUAL_CURSOR_INFO.getUrl(), (req, res) -> {
            // TODO : check use mode
            Pair<Double, Double> virtualCursorPosition = SelectionModeController.INSTANCE.getVirtualCursorPosition();
            if (virtualCursorPosition == null) {
                return ActionConfirmationDto.nok("Selection mode is not configured on virtual cursor");
            } else {
                Pair<Double, Double> virtualCursorSceneSize = SelectionModeController.INSTANCE.getVirtualCursorSceneSize();
                return new VirtualCursorInfoDto(
                        virtualCursorSceneSize.getKey(), virtualCursorSceneSize.getValue(),
                        virtualCursorPosition.getKey(), virtualCursorPosition.getValue()
                );
            }
        });
    }
}
