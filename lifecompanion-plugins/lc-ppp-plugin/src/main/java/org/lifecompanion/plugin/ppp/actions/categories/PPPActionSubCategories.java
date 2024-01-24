package org.lifecompanion.plugin.ppp.actions.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum PPPActionSubCategories implements UseActionSubCategoryI {
    EVALUATOR("ppp.plugin.actions.categories.subs.evaluator.name",
            CategorizedElementColorProvider.nextColor(PPPActionMainCategory.INSTANCE)),
    ASSESSMENT("ppp.plugin.actions.categories.subs.assessment.name",
            CategorizedElementColorProvider.nextColor(PPPActionMainCategory.INSTANCE)),
    ACTION("ppp.plugin.actions.categories.subs.action.name",
            CategorizedElementColorProvider.nextColor(PPPActionMainCategory.INSTANCE)),
    USER_GROUPS("ppp.plugin.actions.categories.subs.groups.name",
            CategorizedElementColorProvider.nextColor(PPPActionMainCategory.INSTANCE)),
    VARIOUS("ppp.plugin.actions.categories.subs.various.name",
            CategorizedElementColorProvider.nextColor(PPPActionMainCategory.INSTANCE));

    private final String nameId;
    private final String id;
    private final Color color;
    private final ObservableList<BaseUseActionI<?>> actions = FXCollections.observableArrayList();

    PPPActionSubCategories(final String nameId, final Color color) {
        this.nameId = nameId;
        this.id = this.name();
        this.color = color;
        PPPActionMainCategory.INSTANCE.getSubCategories().add(this);
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameId);
    }

    @Override
    public UseActionMainCategoryI getMainCategory() {
        return PPPActionMainCategory.INSTANCE;
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
