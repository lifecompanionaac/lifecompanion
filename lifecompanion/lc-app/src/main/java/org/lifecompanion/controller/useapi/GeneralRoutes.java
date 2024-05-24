package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.useapi.dto.AppStatusDto;
import org.lifecompanion.model.impl.useapi.dto.GridDto;
import org.lifecompanion.model.impl.useapi.dto.GridPartDto;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.APP_STATUS;
import static spark.Spark.get;

public class GeneralRoutes {
    public static void init() {
        get(APP_STATUS.getUrl(), (req, res) -> {
            boolean selectionModePlaying = SelectionModeController.INSTANCE.playingProperty().get();
            AppMode appMode = AppModeController.INSTANCE.modeProperty().get();
            if (LifeCompanionControlServerController.INSTANCE.isAppStopping())
                return new AppStatusDto(AppStatusDto.Status.STOPPING, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null, null);
            else if (appMode == null) return new AppStatusDto(AppStatusDto.Status.STARTING, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null, null);
            else if (appMode == AppMode.EDIT) return new AppStatusDto(AppStatusDto.Status.IN_EDIT_MODE, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), null, null);
            else return new AppStatusDto(AppStatusDto.Status.IN_USE_MODE, AppStatusDto.SelectionModeStatus.fromPlayingProperty(selectionModePlaying), getMainCurrentGrid(), getCurrentOverPart());
        });
    }

    private static GridPartDto getCurrentOverPart() {
        GridPartComponentI currentOverPart = SelectionModeController.INSTANCE.currentOverPartProperty().get();
        return currentOverPart != null ? new GridPartDto(currentOverPart.nameProperty().get(),
                currentOverPart.getID(),
                currentOverPart.rowProperty().get(),
                currentOverPart.columnProperty().get()) : null;
    }

    private static GridDto getMainCurrentGrid() {
        GridComponentI targetedGrid = IndicationController.INSTANCE.getTargetedGrid();
        return targetedGrid != null ? new GridDto(targetedGrid.nameProperty().get(), targetedGrid.getID(), targetedGrid.rowCountProperty().get(), targetedGrid.columnCountProperty().get()) : null;
    }
}
