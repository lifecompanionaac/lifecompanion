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
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.OpenWithDefaultAppAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.RunProgramUseAction;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl;
import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OpenWithDefaultAppConfigView extends VBox implements UseActionConfigurationViewI<OpenWithDefaultAppAction> {
    private FileSelectorControl fileSelectorProgramPath;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final OpenWithDefaultAppAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        if (action.filePathProperty().get() != null) {
            this.fileSelectorProgramPath.valueProperty().set(new File(action.filePathProperty().get()));
        } else {
            this.fileSelectorProgramPath.valueProperty().set(null);
        }
    }

    @Override
    public void editEnds(final OpenWithDefaultAppAction action) {
        File pathVal = fileSelectorProgramPath.valueProperty().get();
        action.filePathProperty().set(pathVal != null ? pathVal.getPath() : null);
    }

    @Override
    public Class<OpenWithDefaultAppAction> getConfiguredActionType() {
        return OpenWithDefaultAppAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        fileSelectorProgramPath = new FileSelectorControl(Translation.getText("label.field.file.to.open.with.default.app"), FileChooserType.OPEN_WITH_DEFAULT_APP);
        this.getChildren().addAll(fileSelectorProgramPath);
    }

}
