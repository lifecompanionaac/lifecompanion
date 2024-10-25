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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.WhiteboardSaveImageAction;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl.FileSelectorControlMode;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class WhiteboardSaveImageActionConfigView extends VBox implements UseActionConfigurationViewI<WhiteboardSaveImageAction> {
    private FileSelectorControl fileSelectorControl;

    public WhiteboardSaveImageActionConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final WhiteboardSaveImageAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        if (StringUtils.isNotBlank(action.getDestinationFolder())) {
            File destinationFolderFile = new File(action.getDestinationFolder());
            this.fileSelectorControl.valueProperty().set(destinationFolderFile);
        }
    }

    @Override
    public void editEnds(final WhiteboardSaveImageAction action) {
        File selectedFolder = this.fileSelectorControl.valueProperty().get();
        action.setDestinationFolder(selectedFolder != null ? selectedFolder.getAbsolutePath() : null);
    }

    @Override
    public Class<WhiteboardSaveImageAction> getConfiguredActionType() {
        return WhiteboardSaveImageAction.class;
    }

    @Override
    public void initUI() {
        this.fileSelectorControl = new FileSelectorControl(Translation.getText("save.whiteboard.image.directory.name"), FileSelectorControlMode.FOLDER, FileChooserType.SAVE_USER_TEXT, true);
        this.fileSelectorControl.setOpenDialogTitle(Translation.getText("save.whiteboard.image.directory.dialog.title"));
        this.getChildren().addAll(this.fileSelectorControl);
    }

}
