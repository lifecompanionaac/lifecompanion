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

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.TimeOfDayEventGenerator;
import org.lifecompanion.ui.common.control.generic.TimePickerControl;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class TimeOfDayConfigView extends VBox implements UseEventGeneratorConfigurationViewI<TimeOfDayEventGenerator> {
	private TimePickerControl timePicker;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final TimeOfDayEventGenerator element) {
		this.timePicker.hourProperty().set(element.wantedHourProperty().get());
		this.timePicker.minuteProperty().set(element.wantedMinuteProperty().get());
	}

	@Override
	public void editEnds(final TimeOfDayEventGenerator element) {
		element.wantedHourProperty().set(this.timePicker.hourProperty().get());
		element.wantedMinuteProperty().set(this.timePicker.minuteProperty().get());
	}

	@Override
	public Class<TimeOfDayEventGenerator> getConfiguredActionType() {
		return TimeOfDayEventGenerator.class;
	}

	@Override
	public void initUI() {
		this.timePicker = new TimePickerControl(Translation.getText("use.event.time.of.day.time.field"));
		this.getChildren().addAll(this.timePicker);
	}

}
