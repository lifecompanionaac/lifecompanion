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
package org.lifecompanion.config.view.pane.tabs.useaction.part;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.config.data.action.impl.UseActionConfigActions.*;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedListManageView;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedMainView;
import org.lifecompanion.config.view.pane.categorized.cell.AbstractCategorizedElementListCellView;
import org.lifecompanion.config.view.pane.useaction.UseActionMainView;
import org.lifecompanion.config.view.pane.useaction.cell.BaseUseActionElementListCellView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.*;

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
    protected AbstractCategorizedElementListCellView<BaseUseActionI<?>> createCategorizedListCellView(final BiConsumer<Node, BaseUseActionI<?>> selectionCallback) {
        return new BaseUseActionElementListCellView(selectionCallback);
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
            Alert dialog = ConfigUIUtils.createAlert(source, AlertType.WARNING);
            dialog.setHeaderText(Translation.getText("alert.message.disable.edit.action.header"));
            dialog.setContentText(Translation.getText("alert.message.disable.edit.action.message", item.getName()));
            dialog.show();
            return false;
        }
        return true;
    }
}
