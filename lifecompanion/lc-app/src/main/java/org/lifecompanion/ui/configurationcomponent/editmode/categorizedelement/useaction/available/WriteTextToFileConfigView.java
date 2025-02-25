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

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WriteTextToFileAction;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl.FileSelectorControlMode;
import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WriteTextToFileConfigView extends VBox implements UseActionConfigurationViewI<WriteTextToFileAction> {
    private UseVariableTextArea useVariableTextArea;

    private FileSelectorControl fileSelectorControl;

    public WriteTextToFileConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final WriteTextToFileAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        if (StringUtils.isNotBlank(action.getDestinationFolder())) {
            File destinationFolderFile = new File(action.getDestinationFolder());
            this.fileSelectorControl.valueProperty().set(destinationFolderFile);
        }
        this.useVariableTextArea.getTextArea().clear();
        this.useVariableTextArea.setAvailableUseVariable(possibleVariables);
        this.useVariableTextArea.getTextArea().setText(action.textToWriteProperty().get());
    }

    @Override
    public void editEnds(final WriteTextToFileAction action) {
        File selectedFolder = this.fileSelectorControl.valueProperty().get();
        action.textToWriteProperty().set(useVariableTextArea.getText());
        action.setDestinationFolder(selectedFolder != null ? selectedFolder.getAbsolutePath() : null);
    }

    @Override
    public Class<WriteTextToFileAction> getConfiguredActionType() {
        return WriteTextToFileAction.class;
    }

    @Override
    public void initUI() {
        this.fileSelectorControl = new FileSelectorControl(Translation.getText("save.user.text.save.directory.name"), FileSelectorControlMode.FOLDER, FileChooserType.SAVE_USER_TEXT, true);
        this.fileSelectorControl.setOpenDialogTitle(Translation.getText("save.user.text.save.directory.dialog.title"));
        this.useVariableTextArea = new UseVariableTextArea();
        this.getChildren().addAll(new Label(Translation.getText("save.text.to.file.field.text")), this.useVariableTextArea, this.fileSelectorControl);
    }

}
