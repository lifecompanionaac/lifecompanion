package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.hub.HubController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.SetDeviceLocalIdDto;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.HUB_REFRESH_DEVICE_LOCAL_ID;
import static spark.Spark.post;

public class HubRoutes {
    static void init() {
        post(HUB_REFRESH_DEVICE_LOCAL_ID.getUrl(), (req, res) -> checkUseMode(() -> {
            SetDeviceLocalIdDto deviceLocalIdDto = fromJson(SetDeviceLocalIdDto.class, req);
            HubController.INSTANCE.requestRefreshDeviceLocalId(deviceLocalIdDto.getLocalId());
            return ActionConfirmationDto.ok();
        }));
    }
}
