package org.lifecompanion.plugin.phonecontrol.action.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum PhoneControlActionSubCategories implements UseActionSubCategoryI {
    MISC("phonecontrol.plugin.action.sub.category.misc.name",
            CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE)),
    SMS("phonecontrol.plugin.action.sub.category.sms.name",
            CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE)),
    CALL("phonecontrol.plugin.action.sub.category.call.name",
            CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE)),
    REFRESH("phonecontrol.plugin.action.sub.category.refresh.name",
            CategorizedElementColorProvider.nextColor(PhoneControlActionMainCategory.INSTANCE));

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
