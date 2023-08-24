package org.lifecompanion.controller.useapi;

import javafx.stage.Stage;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint;
import org.lifecompanion.model.impl.useapi.LifeCompanionControlServerException;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.AliveDto;
import org.lifecompanion.model.impl.useapi.dto.ErrorDto;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ResponseTransformer;
import spark.Spark;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.*;
import static spark.Spark.*;

public enum LifeCompanionControlServerController implements ResponseTransformer {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCompanionControlServerController.class);

    private boolean started = false;
    private boolean appStopping = false;

    public void startControlServer() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.ENABLE_CONTROL_SERVER)) {
            int port = 8648;
            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.CONTROL_SERVER_PORT)) {
                // TODO : implementation to
            }
            // Configuration
            port(port);
            defaultResponseTransformer(this);

            // Error handling
            notFound((req, res) -> render(new ErrorDto("error.not.found", "Requested URL not found, check the docs !")));
            internalServerError((req, res) -> render(new ErrorDto("error.internal.error", "Unknown internal server error...")));
            exception(Exception.class, (exc, request, response) -> {
                if (exc instanceof LifeCompanionControlServerException) {
                    LifeCompanionControlServerException lifeCompanionControlServerException = (LifeCompanionControlServerException) exc;
                    response.body(render(lifeCompanionControlServerException.toDto()));
                } else {
                    response.body(render(LifeCompanionControlServerException.toDto(exc)));
                    response.status(500);
                }
            });

            // Services
            path(URL_PREFIX, () -> {
                // General
                get(ALIVE.getUrl(), (req, res) -> {
                    AppMode appMode = AppModeController.INSTANCE.modeProperty().get();
                    if (appStopping) return new AliveDto(AliveDto.Status.STOPPING);
                    else if (appMode == null) return new AliveDto(AliveDto.Status.STARTING);
                    else if (appMode == AppMode.EDIT) return new AliveDto(AliveDto.Status.IN_EDIT_MODE);
                    else return new AliveDto(AliveDto.Status.IN_USE_MODE);
                });
                // Window
                post(MINIMIZE_WINDOW.getUrl(), (req, res) -> {
                    if (AppModeController.INSTANCE.isUseMode()) {
                        Stage stage = AppModeController.INSTANCE.getUseModeContext().getStage();
                        if (stage != null) {
                            FXThreadUtils.runOnFXThread(() -> stage.setIconified(true));
                            return ActionConfirmationDto.ok();
                        } else {
                            return ActionConfirmationDto.nok("Can't find use mode window");
                        }
                    } else {
                        return ActionConfirmationDto.nok("Not in use mode");
                    }
                });
            });
            after((req, res) -> res.type("application/json"));
            started = true;

            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEV_MODE)) {
                LOGGER.info("LifeCompanion server API documentation\n{}\n", LifeCompanionControlServerEndpoint.getAllMarkdownDocumentation());
            }
        }
    }

    public void stopControlServer() {
        if (started) {
            Spark.stop();
        }
    }

    public void setAppStopping(boolean appStopping) {
        this.appStopping = appStopping;
    }

    @Override
    public String render(Object model) {
        return toJson(model);
    }

    public static String toJson(Object model) {
        // TODO : handle null ?
        return JsonHelper.GSON.toJson(model);
    }
}
