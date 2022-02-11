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
package org.lifecompanion.config.view.useevent.impl.control.clic;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import org.lifecompanion.model.impl.categorizedelement.useevent.available.PressTimeReachedEventGenerator;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class PressTimeReachedConfigView extends VBox implements UseEventGeneratorConfigurationViewI<PressTimeReachedEventGenerator> {

	private Spinner<Double> spinnerTimeToReach;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<PressTimeReachedEventGenerator> getConfiguredActionType() {
		return PressTimeReachedEventGenerator.class;
	}

	@Override
	public void initUI() {
		Label labelTimeToReach = new Label(Translation.getText("use.event.time.to.reach.label"));
		this.spinnerTimeToReach = UIUtils.createDoubleSpinner(0.1, 60.0, 2.0, 0.1, 120.0);
		this.spinnerTimeToReach.setMaxWidth(Double.MAX_VALUE);
		this.getChildren().addAll(labelTimeToReach, this.spinnerTimeToReach);
	}

	@Override
	public void editEnds(final PressTimeReachedEventGenerator element) {
		element.timeToReachProperty().set((int) (this.spinnerTimeToReach.getValue() * 1000));
	}

	@Override
	public void editStarts(final PressTimeReachedEventGenerator element) {
		this.spinnerTimeToReach.getValueFactory().setValue(element.timeToReachProperty().get() / 1000.0);
	}
}
