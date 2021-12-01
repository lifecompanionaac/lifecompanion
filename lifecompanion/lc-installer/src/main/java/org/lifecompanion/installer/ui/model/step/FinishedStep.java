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

package org.lifecompanion.installer.ui.model.step;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.installer.controller.InstallerManager;
import org.lifecompanion.installer.ui.model.InstallerStep;

public class FinishedStep extends VBox implements InstallerStep {

    private Button buttonFinished;

    public FinishedStep() {
        super(10.0);
    }

    @Override
    public Node getContent() {
        return this;
    }

    // Class part : "UI"
    //========================================================================

    @Override
    public void initUI() {
        this.setAlignment(Pos.CENTER);
        Label labelFinishedExplain = new Label(Translation.getText("lc.installer.task.text.finished"));
        labelFinishedExplain.setWrapText(true);
        buttonFinished = new Button(Translation.getText("lc.installer.task.button.finished"));
        VBox.setMargin(labelFinishedExplain, new Insets(0, 20, 0, 20));
        this.getChildren().addAll(labelFinishedExplain, buttonFinished);
    }

    @Override
    public void initListener() {
        this.buttonFinished.setOnAction(e -> InstallerManager.INSTANCE.cancelRequest());
    }
    //========================================================================

    // Class part : "MODEL"
    //========================================================================
    @Override
    public void stepDisplayed() {
    }

    @Override
    public void stepHidden() {
    }

    public ReadOnlyBooleanProperty nextButtonAvailable() {
        return new SimpleBooleanProperty(false);
    }

    public ReadOnlyBooleanProperty previousButtonAvailable() {
        return new SimpleBooleanProperty(false);
    }
    //========================================================================

}
