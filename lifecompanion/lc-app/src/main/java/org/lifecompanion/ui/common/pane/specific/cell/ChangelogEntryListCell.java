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

package org.lifecompanion.ui.common.pane.specific.cell;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.model.api.profile.ChangelogEntryI;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

public class ChangelogEntryListCell extends ListCell<ChangelogEntryI> {
    public static final double CELL_HEIGHT = 20.0;
    private final Label labelDate, labelModificationCount, labelProfileUserSystem;
    private final HBox boxContent;

    public ChangelogEntryListCell() {
        this.getStyleClass().add("list-cell-selection-disabled");

        labelDate = new Label();
        labelDate.getStyleClass().addAll("text-weight-bold");
        labelModificationCount = new Label();
        labelModificationCount.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(labelModificationCount, Priority.ALWAYS);
        labelProfileUserSystem = new Label();
        labelProfileUserSystem.getStyleClass().addAll("text-fill-gray", "text-wrap-enabled", "text-font-size-90");
        boxContent = new HBox(3.0, labelDate, new Separator(Orientation.VERTICAL), labelModificationCount, labelProfileUserSystem);
    }

    @Override
    protected void updateItem(final ChangelogEntryI itemP, final boolean emptyP) {
        super.updateItem(itemP, emptyP);
        if (itemP == null || emptyP) {
            this.setGraphic(null);
        } else {
            labelDate.setText(StringUtils.dateToStringDateWithHour(itemP.getWhen()));
            labelModificationCount.setText(Translation.getText("changelog.entry.modification.count.label", itemP.getModificationCount()));
            labelProfileUserSystem.setText(Translation.getText("changelog.entry.profile.user.system", itemP.getProfileName(), Translation.getText(itemP.getSystem().getLabelID()), itemP.getSystemUserName()));
            this.setGraphic(this.boxContent);
        }
    }

}
