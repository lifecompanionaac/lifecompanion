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

package org.lifecompanion.config.view.keyoption.impl;

import javafx.beans.value.ChangeListener;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.controlsfx.control.ToggleSwitch;
import org.lifecompanion.base.data.component.keyoption.WordPredictionKeyOption;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeWordPredictionAddSpace;
import org.lifecompanion.config.data.action.impl.KeyOptionActions.ChangeWordPredictionCorrectionColor;
import org.lifecompanion.config.data.common.LCConfigBindingUtils;
import org.lifecompanion.config.data.control.ConfigActionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.keyoption.BaseKeyOptionConfigView;
import org.lifecompanion.config.view.reusable.colorpicker.LCColorPicker;
import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Configuration view for word prediction option
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WordPredictionOptionConfigView extends BaseKeyOptionConfigView<WordPredictionKeyOption> {

    private ToggleSwitch toggleEnableSpaceAfter;
    private ToggleSwitch toggleEnableColoredCorrection;
    private ChangeListener<Boolean> changeListenerAddSpaceAfter;
    private ChangeListener<Color> changeListenerCorrectionColor, changeListenerCorrectionColorToggle;
    private LCColorPicker colorPickerCorrection;

    @Override
    public Class<WordPredictionKeyOption> getConfiguredKeyOptionType() {
        return WordPredictionKeyOption.class;
    }

    @Override
    public void initUI() {
        super.initUI();
        this.toggleEnableSpaceAfter = ConfigUIUtils.createToggleSwitch("word.prediction.add.space.after",
                "tooltip.explain.word.prediction.add.space");
        this.toggleEnableColoredCorrection = ConfigUIUtils.createToggleSwitch("word.prediction.color.correction.enable",
                "tooltip.explain.word.prediction.color.correction.enable");
        this.colorPickerCorrection = new LCColorPicker();
        this.getChildren().addAll(this.toggleEnableSpaceAfter, this.toggleEnableColoredCorrection,
                new Label(Translation.getText("word.prediction.color.correction.value")), this.colorPickerCorrection);
    }

    @Override
    public void initListener() {
        super.initListener();
        this.changeListenerAddSpaceAfter = LCConfigBindingUtils.createSimpleBinding(this.toggleEnableSpaceAfter.selectedProperty(), this.model,
                m -> m.addSpaceProperty().get(), ChangeWordPredictionAddSpace::new);
        changeListenerCorrectionColor = LCConfigBindingUtils.createSimpleBinding(this.colorPickerCorrection.valueProperty(), this.model,
                c -> c.correctionColorProperty().get(), ChangeWordPredictionCorrectionColor::new);
        changeListenerCorrectionColorToggle = (obs, ov, nv) -> {
            this.toggleEnableColoredCorrection.setSelected(nv != null);
        };
        this.toggleEnableColoredCorrection.selectedProperty().addListener((obs, ov, nv) -> {
            WordPredictionKeyOption modelVal = this.model.get();
            if (modelVal != null) {
                if (nv) {
                    ConfigActionController.INSTANCE.executeAction(new ChangeWordPredictionCorrectionColor(modelVal, Color.RED));
                } else {
                    ConfigActionController.INSTANCE.executeAction(new ChangeWordPredictionCorrectionColor(modelVal, null));
                }
            }
        });
        this.colorPickerCorrection.disableProperty().bind(this.colorPickerCorrection.valueProperty().isNull());
    }

    @Override
    public void bind(final WordPredictionKeyOption model) {
        this.toggleEnableSpaceAfter.setSelected(model.addSpaceProperty().get());
        this.colorPickerCorrection.setValue(model.correctionColorProperty().get());
        this.toggleEnableColoredCorrection.setSelected(model.correctionColorProperty().get() != null);
        model.addSpaceProperty().addListener(this.changeListenerAddSpaceAfter);
        model.correctionColorProperty().addListener(changeListenerCorrectionColor);
        model.correctionColorProperty().addListener(changeListenerCorrectionColorToggle);
    }

    @Override
    public void unbind(final WordPredictionKeyOption model) {
        model.addSpaceProperty().removeListener(this.changeListenerAddSpaceAfter);
        model.correctionColorProperty().removeListener(changeListenerCorrectionColor);
        model.correctionColorProperty().removeListener(changeListenerCorrectionColorToggle);
    }

}
