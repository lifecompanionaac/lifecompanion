package org.lifecompanion.plugin.ppp.actions.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum PPPActionMainCategory implements UseActionMainCategoryI {
    INSTANCE;

    private static final String ID = "PPP_ACTION_MAIN_CATEGORY";

    private final ObservableList<UseActionSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("ppp.plugin.actions.categories.main.description");
    }

    @Override
    public String getName() {
        return Translation.getText("ppp.plugin.actions.categories.main.name");
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
    public ObservableList<UseActionSubCategoryI> getSubCategories() {
        return this.subCategories;
    }

    @Override
    public int order() {
        return 2000;//at the end
    }

    @Override
    public String getID() {
        return PPPActionMainCategory.ID;
    }

    @Override
    public String generateID() {
        return PPPActionMainCategory.ID;
    }
}
