/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.ui.app.profileconfigselect;

import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.profile.ChangelogEntryI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;

import java.util.Optional;

public class DuplicateConfigAlertContent extends GridPane implements LCViewInitHelper {

    private final LCProfileI profile;
    private final LCConfigurationDescriptionI previousConfigDescription, importedConfigurationDescription;

    public DuplicateConfigAlertContent(LCProfileI profile, LCConfigurationDescriptionI previousConfigDescription, LCConfigurationDescriptionI importedConfigurationDescription) {
        this.profile = profile;
        this.previousConfigDescription = previousConfigDescription;
        this.importedConfigurationDescription = importedConfigurationDescription;
        initAll();
    }

    @Override
    public void initUI() {
        this.setVgap(3.0);
        this.setHgap(20.0);
        int rowIndex = this.addToGrid(0,
                FontAwesome.Glyph.DOWNLOAD,
                "action.import.existing.configuration.title.imported",
                importedConfigurationDescription,
                getNewerOlderTextId(importedConfigurationDescription, previousConfigDescription));
        Separator separator = new Separator(Orientation.HORIZONTAL);
        GridPane.setMargin(separator, new Insets(0, 0, 10, 0));
        this.add(separator, 0, rowIndex++, 2, 1);
        this.addToGrid(rowIndex,
                FontAwesome.Glyph.LIST,
                "action.import.existing.configuration.title.current",
                previousConfigDescription,
                getNewerOlderTextId(previousConfigDescription, importedConfigurationDescription));
    }

    private String getNewerOlderTextId(LCConfigurationDescriptionI config1, LCConfigurationDescriptionI config2) {
        Optional<ChangelogEntryI> lastEntry1 = getLastEntry(config1);
        Optional<ChangelogEntryI> lastEntry2 = getLastEntry(config2);
        if (lastEntry1.isPresent() && lastEntry2.isEmpty()) return "action.import.existing.configuration.status.newer";
        else if (lastEntry1.isEmpty() && lastEntry2.isPresent()) return "action.import.existing.configuration.status.older";
        else if (lastEntry1.isEmpty()) return "action.import.existing.configuration.status.equals";
        else {
            int compareTo = lastEntry1.get().getWhen().compareTo(lastEntry2.get().getWhen());
            return compareTo > 0 ? "action.import.existing.configuration.status.newer" :
                    (compareTo < 0 ? "action.import.existing.configuration.status.older" :
                            "action.import.existing.configuration.status.equals");
        }
    }

    private int addToGrid(int rowIndex, FontAwesome.Glyph glyph, String titleId, LCConfigurationDescriptionI configurationDescription, String newerOlderTextId) {
        this.add(GlyphFontHelper.FONT_AWESOME.create(glyph).sizeFactor(2).color(LCGraphicStyle.MAIN_PRIMARY), 0, rowIndex, 1, 3);

        Label labelTitle = new Label(Translation.getText(titleId));
        labelTitle.getStyleClass().addAll("text-weight-bold", "text-font-size-110");
        GridPane.setHgrow(labelTitle, Priority.ALWAYS);
        GridPane.setFillWidth(labelTitle, true);
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        this.add(labelTitle, 1, rowIndex++);

        Label labelDescription = new Label(Translation.getText("action.import.existing.configuration.last.modification.description",
                configurationDescription.configurationNameProperty().get(),
                getLastEntry(configurationDescription).map(c -> StringUtils.dateToStringDateWithHour(c.getWhen())).orElse("?"),
                getLastEntry(configurationDescription).map(c -> c.getProfileName() + " (" + c.getSystemUserName() + ")").orElse("?")
        ));
        labelDescription.setWrapText(true);
        this.add(labelDescription, 1, rowIndex++);

        Label labelNewerOlder = new Label(StringUtils.toUpperCase(Translation.getText(newerOlderTextId)));
        labelNewerOlder.getStyleClass().addAll("text-font-size-90", "text-weight-bold");
        this.add(labelNewerOlder, 1, rowIndex++);
        GridPane.setMargin(labelNewerOlder, new Insets(0, 0, 10, 0));

        return rowIndex;
    }

    private Optional<ChangelogEntryI> getLastEntry(LCConfigurationDescriptionI configurationDescription) {
        return configurationDescription.getChangelogEntries()
                .stream()
                .min((e1, e2) -> e2.getWhen().compareTo(e1.getWhen()));
    }
}
