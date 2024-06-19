package org.lifecompanion.controller.useapi;

import javafx.stage.Stage;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.virtualmouse.VirtualMouseController;
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
        post(WINDOW_SHOW.getUrl(), (req, res) -> executeOnUseStage(stage -> {
            stage.setIconified(false);
            // Setting the position + size is needed on some platform... (later than first call, that's why we directly call Plateform.runLater)
            stage.setX(stage.getX() * 1.00001);
            stage.setY(stage.getY() * 1.00001);
            stage.setWidth(stage.getWidth() * 1.00001);
            stage.setHeight(stage.getHeight() * 1.00001);
            // Request focus and show to front with mouse
            stage.requestFocus();
            stage.toFront();
            VirtualMouseController.INSTANCE.centerMouseOnStage();
        }));
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

    private static Object executeOnUseStage(Consumer<Stage> operation) {
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
