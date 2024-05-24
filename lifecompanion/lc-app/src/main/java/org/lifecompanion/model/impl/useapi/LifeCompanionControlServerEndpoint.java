package org.lifecompanion.model.impl.useapi;

import javafx.scene.paint.Color;
import org.lifecompanion.controller.useapi.LifeCompanionControlServerController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.useapi.EndpointHttpMethod;
import org.lifecompanion.model.api.useapi.LifeCompanionControlServerEndpointI;
import org.lifecompanion.model.impl.selectionmode.SelectionModeEnum;
import org.lifecompanion.model.impl.useapi.dto.*;
import org.lifecompanion.util.javafx.ColorUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LifeCompanionControlServerEndpoint implements LifeCompanionControlServerEndpointI {
    APP_STATUS(
            "app/status",
            EndpointHttpMethod.GET,
            "To get the LifeCompanion current status, will return containing information about the running instance (can be `STARTING`,`IN_USE_MODE`,`IN_EDIT_MODE` or `STOPPING`)",
            null,
            List.of(new AppStatusDto(AppStatusDto.Status.STARTING, AppStatusDto.SelectionModeStatus.PAUSED, null, null),
                    new AppStatusDto(AppStatusDto.Status.IN_USE_MODE,
                            AppStatusDto.SelectionModeStatus.PLAYING,
                            new GridDto("Clavier", "1fee441b-b261-4fe4-85fd-13572f0a1aa3", 4, 6),
                            new GridPartDto("A", "003f6ba7-ff0e-4d1e-893e-8a7d7df880b0", 1, 2)),
                    new AppStatusDto(AppStatusDto.Status.STOPPING, AppStatusDto.SelectionModeStatus.PAUSED, null, null))
    ),
    // Window
    WINDOW_MINIMIZE("window/minimize",
            EndpointHttpMethod.POST,
            "Minimize the current use mode window to hide it from user",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    WINDOW_SHOW("window/show",
            EndpointHttpMethod.POST,
            "Show the current window on top of the others and try to focus it",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    WINDOW_BOUNDS("window/bounds",
            EndpointHttpMethod.POST,
            "Change the window bounds to the wanted bounds (in pixel). Bounds contains the window location top left corner (x,y) from screen top left corner and size (width,height). Will not check that the given bounds respect screen bounds.",
            List.of(new WindowBoundsDto(0, 124, 1366, 644)),
            List.of(ActionConfirmationDto.ok())
    ),

    // Voice synthesizer
    VOICE_STOP("voice/stop",
            EndpointHttpMethod.POST,
            "Stop the current speaking voice synthesizer and empty the voice synthesizer queue to clear the waiting speech. Later calls to voice synthesizer will work as usual.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    // Selection mode
    SELECTION_STOP("selection/stop",
            EndpointHttpMethod.POST,
            "Stop the current selection mode (if applicable). Will disable any user interaction with LifeCompanion UI no matter the current selection mode type (scanning, direct, etc.). To restore a working selection mode, `selection/start` should be called.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    SELECTION_START("selection/start",
            EndpointHttpMethod.POST,
            "Start the selection mode for the current used configuration. Will restore user interaction with LifeCompanion UI. Calling this service once while the selection mode is already started will have no effect.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    SELECTION_SIMULATE_PRESS("selection/simulate/press",
            EndpointHttpMethod.POST,
            "Simulate the selection press if the current selection mode is a scanning selection. The caller is responsible for later calling `selection/simulate/release`. Calling this service on a direct selection mode will have no effect.",
            null,
            List.of(ActionConfirmationDto.ok(), ActionConfirmationDto.nok("Current selection mode is not a scanning selection mode"))
    ),
    SELECTION_SIMULATE_RELEASE("selection/simulate/release",
            EndpointHttpMethod.POST,
            "Simulate the selection release if the current selection mode is a scanning selection. Should be called only after calling `selection/simulate/press`. Calling this service on a direct selection mode will have no effect.",
            null,
            List.of(ActionConfirmationDto.ok(), ActionConfirmationDto.nok("Current selection mode is not a scanning selection mode"))
    ),
    SELECTION_CONFIG("selection/config",
            EndpointHttpMethod.POST,
            "Configure the current selection mode and restart it with the new configuration. Allow configuring the selection mode (direct, scanning, etc.) and some selection mode parameters (scanning loops, time...). Available mode : " + SelectionModeEnum.listForDocs(),
            List.of(new SelectionConfigDto(SelectionModeEnum.MOUSE_CLIC),
                    new SelectionConfigDto(SelectionModeEnum.SCAN_ROW_COLUMN, 2, 2500, true),
                    new SelectionConfigDto(2, 1800),
                    new SelectionConfigDto(SelectionModeEnum.SCAN_KEY_HORIZONTAL, 1, 1500, false)),
            List.of(ActionConfirmationDto.ok())
    ),
    // Media
    MEDIA_STOP("media/stop",
            EndpointHttpMethod.POST,
            "Stop any playing media (sound, video, etc.) and empty the media players queue to be sure that no media will be played without a new play request.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    // HUB
    HUB_REFRESH_DEVICE_LOCAL_ID("hub/update/device-local-id",
            EndpointHttpMethod.POST,
            "Request the local device ID update to be used to sync the used configuration with default configuration for this device set on LifeCompanion Hub. Note that this should be combined with the `deviceSyncMode` parameter. The method will always immediately returns even if the change can be later considered by the app (config synchronization is async). This can also be called to update the configuration for the device when `deviceSyncAutoRefresh` parameter is not enabled.",
            List.of(new SetDeviceLocalIdDto("foobar123")),
            List.of(ActionConfirmationDto.ok())
    ),
    // MOUSE
    MOUSE_MOVE_ABSOLUTE("mouse/move/absolute",
            EndpointHttpMethod.POST,
            "Move the mouse to an absolute position on the used screen to display LifeCompanion. The given position should be absolute no matter the screen scaling factor. Coordinates are relative to the top left corner.",
            List.of(new MoveMouseAbsoluteDto(689, 383)),
            List.of(ActionConfirmationDto.ok(), ActionConfirmationDto.nok("Incorrect position"))),
    MOUSE_MOVE_RELATIVE("mouse/move/relative",
            EndpointHttpMethod.POST,
            "Move the mouse by a given x and y difference that can be positive/negative/null. The given values should be absolute pixel values no matter the screen scaling factor. Positive values means a move to right or bottom, negative values means a move to left or top. The mouse will be blocked to avoid going \"out\" of screen bounds.",
            List.of(new MoveMouseRelativeDto(15, -15), new MoveMouseRelativeDto(-60, null)),
            List.of(ActionConfirmationDto.ok())),
    MOUSE_INFO("mouse/info",
            EndpointHttpMethod.GET,
            "Return information about the current mouse position and screen size.",
            null,
            List.of(new MouseInfoDto(1920, 1080, 564, 855))),
    MOUSE_ACTIVATION_PRIMARY("mouse/activation/primary",
            EndpointHttpMethod.POST,
            "Immediately active the mouse primary (eg left button) button to the current mouse position.",
            null,
            List.of(ActionConfirmationDto.ok())),
    MOUSE_ACTIVATION_SECONDARY("mouse/activation/secondary",
            EndpointHttpMethod.POST,
            "Immediately active the mouse secondary (eg right button) button to the current mouse position.",
            null,
            List.of(ActionConfirmationDto.ok())),
    // FEEDBACK
    INDICATION_TARGET_SHOW_LOCATION("indication/target/show/location",
            EndpointHttpMethod.POST,
            "Show the target indication to a specific location in the current main grid. If the target is reached, an specific use event is generated (Cible de déplacement atteinte) and the target is hidden.",
            List.of(new ShowIndicationTargetDto(ColorUtils.toWebColorWithAlpha(Color.GREEN), 5.0, 2, 4), new ShowIndicationTargetDto(null, null, 1, 3)),
            List.of(ActionConfirmationDto.ok())),
    INDICATION_TARGET_SHOW_RANDOM("indication/target/show/random",
            EndpointHttpMethod.POST,
            "Show the target indication to a random location in the current main grid. If the target is reached, an specific use event is generated (Cible de déplacement atteinte) and the target is hidden.",
            List.of(new ShowIndicationRandomTargetDto(ColorUtils.toWebColorWithAlpha(Color.GREEN), 5.0), new ShowIndicationRandomTargetDto(null, null)),
            List.of(ActionConfirmationDto.ok())),
    INDICATION_TARGET_HIDE("indication/target/hide",
            EndpointHttpMethod.POST,
            "Hide the currently showing target indication. Noop if no target is showing.",
            null,
            List.of(ActionConfirmationDto.ok())),
    INDICATION_ACTIVATION_SHOW("indication/activation/show",
            EndpointHttpMethod.POST,
            "Show the activation indication on the current overed part of the selection mode. The activation indication will \"follow\" the selection mode indicator.  If the selection mode is activated while showing this indication, an specific use event is generated (Demande d'activation effectuée) and the indication is hidden.",
            List.of(new ShowIndicationActivationDto("#2517c263")),
            List.of(ActionConfirmationDto.ok())),
    INDICATION_ACTIVATION_HIDE("indication/activation/hide",
            EndpointHttpMethod.POST,
            "Hide the currently showing activation indication. Noop if no target is showing.",
            null,
            List.of(ActionConfirmationDto.ok())),
    ;

    public final static String URL_PREFIX = "/api/v1/";

    private final String url;
    private final EndpointHttpMethod method;
    private final String description;
    private final List<Object> exampleParameters, exampleReturns;

    LifeCompanionControlServerEndpoint(String url, EndpointHttpMethod method, String description, List<Object> exampleParameters, List<Object> exampleReturns) {
        this.url = url;
        this.method = method;
        this.description = description;
        this.exampleParameters = exampleParameters;
        this.exampleReturns = exampleReturns;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public EndpointHttpMethod getMethod() {
        return method;
    }

    @Override
    public List<Object> getExampleParameters() {
        return exampleParameters;
    }

    @Override
    public List<Object> getExampleReturns() {
        return exampleReturns;
    }

    @Override
    public String toString() {
        return getUrl();
    }

    @Override
    public String getMarkdownDocumentation() {
        StringBuilder content = new StringBuilder("### /").append(getUrl());
        content.append("\n\n**Description** : ").append(getDescription());
        content.append("\n\n**Url structure** : `").append(URL_PREFIX).append(getUrl()).append("`");
        content.append("\n\n**Method** : `").append(getMethod()).append("`");
        content.append("\n\n**Parameters** :");
        appendList(exampleParameters, content);
        content.append("\n\n**Returns** : ");
        appendList(exampleReturns, content);
        return content.toString();
    }

    private void appendList(List<Object> objects, StringBuilder content) {
        if (CollectionUtils.isEmpty(objects)) {
            content.append("\n```\nNONE\n```");
        } else {
            for (Object exampleParameter : objects) {
                content.append("\n```json\n");
                content.append(LifeCompanionControlServerController.toJson(exampleParameter));
                content.append("\n```");
            }
        }
    }

    public static String getAllMarkdownDocumentation() {
        StringBuilder content = new StringBuilder();
        for (LifeCompanionControlServerEndpoint endpoint : LifeCompanionControlServerEndpoint.values()) {
            content.append("- **[").append(endpoint.getUrl()).append("]").append("(#").append(endpoint.getUrl().replace("/", "")).append(")**").append("\n");
        }
        content.append("\n");
        content.append(Arrays.stream(LifeCompanionControlServerEndpoint.values()).map(LifeCompanionControlServerEndpoint::getMarkdownDocumentation).collect(Collectors.joining("\n")));
        return content.toString();
    }
}
