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

import java.io.File;

import org.lifecompanion.config.data.control.FileChooserType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.fx.translation.TranslationFX;
import org.lifecompanion.api.component.definition.useaction.UseActionConfigurationViewI;
import org.lifecompanion.api.component.definition.useevent.UseVariableDefinitionI;
import org.lifecompanion.base.data.useaction.impl.computera.features.RunProgramUseAction;
import org.lifecompanion.config.view.reusable.FileSelectorControl;
import org.lifecompanion.config.view.reusable.UseVariableTextArea;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RunProgramConfigView extends VBox implements UseActionConfigurationViewI<RunProgramUseAction> {
	private UseVariableTextArea textAreaProgramArgs;
	private FileSelectorControl fileSelectorProgramPath;

	public RunProgramConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final RunProgramUseAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		this.textAreaProgramArgs.getTextArea().clear();
		this.textAreaProgramArgs.setAvailableUseVariable(possibleVariables);
		this.textAreaProgramArgs.getTextArea().setText(action.programArgsProperty().get());
		if (action.programPathProperty().get() != null) {
			this.fileSelectorProgramPath.valueProperty().set(new File(action.programPathProperty().get()));
		}
	}

	@Override
	public void editEnds(final RunProgramUseAction action) {
		action.programArgsProperty().set(textAreaProgramArgs.getText());
		File pathVal = fileSelectorProgramPath.valueProperty().get();
		action.programPathProperty().set(pathVal != null ? pathVal.getPath() : null);
	}

	@Override
	public Class<RunProgramUseAction> getConfiguredActionType() {
		return RunProgramUseAction.class;
	}

	@Override
	public void initUI() {
		this.setSpacing(10.0);
		this.setPadding(new Insets(10.0));
		fileSelectorProgramPath = new FileSelectorControl(Translation.getText("label.field.run.program.path"),FileChooserType.RUN_PROGRAM);
		this.textAreaProgramArgs = new UseVariableTextArea();
		this.getChildren().addAll(fileSelectorProgramPath, new Label(Translation.getText("label.field.run.program.args")), this.textAreaProgramArgs);
	}

}
