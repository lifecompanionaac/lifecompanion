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

package org.lifecompanion.util;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.model.Triple;

import java.util.function.Consumer;

public class UIControlHelper {

    public static Triple<HBox, Label, Node> createHeader(String titleId, Consumer<Node> previousCallback) {
        Label labelTitle = new Label(Translation.getText(titleId));
        labelTitle.getStyleClass().addAll("text-h3", "text-fill-white");
        labelTitle.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelTitle, Priority.ALWAYS);
        HBox.setMargin(labelTitle, new Insets(8.0));
        HBox boxTop = new HBox(labelTitle);

        Node nodePrevious = null;
        if (previousCallback != null) {
            boxTop.getStyleClass().add("opacity-80-hover");
            Glyph iconPrevious = GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(16).color(Color.WHITE);
            HBox.setMargin(iconPrevious, new Insets(10.0, 0.0, 10.0, 10.0));
            boxTop.getChildren().add(0, iconPrevious);
            boxTop.setOnMouseClicked(e -> {
                if (iconPrevious.isVisible()) {
                    previousCallback.accept(boxTop);
                }
            });
            Tooltip.install(boxTop, UIUtils.createTooltip(Translation.getText("profile.config.selection.steps.previous.button.tooltip")));
            nodePrevious = iconPrevious;
        }
        boxTop.setAlignment(Pos.CENTER_LEFT);
        boxTop.getStyleClass().addAll("background-primary-dark", "border-transparent");
        boxTop.setPrefHeight(50.0);

        return Triple.of(boxTop, labelTitle, nodePrevious);
    }

    public static Node createActionTableEntry(String actionTranslationId, Node buttonGraphic, Runnable action) {
        Label labelTitle = new Label(Translation.getText(actionTranslationId + ".title"));
        labelTitle.getStyleClass().add("text-weight-bold");
        GridPane.setHgrow(labelTitle, Priority.ALWAYS);
        GridPane.setFillWidth(labelTitle, true);
        labelTitle.setMaxWidth(Double.MAX_VALUE);

        Label labelDescription = new Label(Translation.getText(actionTranslationId + ".description"));
        labelDescription.setWrapText(true);
        GridPane.setMargin(labelDescription, new Insets(0, 0, 20.0, 0));

        Button buttonAction = UIUtils.createGraphicButton(buttonGraphic, actionTranslationId + ".description");
        buttonAction.setMinWidth(50.0);
        GridPane.setValignment(buttonAction, VPos.TOP);

        GridPane actionPane = new GridPane();
        actionPane.setVgap(5.0);
        actionPane.setHgap(5.0);
        actionPane.add(labelTitle, 0, 0);
        actionPane.add(labelDescription, 0, 1);
        actionPane.add(buttonAction, 1, 0, 1, 2);

        actionPane.setMaxWidth(Double.MAX_VALUE);

        if (action != null) {
            actionPane.setOnMouseClicked(me -> action.run());
            buttonAction.setOnAction(me -> action.run());
            actionPane.getStyleClass().addAll("opacity-60-pressed", "opacity-80-hover");
        }
        return actionPane;
    }

    public static Label createTitleLabel(String titleId) {
        Label label = new Label(Translation.getText(titleId));
        label.getStyleClass().addAll("text-font-size-110", "border-bottom-gray", "text-fill-dimgrey");
        label.setTextAlignment(TextAlignment.LEFT);
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }
}
