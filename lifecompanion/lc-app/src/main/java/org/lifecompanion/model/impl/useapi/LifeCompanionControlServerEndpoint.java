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
    ALIVE(
            "alive",
            EndpointHttpMethod.GET,
            "Check if LifeCompanion is alive, will return a status DTO containing information about the running instance.",
            null,
            List.of(new AliveDto(AliveDto.Status.STARTING), new AliveDto(AliveDto.Status.IN_USE_MODE), new AliveDto(AliveDto.Status.STOPPING))
    ),
    MINIMIZE_WINDOW("window/minimize", EndpointHttpMethod.POST,
            "Minimize the current use mode window to hide it from user",
            null,
            List.of(ActionConfirmationDto.ok())
    );

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
        StringBuilder content = new StringBuilder("\n### /").append(getUrl());
        content.append("\n\n**Description** : ").append(getDescription());
        content.append("\n\n**Url structure** : `").append(URL_PREFIX).append(getUrl()).append("`");
        content.append("\n\n**Method** : `").append(getMethod()).append("`");
        content.append("\n\n**Parameters** :");
        appendList(exampleParameters, content);
        content.append("\n\n**Returns** : ");
        appendList(exampleReturns, content);
        content.append("\n");
        return content.toString();
    }

    private void appendList(List<Object> objects, StringBuilder content) {
        if (CollectionUtils.isEmpty(objects)) {
            content.append("\n```\nNONE\n```\n");
        } else {
            for (Object exampleParameter : objects) {
                content.append("\n```json\n");
                content.append(LifeCompanionControlServerController.toJson(exampleParameter));
                content.append("\n```\n");
            }
        }
    }

    public static String getAllMarkdownDocumentation() {
        return Arrays.stream(LifeCompanionControlServerEndpoint.values()).map(LifeCompanionControlServerEndpoint::getMarkdownDocumentation).collect(Collectors.joining("\n"));
    }
}
