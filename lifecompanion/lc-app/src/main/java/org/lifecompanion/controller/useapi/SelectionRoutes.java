package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.hub.HubController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.SelectionConfigDto;
import org.lifecompanion.model.impl.useapi.dto.SetDeviceLocalIdDto;
import org.lifecompanion.util.javafx.FXThreadUtils;

import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.checkUseMode;
import static org.lifecompanion.controller.useapi.LifeCompanionControlServerController.fromJson;
import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.post;

public class SelectionRoutes {
    static void init() {
        post(SELECTION_START.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.startSelectionMode()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Can't start selection mode (already started ?)");
        })));
        post(SELECTION_STOP.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.stopSelectionMode()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Can't stop selection mode (already stopped ?)");
        })));
        post(SELECTION_SIMULATE_PRESS.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.simulateScanSelectionPress()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Current selection mode is not a scanning selection mode");
        })));
        post(SELECTION_SIMULATE_RELEASE.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            if (SelectionModeController.INSTANCE.simulateScanSelectionRelease()) {
                return ActionConfirmationDto.ok();
            } else return ActionConfirmationDto.nok("Current selection mode is not a scanning selection mode");
        })));
        post(SELECTION_CONFIG.getUrl(), (req, res) -> checkUseMode(() -> FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            SelectionConfigDto selectionConfigDto = fromJson(SelectionConfigDto.class, req);
            LCConfigurationI configuration = AppModeController.INSTANCE.getUseModeContext().getConfiguration();
            if (configuration != null) {
                if (selectionConfigDto.getScanLoop() != null) {
                    configuration.getSelectionModeParameter().maxScanBeforeStopProperty().set(selectionConfigDto.getScanLoop());
                }
                if (selectionConfigDto.getScanTime() != null) {
                    configuration.getSelectionModeParameter().scanPauseProperty().set(selectionConfigDto.getScanTime());
                }
                if (selectionConfigDto.getDisableAutoStart() != null) {
                    configuration.getSelectionModeParameter().startScanningOnClicProperty().set(selectionConfigDto.getDisableAutoStart());
                }
            }
            // Try to change the mode
            if (selectionConfigDto.getMode() != null) {
                SelectionModeController.INSTANCE.changeUseModeSelectionModeTo(selectionConfigDto.getMode().getModeClass());
            }
            return ActionConfirmationDto.ok();
        })));
    }
}
