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
import org.lifecompanion.util.LCUtils;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.util.stream.Stream;

/**
 * Control to select a duration
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DurationPickerControl extends HBox implements LCViewInitHelper {

    private Spinner<Double> spinnerDuration;
    private final IntegerProperty duration;
    private ChoiceBox<DurationUnit> choiceBoxDurationUnit;

    public DurationPickerControl() {
        this.duration = new SimpleIntegerProperty(1);
        this.initAll();
    }


    @Override
    public void initUI() {
        this.spinnerDuration = UIUtils.createDoubleSpinner(0, Double.MAX_VALUE, duration.get(), 1, 100);
        this.spinnerDuration.getStyleClass().remove(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        this.spinnerDuration.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);


        choiceBoxDurationUnit = new ChoiceBox<>(FXCollections.observableArrayList(DurationUnit.values()));
        choiceBoxDurationUnit.setConverter(new DurationUnitConverter());
        choiceBoxDurationUnit.getSelectionModel().select(DurationUnit.SECOND);

        this.getChildren().addAll(spinnerDuration, choiceBoxDurationUnit);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5.0);
    }

    @Override
    public void initBinding() {
        // Duration changed : change the spinner value with the correct unit convert
        this.duration.addListener((obs, ov, nv) -> {
            setSpinnerValueIfDiff(nv.doubleValue() / this.choiceBoxDurationUnit.getValue().toMsRatio);
        });
        // Duration unit changed : convert the value
        this.choiceBoxDurationUnit.valueProperty().addListener((obs, ov, nv) -> {
            setSpinnerValueIfDiff(this.duration.get() / nv.toMsRatio);
        });
        // Spinner change : change the value
        this.spinnerDuration.valueProperty().addListener((obs, ov, nv) -> {
            int newValue = (int) (nv.doubleValue() * choiceBoxDurationUnit.getValue().toMsRatio);
            if (newValue != duration.get()) {
                duration.set(newValue);
            }
        });
    }

    private void setSpinnerValueIfDiff(double newValue) {
        if (LCUtils.tolerantRound(newValue) != LCUtils.tolerantRound(this.spinnerDuration.getValue())) {
            this.spinnerDuration.getValueFactory().setValue(newValue);
        }
    }

    public IntegerProperty durationProperty() {
        return duration;
    }

    public void tryToPickBestUnit() {
        // try to find the biggest unit that give > 1.0
        for (int i = DurationUnit.values().length - 1; i >= 0; i--) {
            if (duration.get() / DurationUnit.values()[i].toMsRatio >= 1.0) {
                choiceBoxDurationUnit.getSelectionModel().select(DurationUnit.values()[i]);
                break;
            }
        }
    }

    enum DurationUnit {
        MILLISECOND("duration.unit.millisecond", 1.0),
        SECOND("duration.unit.second", 1000.0),
        MINUTE("duration.unit.minute", 60_000.0),
        HOUR("duration.unit.hour", 3600_000.0);

        private final String translationId;
        private final double toMsRatio;

        DurationUnit(String translationId, double toMsRatio) {
            this.translationId = translationId;
            this.toMsRatio = toMsRatio;
        }

        String getTranslatedName() {
            return Translation.getText(translationId);
        }
    }

    static class DurationUnitConverter extends StringConverter<DurationUnit> {

        @Override
        public String toString(DurationUnit object) {
            return object != null ? object.getTranslatedName() : null;
        }

        @Override
        public DurationUnit fromString(String string) {
            return Stream.of(DurationUnit.values()).filter(d -> StringUtils.isEquals(d.getTranslatedName(), string)).findAny().orElse(null);
        }
    }
}
