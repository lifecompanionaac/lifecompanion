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

package org.lifecompanion.base.view.reusable;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.lifecompanion.base.data.common.BoundIntConverter;
import org.lifecompanion.base.data.common.UIUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

import java.text.DecimalFormat;

/**
 * Control to select a hour in a day.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class TimePickerControl extends HBox implements LCViewInitHelper {
    private static final DecimalFormat TWO_DIGIT = new DecimalFormat("00");

    /**
     * Spinner to select values
     */
    private Spinner<Integer> spinnerHour, spinnerMinutes;

    private final IntegerProperty hour;
    private final IntegerProperty minute;
    private final String fieldLabel;

    public TimePickerControl(final String textP) {
        this.fieldLabel = textP;
        this.hour = new SimpleIntegerProperty();
        this.minute = new SimpleIntegerProperty();
        this.initAll();
    }

    public TimePickerControl() {
        this(null);
    }

    public IntegerProperty hourProperty() {
        return this.hour;
    }

    public IntegerProperty minuteProperty() {
        return this.minute;
    }

    @Override
    public void initUI() {
        //Hours
        Label labelSeparator = new Label(":");
        this.spinnerHour = UIUtils.createIntSpinner(0, 23, 10, 1, 70);
        this.spinnerHour.getStyleClass().remove(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        this.spinnerHour.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        this.spinnerHour.getValueFactory().setConverter(new BoundIntConverter(0, 23, TWO_DIGIT));

        //Minutes
        this.spinnerMinutes = UIUtils.createIntSpinner(0, 59, 30, 10, 70);
        this.spinnerMinutes.getStyleClass().remove(Spinner.STYLE_CLASS_SPLIT_ARROWS_HORIZONTAL);
        this.spinnerMinutes.getStyleClass().add(Spinner.STYLE_CLASS_ARROWS_ON_LEFT_VERTICAL);
        this.spinnerMinutes.getValueFactory().setConverter(new BoundIntConverter(0, 59, TWO_DIGIT));

        if (StringUtils.isNotBlank(this.fieldLabel)) {
            Label labelFieldLabel = new Label(this.fieldLabel);
            labelFieldLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(labelFieldLabel, Priority.ALWAYS);
            this.getChildren().add(labelFieldLabel);
        }
        this.getChildren().addAll(spinnerHour, labelSeparator, spinnerMinutes);
        this.setAlignment(Pos.CENTER);
        this.setSpacing(5.0);
    }

    @Override
    public void initBinding() {
        this.hour.addListener((obs, ov, nv) -> {
            if (this.spinnerHour.getValue().intValue() != nv.intValue()) {
                this.spinnerHour.getValueFactory().setValue(nv.intValue());
            }
        });
        this.minute.addListener((obs, ov, nv) -> {
            if (this.spinnerMinutes.getValue().intValue() != nv.intValue()) {
                this.spinnerMinutes.getValueFactory().setValue(nv.intValue());
            }
        });
        this.spinnerHour.valueProperty().addListener((obs, ov, nv) -> {
            if (nv.intValue() != this.hour.get()) {
                this.hour.set(nv.intValue());
            }
        });
        this.spinnerMinutes.valueProperty().addListener((obs, ov, nv) -> {
            if (nv.intValue() != this.minute.get()) {
                this.minute.set(nv.intValue());
            }
        });
    }
}
