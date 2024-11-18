package org.lifecompanion.plugin.phonecontrol1.event.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

/**
 * @author Etudiants IUT Vannes : HASCOËT Anthony, GUERNY Baptiste,
 *         Le CHANU Simon, PAVOINE Oscar
 */
public enum PhoneControlEventMainCategory implements UseEventMainCategoryI {
    INSTANCE;

    private static final String ID = "PHONECONTROL1_EVENT_MAIN_CATEGORY";

    private ObservableList<UseEventSubCategoryI> subCategories = FXCollections.observableArrayList();

    @Override
    public String getStaticDescription() {
        return Translation.getText("phonecontrol1.plugin.event.main.category.description");
    }

    @Override
    public String getName() {
        return Translation.getText("phonecontrol1.plugin.event.main.category.name");
    }

    @Override
    public String getConfigIconPath() {
        return "use-events/phonecontrol.png";
    }

    @Override
    public Color getColor() {
        return Color.FORESTGREEN;
    }

    @Override
    public ObservableList<UseEventSubCategoryI> getSubCategories() {
        return this.subCategories;
    }

    @Override
    public int order() {
        return 1000;//at the end
    }

    @Override
    public String getID() {
        return PhoneControlEventMainCategory.ID;
    }

    @Override
    public String generateID() {
        return PhoneControlEventMainCategory.ID;
    }

}
