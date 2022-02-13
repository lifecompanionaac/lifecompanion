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

import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.GlyphFontRegistry;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.function.Consumer;

public class CorrectionRuleStringListView extends BorderPane implements LCViewInitHelper {
    private ListView<String> listViewStrings;
    private TextField fieldRuleToAdd;

    public CorrectionRuleStringListView() {
        this.initAll();
    }

    @Override
    public void initUI() {
        this.listViewStrings = new ListView<>();
        this.listViewStrings.setCellFactory(lv -> new CorrectionRuleStringListCell());
        this.listViewStrings.setMaxHeight(50.0);
        this.listViewStrings.setFixedCellSize(13.0);
        this.listViewStrings.setSelectionModel(new MultipleSelectionModel<String>() {

            @Override
            public void selectPrevious() {
            }

            @Override
            public void selectNext() {
            }

            @Override
            public void select(final String obj) {
            }

            @Override
            public void select(final int index) {
            }

            @Override
            public boolean isSelected(final int index) {
                return false;
            }

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public void clearSelection(final int index) {
            }

            @Override
            public void clearSelection() {
            }

            @Override
            public void clearAndSelect(final int index) {
            }

            @Override
            public void selectLast() {
            }

            @Override
            public void selectIndices(final int index, final int... indices) {
            }

            @Override
            public void selectFirst() {
            }

            @Override
            public void selectAll() {
            }

            @Override
            public ObservableList<String> getSelectedItems() {
                return FXCollections.emptyObservableList();
            }

            @Override
            public ObservableList<Integer> getSelectedIndices() {
                return FXCollections.emptyObservableList();
            }
        });

        this.fieldRuleToAdd = new TextField();
        this.fieldRuleToAdd.setPromptText("Ajouter une modification (avec Entrée)");

        HBox boxButtons = new HBox(5.0, this.fieldRuleToAdd);
        HBox.setHgrow(this.fieldRuleToAdd, Priority.ALWAYS);
        boxButtons.setAlignment(Pos.CENTER);

        this.setCenter(this.listViewStrings);
        this.setBottom(boxButtons);
        BorderPane.setMargin(boxButtons, new Insets(5.0, 0.0, 0.0, 0.0));
    }

    @Override
    public void initListener() {
        LCViewInitHelper.super.initListener();
        this.fieldRuleToAdd.setOnAction(this::addCurrentRule);
    }

    @Override
    public void initBinding() {
        LCViewInitHelper.super.initBinding();
    }

    private void addCurrentRule(final ActionEvent e) {
        String str = this.fieldRuleToAdd.getText();
        if (str != null) {
            this.fieldRuleToAdd.clear();
            this.listViewStrings.getItems().add(str);
        }
        this.listViewStrings.scrollTo(this.listViewStrings.getItems().size() - 1);
    }

    public void setItems(final String[] items) {
        this.listViewStrings.getItems().clear();
        if (items != null) {
            this.listViewStrings.getItems().addAll(items);
        }
    }

    public String[] getItems() {
        return this.listViewStrings.getItems().toArray(new String[0]);
    }

    public void setChangeListener(final Consumer<String[]> consumer) {
        this.listViewStrings.getItems().addListener((InvalidationListener) inv -> consumer.accept(this.getItems()));
    }

    private class CorrectionRuleStringListCell extends ListCell<String> {
        private HBox boxTotal;
        private Label label;
        private Button buttonRemove;

        public CorrectionRuleStringListCell() {
            this.label = new Label();
            this.label.setMaxWidth(Double.MAX_VALUE);
            this.label.getStyleClass().add("correction-rule-list-view-label");
            this.buttonRemove = FXControlUtils.createGraphicButton(
                    GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.TRASH).size(10).color(LCGraphicStyle.SECOND_DARK), "TODO");
            this.buttonRemove.getStyleClass().add("small-button");
            this.boxTotal = new HBox(0.0, this.label, this.buttonRemove);
            HBox.setHgrow(this.label, Priority.ALWAYS);
            this.setGraphic(this.boxTotal);
            this.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);

            this.buttonRemove.setOnAction(a -> {
                int index = this.getIndex();
                if (index >= 0) {
                    CorrectionRuleStringListView.this.listViewStrings.getItems().remove(index);
                }
            });
        }

        @Override
        protected void updateItem(final String item, final boolean empty) {
            super.updateItem(item, empty);
            if (item != null && !empty) {
                this.label.setText(P4ACorrectionTranslateUtils.getConvertedStringSeparator(item));
                this.setGraphic(this.boxTotal);
            } else {
                this.setGraphic(null);
            }
        }

    }

}
