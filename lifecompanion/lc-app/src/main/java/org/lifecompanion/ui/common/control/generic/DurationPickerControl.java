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

package org.lifecompanion.ui.common.control.generic;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;
import org.lifecompanion.util.LangUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.model.impl.configurationcomponent.DurationUnitEnum;

import java.util.stream.Stream;

/**
 * Control to select a duration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DurationPickerControl extends HBox implements LCViewInitHelper {

    private Spinner<Double> spinnerDuration;
    private final IntegerProperty duration;
    private ChoiceBox<DurationUnitEnum> choiceBoxDurationUnit;

    public DurationPickerControl() {
        this.duration = new SimpleIntegerProperty(1);
        this.initAll();
    }


    @Override
    public void initUI() {
        this.spinnerDuration = FXControlUtils.createDoubleSpinner(0, Double.MAX_VALUE, duration.get(), 1, 100);
        this.spinnerDuration.getStyleClass().remove(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        this.spinnerDuration.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);


        choiceBoxDurationUnit = new ChoiceBox<>(FXCollections.observableArrayList(DurationUnitEnum.values()));
        choiceBoxDurationUnit.setConverter(new DurationUnitConverter());
        choiceBoxDurationUnit.getSelectionModel().select(DurationUnitEnum.SECOND);

        this.getChildren().addAll(spinnerDuration, choiceBoxDurationUnit);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5.0);
    }

    @Override
    public void initBinding() {
        // Duration changed : change the spinner value with the correct unit convert
        this.duration.addListener((obs, ov, nv) -> {
            setSpinnerValueIfDiff(nv.doubleValue() / this.choiceBoxDurationUnit.getValue().getToMsRatio());
        });
        // Duration unit changed : convert the value
        this.choiceBoxDurationUnit.valueProperty().addListener((obs, ov, nv) -> {
            setSpinnerValueIfDiff(this.duration.get() / nv.getToMsRatio());
        });
        // Spinner change : change the value
        this.spinnerDuration.valueProperty().addListener((obs, ov, nv) -> {
            int newValue = (int) (nv.doubleValue() * choiceBoxDurationUnit.getValue().getToMsRatio());
            if (newValue != duration.get()) {
                duration.set(newValue);
            }
        });
    }

    private void setSpinnerValueIfDiff(double newValue) {
        if (LangUtils.tolerantRound(newValue) != LangUtils.tolerantRound(this.spinnerDuration.getValue())) {
            this.spinnerDuration.getValueFactory().setValue(newValue);
        }
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    /** 
    * Automatically sets the checkbox to the biggest unit that gives a value > 1.0.
    */
    public void tryToPickBestUnit() {
        for (int i = DurationUnitEnum.values().length - 1; i >= 0; i--) {
            if (duration.get() / DurationUnitEnum.values()[i].getToMsRatio() >= 1.0) {
                choiceBoxDurationUnit.getSelectionModel().select(DurationUnitEnum.values()[i]);
                break;
            }
        }
    }

    static class DurationUnitConverter extends StringConverter<DurationUnitEnum> {

        @Override
        public String toString(DurationUnitEnum object) {
            return object != null ? object.getTranslatedName() : null;
        }

        @Override
        public DurationUnitEnum fromString(String string) {
            return Stream.of(DurationUnitEnum.values()).filter(d -> StringUtils.isEquals(d.getTranslatedName(), string)).findAny().orElse(null);
        }
    }
}
