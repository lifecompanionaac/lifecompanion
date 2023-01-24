/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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
package org.lifecompanion.plugin.predict4allevaluation.event;

import org.lifecompanion.plugin.predict4allevaluation.clinicalstudy.Predict4AllClinicalStudyManager;
import org.lifecompanion.plugin.predict4allevaluation.event.categories.Predict4AllEventSubCategories;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.categorizedelement.useevent.BaseUseEventGeneratorImpl;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OnClinicalStudyLoggingStartEventGenerator extends BaseUseEventGeneratorImpl {

	private final Runnable loggingStartedCallback;

	public OnClinicalStudyLoggingStartEventGenerator() {
		super();
		this.parameterizableAction = false;
		this.order = 20;
		this.category = Predict4AllEventSubCategories.CLINICAL_STUDY;
		this.nameID = "predict4all.event.clinical.study.log.started.name";
		this.staticDescriptionID = "predict4all.event.clinical.study.log.started.description";
		this.variableDescriptionProperty().set(this.getStaticDescription());
		loggingStartedCallback = () -> this.useEventListener.fireEvent(this, null, null);
	}

	@Override
	public String getConfigIconPath() {
		return "use-actions/icon_p4a.png";
	}

	// Class part : "Mode start/stop"
	//========================================================================
	@Override
	public void modeStart(final LCConfigurationI configuration) {
		Predict4AllClinicalStudyManager.INSTANCE.setStartCallback(loggingStartedCallback);
	}

	@Override
	public void modeStop(final LCConfigurationI configuration) {
		Predict4AllClinicalStudyManager.INSTANCE.setStartCallback(null);
	}
	//========================================================================
}
