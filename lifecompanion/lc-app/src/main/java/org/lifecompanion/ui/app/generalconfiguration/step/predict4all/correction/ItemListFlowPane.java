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

package org.lifecompanion.ui.app.generalconfiguration.step.predict4all.correction;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

public class ItemListFlowPane extends FlowPane implements LCViewInitHelper {
    private Button buttonAdd;
    private Runnable changeListener;

    public ItemListFlowPane() {
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        this.setVgap(3.0);
        this.setHgap(5.0);
        this.setAlignment(Pos.CENTER_LEFT);

        this.buttonAdd = FXControlUtils.createGraphicButton(
                GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.PLUS_CIRCLE).size(18).color(LCGraphicStyle.MAIN_DARK), "tooltip.button.add.element.item.flow");
        this.buttonAdd.getStyleClass().add("item-flow-pane-add-button");

        this.getChildren().add(this.buttonAdd);
    }

    @Override
    public void initListener() {
        this.buttonAdd.setOnAction(e -> this.addItem());
    }
    //========================================================================

    private void addItem() {
        ItemPane element = new ItemPane();
        this.getChildren().add(this.getChildren().size() - 1, element);
        element.edit(true);
    }

    // Class part : "MODEL"
    //========================================================================
    public void setOnChangeListener(final Runnable changeListener) {
        this.changeListener = changeListener;
    }

    private void fireChange() {
        if (this.changeListener != null) {
            this.changeListener.run();
        }
    }

    public String[] getItems() {
        return this.getChildren().stream().filter(n -> n instanceof ItemPane).map(n -> ((ItemPane) n).getValue()).toArray(String[]::new);
    }

    public void setItems(final String[] items) {
        this.getChildren().removeIf(n -> n instanceof ItemPane);
        if (items != null) {
            for (String item : items) {
                this.getChildren().add(this.getChildren().size() - 1, new ItemPane().setValue(item));
            }
        }
    }

    private void removeItem(final ItemPane item) {
        this.getChildren().remove(item);
        this.fireChange();
    }
    //========================================================================

    private class ItemPane extends HBox implements LCViewInitHelper {
        private TextField editLabel;
        private Label label;
        private Button buttonRemove;

        public ItemPane() {
            this.initAll();
            this.edit(false);
        }

        @Override
        public void initUI() {
            this.label = new Label();
            this.label.setMaxWidth(Double.MAX_VALUE);
            this.label.getStyleClass().add("item-list-flow-pane-label");
            HBox.setHgrow(this.label, Priority.ALWAYS);

            this.editLabel = new TextField();
            this.editLabel.getStyleClass().add("item-list-flow-pane-field");

            this.buttonRemove = FXControlUtils.createGraphicButton(
                    GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.TIMES_CIRCLE).size(12).color(LCGraphicStyle.LC_GRAY), null);
            this.buttonRemove.getStyleClass().add("small-button");

            this.setAlignment(Pos.CENTER);
            this.setSpacing(5.0);
            this.getChildren().addAll(this.label, this.editLabel, this.buttonRemove);
            this.getStyleClass().add("item-list-flow-pane-rec");
        }

        public String getValue() {
            return this.editLabel.isFocused() ? this.editLabel.getText() : this.label.getText();
        }

        public ItemPane setValue(final String val) {
            this.label.setText(P4ACorrectionTranslateUtils.getConvertedStringSeparator(val));
            return this;
        }

        @Override
        public void initListener() {
            this.buttonRemove.setOnAction(a -> ItemListFlowPane.this.removeItem(this));
            this.label.setOnMouseClicked(me -> {
                if (me.getClickCount() > 1) {
                    this.edit(true);
                }
            });
            this.editLabel.setOnAction(e -> this.edit(false));
            this.editLabel.focusedProperty().addListener((obs, ov, nv) -> {
                if (!nv) {
                    this.edit(false);
                }
            });
        }

        private void edit(final boolean edit) {
            this.editLabel.setManaged(edit);
            this.editLabel.setVisible(edit);
            this.label.setManaged(!edit);
            this.label.setVisible(!edit);
            this.label.setText(P4ACorrectionTranslateUtils.getConvertedStringSeparator(this.editLabel.getText()));
            if (edit) {
                this.editLabel.requestFocus();
            } else {
                this.label.setText(P4ACorrectionTranslateUtils.getConvertedStringSeparator(this.editLabel.getText()));
                ItemListFlowPane.this.fireChange();
            }
        }

    }

}
