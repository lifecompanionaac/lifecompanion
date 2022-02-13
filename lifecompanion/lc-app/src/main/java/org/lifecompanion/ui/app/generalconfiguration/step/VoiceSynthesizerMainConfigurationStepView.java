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

package org.lifecompanion.ui.app.generalconfiguration.step;

import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceInfoI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerI;
import org.lifecompanion.model.api.voicesynthesizer.VoiceSynthesizerParameterI;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.controller.voicesynthesizer.VoiceSynthesizerController;
import org.lifecompanion.model.impl.voicesynthesizer.VoiceSynthesizerParameter;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStep;
import org.lifecompanion.ui.app.generalconfiguration.GeneralConfigurationStepViewI;
import org.lifecompanion.controller.editmode.GeneralConfigurationController;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.ui.common.pane.specific.voicesynthesizer.VoiceInfoStringConverter;
import org.lifecompanion.ui.common.pane.specific.cell.VoiceSynthesizerDetailListCell;
import org.lifecompanion.ui.common.pane.specific.cell.VoiceSynthesizerSimpleListCell;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;

public class VoiceSynthesizerMainConfigurationStepView extends BorderPane implements GeneralConfigurationStepViewI, LCViewInitHelper {
    private LCConfigurationI model;

    private ComboBox<VoiceSynthesizerI> comboboxVoiceEngine;
    private ChoiceBox<VoiceInfoI> choiceBoxVoice;
    private TextField fieldExample;
    private Button buttonPlayExample;
    private Slider sliderVolume;
    private Slider sliderRate;
    private Slider sliderPitch;
    private Button buttonOpenPrononciationException;

    public VoiceSynthesizerMainConfigurationStepView() {
        initAll();
    }

    @Override
    public boolean shouldBeAddedToMainMenu() {
        return true;
    }

    @Override
    public String getTitleId() {
        return "general.configuration.view.step.voice.synthesizer.title";
    }

    @Override
    public String getStep() {
        return GeneralConfigurationStep.VOICE_SYNTHESIZER_MAIN.name();
    }

    @Override
    public String getPreviousStep() {
        return null;
    }

    @Override
    public Node getViewNode() {
        return this;
    }


    // UI
    //========================================================================
    @Override
    public void initUI() {
        Label labelEngine = new Label(Translation.getText("voice.synthesizer.engine"));
        labelEngine.setMinWidth(GeneralConfigurationStepViewI.LEFT_COLUMN_MIN_WIDTH);
        Label labelVoice = new Label(Translation.getText("voice.synthesizer.voice"));
        //Create combobox fields
        this.comboboxVoiceEngine = new ComboBox<>(VoiceSynthesizerController.INSTANCE.getVoiceSynthesizers());
        this.comboboxVoiceEngine.setMaxWidth(Double.MAX_VALUE);
        this.comboboxVoiceEngine.setButtonCell(new VoiceSynthesizerSimpleListCell());
        this.comboboxVoiceEngine.setCellFactory((l) -> new VoiceSynthesizerDetailListCell());
        FXControlUtils.createAndAttachTooltip(comboboxVoiceEngine, "tooltip.explain.voice.synthesizer.voice.engine");

        this.choiceBoxVoice = new ChoiceBox<>();
        this.choiceBoxVoice.setMaxWidth(Double.MAX_VALUE);
        GridPane.setHgrow(choiceBoxVoice,Priority.ALWAYS);
        this.choiceBoxVoice.setConverter(new VoiceInfoStringConverter());
        FXControlUtils.createAndAttachTooltip(choiceBoxVoice, "tooltip.explain.voice.synthesizer.voice.select");

        //Example
        this.fieldExample = new TextField(Translation.getText("voice.synthesizer.test.default.text"));
        Label labelExample = new Label(Translation.getText("voice.synthesizer.test"));
        this.buttonPlayExample = FXControlUtils.createGraphicButton(
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.VOLUME_UP).size(18).color(LCGraphicStyle.MAIN_PRIMARY),
                "tooltip.voice.play.example");
        HBox.setHgrow(this.fieldExample, Priority.ALWAYS);
        HBox boxExample = new HBox(5, this.fieldExample, this.buttonPlayExample);
        boxExample.setAlignment(Pos.CENTER);

        this.buttonOpenPrononciationException = FXControlUtils.createSimpleTextButton(Translation.getText("voice.synthesizer.configure.pron.exceptions"), null);
        GridPane.setHalignment(buttonOpenPrononciationException, HPos.CENTER);

        //Parameters
        this.sliderVolume = FXControlUtils.createBaseSlider(0, 100, 100);
        this.sliderVolume.setMajorTickUnit(10.0);
        this.sliderVolume.setMinorTickCount(1);
        this.sliderRate = FXControlUtils.createBaseSlider(-10, 10, 0);
        this.sliderRate.setMajorTickUnit(5);
        this.sliderRate.setMinorTickCount(5);
        this.sliderPitch = FXControlUtils.createBaseSlider(-10, 10, 0);
        this.sliderPitch.setMajorTickUnit(5);
        this.sliderPitch.setMinorTickCount(5);

