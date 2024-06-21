package org.lifecompanion.controller.useapi;

import javafx.application.Platform;
import javafx.util.Pair;
import org.lifecompanion.controller.feedback.IndicationController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartComponentI;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.AppStatusDto;
import org.lifecompanion.model.impl.useapi.dto.GridDto;
import org.lifecompanion.model.impl.useapi.dto.GridPartDto;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.util.model.ConfigurationComponentLayoutUtils;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.APP_EXIT;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.APP_STATUS;
import static spark.Spark.get;
import static spark.Spark.post;

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
        post(APP_EXIT.getUrl(), (req, res) -> {
            if (!GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.DISABLE_EXIT)) {
                Platform.exit();
                return ActionConfirmationDto.ok();
            } else {
                return ActionConfirmationDto.nok("Parameter \"" + GlobalRuntimeConfiguration.DISABLE_EXIT + "\" is enabled, can't exit");
            }
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
        if (targetedGrid != null) {
            GridPartComponentI firstComponent = targetedGrid.getGrid().getComponent(0, 0);
            // Define first element in grid
            Pair<Double, Double> firstKeyPosition = ConfigurationComponentLayoutUtils.getConfigurationPosition(firstComponent);
            Pair<Double, Double> size = new Pair<>(firstComponent.layoutWidthProperty().get(), firstComponent.layoutHeightProperty().get());
            // Define space between keys
            return new GridDto(targetedGrid.nameProperty().get(),
                    targetedGrid.getID(),
                    targetedGrid.rowCountProperty().get(),
                    targetedGrid.columnCountProperty().get(),
                    LangUtils.nullToZeroInt(firstKeyPosition.getKey() + size.getKey() / 2.0),
                    LangUtils.nullToZeroInt(firstKeyPosition.getValue() + size.getValue() / 2.0),
                    (int) (targetedGrid.caseWidthProperty().get() + targetedGrid.getGridShapeStyle().hGapProperty().valueAsInt().get()),
                    (int) (targetedGrid.caseHeightProperty().get() + targetedGrid.getGridShapeStyle().vGapProperty().valueAsInt().get())
            );
        }
        return null;

    }
}
