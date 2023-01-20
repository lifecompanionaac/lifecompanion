package org.lifecompanion.plugin.homeassistant.model;

import java.util.Map;

public class HARawDomain {
    private String domain;
    private Map<String, HAService> services;

    public String getDomain() {
        return domain;
    }

    public Map<String, HAService> getServices() {
        return services;
    }

    @Override
    public String toString() {
        return "HADomain{" +
                "domain='" + domain + '\'' +
                ", services=" + services +
                '}';
    }
}
