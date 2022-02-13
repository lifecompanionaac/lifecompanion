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

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.controller.editaction.PluginActions;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.controller.editmode.ConfigActionController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.javafx.FXControlUtils;

public class PluginInfoListCell extends ListCell<PluginInfo> {
    private GridPane gridPaneGraphics;
    private Label labelName, labelAuthor, labelDescription;
    private Label labelState;
    private Button buttonRemove;
    // TODO : config view ?

    public PluginInfoListCell(ListView<PluginInfo> listView) {
        super();
        DoubleBinding prefSizeForWrapText = listView.widthProperty().subtract(60.0);

        //Global
        this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        this.getStyleClass().add("list-cell-selection-disabled");
        //Labels
        this.labelName = new Label();
        this.labelName.getStyleClass().add("plugin-info-list-cell-name");
        GridPane.setHgrow(labelName, Priority.ALWAYS);
        labelName.setMaxWidth(Double.MAX_VALUE);

        this.labelAuthor = new Label();
        this.labelAuthor.getStyleClass().add("plugin-info-list-cell-author");

        this.labelDescription = new Label();
        this.labelDescription.getStyleClass().add("plugin-info-list-cell-description");
        this.labelDescription.setWrapText(true);
        this.labelDescription.prefWidthProperty().bind(prefSizeForWrapText);

        labelState = new Label();
        labelState.setWrapText(true);
        this.labelState.prefWidthProperty().bind(prefSizeForWrapText);

        buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(22).color(LCGraphicStyle.SECOND_DARK), "TODO");

        gridPaneGraphics = new GridPane();
        gridPaneGraphics.setHgap(5.0);
        gridPaneGraphics.setVgap(5.0);

        gridPaneGraphics.add(labelName, 0, 0);
        gridPaneGraphics.add(labelAuthor, 0, 1);
        gridPaneGraphics.add(labelDescription, 0, 2);
        gridPaneGraphics.add(labelState, 0, 3);
        gridPaneGraphics.add(buttonRemove, 1, 0, 1, 4);

        buttonRemove.setOnAction(e -> {
            if (this.getItem() != null) {
                ConfigActionController.INSTANCE.executeAction(new PluginActions.RemovePluginAction(buttonRemove, this.getItem()));
            }
        });
    }

    @Override
    protected void updateItem(final PluginInfo item, final boolean empty) {
        super.updateItem(item, empty);
        if (item == null || empty) {
            this.labelState.textFillProperty().unbind();
            this.labelState.textProperty().unbind();
            this.setGraphic(null);
            this.setText(null);
        } else {
            this.setGraphic(this.gridPaneGraphics);
            this.labelName.setText(item.getPluginName() + " - " + item.getPluginVersion() + " (" + StringUtils.dateToStringDateWithHour(item.getPluginBuildDate()) + ")");
            this.labelAuthor.setText(item.getPluginAuthor());
            this.labelState.textFillProperty().bind(Bindings.createObjectBinding(() -> item.stateProperty().get().getStateColor(), item.stateProperty()));
            this.labelState.textProperty().bind(Bindings.createStringBinding(() -> item.stateProperty().get().getStateText(), item.stateProperty()));
            this.labelDescription.setText(item.getPluginDescription());
        }
    }

}
