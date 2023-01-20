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

package org.lifecompanion.plugin.spellgame.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.controller.task.ImportWordTask;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameWordListListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.common.control.generic.OrderModifiableListView;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.lifecompanion.util.model.LCTask;
import spark.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpellGameWordListConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "SpellGameWordListConfigView";

    private TextField fieldListName, fieldAddWord;
    private ListView<String> listViewWords;
    private Button buttonAdd, buttonRemove, buttonUp, buttonDown, buttonImportWordList;
    // shuffle

    public SpellGameWordListConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return false;
    }

    @Override
    public String getTitleId() {
        return "spellgame.plugin.config.view.wordlist.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
    }

    @Override
    public String getPreviousStep() {
        return SpellGameGeneralConfigView.STEP_ID;
    }

    @Override
    public String getMenuStepToSelect() {
        return SpellGameGeneralConfigView.STEP_ID;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        fieldListName = new TextField();
        HBox.setHgrow(fieldListName, Priority.ALWAYS);
        fieldListName.setMaxWidth(Double.MAX_VALUE);
        Label labelListName = new Label(Translation.getText("spellgame.plugin.config.field.list.name"));
        HBox.setHgrow(labelListName, Priority.ALWAYS);
        labelListName.setMaxWidth(Double.MAX_VALUE);
        HBox boxListName = new HBox(5.0, labelListName, fieldListName);
        boxListName.setAlignment(Pos.CENTER);
        this.setTop(boxListName);

        // Word list
        listViewWords = new ListView<>();
        listViewWords.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        BorderPane.setMargin(listViewWords, new Insets(5.0, 5.0, 5.0, 0));
        this.setCenter(listViewWords);

        //Button on right
        this.buttonRemove = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT)
                .size(20).color(LCGraphicStyle.SECOND_PRIMARY), null);
        this.buttonUp = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_UP).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.buttonDown = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_DOWN)
                .size(20).color(LCGraphicStyle.MAIN_DARK), null);
        VBox buttons = new VBox(this.buttonRemove, this.buttonUp, this.buttonDown);
        buttons.setAlignment(Pos.CENTER);
        this.setRight(buttons);

        // Add field
        fieldAddWord = new TextField();
        fieldAddWord.setPromptText(Translation.getText("spellgame.plugin.config.field.add.word.prompt"));
        HBox.setHgrow(fieldAddWord, Priority.ALWAYS);
        fieldAddWord.setMaxWidth(Double.MAX_VALUE);
        Label labelAddWord = new Label(Translation.getText("spellgame.plugin.config.field.add.word"));
        HBox.setHgrow(labelAddWord, Priority.ALWAYS);
        labelAddWord.setMaxWidth(Double.MAX_VALUE);
        this.buttonAdd = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE)
                .size(20).color(LCGraphicStyle.MAIN_PRIMARY), null);
        HBox boxAddPart = new HBox(5.0, labelAddWord, fieldAddWord, buttonAdd);
        boxAddPart.setAlignment(Pos.CENTER);

        this.buttonImportWordList = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.field.import.list"), GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(14).color(LCGraphicStyle.MAIN_DARK), null);
        this.buttonImportWordList.setMaxWidth(Double.MAX_VALUE);
        this.buttonImportWordList.setAlignment(Pos.CENTER);
        VBox boxWordsNameAndImport = new VBox(5.0, boxAddPart, buttonImportWordList);

        this.setBottom(boxWordsNameAndImport);

        this.setPadding(new Insets(PADDING));
    }

    @Override
    public void initListener() {
        this.fieldAddWord.setOnAction(e -> addCurrentWord());
        this.buttonAdd.setOnAction(e -> addCurrentWord());
        this.buttonRemove.setOnAction(e -> {
            List<String> toDelete = new ArrayList<>(this.listViewWords.getSelectionModel().getSelectedItems());
            this.listViewWords.getItems().removeAll(toDelete);
        });
        this.buttonDown.setOnAction(e -> swapItemInList(1));
        this.buttonUp.setOnAction(e -> swapItemInList(-1));
        this.buttonImportWordList.setOnAction(e -> {
            File selectedFileToImport = LCFileChoosers.getOtherFileChooser(
                            Translation.getText("spellgame.plugin.config.field.import.list.chooser.title"),
                            new FileChooser.ExtensionFilter(Translation.getText("spellgame.plugin.config.import.list.format"), Collections.singletonList("*.txt")),
                            FileChooserType.OTHER_MISC_EXTERNAL)
                    .showOpenDialog(FXUtils.getSourceWindow(buttonImportWordList));
            if (selectedFileToImport != null) {
                ImportWordTask importWordTask = new ImportWordTask(selectedFileToImport);
                importWordTask.setOnSucceeded(event -> this.listViewWords.getItems().addAll(importWordTask.getValue()));
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, importWordTask);
            }
        });
    }

    private void swapItemInList(int change) {
        int selectedIndex = this.listViewWords.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            int newIndex = selectedIndex + change;
            if (newIndex >= 0 && newIndex < this.listViewWords.getItems().size()) {
                Collections.swap(this.listViewWords.getItems(), selectedIndex, newIndex);
                this.listViewWords.getSelectionModel().select(newIndex);
            }
        }
    }

    private void addCurrentWord() {
        String text = fieldAddWord.getText();
        if (StringUtils.isNotBlank(text)) {
            this.listViewWords.getItems().add(text);
            fieldAddWord.clear();
        }
    }

    @Override
    public void initBinding() {
        this.buttonUp.disableProperty().bind(this.listViewWords.getSelectionModel().selectedItemProperty().isNull());
        this.buttonDown.disableProperty().bind(this.listViewWords.getSelectionModel().selectedItemProperty().isNull());
        this.buttonRemove.disableProperty().bind(this.listViewWords.getSelectionModel().selectedItemProperty().isNull());
    }

    private SpellGameWordList editedWordList;

    @Override
    public void beforeShow(Object[] stepArgs) {
        editedWordList = (SpellGameWordList) stepArgs[0];
        fieldListName.textProperty().bindBidirectional(editedWordList.nameProperty());
        this.listViewWords.setItems(editedWordList.getWords());
    }

    @Override
    public void afterHide() {
        fieldListName.textProperty().unbindBidirectional(editedWordList.nameProperty());
        this.listViewWords.setItems(null);
    }

    @Override
    public void bind(LCConfigurationI model) {
    }

    @Override
    public void unbind(LCConfigurationI model) {
    }

    @Override
    public void saveChanges() {
    }
}
