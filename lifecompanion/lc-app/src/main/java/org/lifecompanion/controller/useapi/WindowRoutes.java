package org.lifecompanion.controller.useapi;

import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.function.Consumer;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.WINDOW_MINIMIZE;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.WINDOW_SHOW;
import static spark.Spark.post;

public class WindowRoutes {
    static void init() {
        post(WINDOW_MINIMIZE.getUrl(), (req, res) -> executeOnUseStage(stage -> stage.setIconified(true)));
        post(WINDOW_SHOW.getUrl(), (req, res) -> executeOnUseStage(stage -> stage.setIconified(false)));
    }

    private static ActionConfirmationDto executeOnUseStage(Consumer<Stage> operation) {
        if (AppModeController.INSTANCE.isUseMode()) {
            Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (stage != null) {
                FXThreadUtils.runOnFXThread(() -> operation.accept(stage));
                return ActionConfirmationDto.ok();
            } else {
                return ActionConfirmationDto.nok("Can't find use mode window");
            }
        } else {
            return ActionConfirmationDto.nok("Not in use mode");
        }
    }
}
