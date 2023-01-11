package org.lifecompanion.plugin.spellgame.model.useevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

public enum SpellGameEventMainCategory implements UseEventMainCategoryI {
    INSTANCE;

    private static final String ID = "SPELLGAME_EVENT_MAIN_CATEGORY";

    private final ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("spellgame.plugin.main.event.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("spellgame.plugin.main.event.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "filler_icon.png";
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
