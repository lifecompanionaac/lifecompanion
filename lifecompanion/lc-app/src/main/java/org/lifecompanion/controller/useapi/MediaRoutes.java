package org.lifecompanion.controller.useapi;

import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.MEDIA_STOP;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.VOICE_STOP;
import static spark.Spark.post;

public class MediaRoutes {
    static void init() {
        post(MEDIA_STOP.getUrl(), (req, res) -> ActionConfirmationDto.nok("Not implemented yet, sorry !"));
    }
}
