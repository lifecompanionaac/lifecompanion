package org.lifecompanion.model.api.useapi;

import java.util.List;

public interface LifeCompanionControlServerEndpointI {
    String getUrl();

    String getDescription();

    EndpointHttpMethod getMethod();

    List<Object> getExampleParameters();

    List<Object> getExampleReturns();

    String getMarkdownDocumentation();
}
