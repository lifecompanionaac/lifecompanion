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

package org.lifecompanion.ui.app.main.ribbon.available.withselection.useaction;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionTriggerComponentI;
import org.lifecompanion.util.binding.BindingUtils;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MultiActionManagerContentHelper {
    private final ObservableList<? extends UseActionTriggerComponentI> sourceList;
    private final ObservableList<BaseUseActionI<?>> resultList;
    private final UseActionEvent eventType;
    private final ListChangeListener<BaseUseActionI<?>> actionListSubListener;
    private final ListChangeListener<UseActionTriggerComponentI> sourceListListChangeListener;
    private final HashMap<Class<? extends BaseUseActionI>, AtomicInteger> countingTypeMap = new HashMap<>();

    public MultiActionManagerContentHelper(ObservableList<? extends UseActionTriggerComponentI> sourceList, final UseActionEvent eventType) {
        this.sourceList = sourceList;
        this.eventType = eventType;
        resultList = FXCollections.observableArrayList();
        actionListSubListener = BindingUtils.createListChangeListener(this::actionAdded, this::actionRemoved);
        this.sourceList.forEach(this::elementAdded);
        sourceListListChangeListener = BindingUtils.createListChangeListener(this::elementAdded, this::elementRemoved);
        this.sourceList.addListener(sourceListListChangeListener);
    }

    private void elementAdded(UseActionTriggerComponentI element) {
        ObservableList<BaseUseActionI<?>> addedActions = element.getActionManager().componentActions().get(eventType);
        addedActions.forEach(this::actionAdded);
        addedActions.addListener(actionListSubListener);
    }

    private void elementRemoved(UseActionTriggerComponentI element) {
        ObservableList<BaseUseActionI<?>> addedActions = element.getActionManager().componentActions().get(eventType);
        addedActions.removeListener(actionListSubListener);
        addedActions.forEach(this::actionRemoved);
    }

    private void actionAdded(BaseUseActionI<?> action) {
        // Action wasn't already in result list
        if (countingTypeMap.computeIfAbsent(action.getClass(), v -> new AtomicInteger()).incrementAndGet() == 1) {
            resultList.add(action);
        }
    }

    private void actionRemoved(BaseUseActionI<?> action) {
        // This action type in the list has no instance left
        if (countingTypeMap.computeIfAbsent(action.getClass(), v -> new AtomicInteger()).decrementAndGet() == 0) {
            // Remove first action of the same exact type (because the removed action can be different than the one in the list, depending on selection order)
            resultList.removeIf(a -> a.getClass().equals(action.getClass()));
        }
    }

    public ObservableList<BaseUseActionI<?>> getResultList() {
        return resultList;
    }

    public ObservableList<? extends UseActionTriggerComponentI> getSourceList() {
        return sourceList;
    }

    public UseActionEvent getEventType() {
        return eventType;
    }

    public void clearListeners() {
        sourceList.removeListener(sourceListListChangeListener);
    }
}
