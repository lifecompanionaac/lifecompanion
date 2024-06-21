package org.lifecompanion.controller.useapi;

import javafx.util.Pair;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.impl.useapi.dto.*;

import java.util.function.Supplier;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.get;
import static spark.Spark.post;

public class SelectionVirtualCursorRoutes {
    static void init() {
        get(SELECTION_VIRTUAL_CURSOR_INFO.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                                    Pair<Double, Double> virtualCursorPosition = SelectionModeController.INSTANCE.getVirtualCursorPosition();
                                    Pair<Double, Double> virtualCursorSelectionZoneSize = SelectionModeController.INSTANCE.getVirtualCursorSelectionZoneSize();
                                    return new VirtualCursorInfoDto(
                                            virtualCursorSelectionZoneSize.getKey(), virtualCursorSelectionZoneSize.getValue(),
                                            virtualCursorPosition.getKey(), virtualCursorPosition.getValue()
                                    );
                                }
                        )
                )
        );
        post(SELECTION_VIRTUAL_CURSOR_MOVE_RELATIVE.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                            MoveVirtualCursorRelativeDto moveVirtualCursorRelativeDto = fromJson(MoveVirtualCursorRelativeDto.class, req);
                            SelectionModeController.INSTANCE.moveVirtualCursorRelative(moveVirtualCursorRelativeDto.getDx(), moveVirtualCursorRelativeDto.getDy());
                            return ActionConfirmationDto.ok();
                        })));
        post(SELECTION_VIRTUAL_CURSOR_MOVE_ABSOLUTE.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                            MoveVirtualCursorAbsoluteDto moveVirtualCursorAbsoluteDto = fromJson(MoveVirtualCursorAbsoluteDto.class, req);
                            if (moveVirtualCursorAbsoluteDto.getX() == null || moveVirtualCursorAbsoluteDto.getY() == null) return ActionConfirmationDto.nok("Both coord must be provided");
                            SelectionModeController.INSTANCE.moveVirtualCursorAbsolute(moveVirtualCursorAbsoluteDto.getX(), moveVirtualCursorAbsoluteDto.getY());
                            return ActionConfirmationDto.ok();
                        })));
        post(SELECTION_VIRTUAL_CURSOR_PRESS.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                            SelectionModeController.INSTANCE.virtualCursorPressed();
                            return ActionConfirmationDto.ok();
                        })));
        post(SELECTION_VIRTUAL_CURSOR_RELEASE.getUrl(), (req, res) ->
                LifeCompanionControlServerController.checkUseMode(() ->
                        checkVirtualCursorSelectionMode(() -> {
                            SelectionModeController.INSTANCE.virtualCursorReleased();
                            return ActionConfirmationDto.ok();
                        })));
    }

    static Object checkVirtualCursorSelectionMode(Supplier<Object> ifSelectionMode) {
        if (!SelectionModeController.INSTANCE.isVirtualCursorSelectionMode()) {
            return ActionConfirmationDto.nok("Selection mode is not configured on virtual cursor");
        }
        return ifSelectionMode.get();
    }
}
