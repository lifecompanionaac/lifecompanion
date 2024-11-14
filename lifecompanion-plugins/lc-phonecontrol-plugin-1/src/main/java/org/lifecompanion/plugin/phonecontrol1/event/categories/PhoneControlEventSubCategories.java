package org.lifecompanion.plugin.phonecontrol1.event.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public enum PhoneControlEventSubCategories implements UseEventSubCategoryI {
    MISC("phonecontrol1.plugin.event.sub.category.misc.name", Color.web("#8BC34A"));
    private String nameId;
    private String id;
    private Color color;
    private ObservableList<UseEventGeneratorI> events = FXCollections.observableArrayList();

    private PhoneControlEventSubCategories(final String nameId, final Color color) {
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
    public PhoneControlEventMainCategory getMainCategory() {
        return PhoneControlEventMainCategory.INSTANCE;
    }

    @Override
    public ObservableList<UseEventGeneratorI> getContent() {
        return this.events;
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
