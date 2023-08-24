package org.lifecompanion.controller.useapi;

import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class SelectionRoutes {
    static void init() {
        post(SELECTION_PLAY.getUrl(), (req, res) -> ActionConfirmationDto.nok("Not implemented yet, sorry !"));
        post(SELECTION_STOP.getUrl(), (req, res) -> ActionConfirmationDto.nok("Not implemented yet, sorry !"));
    }
}
