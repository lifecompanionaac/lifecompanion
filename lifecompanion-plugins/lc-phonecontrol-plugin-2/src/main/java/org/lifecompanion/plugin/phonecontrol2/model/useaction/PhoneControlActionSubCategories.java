package org.lifecompanion.plugin.phonecontrol2.model.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum PhoneControlActionSubCategories implements UseActionSubCategoryI {
    // POUR L'EXTENSION PHONE CONTROL
    APPEL("phonecontrol2.plugin.sub.action.category.appel.name", CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE)),

    VOLUME("phonecontrol2.plugin.sub.action.category.volume.name", CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE)),

    CONTACT("phonecontrol2.plugin.sub.action.category.contact.name", CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE));
    private final String nameId;
    private final String id;
    private final Color color;
    private final ObservableList<BaseUseActionI<?>> actions = FXCollections.observableArrayList();

    PhoneControlActionSubCategories(final String nameId, final Color color) {
        this.nameId = nameId;
        this.id = this.name();
        this.color = color;
        PhoneControlActionMainCategory.INSTANCE.getSubCategories().add(this);
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameId);
    }

    @Override
    public UseActionMainCategoryI getMainCategory() {
        return PhoneControlActionMainCategory.INSTANCE;
    }

    @Override
    public ObservableList<BaseUseActionI<?>> getContent() {
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
