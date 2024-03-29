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
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.spellgame.SpellGamePlugin;
import org.lifecompanion.plugin.spellgame.SpellGamePluginProperties;
import org.lifecompanion.plugin.spellgame.model.SpellGameWordList;
import org.lifecompanion.plugin.spellgame.ui.cell.SpellGameWordListListCell;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.util.javafx.FXControlUtils;

import java.util.stream.Collectors;

public class SpellGameGeneralConfigView extends BorderPane implements GeneralConfigurationStepViewI {

    static final String STEP_ID = "SpellGameGeneralConfigView";

    private Spinner<Double> spinnerWordDisplayInS;
    private ComboBox<SpellGameWordList> comboBoxWordList;
    private Button buttonEditCurrentList, buttonDeleteCurrentList, buttonAddList, buttonShowResults;
    private ToggleSwitch toggleSwitchValidateWithEnter, toggleSwitchEnableFeedbackSound, toggleSwitchIgnoreAccents;
    private Slider sliderFeedbackVolume;

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

        toggleSwitchValidateWithEnter = FXControlUtils.createToggleSwitch("spellgame.plugin.config.view.field.validate.with.enter", "spellgame.plugin.config.view.field.validate.with.enter.tooltip");
        toggleSwitchIgnoreAccents = FXControlUtils.createToggleSwitch("spellgame.plugin.config.view.field.ignore.accents",
                "spellgame.plugin.config.view.field.ignore.accents.tooltip");
        toggleSwitchEnableFeedbackSound = FXControlUtils.createToggleSwitch("spellgame.plugin.config.view.field.enable.feedback.sound",
                "spellgame.plugin.config.view.field.enable.feedback.sound.tooltip");
        sliderFeedbackVolume = FXControlUtils.createBaseSlider(0.0, 1.0, 0.5);
        this.sliderFeedbackVolume.setShowTickLabels(false);
        this.sliderFeedbackVolume.setMajorTickUnit(0.1);
        this.sliderFeedbackVolume.setMinorTickCount(3);

        gridPaneConfiguration.add(labelWordDisplaySecond, 0, gridRowIndex);
        gridPaneConfiguration.add(spinnerWordDisplayInS, 1, gridRowIndex++);
        gridPaneConfiguration.add(toggleSwitchValidateWithEnter, 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(toggleSwitchIgnoreAccents, 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(toggleSwitchEnableFeedbackSound, 0, gridRowIndex++, 2, 1);
        gridPaneConfiguration.add(new Label(Translation.getText("spellgame.plugin.config.field.slider.volume")), 0, gridRowIndex);
        gridPaneConfiguration.add(sliderFeedbackVolume, 1, gridRowIndex++);


        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("spellgame.plugin.config.word.list.title.part"), 0, gridRowIndex++, 2, 1);

        comboBoxWordList = new ComboBox<>();
        comboBoxWordList.setButtonCell(new SpellGameWordListListCell());
        comboBoxWordList.setCellFactory(lv -> new SpellGameWordListListCell());
        HBox.setHgrow(comboBoxWordList, Priority.ALWAYS);
        comboBoxWordList.setMaxWidth(Double.MAX_VALUE);

        buttonEditCurrentList = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.modify.current.list"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.GEAR).size(16).color(LCGraphicStyle.MAIN_DARK),
                null);
        buttonDeleteCurrentList = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.delete.current.list"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH).size(16).color(LCGraphicStyle.SECOND_DARK),
                null);
        buttonAddList = FXControlUtils.createGraphicButton(GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.PLUS_CIRCLE).size(16).color(LCGraphicStyle.MAIN_DARK), null);

        final HBox hboxListAndAddButton = new HBox(5.0, comboBoxWordList, buttonAddList);
        hboxListAndAddButton.setAlignment(Pos.CENTER);
        gridPaneConfiguration.add(hboxListAndAddButton, 0, gridRowIndex++, 2, 1);

        final HBox hboxActionCurrentList = new HBox(5.0, buttonEditCurrentList, buttonDeleteCurrentList);
        hboxActionCurrentList.setAlignment(Pos.CENTER);
        gridPaneConfiguration.add(hboxActionCurrentList, 0, gridRowIndex++, 2, 1);

        gridPaneConfiguration.add(FXControlUtils.createTitleLabel("spellgame.plugin.config.word.list.history.part"), 0, gridRowIndex++, 2, 1);
        buttonShowResults = FXControlUtils.createLeftTextButton(Translation.getText("spellgame.plugin.config.button.show.results"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.BARS).size(16).color(LCGraphicStyle.MAIN_DARK),
                null);
        gridPaneConfiguration.add(buttonShowResults, 0, gridRowIndex++, 2, 1);
        GridPane.setHalignment(buttonShowResults, HPos.CENTER);

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
        buttonShowResults.setOnAction(e -> GeneralConfigurationController.INSTANCE.showStep(SpellGameResultConfigView.STEP_ID));
    }

    @Override
    public void initBinding() {
        this.buttonDeleteCurrentList.disableProperty().bind(comboBoxWordList.getSelectionModel().selectedItemProperty().isNull());
        this.buttonEditCurrentList.disableProperty().bind(comboBoxWordList.getSelectionModel().selectedItemProperty().isNull());
        this.sliderFeedbackVolume.disableProperty().bind(toggleSwitchEnableFeedbackSound.selectedProperty().not());
    }

    private LCConfigurationI configuration;

    @Override
    public void saveChanges() {
        SpellGamePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
        pluginConfigProperties.wordDisplayTimeInMsProperty().set((int) (spinnerWordDisplayInS.getValue() * 1000.0));
        pluginConfigProperties.ignoreAccentsProperty().set(toggleSwitchIgnoreAccents.isSelected());
        pluginConfigProperties.enableFeedbackSoundProperty().set(toggleSwitchEnableFeedbackSound.isSelected());
        pluginConfigProperties.feedbacksVolumeProperty().set(sliderFeedbackVolume.getValue());
        pluginConfigProperties.validateWithEnterProperty().set(toggleSwitchValidateWithEnter.isSelected());
        ObservableList<SpellGameWordList> items = comboBoxWordList.getItems();
        pluginConfigProperties.getWordLists().setAll(items);
    }

    @Override
    public void bind(LCConfigurationI model) {
        this.configuration = model;
        SpellGamePluginProperties pluginConfigProperties = configuration.getPluginConfigProperties(SpellGamePlugin.ID, SpellGamePluginProperties.class);
        spinnerWordDisplayInS.getValueFactory().setValue(pluginConfigProperties.wordDisplayTimeInMsProperty().get() / 1000.0);
        toggleSwitchIgnoreAccents.setSelected(pluginConfigProperties.ignoreAccentsProperty().get());
        toggleSwitchEnableFeedbackSound.setSelected(pluginConfigProperties.enableFeedbackSoundProperty().get());
        sliderFeedbackVolume.setValue(pluginConfigProperties.feedbacksVolumeProperty().get());
        toggleSwitchValidateWithEnter.setSelected(pluginConfigProperties.validateWithEnterProperty().get());
        this.comboBoxWordList.setItems(FXCollections.observableArrayList(pluginConfigProperties.getWordLists().stream().map(l -> (SpellGameWordList) l.duplicate(false)).collect(Collectors.toList())));
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.configuration = null;
        this.comboBoxWordList.setItems(null);
    }

    @Override
    public boolean shouldCancelBeConfirmed() {
        // FIXME : implement correctly
        return GeneralConfigurationStepViewI.super.shouldCancelBeConfirmed();
    }
}