        //Grid layout
        GridPane grid = new GridPane();
        grid.setHgap(GeneralConfigurationStepViewI.GRID_H_GAP);
        grid.setVgap(GeneralConfigurationStepViewI.GRID_V_GAP);
        grid.setAlignment(Pos.TOP_CENTER);

        int gridRowIndex = 0;
        grid.add(FXControlUtils.createTitleLabel("voice.synthesizer.title.part.engine.and.voice"), 0, gridRowIndex++, 2, 1);
        grid.add(labelEngine, 0, gridRowIndex);
        grid.add(this.comboboxVoiceEngine, 1, gridRowIndex++);
        grid.add(labelVoice, 0, gridRowIndex);
        grid.add(this.choiceBoxVoice, 1, gridRowIndex++);

        grid.add(FXControlUtils.createTitleLabel("voice.synthesizer.title.part.test.synthesizer"), 0, gridRowIndex++, 2, 1);
        grid.add(labelExample, 0, gridRowIndex++);
        grid.add(boxExample, 0, gridRowIndex++, 2, 1);

        grid.add(FXControlUtils.createTitleLabel("voice.synthesizer.title.part.synthesizer.configuration"), 0, gridRowIndex++, 2, 1);
        grid.add(new Label(Translation.getText("voice.synthesizer.volume")), 0, gridRowIndex);
        grid.add(this.sliderVolume, 1, gridRowIndex++);
        grid.add(new Label(Translation.getText("voice.synthesizer.rate")), 0, gridRowIndex);
        grid.add(sliderRate, 1, gridRowIndex++);
        grid.add(new Label(Translation.getText("voice.synthesizer.pitch")), 0, gridRowIndex);
        grid.add(sliderPitch, 1, gridRowIndex++);

        grid.add(new Separator(Orientation.HORIZONTAL), 0, gridRowIndex++, 2, 1);
        grid.add(buttonOpenPrononciationException, 0, gridRowIndex, 2, 1);


        this.setPadding(new Insets(GeneralConfigurationStepViewI.PADDING));
        this.setCenter(grid);
    }

    @Override
    public void initListener() {
        //Open parameter box
        this.buttonOpenPrononciationException.setOnAction((ae) -> GeneralConfigurationController.INSTANCE.showStep(GeneralConfigurationStep.VOICE_SYNTHESIZER_EXCEPTIONS));

        // Set voices for the selected engine
        this.comboboxVoiceEngine.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.choiceBoxVoice.setItems(FXCollections.observableArrayList(nv.getVoices()));
            } else {
                this.choiceBoxVoice.setItems(FXCollections.emptyObservableList());
            }
        });
        //Button to test voice
        this.buttonPlayExample.setOnAction((ea) -> VoiceSynthesizerController.INSTANCE.speakAsync(this.fieldExample.getText(), applyUIChangeTo(new VoiceSynthesizerParameter()), null));
    }

    @Override
    public void initBinding() {
        //Model engine change
        BooleanBinding noEngineBinding = this.comboboxVoiceEngine.getSelectionModel().selectedItemProperty().isNull();
        this.choiceBoxVoice.disableProperty().bind(noEngineBinding);
        this.buttonPlayExample.disableProperty().bind(noEngineBinding);
        this.buttonOpenPrononciationException.disableProperty().bind(noEngineBinding);
    }
    //========================================================================


    @Override
    public void saveChanges() {
        applyUIChangeTo(model.getVoiceSynthesizerParameter());
    }

    private VoiceSynthesizerParameterI applyUIChangeTo(VoiceSynthesizerParameterI voiceParameters) {
        voiceParameters.selectedVoiceSynthesizerProperty().set(comboboxVoiceEngine.getValue());
        voiceParameters.getVoiceParameter().selectedVoiceInfoProperty().set(choiceBoxVoice.getValue());
        voiceParameters.volumeProperty().set((int) sliderVolume.getValue());
        voiceParameters.rateProperty().set((int) sliderRate.getValue());
        voiceParameters.pitchProperty().set((int) sliderPitch.getValue());
        return voiceParameters;
    }


    @Override
    public void bind(LCConfigurationI model) {
        this.model = model;
        this.comboboxVoiceEngine.getSelectionModel().select(model.getVoiceSynthesizerParameter().selectedVoiceSynthesizerProperty().get());
        this.choiceBoxVoice.getSelectionModel().select(model.getVoiceSynthesizerParameter().getVoiceParameter().selectedVoiceInfoProperty().get());
        this.sliderPitch.adjustValue(model.getVoiceSynthesizerParameter().pitchProperty().get());
        this.sliderVolume.adjustValue(model.getVoiceSynthesizerParameter().volumeProperty().get());
        this.sliderRate.adjustValue(model.getVoiceSynthesizerParameter().rateProperty().get());
    }

    @Override
    public void unbind(LCConfigurationI model) {
        this.model = null;
    }


}
