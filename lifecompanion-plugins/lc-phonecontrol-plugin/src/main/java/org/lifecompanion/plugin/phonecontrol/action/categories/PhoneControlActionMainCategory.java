package org.lifecompanion.plugin.phonecontrol.action.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum PhoneControlActionMainCategory implements UseActionMainCategoryI {
    INSTANCE;

    private static final String ID = "PHONECONTROL1_ACTION_MAIN_CATEGORY";

    private final ObservableList<UseActionSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getName() {
        return Translation.getText("phonecontrol1.plugin.action.main.category.name");
    }

    @Override
    public String getStaticDescription() {
        return Translation.getText("phonecontrol1.plugin.action.main.category.description");
    }

    @Override
    public String getConfigIconPath() {
        return "use-actions/phonecontrol.png";
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
        return ID;
    }

    @Override
    public String generateID() {
        return ID;
    }
}
