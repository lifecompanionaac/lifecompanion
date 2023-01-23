/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2021 CMRRF KERPAPE (Lorient, France)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.lifecompanion.plugin.email.event.categories;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum EmailEventSubCategories implements UseEventSubCategoryI {
    MISC("email.plugin.use.event.category.misc.name", Color.web("#8BC34A")), //
    SEND("email.plugin.use.event.category.send.name", Color.web("#00BCD4")); //

    private String nameId;
    private String id;
    private Color color;
    private ObservableList<UseEventGeneratorI> events = FXCollections.observableArrayList();

    private EmailEventSubCategories(final String nameId, final Color color) {
        this.nameId = nameId;
        this.id = this.name();
        this.color = color;
        EmailEventMainCategory.INSTANCE.getSubCategories().add(this);
    }

    @Override
    public String getName() {
        return Translation.getText(this.nameId);
    }

    @Override
    public EmailEventMainCategory getMainCategory() {
        return EmailEventMainCategory.INSTANCE;
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
