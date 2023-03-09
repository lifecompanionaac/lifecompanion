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
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SimulateKeyboardKeyPressedAction;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SimulateKeyboardKeyToggleAction;
import org.lifecompanion.ui.common.control.specific.KeyCodeSelectorControl;

public class SimulateKeyboardKeyToggleConfigView extends VBox implements UseActionConfigurationViewI<SimulateKeyboardKeyToggleAction> {
    private KeyCodeSelectorControl keyCodeSelectorControl;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SimulateKeyboardKeyToggleAction> getConfiguredActionType() {
        return SimulateKeyboardKeyToggleAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(10.0);
        this.setPadding(new Insets(10.0));
        this.keyCodeSelectorControl = new KeyCodeSelectorControl(Translation.getText("use.action.simulate.key.toggle.label"));
        this.getChildren().addAll(this.keyCodeSelectorControl);

    }

    @Override
    public void editStarts(final SimulateKeyboardKeyToggleAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.keyCodeSelectorControl.valueProperty().set(element.keyToToggleProperty().get());
    }

    @Override
    public void editEnds(final SimulateKeyboardKeyToggleAction element) {
        element.keyToToggleProperty().set(this.keyCodeSelectorControl.valueProperty().get());
    }

}
