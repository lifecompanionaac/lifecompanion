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

package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useevent.available;

import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.IntervalOfDayAndWeekEventGenerator;
import org.lifecompanion.ui.common.control.generic.DayPickerControl;
import org.lifecompanion.ui.common.control.generic.TimePickerControl;

public class IntervalOfDayAndWeekConfigView extends VBox implements UseEventGeneratorConfigurationViewI<IntervalOfDayAndWeekEventGenerator> {
	private DayPickerControl dayPicker;
	private TimePickerControl startTimePicker;
	private TimePickerControl endTimePicker;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final IntervalOfDayAndWeekEventGenerator element) {
		this.dayPicker.dayProperty().set(element.wantedDayProperty().get());
		this.startTimePicker.hourProperty().set(element.getStartTimeOfDay().hoursProperty().get());
		this.startTimePicker.minuteProperty().set(element.getStartTimeOfDay().minutesProperty().get());
		this.endTimePicker.hourProperty().set(element.getEndTimeOfDay().hoursProperty().get());
		this.endTimePicker.minuteProperty().set(element.getEndTimeOfDay().minutesProperty().get());
	}

	@Override
	public void editEnds(final IntervalOfDayAndWeekEventGenerator element) {
		element.wantedDayProperty().set(this.dayPicker.dayProperty().get());
		element.getStartTimeOfDay().hoursProperty().set(this.startTimePicker.hourProperty().get());
		element.getStartTimeOfDay().minutesProperty().set(this.startTimePicker.minuteProperty().get());
		element.getEndTimeOfDay().hoursProperty().set(this.endTimePicker.hourProperty().get());
		element.getEndTimeOfDay().minutesProperty().set(this.endTimePicker.minuteProperty().get());
	}

	@Override
	public Class<IntervalOfDayAndWeekEventGenerator> getConfiguredActionType() {
		return IntervalOfDayAndWeekEventGenerator.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(10);
		this.dayPicker = new DayPickerControl(Translation.getText("use.event.day.of.week.field"));
		this.getChildren().add(this.dayPicker);
		this.startTimePicker = new TimePickerControl(Translation.getText("use.event.interval.of.day.start.time.field"));
		this.endTimePicker = new TimePickerControl(Translation.getText("use.event.interval.of.day.end.time.field"));
		this.getChildren().addAll(this.startTimePicker, this.endTimePicker);
	}

}
