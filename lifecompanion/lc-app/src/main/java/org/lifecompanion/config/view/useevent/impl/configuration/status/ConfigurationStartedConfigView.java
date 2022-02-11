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

package org.lifecompanion.config.view.useevent.impl.configuration.status;

import org.lifecompanion.model.impl.categorizedelement.useevent.available.ConfigurationStartedEventGenerator;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorConfigurationViewI;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ConfigurationStartedConfigView extends VBox implements UseEventGeneratorConfigurationViewI<ConfigurationStartedEventGenerator> {

	private Spinner<Double> spinnerDelay;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final ConfigurationStartedEventGenerator element) {
		spinnerDelay.getValueFactory().setValue(element.delayProperty().get() / 1000.0);
	}

	@Override
	public void editEnds(final ConfigurationStartedEventGenerator element) {
		element.delayProperty().set((int) (spinnerDelay.getValue() * 1000.0));
	}

	@Override
	public Class<ConfigurationStartedEventGenerator> getConfiguredActionType() {
		return ConfigurationStartedEventGenerator.class;
	}

	@Override
	public void initUI() {
		spinnerDelay = UIUtils.createDoubleSpinner(0.0001, 60.0 * 60.0, 1.0, 0.1, 100.0);
		this.setSpacing(10.0);
		this.getChildren().addAll(new Label(Translation.getText("label.field.configuration.started.delay.launch")), spinnerDelay);
	}

}
