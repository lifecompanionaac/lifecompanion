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

import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.Locale;

import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.StringConverter;

/**
 * Control to select a day in a week
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class DayPickerControl extends GridPane implements LCViewInitHelper {

	private ChoiceBox<DayOfWeek> choiceBoxDay;

	private IntegerProperty day;

	private String text;

	public DayPickerControl(final String textP) {
		this.text = textP;
		this.day = new SimpleIntegerProperty();
		this.initAll();
	}

	public IntegerProperty dayProperty() {
		return this.day;
	}

	@Override
	public void initUI() {
		//Day selector
		this.choiceBoxDay = new ChoiceBox<>(FXCollections.observableArrayList(DayOfWeek.values()));
		this.choiceBoxDay.setConverter(new StringConverter<DayOfWeek>() {
			@Override
			public String toString(final DayOfWeek day) {
				return day != null ? StringUtils.capitalize(day.getDisplayName(TextStyle.FULL, Locale.getDefault())) : "";
			}

			@Override
			public DayOfWeek fromString(final String day) {
				return null;
			}
		});
		GridPane.setFillWidth(this.choiceBoxDay, true);
		//Field label
		Label labelFieldLabel = new Label(this.text);
		labelFieldLabel.setMaxWidth(Double.MAX_VALUE);
		GridPane.setHgrow(labelFieldLabel, Priority.ALWAYS);

		//Add
		this.add(labelFieldLabel, 0, 0);
		this.add(this.choiceBoxDay, 1, 0);
		this.setMaxWidth(Double.MAX_VALUE);
	}

	@Override
	public void initBinding() {
		this.choiceBoxDay.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
			if (nv != null && nv.getValue() != this.day.get()) {
				this.day.set(nv.getValue());
			}
		});
		this.day.addListener((obs, ov, nv) -> {
			if (this.choiceBoxDay.getSelectionModel().getSelectedItem() == null
					|| this.choiceBoxDay.getSelectionModel().getSelectedItem().getValue() != nv.intValue()) {
				this.choiceBoxDay.setValue(DayOfWeek.of(nv.intValue()));
			}
		});
	}

}
