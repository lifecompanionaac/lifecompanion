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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameWordListListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.stream.Collectors;

public class SpellGameGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {

    static final String STEP_ID = "SpellGameGeneralConfigView";

    private Spinner<Double> spinnerWordDisplayInS;
    private ComboBox<SpellGameWordList> comboBoxWordList;
    private Button buttonEditCurrentList, buttonDeleteCurrentList, buttonAddList;

    public SpellGameGeneralConfigView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "spellgame.plugin.config.view.general.title";
    }

    @Override
    public String getStep() {
        return STEP_ID;
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }

    @Override
    public void initUI() {
        this.spinnerWordDisplayInS = FXControlUtils.createDoubleSpinner(0.1, 60, 5.0, 1, GeneralConfigurationStepViewI.FIELD_WIDTH);

        GridPane gridPaneConfiguration = new GridPane();
        gridPaneConfiguration.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        gridPaneConfiguration.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);

        int gridRowIndex = 0;
        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("spellgame.plugin.config.general.configuration.title.part"), 0, gridRowIndex++, 2, 1);
        Label labelWordDisplaySecond = new Label(Translation.getText("spellgame.plugin.config.view.field.word.display.second"));
        GridPane.setHgrow(labelWordDisplaySecond, Priority.ALWAYS);
        labelWordDisplaySecond.setMaxWidth(Double.MAX_VALUE);
        gridPaneConfiguration.add(labelWordDisplaySecond, 0, gridRowIndex);
        gridPaneConfiguration.add(spinnerWordDisplayInS, 1, gridRowIndex++);

        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("spellgame.plugin.config.word.list.title.part"), 0, gridRowIndex++, 2, 1);

        comboBoxWordList = new ComboBox<>();
        comboBoxWordList.setButtonCell(new SpellGameWordListListCell());
        comboBoxWordList.setCellFactory(lv -> new SpellGameWordListListCell());
        HBox.setHgrow(comboBoxWordList, Priority.ALWAYS);
        comboBoxWordList.setMaxWidth(Double.MAX_VALUE);

        buttonEditCurrentList = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.modify.current.list"), GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(16).color(LCGraphicStyle.MAIN_DARK), null);
        buttonDeleteCurrentList = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.delete.current.list"), GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(16).color(LCGraphicStyle.SECOND_DARK), null);
        buttonAddList = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);

        final HBox hboxListAndAddButton = new HBox(5.0, comboBoxWordList, buttonAddList);
        hboxListAndAddButton.setAlignment(Pos.CENTER);
        gridPaneConfiguration.add(hboxListAndAddButton, 0, gridRowIndex++, 2, 1);

        final HBox hboxActionCurrentList = new HBox(5.0, buttonEditCurrentList, buttonDeleteCurrentList);
        hboxActionCurrentList.setAlignment(Pos.CENTER);
        gridPaneConfiguration.add(hboxActionCurrentList, 0, gridRowIndex++, 2, 1);

        gridPaneConfiguration.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(gridPaneConfiguration);
    }

    @Override
    public void initListener() {
        this.buttonAddList.setOnAction(e -> {
            SpellGameWordList spellGameWordList = new SpellGameWordList();
            spellGameWordList.nameProperty().set(Translation.getText("spellgame.plugin.default.list.name"));
            this.comboBoxWordList.getItems().add(spellGameWordList);
            this.comboBoxWordList.getSelectionModel().select(spellGameWordList);
        });
        this.buttonEditCurrentList.setOnAction(e -> {
            SpellGameWordList selectedItem = this.comboBoxWordList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                GeneralConfigurationController.INSTANCE.showStep(SpellGameWordListConfigView.STEP_ID, selectedItem);
            }
        });
        this.buttonDeleteCurrentList.setOnAction(e -> {
            SpellGameWordList selectedItem = this.comboBoxWordList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                this.comboBoxWordList.getItems().remove(selectedItem);
            }
        });
    }

    @Override
    public void initBinding() {
        this.buttonDeleteCurrentList.disableProperty().bind(comboBoxWordList.getSelectionModel().selectedItemProperty().isNull());
        this.buttonEditCurrentList.disableProperty().bind(comboBoxWordList.getSelectionModel().selectedItemProperty().isNull());
    }

    private LCConfigurationI configuration;

    @Override
    public void saveChanges() {
        SpellGamePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
        pluginConfigProperties.wordDisplayTimeInMsProperty().set((int) (spinnerWordDisplayInS.getValue() * 1000.0));
        ObservableList<SpellGameWordList> items = comboBoxWordList.getItems();
        pluginConfigProperties.getWordLists().setAll(items);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        SpellGamePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
        spinnerWordDisplayInS.getValueFactory().setValue(pluginConfigProperties.wordDisplayTimeInMsProperty().get() / 1000.0);
        this.comboBoxWordList.setItems(FXCollections.observableArrayList(pluginConfigProperties.getWordLists().stream().map(l -> (SpellGameWordList) l.duplicate(false)).collect(Collectors.toList())));
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
        this.comboBoxWordList.setItems(null);
    }
}
