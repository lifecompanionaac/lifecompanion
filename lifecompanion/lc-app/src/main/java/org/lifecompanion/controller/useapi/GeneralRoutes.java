package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.useapi.dto.AliveDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.APP_STATUS;
import static spark.Spark.get;

public class GeneralRoutes {
    public static void init() {
        get(APP_STATUS.getUrl(), (req, res) -> {
            AppMode appMode = AppModeController.INSTANCE.modeProperty().get();
            if (LifeCompanionControlServerController.INSTANCE.isAppStopping()) return new AliveDto(AliveDto.Status.STOPPING);
            else if (appMode == null) return new AliveDto(AliveDto.Status.STARTING);
            else if (appMode == AppMode.EDIT) return new AliveDto(AliveDto.Status.IN_EDIT_MODE);
            else return new AliveDto(AliveDto.Status.IN_USE_MODE);
        });
    }
}
