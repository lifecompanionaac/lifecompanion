package org.lifecompanion.plugin.aac4all.wp2.model.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementColorProvider;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;

public enum AAC4AllWp2SubCategories implements UseActionSubCategoryI {


    //class part
    TODO("aac4aal.wp2.plugin.main.action.category.name", CategorizedElementColorProvider.nextColor(AAC4AllWp2MainCategory.INSTANCE)),
    ;

    private final String nameId;
    private final String id;
    private final Color color;
    private final ObservableList<BaseUseActionI<?>> actions = FXCollections.observableArrayList();

    AAC4AllWp2SubCategories(final String nameId, final Color color) {
        this.nameId = nameId;
        this.id = this.name();
        this.color = color;
        AAC4AllWp2MainCategory.INSTANCE.getSubCategories().add(this);
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameId);
    }

    @Override
    public UseActionMainCategoryI getMainCategory() {
        return AAC4AllWp2MainCategory.INSTANCE;
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
