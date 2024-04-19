package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.virtualmouse.GraphicContext;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.MouseInfoDto;
import org.lifecompanion.model.impl.useapi.dto.MoveMouseAbsoluteDto;
import org.lifecompanion.model.impl.useapi.dto.MoveMouseRelativeDto;
import org.lifecompanion.util.LangUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;
import static spark.Spark.get;

public class MouseRoutes {
    static void init() {
        post(MOUSE_MOVE_ABSOLUTE.getUrl(), (req, res) -> checkUseMode(() -> {
            MoveMouseAbsoluteDto moveMouseAbsoluteDto = fromJson(MoveMouseAbsoluteDto.class, req);
            if (moveMouseAbsoluteDto.getX() == null || moveMouseAbsoluteDto.getY() == null) return ActionConfirmationDto.nok("Both coord must be provided");
            String msg = VirtualMouseController.INSTANCE.moveMouseRelativeToScreenUnscaled(moveMouseAbsoluteDto.getX(), moveMouseAbsoluteDto.getY());
            return msg == null ? ActionConfirmationDto.ok() : ActionConfirmationDto.nok(msg);
        }));
        post(MOUSE_MOVE_RELATIVE.getUrl(), (req, res) -> checkUseMode(() -> {
            MoveMouseRelativeDto moveMouseRelativeDto = fromJson(MoveMouseRelativeDto.class, req);
            String msg = VirtualMouseController.INSTANCE.moveMouseRelativeToMouseUnscaled(LangUtils.nullToZero(moveMouseRelativeDto.getDx()), LangUtils.nullToZero(moveMouseRelativeDto.getDy()));
            return msg == null ? ActionConfirmationDto.ok() : ActionConfirmationDto.nok(msg);
        }));
        post(MOUSE_ACTIVATION_PRIMARY.getUrl(), (req, res) -> checkUseMode(() -> {
            VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON1);
            return ActionConfirmationDto.ok();
        }));
        post(MOUSE_ACTIVATION_SECONDARY.getUrl(), (req, res) -> checkUseMode(() -> {
            VirtualMouseController.INSTANCE.executeMouseClic(MouseEvent.BUTTON3);
            return ActionConfirmationDto.ok();
        }));
        get(MOUSE_INFO.getUrl(), (req, res) -> {
            Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
            GraphicContext graphicContext = VirtualMouseController.INSTANCE.getGraphicContext();
            return new MouseInfoDto(graphicContext.getUnscaledScreenWidth(),
                    graphicContext.getUnscaledScreenHeight(),
                    (mouseLocation.x - graphicContext.getAwtBounds().getMinX()) * graphicContext.getAwtXScale(),
                    (mouseLocation.y - graphicContext.getAwtBounds().getMinY()) * graphicContext.getAwtYScale());
        });
    }
}
