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

package org.lifecompanion.ui.configurationcomponent.editmode.categorizedelement.useaction.available;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.util.UIUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.ScrollDownMouseAction;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class ScrollDownMouseActionConfigView extends VBox implements UseActionConfigurationViewI<ScrollDownMouseAction> {

	private Slider sliderScrollAmount;

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public Class<ScrollDownMouseAction> getConfiguredActionType() {
		return ScrollDownMouseAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(10.0);
		this.setPadding(new Insets(10.0));
		sliderScrollAmount = UIUtils.createBaseSlider(1.0, 20.0, 5.0);
		sliderScrollAmount.setMajorTickUnit(1.0);
		final Label labelScrollAmount = new Label(Translation.getText("use.action.mouse.scroll.amount.label"));
		labelScrollAmount.setWrapText(true);
		this.getChildren().addAll(labelScrollAmount, sliderScrollAmount);

	}

	@Override
	public void editStarts(final ScrollDownMouseAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		sliderScrollAmount.setValue(element.scrollAmountProperty().get());
	}

	@Override
	public void editEnds(final ScrollDownMouseAction element) {
		element.scrollAmountProperty().set(this.sliderScrollAmount.getValue());
	}

}
