package org.lifecompanion.plugin.homeassistant.model;

import com.google.gson.annotations.SerializedName;

public class HAEntityState {
    @SerializedName("entity_id")
    private String entityId;
    private String state;
    private HAEntityAttribute attributes;

    public String getEntityId() {
        return entityId;
    }

    public String getState() {
        return state;
    }

    public HAEntityAttribute getAttributes() {
        return attributes;
    }

    public static class HAEntityAttribute {
        @SerializedName("friendly_name")
        private String friendlyName;

        public String getFriendlyName() {
            return friendlyName;
        }
    }

}
