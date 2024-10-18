package org.lifecompanion.plugin.aac4all.wp2.model.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum AAC4AllWp2MainCategory implements UseActionMainCategoryI {
    INSTANCE;

    private static final String ID = "AAC4ALLWP2_ACTION_MAIN_CATEGORY";

    private final ObservableList<UseActionSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("saac4aal.wp2.plugin.main.action.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("aac4aal.wp2.plugin.main.action.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "filler_icon_32px.png";
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
