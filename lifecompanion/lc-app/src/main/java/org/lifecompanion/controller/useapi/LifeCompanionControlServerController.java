package org.lifecompanion.controller.useapi;

import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.useapi.GlobalRuntimeConfiguration;
import org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint;
import org.lifecompanion.model.impl.useapi.LifeCompanionControlServerException;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.ErrorDto;
import org.lifecompanion.util.LangUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Request;
import spark.ResponseTransformer;
import spark.Spark;

import java.util.function.Supplier;

import static org.lifecompanion.model.impl.useapi.LifeCompanionControlServerEndpoint.URL_PREFIX;
import static spark.Spark.*;

public enum LifeCompanionControlServerController implements ResponseTransformer {
    INSTANCE;

    private static final Logger LOGGER = LoggerFactory.getLogger(LifeCompanionControlServerController.class);

    // TODO : replace with main branch when ready
    public static final String DOC_URL = "https://github.com/lifecompanionaac/lifecompanion/blob/develop/docs/USER_API.md#lifecompanion-control-server-api";

    private boolean started = false;
    private boolean appStopping = false;

    public void startControlServer() {
        if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.ENABLE_CONTROL_SERVER)) {
            int port = 8648;
            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.CONTROL_SERVER_PORT)) {
                String portStr = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.CONTROL_SERVER_PORT);
                Integer portParam = LangUtils.safeParseInt(portStr);
                if (portParam != null) {
                    port = portParam;
                } else {
                    LOGGER.warn("Control server port from {} ignored because cannot be parsed from {}", GlobalRuntimeConfiguration.CONTROL_SERVER_PORT, portStr);
                }
            }
            final String authToken;
            if (GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.CONTROL_SERVER_AUTH_TOKEN)) {
                LOGGER.info("\"Authorization: Bearer <token>\" will be request on each request as {} is present", GlobalRuntimeConfiguration.CONTROL_SERVER_AUTH_TOKEN);
                authToken = GlobalRuntimeConfigurationController.INSTANCE.getParameter(GlobalRuntimeConfiguration.CONTROL_SERVER_AUTH_TOKEN);
            } else {
                authToken = null;
            }

            LOGGER.info("Control server will be running on http://localhost:{}", port);
            LOGGER.info("Check control server documentation on {}", DOC_URL);
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
            get("/", (req, res) -> {
                res.redirect(DOC_URL);
                return DOC_URL;
            });
            path(URL_PREFIX, () -> {
                before("/*", (req, res) -> {
                    if (authToken != null) {
                        String authHeader = req.headers("Authorization");
                        if (authHeader != null) {
                            String[] headerParts = authHeader.split(" ");
                            if (headerParts.length == 2 && "Bearer".equals(headerParts[0])) {
                                String token = headerParts[1];
                                if (StringUtils.isEquals(authToken, token)) {
                                    return;
                                }
                            }
                        }
                        halt(401, "Authentication required");
                    }
                });
                GeneralRoutes.init();
                WindowRoutes.init();
                SelectionRoutes.init();
                VoiceRoutes.init();
                MediaRoutes.init();
                HubRoutes.init();
                MouseRoutes.init();
                IndicationRoutes.init();
                VirtualCursorRoutes.init();
            });
            after((req, res) -> res.type("application/json"));
            started = true;

            if (LCConstant.GENERATE_DEV_DOCS && GlobalRuntimeConfigurationController.INSTANCE.isPresent(GlobalRuntimeConfiguration.PROP_DEV_MODE)) {
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

    boolean isAppStopping() {
        return appStopping;
    }

    // COMMONS
    //========================================================================
    static Object checkUseMode(Supplier<Object> ifUseMode) {
        if (AppModeController.INSTANCE.isUseMode()) {
            return ifUseMode.get();
        } else {
            return ActionConfirmationDto.nok("Not in use mode");
        }
    }
    //========================================================================

    // RENDER
    //========================================================================
    @Override
    public String render(Object model) {
        return toJson(model);
    }

    public static String toJson(Object model) {
        // TODO : handle null ?
        return JsonHelper.GSON.toJson(model);
    }

    public static <T> T fromJson(Class<T> typeClass, Request request) {
        return JsonHelper.GSON.fromJson(request.body(), typeClass);
    }
    //========================================================================

}
