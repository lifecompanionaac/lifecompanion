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

package org.lifecompanion.model.impl.categorizedelement.useaction.available;

import java.util.Map;

import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.usevariable.UseVariableI;
import org.lifecompanion.model.api.textcomponent.WritingEventSource;
import org.lifecompanion.model.api.categorizedelement.useaction.DefaultUseActionSubCategories;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.AutoCharKeyOption;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.CustomCharKeyOption;
import org.lifecompanion.base.data.control.WritingStateController;
import org.lifecompanion.model.impl.categorizedelement.useaction.SimpleUseActionImpl;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;

/**
 * Action to write the label of the parent key.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteCharPredictionAction extends SimpleUseActionImpl<GridPartKeyComponentI> {

	public WriteCharPredictionAction() {
		super(GridPartKeyComponentI.class);
		this.category = DefaultUseActionSubCategories.PREDICTION;
		this.nameID = "action.write.char.prediction.name";
		this.staticDescriptionID = "action.write.char.prediction.description";
		this.configIconPath = "text/icon_write_char_prediction.png";
		this.parameterizableAction = false;
		this.variableDescriptionProperty().set(this.getStaticDescription());
	}

	// Class part : "Execute"
	//========================================================================
	@Override
	public void execute(final UseActionEvent eventP, final Map<String, UseVariableI<?>> variables) {
		GridPartKeyComponentI parentKey = this.parentComponentProperty().get();
		if (parentKey != null) {
			String prediction = null;
			if (parentKey.keyOptionProperty().get() instanceof AutoCharKeyOption) {
				AutoCharKeyOption predOption = (AutoCharKeyOption) parentKey.keyOptionProperty().get();
				prediction = predOption.predictionProperty().get();
			}
			if (parentKey.keyOptionProperty().get() instanceof CustomCharKeyOption) {
				CustomCharKeyOption predOption = (CustomCharKeyOption) parentKey.keyOptionProperty().get();
				prediction = predOption.predictionProperty().get();
			}
			//TODO : better special char like space handling
			if (prediction != null) {
				if (!prediction.isEmpty() && StringUtils.isEquals(prediction,
						parentKey.configurationParentProperty().get().getPredictionParameters().charPredictionSpaceCharProperty().get())) {
					WritingStateController.INSTANCE.space(WritingEventSource.USER_ACTIONS);
				} else {
					WritingStateController.INSTANCE.insertCharPrediction(WritingEventSource.USER_ACTIONS, prediction);
				}
			}
		}
	}
	//========================================================================
}
