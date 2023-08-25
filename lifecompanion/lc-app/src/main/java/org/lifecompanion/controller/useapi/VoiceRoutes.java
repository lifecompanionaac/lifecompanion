package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.VOICE_STOP;
import static spark.Spark.post;

public class VoiceRoutes {
    static void init() {
        post(VOICE_STOP.getUrl(), (req, res) -> checkUseMode(() -> {
            VoiceSynthesizerController.INSTANCE.stopCurrentSpeakAndClearQueue();
            return ActionConfirmationDto.ok();
        }));
    }
}
