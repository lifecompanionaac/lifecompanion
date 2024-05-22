package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.feedback.FeedbackController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.impl.useapi.dto.AppStatusDto;
import org.lifecompanion.model.impl.useapi.dto.GridDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.APP_STATUS;
import static spark.Spark.get;

public class GeneralRoutes {
    public static void init() {
        get(APP_STATUS.getUrl(), (req, res) -> {
            boolean selectionModePlaying = SelectionModeController.INSTANCE.playingProperty().get();
            AppMode appMode = AppModeController.INSTANCE.modeProperty().get();
            if (LifeCompanionControlServerController.INSTANCE.isAppStopping())
                return new AppStatusDto(AppStatusDto.Status.STOPPING, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null);
            else if (appMode == null) return new AppStatusDto(AppStatusDto.Status.STARTING, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null);
            else if (appMode == AppMode.EDIT) return new AppStatusDto(AppStatusDto.Status.IN_EDIT_MODE, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null);
            else return new AppStatusDto(AppStatusDto.Status.IN_USE_MODE, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), getMainCurrentGrid());
        });
    }

    private static GridDto getMainCurrentGrid() {
        GridComponentI targetedGrid = FeedbackController.INSTANCE.getTargetedGrid();
        if (targetedGrid != null) return new GridDto(targetedGrid.nameProperty().get(), targetedGrid.getID(), targetedGrid.rowCountProperty().get(), targetedGrid.columnCountProperty().get());
        return null;
    }
}
