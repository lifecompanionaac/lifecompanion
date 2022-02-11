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
package org.lifecompanion.config.view.pane.tabs.useaction.part.multi;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import org.lifecompanion.model.api.editaction.BaseEditActionI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.config.data.action.impl.UseActionConfigActions;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.view.common.ConfigUIUtils;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedListManageView;
import org.lifecompanion.config.view.pane.categorized.AbstractCategorizedMainView;
import org.lifecompanion.config.view.pane.categorized.cell.AbstractCategorizedElementListCellView;
import org.lifecompanion.config.view.pane.useaction.UseActionMainView;
import org.lifecompanion.config.view.pane.useaction.cell.BaseUseActionElementListCellView;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.api.categorizedelement.useaction.*;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class MultiUseActionListManageView extends AbstractCategorizedListManageView<LCConfigurationI, BaseUseActionI<?>, UseActionSubCategoryI, UseActionMainCategoryI> {

    /**
     * The event type we should display in this part
     */
    private final UseActionEvent eventType;

    private final ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator;

    private MultiActionManagerContentHelper currentMultiActionManagerContentHelper;

    /**
     * Create a component to manager action
     *
     * @param eventTypeP     the event type of the action to manage
     * @param alwaysDisplayP if we need to display this component when the action list is empty
     */
    public MultiUseActionListManageView(final UseActionEvent eventTypeP, final boolean alwaysDisplayP, final ReadOnlyObjectProperty<UseEventGeneratorI> associatedUseEventGenerator) {
        super(alwaysDisplayP, true, Pos.CENTER_RIGHT);
        this.eventType = eventTypeP;
        this.associatedUseEventGenerator = associatedUseEventGenerator;
        this.initAll();
        this.orderListButtonVisibleProperty().set(false);
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
        return new UseActionConfigActions.MultiEditUseActionAction(editedElement, new ArrayList<>(currentMultiActionManagerContentHelper.getSourceList()), currentMultiActionManagerContentHelper.getEventType());
    }

    @Override
    protected boolean editActionForHistoryOnly() {
        return false;
    }

    @Override
    protected BaseEditActionI createAddAction(Node source, final LCConfigurationI model, final BaseUseActionI<?> addedElement) {
        return new UseActionConfigActions.MultiAddUseActionAction(source, addedElement, new ArrayList<>(currentMultiActionManagerContentHelper.getSourceList()), currentMultiActionManagerContentHelper.getEventType());
    }

    @Override
    protected BaseEditActionI createRemoveAction(Node source, final LCConfigurationI model, final BaseUseActionI<?> removedElement) {
        return new UseActionConfigActions.MultiRemoveUseActionAction(source, removedElement, new ArrayList<>(currentMultiActionManagerContentHelper.getSourceList()), currentMultiActionManagerContentHelper.getEventType());
    }

    @Override
    protected BaseEditActionI createShiftUpAction(final LCConfigurationI model, final BaseUseActionI<?> element) {
        return null;
    }

    @Override
    protected BaseEditActionI createShiftDownAction(final LCConfigurationI model, final BaseUseActionI<?> element) {
        return null;
    }

    @Override
    protected ObservableList<BaseUseActionI<?>> getContentFromModel(final LCConfigurationI model) {
        return currentMultiActionManagerContentHelper.getResultList();
    }

    @Override
    protected String getAddWhenEmptyButtonText() {
        return Translation.getText("add.action.when.empty") + "\n" + Translation.getText(this.eventType.getEventLabelId());
    }

    @Override
    public void bind(LCConfigurationI modelP) {
        this.currentMultiActionManagerContentHelper = new MultiActionManagerContentHelper(SelectionController.INSTANCE.getSelectedKeys(), eventType);
        super.bind(modelP);
    }

    @Override
    public void unbind(LCConfigurationI modelP) {
        if (this.currentMultiActionManagerContentHelper != null) {
            this.currentMultiActionManagerContentHelper.clearListeners();
            this.currentMultiActionManagerContentHelper = null;
        }
        super.unbind(modelP);
    }

    @Override
    protected boolean checkEditPossible(Node source, final BaseUseActionI<?> item) {
        // In the source list, search if this action type is bound to key option
        if (oneOfTheActionsHasKeyOptionAttached(item.getClass())) {
            Alert dialog = ConfigUIUtils.createAlert(source, AlertType.WARNING);
            dialog.setHeaderText(Translation.getText("alert.message.disable.edit.action.header"));
            dialog.setContentText(Translation.getText("alert.message.disable.edit.action.message", item.getName()));
            dialog.show();
            return false;
        }
        return true;
    }

    private boolean oneOfTheActionsHasKeyOptionAttached(final Class<? extends BaseUseActionI> actionType) {
        ObservableList<? extends UseActionTriggerComponentI> elements = this.currentMultiActionManagerContentHelper.getSourceList();
        for (UseActionTriggerComponentI element : elements) {
            ObservableList<BaseUseActionI<?>> actionsForEvent = element.getActionManager().componentActions().get(eventType);
            for (BaseUseActionI<?> action : actionsForEvent) {
                if (actionType.isAssignableFrom(action.getClass()) && action.attachedToKeyOptionProperty().get()) {
                    return true;
                }
            }
        }
        return false;
    }
}
