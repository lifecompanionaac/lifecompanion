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

package org.lifecompanion.config.view.useaction.impl.text.write;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.base.data.useaction.impl.text.write.WriteTextAction;
import org.lifecompanion.config.view.reusable.UseVariableTextArea;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Action configuration view for {@link WriteTextAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteTextConfigView extends VBox implements UseActionConfigurationViewI<WriteTextAction> {
	private UseVariableTextArea useVariableTextArea;

	public WriteTextConfigView() {}

	@Override
	public void initUI() {
		this.setSpacing(10.0);
		this.setPadding(new Insets(10.0));
		this.useVariableTextArea = new UseVariableTextArea();
		this.getChildren().addAll(new Label(Translation.getText("use.action.write.text.towrite")), this.useVariableTextArea);
	}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final WriteTextAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.useVariableTextArea.getTextArea().clear();
		this.useVariableTextArea.setAvailableUseVariable(possibleVariables);
		this.useVariableTextArea.getTextArea().setText(action.textToWriteProperty().get());
	}

	@Override
	public void editEnds(final WriteTextAction action) {
		action.textToWriteProperty().set(this.useVariableTextArea.getTextArea().getText());
	}

	@Override
	public Class<WriteTextAction> getConfiguredActionType() {
		return WriteTextAction.class;
	}

}
