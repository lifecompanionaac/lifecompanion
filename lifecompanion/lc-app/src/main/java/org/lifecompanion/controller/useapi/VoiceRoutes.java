package org.lifecompanion.controller.useapi;

import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class VoiceRoutes {
    static void init() {
        post(VOICE_STOP.getUrl(), (req, res) -> ActionConfirmationDto.nok("Not implemented yet, sorry !"));
    }
}
