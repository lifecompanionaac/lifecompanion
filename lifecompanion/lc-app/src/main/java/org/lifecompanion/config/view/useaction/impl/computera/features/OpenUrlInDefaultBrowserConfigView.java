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

package org.lifecompanion.config.view.useaction.impl.computera.features;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.OpenUrlInDefaultBrowserAction;
import org.lifecompanion.config.view.reusable.UseVariableTextArea;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OpenUrlInDefaultBrowserConfigView extends VBox implements UseActionConfigurationViewI<OpenUrlInDefaultBrowserAction> {
	private UseVariableTextArea textAreaUrl;

	public OpenUrlInDefaultBrowserConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final OpenUrlInDefaultBrowserAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.textAreaUrl.getTextArea().clear();
		this.textAreaUrl.setAvailableUseVariable(possibleVariables);
		this.textAreaUrl.getTextArea().setText(action.urlProperty().get());
	}

	@Override
	public void editEnds(final OpenUrlInDefaultBrowserAction action) {
		action.urlProperty().set(textAreaUrl.getText());
	}

	@Override
	public Class<OpenUrlInDefaultBrowserAction> getConfiguredActionType() {
		return OpenUrlInDefaultBrowserAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(10.0);
		this.setPadding(new Insets(10.0));
		this.textAreaUrl = new UseVariableTextArea();
		this.getChildren().addAll(new Label(Translation.getText("use.action.open.url.default.browser")), this.textAreaUrl);
	}

}
