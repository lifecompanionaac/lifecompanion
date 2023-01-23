package org.lifecompanion.plugin.ppp.events.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

public enum PPPEventMainCategory implements UseEventMainCategoryI {
    INSTANCE;
    private static final String ID = "PPP_EVENT_MAIN_CATEGORY";

    private final ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("ppp.plugin.events.categories.main.description");
    }

    @Override
    public String getName() {
        return Translation.getText("ppp.plugin.events.categories.main.name");
    }

    @Override
    public String getConfigIconPath() {
        return "icon_ppp_plugin.png";
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
        return PPPEventMainCategory.ID;
    }

    @Override
    public String generateID() {
        return PPPEventMainCategory.ID;
    }

}
