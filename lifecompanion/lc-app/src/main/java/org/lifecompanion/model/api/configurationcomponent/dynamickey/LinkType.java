package org.lifecompanion.model.api.configurationcomponent.dynamickey;

import org.lifecompanion.framework.commons.translation.Translation;

public enum LinkType {
    KEYLIST("keylist.linktype.type.keylist.name", "keylist.linktype.type.keylist.description"),
    GRID("keylist.linktype.type.grid.name", "keylist.linktype.type.grid.description");

    private final String nameId, descriptionId;

    LinkType(String nameId, String descriptionId) {
        this.nameId = nameId;
        this.descriptionId = descriptionId;
    }

    public String getName() {
        return Translation.getText(nameId);
    }

    public String getDescription() {
        return Translation.getText(descriptionId);
    }
}
