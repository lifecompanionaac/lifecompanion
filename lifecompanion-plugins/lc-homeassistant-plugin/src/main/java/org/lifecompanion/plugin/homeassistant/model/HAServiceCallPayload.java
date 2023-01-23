package org.lifecompanion.plugin.homeassistant.model;

import com.google.gson.annotations.SerializedName;

public class HAServiceCallPayload {
    @SerializedName("entity_id")
    private String entityId;

    public HAServiceCallPayload(String entityId) {
        this.entityId = entityId;
    }
}
