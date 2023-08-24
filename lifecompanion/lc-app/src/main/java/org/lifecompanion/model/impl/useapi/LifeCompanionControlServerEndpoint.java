package org.lifecompanion.model.impl.useapi;

import org.lifecompanion.controller.useapi.LifeCompanionControlServerController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.useapi.EndpointHttpMethod;
import org.lifecompanion.model.api.useapi.LifeCompanionControlServerEndpointI;
import org.lifecompanion.model.impl.useapi.dto.ActionConfirmationDto;
import org.lifecompanion.model.impl.useapi.dto.AliveDto;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum LifeCompanionControlServerEndpoint implements LifeCompanionControlServerEndpointI {
    APP_STATUS(
            "app/status",
            EndpointHttpMethod.GET,
            "To get the LifeCompanion current status, will return containing information about the running instance (can be `STARTING`,`IN_USE_MODE`,`IN_EDIT_MODE` or `STOPPING`)",
            null,
            List.of(new AliveDto(AliveDto.Status.STARTING), new AliveDto(AliveDto.Status.IN_USE_MODE), new AliveDto(AliveDto.Status.STOPPING))
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
    // TODO : size, location ?

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
            "Stop the current selection mode (if applicable). Can be useful if the current selection mode is a scanning mode. Scanning will be able to be played again will the `selection/play` endpoint",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    SELECTION_PLAY("selection/play",
            EndpointHttpMethod.POST,
            "Play the current selection mode (if applicable). Useful if `selection/stop` endpoint has been called. Will not have any effect if the selection mode is already playing.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    // Media
    MEDIA_STOP("media/stop",
            EndpointHttpMethod.POST,
            "Stop any playing media (sound, video, etc.) and empty the media players queue to be sure that no media will be played without a new play request.",
            null,
            List.of(ActionConfirmationDto.ok())
    ),
    // TODO : configuration synchronization, backoffice secrets refresh, etc ?

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
