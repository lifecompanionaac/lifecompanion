package org.lifecompanion.controller.useapi;

import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.WindowBoundsDto;
import org.lifecompanion.util.javafx.FXThreadUtils;

import java.util.function.Consumer;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class WindowRoutes {
    static void init() {
        post(WINDOW_MINIMIZE.getUrl(), (req, res) -> executeOnUseStage(stage -> stage.setIconified(true)));
        post(WINDOW_SHOW.getUrl(), (req, res) -> executeOnUseStage(stage -> stage.setIconified(false)));
        post(WINDOW_BOUNDS.getUrl(), (req, res) -> {
            WindowBoundsDto bounds = fromJson(WindowBoundsDto.class, req);
            return executeOnUseStage(stage -> {
                stage.setX(bounds.getX());
                stage.setY(bounds.getY());
                stage.setWidth(bounds.getWidth());
                stage.setHeight(bounds.getHeight());
            });
        });
    }

    private static ActionConfirmationDto executeOnUseStage(Consumer<Stage> operation) {
        return checkUseMode(() -> {
            Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
            if (stage != null) {
                FXThreadUtils.runOnFXThread(() -> operation.accept(stage));
                return ActionConfirmationDto.ok();
            } else {
                return ActionConfirmationDto.nok("Can't find use mode window");
            }
        });
    }
}
