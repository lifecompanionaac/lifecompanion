package org.lifecompanion.model.api.ui.editmode;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lifecompanion.framework.commons.translation.Translation;

public enum AddComponentCategoryEnum {
    ROOT("ribbon.part.create.root"),
    GRID("ribbon.part.create.grid"),
    KEY("ribbon.part.create.key"),
    MISC("ribbon.part.create.misc");

    private String titleId;

    private AddComponentCategoryEnum(final String titleId) {
        this.titleId = titleId;
    }

    public String getTitle() {
        return Translation.getText(this.titleId);
    }
}
