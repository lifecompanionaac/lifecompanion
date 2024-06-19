package org.lifecompanion.controller.useapi;

import javafx.util.Pair;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.VirtualCursorInfoDto;

import java.util.function.Supplier;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.SELECTION_VIRTUAL_CURSOR_INFO;
import static spark.Spark.get;

public class VirtualCursorRoutes {
    static void init() {
        get(SELECTION_VIRTUAL_CURSOR_INFO.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                                    Pair<Double, Double> virtualCursorPosition = SelectionModeController.INSTANCE.getVirtualCursorPosition();
                                    Pair<Double, Double> virtualCursorSceneSize = SelectionModeController.INSTANCE.getVirtualCursorSceneSize();
                                    return new VirtualCursorInfoDto(
                                            virtualCursorSceneSize.getKey(), virtualCursorSceneSize.getValue(),
                                            virtualCursorPosition.getKey(), virtualCursorPosition.getValue()
                                    );
                                }
                        )
                )
        );
    }

    static Object checkVirtualCursorSelectionMode(Supplier<Object> ifSelectionMode) {
        if (!SelectionModeController.INSTANCE.isVirtualCursorSelectionMode()) {
            return ActionConfirmationDto.nok("Selection mode is not configured on virtual cursor");
        }
        return ifSelectionMode.get();
    }
}
