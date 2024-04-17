package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.MoveMouseDto;
import org.lifecompanion.util.ThreadUtils;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.MOUSE_ACTIVATION_PRIMARY;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.MOUSE_MOVE;
import static spark.Spark.post;

public class MouseRoutes {
    static void init() {
        post(MOUSE_MOVE.getUrl(), (req, res) -> checkUseMode(() -> {
            MoveMouseDto moveMouseDto = fromJson(MoveMouseDto.class, req);
            // TODO : validate coords
            VirtualMouseController.INSTANCE.moveMouse(moveMouseDto.getX(), moveMouseDto.getY());
            return ActionConfirmationDto.ok();
        }));
        post(MOUSE_ACTIVATION_PRIMARY.getUrl(), (req, res) -> checkUseMode(() -> {
            VirtualMouseController.INSTANCE.mousePrimaryClic();
            return ActionConfirmationDto.ok();
        }));
    }
}
