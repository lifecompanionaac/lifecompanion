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
package org.lifecompanion.ui.app.generalconfiguration.step.predict4all;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.lifecompanion.ui.controlsfx.control.ToggleSwitch;
import org.lifecompanion.ui.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.ui.controlsfx.glyphfont.GlyphFontRegistry;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.predict4all.nlp.words.model.Word;

import java.util.Date;

public class WordDetailPane extends VBox implements LCViewInitHelper {

    private Word word;
    private Label labelWord;
    private ToggleSwitch toggleForceValid, toggleForceInvalid;
    private Slider sliderFactor;
    private Button buttonResetSlider;
    private Label labelGeneralInfo, labelUpdateDate;
    private final ListView<Word> listViewWords;

    public WordDetailPane(final ListView<Word> listViewWords) {
        this.listViewWords = listViewWords;
        this.initAll();
    }

    @Override
    public void initUI() {
        // Top label : word
        this.labelWord = new Label();
        this.labelWord.setAlignment(Pos.CENTER);
        this.labelWord.setMaxWidth(Double.MAX_VALUE);
        this.labelWord.setStyle("-fx-font-size: 14.0px;-fx-font-weight: bold;");

        // Word configuration
        this.toggleForceInvalid = new ToggleSwitch(Translation.getText("predict4all.config.force.invalid.word"));
        FXControlUtils.createAndAttachTooltip(this.toggleForceInvalid, "predict4all.config.force.invalid.word.tooltip");
        this.toggleForceInvalid.setMaxWidth(Double.MAX_VALUE);

        this.toggleForceValid = new ToggleSwitch(Translation.getText("predict4all.config.force.valid.word"));
        FXControlUtils.createAndAttachTooltip(this.toggleForceValid, "predict4all.config.force.valid.word.tooltip");
        this.toggleForceValid.setMaxWidth(Double.MAX_VALUE);

        this.sliderFactor = FXControlUtils.createBaseSlider(-P4AConfigUtils.FACTOR_BOUND, P4AConfigUtils.FACTOR_BOUND, 0.0);
        this.sliderFactor.setMajorTickUnit(P4AConfigUtils.FACTOR_BOUND);
        this.sliderFactor.setMinorTickCount(9);
        this.sliderFactor.setSnapToTicks(false);
        this.sliderFactor.setShowTickLabels(true);
        this.sliderFactor.setShowTickMarks(true);
        this.sliderFactor.setLabelFormatter(WordDetailPane.STR_CONVERTER_FACTOR);
        this.buttonResetSlider = FXControlUtils.createGraphicButton(
                GlyphFontRegistry.font("FontAwesome").create(FontAwesome.Glyph.UNDO).size(12).color(LCGraphicStyle.SECOND_DARK), "todo");
        this.buttonResetSlider.setPadding(new Insets(0.0, 0.0, 3.0, 0.0));

        HBox.setHgrow(this.sliderFactor, Priority.ALWAYS);
        HBox.setMargin(this.sliderFactor, new Insets(0.0, 3.0, 0.0, 12.0));
        HBox boxSlider = new HBox(this.sliderFactor, this.buttonResetSlider);

        // Supp informations
        this.labelGeneralInfo = new Label();
        this.labelGeneralInfo.setWrapText(true);
        this.labelUpdateDate = new Label();
        this.labelUpdateDate.setWrapText(true);

        // Add all
        this.setPadding(new Insets(5.0));
        this.setSpacing(5.0);
        this.getChildren().addAll(this.labelWord, new Separator(Orientation.HORIZONTAL), new Label("Déprioriser/Prioriser"), boxSlider,
                this.toggleForceInvalid, this.toggleForceValid, new Separator(Orientation.HORIZONTAL), this.labelGeneralInfo, this.labelUpdateDate);
        this.updateNodeVisibility();
    }

    @Override
    public void initListener() {
        this.buttonResetSlider.setOnAction(e -> this.sliderFactor.setValue(0.0));
        this.toggleForceInvalid.selectedProperty().addListener(i -> {
            if (this.toggleForceInvalid.isSelected()) {
                this.toggleForceValid.setSelected(false);
            }
            if (this.word != null) {
                this.word.setForceInvalid(this.toggleForceInvalid.isSelected(), true);
                this.updateItems();
            }
        });
        this.toggleForceValid.selectedProperty().addListener(i -> {
            if (this.toggleForceValid.isSelected()) {
                this.toggleForceInvalid.setSelected(false);
            }
            if (this.word != null) {
                this.word.setForceValid(this.toggleForceValid.isSelected(), true);
                this.updateItems();
            }
        });
    }

    public void setWord(final Word nWord) {
        // Save previous word
        if (this.word != null) {
            this.word.setProbFactor(P4AConfigUtils.getFactorForWord(this.sliderFactor.getValue()), true);
            this.word.setForceInvalid(this.toggleForceInvalid.isSelected(), true);
            this.word.setForceValid(this.toggleForceValid.isSelected(), true);
        }
        // Display new word
        this.word = nWord;
        if (this.word != null) {
            this.labelWord.setText(this.word.getWord());
            this.sliderFactor.setValue(P4AConfigUtils.getFactorForSlider(this.word.getProbFactor()));
            this.toggleForceInvalid.setSelected(this.word.isForceInvalid());
            this.toggleForceValid.setSelected(this.word.isForceValid());
            if (this.word.isUserWord()) {
                this.labelGeneralInfo.setText(Translation.getText("predict4all.config.count.use", this.word.getUsageCount()));
                this.labelUpdateDate.setText(
                        Translation.getText("predict4all.config.last.use", StringUtils.dateToStringDateWithHour(new Date(this.word.getLastUseDate()))));
            } else {
                this.labelGeneralInfo.setText(Translation.getText("predict4all.config.base.dict.word"));
                this.labelUpdateDate.setText("");
            }
        } else {
            this.labelWord.setText("");
        }
        this.updateNodeVisibility();
    }

    private void updateNodeVisibility() {
        ObservableList<Node> children = this.getChildren();
        for (Node node : children) {
            node.setVisible(this.word != null);
        }
    }

    private void updateItems() {
        this.listViewWords.refresh();
    }

    // Class part : "STRING CONVERTER COST"
    //========================================================================
    private static final StringConverter<Double> STR_CONVERTER_FACTOR = new StringConverter<Double>() {

        @Override
        public String toString(final Double val) {
            double valI = val != null ? val : 0.0;
            if (valI < 0) {
                return Translation.getText("predict4all.config.tick.word.factor.low");
            }
            if (valI > 0) {
                return Translation.getText("predict4all.config.tick.word.factor.high");
            }
            return Translation.getText("predict4all.config.tick.word.factor.medium");
        }

        @Override
        public Double fromString(final String string) {
            return 0.0;
        }
    };
    //========================================================================

}
