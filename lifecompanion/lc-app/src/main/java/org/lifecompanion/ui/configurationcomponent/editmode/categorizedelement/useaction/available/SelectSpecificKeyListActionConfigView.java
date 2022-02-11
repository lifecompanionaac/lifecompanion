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
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.SelectSpecificKeyListAction;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedListManageView;
import org.lifecompanion.ui.common.control.specific.selector.KeyListSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;

public class SelectSpecificKeyListActionConfigView extends VBox implements UseActionConfigurationViewI<SelectSpecificKeyListAction> {

    private KeyListSelectorControl linkedNodeSelector;

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public Class<SelectSpecificKeyListAction> getConfiguredActionType() {
        return SelectSpecificKeyListAction.class;
    }

    @Override
    public void initUI() {
        linkedNodeSelector = new KeyListSelectorControl(Translation.getText("keylist.toselect.field"));
        linkedNodeSelector.setMaxWidth(AbstractCategorizedListManageView.STAGE_WIDTH-20.0);
        this.getChildren().add(linkedNodeSelector);
    }

    @Override
    public void editStarts(final SelectSpecificKeyListAction element, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().configurationProperty().get();
        this.linkedNodeSelector.setInputKeyNode(configuration.rootKeyListNodeProperty().get());
        this.linkedNodeSelector.selectedKeylistCategoryIdProperty().set(element.linkedNodeIdProperty().get());
    }

    @Override
    public void editEnds(final SelectSpecificKeyListAction element) {
        element.linkedNodeIdProperty().set(this.linkedNodeSelector.selectedKeylistCategoryIdProperty().get());
        this.linkedNodeSelector.setInputKeyNode(null);
    }
}
