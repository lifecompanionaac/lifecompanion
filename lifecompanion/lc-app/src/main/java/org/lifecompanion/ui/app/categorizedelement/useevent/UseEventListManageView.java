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
package org.lifecompanion.ui.app.categorizedelement.useevent;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorHolderI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;
import org.lifecompanion.controller.editaction.UseEventGeneratorActions.AddUseEventAction;
import org.lifecompanion.controller.editaction.UseEventGeneratorActions.EditUseEventAction;
import org.lifecompanion.controller.editaction.UseEventGeneratorActions.RemoveUseEventAction;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedListManageView;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedMainView;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractCategorizedElementListCellView;
import org.lifecompanion.ui.app.categorizedelement.useevent.UseEventMainView;
import org.lifecompanion.ui.common.pane.specific.cell.BaseUseEventElementListCellView;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.BiConsumer;

public class UseEventListManageView extends AbstractCategorizedListManageView<UseEventGeneratorHolderI, UseEventGeneratorI, UseEventSubCategoryI, UseEventMainCategoryI> {

    public UseEventListManageView(final boolean alwaysDisplayP) {
        super(alwaysDisplayP, false, Pos.CENTER_RIGHT);
        this.orderListButtonVisibleProperty().set(false);
    }

    @Override
    protected AbstractCategorizedMainView<UseEventGeneratorI, UseEventSubCategoryI, UseEventMainCategoryI> createCategorizedMainView() {
        return new UseEventMainView();
    }

    @Override
    protected AbstractCategorizedElementListCellView<UseEventGeneratorI> createCategorizedListCellView(ListView<UseEventGeneratorI> listView, final BiConsumer<Node, UseEventGeneratorI> selectionCallback) {
        return new BaseUseEventElementListCellView(listView, selectionCallback);
    }

    @Override
    protected BaseEditActionI createEditAction(final UseEventGeneratorI editedElement) {
        return new EditUseEventAction();
    }

    @Override
    protected BaseEditActionI createAddAction(Node source, final UseEventGeneratorHolderI model, final UseEventGeneratorI addedElement) {
        return new AddUseEventAction(model.getEventManager(), addedElement);
    }

    @Override
    protected BaseEditActionI createRemoveAction(Node source, final UseEventGeneratorHolderI model, final UseEventGeneratorI removedElement) {
        return new RemoveUseEventAction(model.getEventManager(), removedElement);
    }

    @Override
    protected BaseEditActionI createShiftUpAction(final UseEventGeneratorHolderI model, final UseEventGeneratorI element) {
        return null;
    }

    @Override
    protected BaseEditActionI createShiftDownAction(final UseEventGeneratorHolderI model, final UseEventGeneratorI element) {
        return null;
    }

    @Override
    protected ObservableList<UseEventGeneratorI> getContentFromModel(final UseEventGeneratorHolderI model) {
        return model.getEventManager().componentEventGenerators();
    }

    @Override
    protected String getAddWhenEmptyButtonText() {
        return Translation.getText("add.use.event.when.empty");
    }

    @Override
    protected boolean checkEditPossible(Node source, final UseEventGeneratorI item) {
        return true;
    }

}
