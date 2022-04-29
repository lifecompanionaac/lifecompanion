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
import org.lifecompanion.model.impl.categorizedelement.useaction.available.RunCommandUseAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.RunProgramUseAction;
import org.lifecompanion.ui.common.control.generic.FileSelectorControl;
import org.lifecompanion.ui.common.control.specific.usevariable.UseVariableTextArea;

import java.io.File;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class RunCommandConfigView extends VBox implements UseActionConfigurationViewI<RunCommandUseAction> {
    private UseVariableTextArea textAreaProgramArgs;

    public RunCommandConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final RunCommandUseAction action, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.textAreaProgramArgs.getTextArea().clear();
        this.textAreaProgramArgs.setAvailableUseVariable(possibleVariables);
        this.textAreaProgramArgs.getTextArea().setText(action.commandToRunProperty().get());
    }

    @Override
    public void editEnds(final RunCommandUseAction action) {
        action.commandToRunProperty().set(textAreaProgramArgs.getText());
    }

    @Override
    public Class<RunCommandUseAction> getConfiguredActionType() {
        return RunCommandUseAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.textAreaProgramArgs = new UseVariableTextArea();
        Label labelExplain = new Label(Translation.getText("label.field.run.command.explain"));
        labelExplain.getStyleClass().addAll("text-wrap-enabled", "text-font-italic");
        this.getChildren().addAll(new Label(Translation.getText("label.field.run.command.field.to.run")), this.textAreaProgramArgs, labelExplain);
    }

}
