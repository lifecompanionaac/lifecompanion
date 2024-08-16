package org.lifecompanion.plugin.caaai.model.useevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

public enum CAAAIEventMainCategory implements UseEventMainCategoryI {
    INSTANCE;

    private static final String ID = "CAAAI_EVENT_MAIN_CATEGORY";

    private final ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("caa.ai.plugin.todo");
    }

    @Override
    public String getName() {
        return Translation.getText("caa.ai.plugin.todo");
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/filler_icon_32px.png";
    }

    @Override
    public Color getColor() {
        return CategorizedElementColorProvider.nextColor(ID);
    }

    @Override
    public ObservableList<UseEventSubCategoryI> getSubCategories() {
        return this.subCategories;
    }

    @Override
    public int order() {
        return 2000;//at the end
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public String generateID() {
        return ID;
    }
}
