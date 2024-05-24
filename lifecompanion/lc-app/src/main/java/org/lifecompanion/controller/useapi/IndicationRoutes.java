package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.ShowIndicationActivationDto;
import org.lifecompanion.model.impl.useapi.dto.ShowIndicationTargetDto;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class IndicationRoutes {
    static void init() {
        post(INDICATION_TARGET_SHOW_LOCATION.getUrl(), (req, res) -> checkUseMode(() -> {
            ShowIndicationTargetDto showIndicationTargetDto = fromJson(ShowIndicationTargetDto.class, req);
            IndicationController.INSTANCE.showTarget(showIndicationTargetDto);
            return ActionConfirmationDto.ok();
        }));
        post(INDICATION_TARGET_HIDE.getUrl(), (req, res) -> checkUseMode(() -> {
            IndicationController.INSTANCE.hideTarget();
            return ActionConfirmationDto.ok();
        }));
        post(INDICATION_ACTIVATION_SHOW.getUrl(), (req, res) -> checkUseMode(() -> {
            ShowIndicationActivationDto showIndicationActivationDto = fromJson(ShowIndicationActivationDto.class, req);
            IndicationController.INSTANCE.showActivation(showIndicationActivationDto);
            return ActionConfirmationDto.ok();
        }));
        post(INDICATION_ACTIVATION_HIDE.getUrl(), (req, res) -> checkUseMode(() -> {
            IndicationController.INSTANCE.hideActivation();
            return ActionConfirmationDto.ok();
        }));
    }
}
