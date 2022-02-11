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

package org.lifecompanion.ui.app.main.usercomponent;

import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.profile.UserCompDescriptionI;
import org.lifecompanion.model.api.ui.editmode.BaseConfigurationViewI;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * View to edit user comp information.
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class UserCompEditView extends VBox implements LCViewInitHelper, BaseConfigurationViewI<UserCompDescriptionI> {
	private static final double WIDTH = 250.0, HEIGHT = 120.0;
	private TextField fieldCompName;

	public UserCompEditView() {
		this.initAll();
	}

	// Class part : "UI"
	//========================================================================
	@Override
	public void initUI() {
		Label labelName = new Label(Translation.getText("user.comp.name.field"));
		this.fieldCompName = new TextField();

		//Total
		this.setPrefSize(UserCompEditView.WIDTH, UserCompEditView.HEIGHT);
		this.setSpacing(5.0);
		this.getChildren().addAll(labelName, this.fieldCompName);
	}

	@Override
	public void initListener() {}

	@Override
	public void initBinding() {}
	//========================================================================

	// Class part : "Model"
	//========================================================================
	@Override
	public void bind(final UserCompDescriptionI model) {
		this.fieldCompName.setText(model.nameProperty().get());
	}

	@Override
	public void unbind(final UserCompDescriptionI model) {
		model.nameProperty().set(this.fieldCompName.getText());
	}
	//========================================================================
}
