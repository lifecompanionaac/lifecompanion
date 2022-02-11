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
import org.lifecompanion.model.api.configurationcomponent.StackComponentI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionConfigurationViewI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.NextPageAndLoopInStackAction;
import org.lifecompanion.ui.common.control.specific.selector.ComponentSelectorControl;
import org.lifecompanion.framework.commons.translation.Translation;

public class NextPageAndLoopInStackConfigView extends VBox implements UseActionConfigurationViewI<NextPageAndLoopInStackAction> {
    private ComponentSelectorControl<StackComponentI> componentSelector;

    public NextPageAndLoopInStackConfigView() {
    }

    @Override
    public Region getConfigurationView() {
        return this;
    }

    @Override
    public void editStarts(final NextPageAndLoopInStackAction actionP, final ObservableList<UseVariableDefinitionI> possibleVariables) {
        this.componentSelector.selectedComponentProperty().set(actionP.changedPageParentStackProperty().get());
    }

    @Override
    public void editEnds(final NextPageAndLoopInStackAction actionP) {
        actionP.changedPageParentStackProperty().set(this.componentSelector.selectedComponentProperty().get());
        this.componentSelector.clearSelection();
    }

    @Override
    public void editCancelled(final NextPageAndLoopInStackAction element) {
        this.componentSelector.clearSelection();
    }

    @Override
    public Class<NextPageAndLoopInStackAction> getConfiguredActionType() {
        return NextPageAndLoopInStackAction.class;
    }

    @Override
    public void initUI() {
        this.setSpacing(4.0);
        this.componentSelector = new ComponentSelectorControl<>(StackComponentI.class, Translation.getText("use.action.next.page.stack.to.change"));
        this.getChildren().add(this.componentSelector);
    }
}
