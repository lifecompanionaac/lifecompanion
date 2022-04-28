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

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.PeriodicEventGenerator;
import org.lifecompanion.ui.common.control.generic.DurationPickerControl;

public class PeriodicConfigView extends HBox implements UseEventGeneratorConfigurationViewI<PeriodicEventGenerator> {
    private DurationPickerControl durationPicker;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final PeriodicEventGenerator element) {
        this.durationPicker.durationProperty().set(element.periodInMSProperty().get());
        this.durationPicker.tryToPickBestUnit();
    }

    @Override
    public void editEnds(final PeriodicEventGenerator element) {
        element.periodInMSProperty().set(this.durationPicker.durationProperty().get());
    }

    @Override
    public Class<PeriodicEventGenerator> getConfiguredActionType() {
        return PeriodicEventGenerator.class;
    }

    @Override
    public void initUI() {
        this.durationPicker = new DurationPickerControl();
        this.durationPicker.setAlignment(Pos.BASELINE_RIGHT);
        Label labelDurationPicker = new Label(Translation.getText("use.event.periodic.time.interval.field"));
        labelDurationPicker.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(labelDurationPicker, Priority.ALWAYS);
        this.getChildren().addAll(labelDurationPicker, this.durationPicker);
    }

}
