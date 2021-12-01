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
package org.lifecompanion.config.view.pane.voice;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.api.component.definition.VoiceSynthesizerUserI;
import org.lifecompanion.api.voice.PronunciationExceptionI;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.base.data.config.LCGraphicStyle;
import org.lifecompanion.base.data.voice.PronunciationException;
import org.lifecompanion.base.data.voice.VoiceSynthesizerController;
import org.lifecompanion.config.data.config.LCGlyphFont;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * To show and modify pronunciation exception list
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class PronunciationExceptionView extends BorderPane implements LCViewInitHelper {
    private TableView<PronunciationExceptionI> tableViewExceptions;
    private TableColumn<PronunciationExceptionI, String> originalWordColumn, replaceWordColumn;
    private Button buttonAdd, buttonRemove, buttonSpeak;

    private VoiceSynthesizerUserI voiceSynthesizerUser;
    private ObservableList<PronunciationExceptionI> items;

    public PronunciationExceptionView() {
        items = FXCollections.observableArrayList();
        this.initAll();
    }

    // Class part : "UI"
    //========================================================================
    @Override
    public void initUI() {
        //Table
        this.tableViewExceptions = new TableView<>(items);
        this.tableViewExceptions.setPrefHeight(150);
        this.tableViewExceptions.setPrefWidth(270);
        this.tableViewExceptions.setEditable(true);
        Label labelPlaceholder = new Label(Translation.getText("pronunciation.exception.empty.list.placeholder"));
        labelPlaceholder.setWrapText(true);
        labelPlaceholder.setPadding(new Insets(10.0));
        this.tableViewExceptions.setPlaceholder(labelPlaceholder);
        BorderPane.setMargin(this.tableViewExceptions, new Insets(0.0, 5.0, 0.0, 5.0));
        //Original column
        this.originalWordColumn = new TableColumn<>(Translation.getText("pronunciation.exception.column.original"));
        this.originalWordColumn.prefWidthProperty().bind(this.tableViewExceptions.widthProperty().subtract(5.0).multiply(0.5));
        this.originalWordColumn.setResizable(false);
        this.originalWordColumn.setReorderable(false);
        this.originalWordColumn.setSortable(false);
        this.originalWordColumn.setEditable(true);
        this.originalWordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        this.replaceWordColumn = new TableColumn<>(Translation.getText("pronunciation.exception.column.replace"));
        this.replaceWordColumn.prefWidthProperty().bind(this.tableViewExceptions.widthProperty().subtract(5.0).multiply(0.5));
        this.replaceWordColumn.setResizable(false);
        this.replaceWordColumn.setReorderable(false);
        this.replaceWordColumn.setSortable(false);
        this.replaceWordColumn.setEditable(true);
        this.replaceWordColumn.setCellFactory(TextFieldTableCell.forTableColumn());

        //Add to table
        this.tableViewExceptions.getColumns().add(this.originalWordColumn);
        this.tableViewExceptions.getColumns().add(this.replaceWordColumn);

        //Buttons
        this.buttonAdd = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(14).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.pronunciation.exception.add.button");
        this.buttonRemove = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(14).color(LCGraphicStyle.SECOND_PRIMARY),
                "tooltip.pronunciation.exception.remove.button");
        this.buttonSpeak = UIUtils.createGraphicButton(
                LCGlyphFont.FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP).size(14).color(LCGraphicStyle.MAIN_DARK),
                "tooltip.pronunciation.exception.speak.button");
        HBox boxButtons = new HBox(10, this.buttonAdd, this.buttonRemove, this.buttonSpeak);

        //Total
        this.setPadding(new Insets(20.0));
        this.setCenter(this.tableViewExceptions);
        this.setBottom(boxButtons);
    }

    @Override
    public void initBinding() {
        this.originalWordColumn.setCellValueFactory((data) -> data.getValue() != null ? data.getValue().originalTextProperty() : null);
        this.replaceWordColumn.setCellValueFactory((data) -> data.getValue() != null ? data.getValue().replaceTextProperty() : null);
        this.buttonRemove.disableProperty().bind(this.tableViewExceptions.getSelectionModel().selectedItemProperty().isNull());
        this.buttonSpeak.disableProperty().bind(this.tableViewExceptions.getSelectionModel().selectedItemProperty().isNull());
    }

    @Override
    public void initListener() {
        this.buttonAdd.setOnAction((event) -> {
            PronunciationException added = new PronunciationException();
            items.add(added);
            // TODO : focus on original field
            this.tableViewExceptions.edit(items.indexOf(added), this.originalWordColumn);
        });
        this.buttonRemove.setOnAction((event) -> {
            PronunciationExceptionI selectedItem = this.tableViewExceptions.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                items.remove(selectedItem);
            }
        });
        this.buttonSpeak.setOnAction((event) -> {
            PronunciationExceptionI selectedItem = this.tableViewExceptions.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                VoiceSynthesizerController.INSTANCE.speakAsync(selectedItem.replaceTextProperty().get(), voiceSynthesizerUser.getVoiceSynthesizerParameter(), null);
            }
        });
        this.originalWordColumn.setOnEditCommit((editEvent) -> editEvent.getRowValue().originalTextProperty().set(editEvent.getNewValue()));
        this.replaceWordColumn.setOnEditCommit((editEvent) -> editEvent.getRowValue().replaceTextProperty().set(editEvent.getNewValue()));
    }
    //========================================================================

    // Class part : "Binding"
    //========================================================================
    public void setVoiceSynthesizerUser(final VoiceSynthesizerUserI model) {
        voiceSynthesizerUser = model;
        if (model != null) {
            items.addAll(model.getVoiceSynthesizerParameter().getPronunciationExceptions().stream().map(PronunciationExceptionI::clone).collect(Collectors.toList()));
        } else {
            items.clear();
        }
    }

    public List<PronunciationExceptionI> getModifiedPronunciationExceptions() {
        return this.items;
    }
    //========================================================================

}
