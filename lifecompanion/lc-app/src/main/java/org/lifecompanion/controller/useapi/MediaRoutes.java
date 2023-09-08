package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.media.SoundPlayerController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.MEDIA_STOP;
import static spark.Spark.post;

public class MediaRoutes {
    static void init() {
        post(MEDIA_STOP.getUrl(), (req, res) -> checkUseMode(() -> {
            SoundPlayerController.INSTANCE.stopEveryPlayer();
            return ActionConfirmationDto.ok();
        }));
    }
}
