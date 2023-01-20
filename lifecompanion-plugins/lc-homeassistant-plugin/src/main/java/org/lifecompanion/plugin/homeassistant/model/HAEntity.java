package org.lifecompanion.plugin.homeassistant.model;

import com.google.gson.annotations.SerializedName;

public class HAEntity {
    private final String id;
    private final String domainId;
    private final String friendlyName;

    public HAEntity(String id, String friendlyName) {
        this.id = id;
        this.friendlyName = friendlyName;
        domainId = getDomainFromEntityId(id);
    }

    public static String getDomainFromEntityId(String id) {
        final int i = id != null ? id.indexOf(".") : -1;
        return i >= 0 ? id.substring(0, i) : null;
    }

    public String getId() {
        return id;
    }

    public String getDomainId() {
        return domainId;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    @Override
    public String toString() {
        return "HAEntity{" +
                "id='" + id + '\'' +
                ", domainId='" + domainId + '\'' +
                ", friendlyName='" + friendlyName + '\'' +
                '}';
    }
}
