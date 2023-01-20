package org.lifecompanion.plugin.homeassistant.model;

public class HAService {
    private String domainId;
    private String serviceId;
    private String name;
    private String description;

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setDomainId(String domainId) {
        this.domainId = domainId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public String toString() {
        return "HAService{" +
                "domainId='" + domainId + '\'' +
                ", serviceId='" + serviceId + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
