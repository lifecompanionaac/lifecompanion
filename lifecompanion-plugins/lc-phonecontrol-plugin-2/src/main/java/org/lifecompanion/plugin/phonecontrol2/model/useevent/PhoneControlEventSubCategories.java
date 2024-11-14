package org.lifecompanion.plugin.phonecontrol2.model.useevent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

public enum PhoneControlEventSubCategories implements UseEventSubCategoryI {
    GENERAL("phonecontrol2.plugin.sub.event.category.general.name",
            CategorizedElementColorProvider.nextColor(PhoneControlEventMainCategory.INSTANCE)),
            ;

    private final String nameId;
    private final String id;
    private final Color color;
    private final ObservableList<UseEventGeneratorI> actions = FXCollections.observableArrayList();

    PhoneControlEventSubCategories(final String nameId, final Color color) {
        this.nameId = nameId;
        this.id = this.name();
        this.color = color;
        PhoneControlEventMainCategory.INSTANCE.getSubCategories().add(this);
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameId);
    }

    @Override
    public UseEventMainCategoryI getMainCategory() {
        return PhoneControlEventMainCategory.INSTANCE;
    }

    @Override
    public ObservableList<UseEventGeneratorI> getContent() {
        return this.actions;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public int order() {
        return this.ordinal();
    }

    @Override
    public String getID() {
        return this.id;
    }

    @Override
    public String generateID() {
        return this.id;
    }
}
