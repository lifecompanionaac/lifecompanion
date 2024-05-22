package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.feedback.FeedbackController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackActivationDto;
import org.lifecompanion.model.impl.useapi.dto.ShowFeedbackTargetDto;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class FeedbackRoutes {
    static void init() {
        post(FEEDBACK_TARGET_SHOW.getUrl(), (req, res) -> checkUseMode(() -> {
            ShowFeedbackTargetDto showFeedbackTargetDto = fromJson(ShowFeedbackTargetDto.class, req);
            FeedbackController.INSTANCE.showTarget(showFeedbackTargetDto);
            return ActionConfirmationDto.ok();
        }));
        post(FEEDBACK_TARGET_HIDE.getUrl(), (req, res) -> checkUseMode(() -> {
            FeedbackController.INSTANCE.hideTarget();
            return ActionConfirmationDto.ok();
        }));
        post(FEEDBACK_ACTIVATION_SHOW.getUrl(), (req, res) -> checkUseMode(() -> {
            ShowFeedbackActivationDto showFeedbackActivationDto = fromJson(ShowFeedbackActivationDto.class, req);
            FeedbackController.INSTANCE.showActivation(showFeedbackActivationDto);
            return ActionConfirmationDto.ok();
        }));
        post(FEEDBACK_ACTIVATION_HIDE.getUrl(), (req, res) -> checkUseMode(() -> {
            FeedbackController.INSTANCE.hideActivation();
            return ActionConfirmationDto.ok();
        }));
    }
}
