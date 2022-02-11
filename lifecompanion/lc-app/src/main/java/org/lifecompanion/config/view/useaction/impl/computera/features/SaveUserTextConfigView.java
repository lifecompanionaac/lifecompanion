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
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SaveUserTextAction;
import org.lifecompanion.config.view.reusable.FileSelectorControl;
import org.lifecompanion.config.view.reusable.FileSelectorControl.FileSelectorControlMode;
import javafx.collections.ObservableList;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Action configuration view for {@link SaveUserTextAction}
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class SaveUserTextConfigView extends VBox implements UseActionConfigurationViewI<SaveUserTextAction> {
	private FileSelectorControl fileSelectorControl;

	public SaveUserTextConfigView() {}

	@Override
	public Region getConfigurationView() {
		return this;
	}

	@Override
	public void editStarts(final SaveUserTextAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
		File destinationFolderFile = new File(action.getDestinationFolder());
		this.fileSelectorControl.valueProperty().set(destinationFolderFile);
	}

	@Override
	public void editEnds(final SaveUserTextAction action) {
		File selectedFolder = this.fileSelectorControl.valueProperty().get();
		action.setDestinationFolder(selectedFolder.getAbsolutePath());
	}

	@Override
	public Class<SaveUserTextAction> getConfiguredActionType() {
		return SaveUserTextAction.class;
	}

	@Override
	public void initUI() {
		this.fileSelectorControl = new FileSelectorControl(Translation.getText("save.user.text.save.directory.name"), FileSelectorControlMode.FOLDER, FileChooserType.SAVE_USER_TEXT);
		this.fileSelectorControl.setOpenDialogTitle(Translation.getText("save.user.text.save.directory.dialog.title"));
		this.getChildren().addAll(this.fileSelectorControl);
	}

}
