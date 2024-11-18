package org.lifecompanion.plugin.phonecontrol2.model.useevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

public enum PhoneControlEventMainCategory implements UseEventMainCategoryI, UseEventSubCategoryI {
    INSTANCE;

    private static final String ID = "PHONECONTROL2_EVENT_MAIN_CATEGORY";

    private final ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("phonecontrol2.plugin.main.event.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("phonecontrol2.plugin.main.event.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
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

    @Override
    public UseEventMainCategoryI getMainCategory() {
        return null;
    }

    @Override
    public ObservableList<UseEventGeneratorI> getContent() {
        return null;
    }
}
