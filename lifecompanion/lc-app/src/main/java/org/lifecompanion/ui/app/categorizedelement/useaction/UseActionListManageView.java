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
package org.lifecompanion.ui.app.categorizedelement.useaction;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.controller.editaction.UseActionConfigActions.*;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedListManageView;
import org.lifecompanion.ui.app.categorizedelement.AbstractCategorizedMainView;
import org.lifecompanion.ui.common.pane.specific.cell.AbstractCategorizedElementListCellView;
import org.lifecompanion.ui.common.pane.specific.cell.BaseUseActionElementListCellView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.*;
import org.lifecompanion.util.javafx.DialogUtils;

import java.util.function.BiConsumer;

public class UseActionListManageView
        extends AbstractCategorizedListManageView<UseActionTriggerComponentI, BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> {

    /**
     * The event type we should display in this part
     */
    private final UseActionEvent eventType;

    private final ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator;

    /**
     * Create a component to manager action
     *
     * @param eventTypeP     the event type of the action to manage
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public UseActionListManageView(final UseActionEvent eventTypeP, final boolean alwaysDisplayP,
                                   final ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator) {
        super(alwaysDisplayP, true, Pos.CENTER_RIGHT);
        this.eventType = eventTypeP;
        this.associatedUseEventGenerator = associatedUseEventGenerator;
        this.initAll();
    }

    @Override
    protected AbstractCategorizedMainView<BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> createCategorizedMainView() {
        return new UseActionMainView(this.associatedUseEventGenerator);
    }

    @Override
    protected AbstractCategorizedElementListCellView<BaseUseActionI<?>> createCategorizedListCellView(ListView<BaseUseActionI<?>> listView, final BiConsumer<Node, BaseUseActionI<?>> selectionCallback) {
        return new BaseUseActionElementListCellView(listView, selectionCallback);
    }

    @Override
    protected BaseEditActionI createEditAction(final BaseUseActionI<?> editedElement) {
        return new EditUseActionAction();
    }

    @Override
    protected BaseEditActionI createAddAction(Node source, final UseActionTriggerComponentI model, final BaseUseActionI<?> addedElement) {
        return new AddUseActionAction(source, model.getActionManager(), addedElement, this.eventType);
    }

    @Override
    protected BaseEditActionI createRemoveAction(Node source, final UseActionTriggerComponentI model, final BaseUseActionI<?> removedElement) {
        return new RemoveUseActionAction(source, model.getActionManager(), removedElement, this.eventType);
    }

    @Override
    protected BaseEditActionI createShiftUpAction(final UseActionTriggerComponentI model, final BaseUseActionI<?> element) {
        return new ShiftActionUpAction(element, model.getActionManager(), this.eventType);
    }

    @Override
    protected BaseEditActionI createShiftDownAction(final UseActionTriggerComponentI model, final BaseUseActionI<?> element) {
        return new ShiftActionDownAction(element, model.getActionManager(), this.eventType);
    }

    @Override
    protected ObservableList<BaseUseActionI<?>> getContentFromModel(final UseActionTriggerComponentI model) {
        return model.getActionManager().componentActions().get(this.eventType);
    }

    @Override
    protected String getAddWhenEmptyButtonText() {
        return Translation.getText("add.action.when.empty") + "\n" + Translation.getText(this.eventType.getEventLabelId());
    }

    @Override
    protected boolean checkEditPossible(Node source, final BaseUseActionI<?> item) {
        if (item.attachedToKeyOptionProperty().get()) {
            DialogUtils
                    .alertWithSourceAndType(source, AlertType.WARNING)
                    .withHeaderText(Translation.getText("alert.message.disable.edit.action.header"))
                    .withContentText(Translation.getText("alert.message.disable.edit.action.message", item.getName()))
                    .show();
            return false;
        }
        return true;
    }
}
