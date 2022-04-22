package org.lifecompanion.model.api.ui.editmode;

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;

public interface AddComponentI {
    /**
     * @return the icon that represent this component
     */
    String getIconPath();

    /**
     * @return name of the component
     */
    String getNameID();

    /**
     * @return a description of the added component
     */
    String getDescriptionID();

    AddComponentCategoryEnum getCategory();

    Class<? extends DisplayableComponentI> getSelectionFilter();

    UndoRedoActionI createAddAction();
}
