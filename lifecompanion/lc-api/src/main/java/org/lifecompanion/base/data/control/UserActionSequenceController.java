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
package org.lifecompanion.base.data.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.LCConfigurationI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceI;
import org.lifecompanion.api.component.definition.simplercomp.UserActionSequenceItemI;
import org.lifecompanion.api.mode.ModeListenerI;
import org.lifecompanion.base.data.common.LCUtils;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceCurrentKeyOption;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceDisplayFilter;
import org.lifecompanion.base.data.component.keyoption.simplercomp.UserActionSequenceItemKeyOption;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public enum UserActionSequenceController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(UserActionSequenceController.class);

    private final ObjectProperty<UserActionSequenceI> currentSequence;
    private final ObjectProperty<UserActionSequenceItemI> currentItem;
    private final AtomicInteger tempDisableNextAndPrevious;

    private LCConfigurationI currentConfiguration;

    private final Map<GridComponentI, List<UserActionSequenceCurrentKeyOption>> currentItemKeyOptions;
    private final Map<GridComponentI, List<UserActionSequenceItemKeyOption>> currentSequenceItemKeyOptions;

    private final Set<Consumer<String>> onSequenceFinishedListeners;

    // TODO : add use variable with current sequence name

    UserActionSequenceController() {
        this.currentSequence = new SimpleObjectProperty<>();
        this.currentItem = new SimpleObjectProperty<>();
        this.tempDisableNextAndPrevious = new AtomicInteger();
        this.currentSequenceItemKeyOptions = new HashMap<>();
        this.currentItemKeyOptions = new HashMap<>();
        this.onSequenceFinishedListeners = new HashSet<>();
        this.currentSequence.addListener((obs, ov, nv) -> currentSequenceChanged());
        this.currentItem.addListener((obs, ov, nv) -> currentItemChanged());
    }

    public Set<Consumer<String>> getOnSequenceFinishedListeners() {
        return onSequenceFinishedListeners;
    }

    public UserActionSequenceCurrentKeyOption getFirstCurrentItemKeyOption() {
        return currentItemKeyOptions.values().stream().flatMap(Collection::stream).findFirst().orElse(null);
    }

    // ACTIONS
    //========================================================================
    public void startSequenceById(String sequenceId) {
        if (this.currentConfiguration != null && StringUtils.isNotBlank(sequenceId)) {
            currentConfiguration.userActionSequencesProperty().get().getUserActionSequences().stream().filter(s -> StringUtils.isEquals(s.getID(), sequenceId)).findAny().ifPresent(sequence -> {
                this.tempDisableNextAndPrevious.set(0);
                currentSequence.set(sequence);
            });
        }
    }

    public void nextItemInSequence() {
        if (tempDisableNextAndPrevious.get() <= 0) {
            final UserActionSequenceI sequence = currentSequence.get();
            if (sequence != null) {
                final int currentItemIndex = getCurrentItemIndex(sequence);
                if (currentItemIndex + 1 < sequence.getItems().size()) {
                    currentItem.set(sequence.getItems().get(currentItemIndex + 1));
                } else if (currentItem.get() != null) {
                    // It is the last item in the sequence, set it as done and fire action/event
                    LCUtils.runOnFXThread(() -> {
                        currentItem.get().actionExecutedProperty().set(true);
                        currentItem.get().currentActionProperty().set(false);
                    });
                    for (Consumer<String> onSequenceFinishedListener : this.onSequenceFinishedListeners) {
                        onSequenceFinishedListener.accept(sequence.getID());
                    }
                }
            }
        }
    }

    public void previousItemInSequence() {
        // TODO : if it is possible ?
        if (tempDisableNextAndPrevious.get() <= 0) {
            final UserActionSequenceI sequence = currentSequence.get();
            if (sequence != null) {
                final int currentItemIndex = getCurrentItemIndex(sequence);
                if (currentItemIndex - 1 >= 0) {
                    currentItem.set(sequence.getItems().get(currentItemIndex - 1));
                }
            }
        }
    }

    public void cancelRunningSequence() {
        tempDisableNextAndPrevious.set(0);
        this.currentSequence.set(null);
    }
    //========================================================================

    // UPDATE
    //========================================================================
    private void currentSequenceChanged() {
        final UserActionSequenceI sequence = currentSequence.get();
        currentItem.set(sequence == null || CollectionUtils.isEmpty(sequence.getItems()) ? null : sequence.getItems().get(0));
    }

    private void currentItemChanged() {
        final UserActionSequenceItemI currentItem = this.currentItem.get();
        LCUtils.runOnFXThread(() -> {
            final UserActionSequenceI sequence = currentSequence.get();
            if (sequence != null) {
                currentSequenceItemKeyOptions.forEach((grid, keyOptions) -> {
                    final ObservableList<UserActionSequenceItemI> sequenceItems = sequence.getItems();
                    sequenceItems.forEach(item -> {
                        item.currentActionProperty().set(false);
                        item.actionExecutedProperty().set(false);
                    });
                    currentItem.currentActionProperty().set(true);
                    int currentIndex = sequenceItems.indexOf(currentItem);
                    boolean currentItemIsSub = currentItem.subItemProperty().get();
                    boolean foundNotSub = !currentItemIsSub;
                    // Going back to set executed
                    for (int i = currentIndex - 1; i >= 0; i--) {
                        final UserActionSequenceItemI prevItem = sequenceItems.get(i);
                        if (prevItem.subItemProperty().get() || foundNotSub) {
                            prevItem.actionExecutedProperty().set(true);
                        } else if (!prevItem.subItemProperty().get()) {
                            prevItem.currentActionProperty().set(true);
                            foundNotSub = true;
                        }
                    }
                    showItemsFromSequenceInWithFilter(UserActionSequenceDisplayFilter.BOTH, sequenceItems, keyOptions);
                    showItemsFromSequenceInWithFilter(UserActionSequenceDisplayFilter.ONLY_NOT_SUB, sequenceItems, keyOptions);
                    showItemsFromSequenceInWithFilter(UserActionSequenceDisplayFilter.ONLY_SUB, currentItemIsSub ? sequenceItems : Collections.emptyList(), keyOptions);
                });
                AtomicReference<Pair<GridPartKeyComponentI, UserActionSequenceCurrentKeyOption>> currentItemKeyAndKeyOption = new AtomicReference<>();
                currentItemKeyOptions.forEach((grid, keyOptions) -> {
                    for (UserActionSequenceCurrentKeyOption currentItemDisplayer : keyOptions) {
                        currentItemDisplayer.currentSimplerKeyContentContainerProperty().set(currentItem);
                        if (currentItemKeyAndKeyOption.get() == null) {
                            currentItemKeyAndKeyOption.set(Pair.of(currentItemDisplayer.attachedKeyProperty().get(), currentItemDisplayer));
                        }
                    }
                });
                if (currentItemKeyAndKeyOption.get() != null) {
                    tempDisableNextAndPrevious.incrementAndGet();
                    if (currentItem.enableAutomaticItemProperty().get()) {
                        tempDisableNextAndPrevious.incrementAndGet();
                        UseModeProgressDisplayerController.INSTANCE.launchTimer(currentItem.automaticItemTimeMsProperty().get(), tempDisableNextAndPrevious::decrementAndGet);
                    } else {
                        UseModeProgressDisplayerController.INSTANCE.hideAllProgress();
                    }
                    UserActionController.INSTANCE.executeSimpleDetachedActionsInNewThread(currentItemKeyAndKeyOption.get().getLeft(), currentItemKeyAndKeyOption.get().getRight().getActionsToExecuteOnStart(), result -> tempDisableNextAndPrevious.decrementAndGet());
                }
            } else {
                currentItemKeyOptions.forEach((grid, keyOptions) -> keyOptions.forEach(ko -> ko.currentSimplerKeyContentContainerProperty().set(null)));
                currentSequenceItemKeyOptions.forEach((grid, keyOptions) -> keyOptions.forEach(ko -> ko.currentSimplerKeyContentContainerProperty().set(null)));
            }
        });
    }


    private void showItemsFromSequenceInWithFilter(UserActionSequenceDisplayFilter filter, List<UserActionSequenceItemI> items, List<UserActionSequenceItemKeyOption> keyOptions) {
        final List<UserActionSequenceItemI> filteredItems = items.stream().filter(filter::filter).collect(Collectors.toList());
        // For sub item filter, filter out items with the same parent item
        if (filter == UserActionSequenceDisplayFilter.ONLY_SUB) {
            final int currentItemIndex = findLastCurrentActionProp(filteredItems);
            if (currentItemIndex != -1) {
                final UserActionSequenceItemI commonParent = filteredItems.get(currentItemIndex).itemParentProperty().get();
                if (commonParent != null) {
                    filteredItems.removeIf(item -> item.itemParentProperty().get() != commonParent);
                }
            }
        }
        final List<UserActionSequenceItemKeyOption> filteredKeyOptions = keyOptions.stream().filter(ko -> ko.displayFilterProperty().get() == filter).collect(Collectors.toList());
        showItemsFromSequenceIn(filter, filteredItems, filteredKeyOptions);
    }

    private void showItemsFromSequenceIn(UserActionSequenceDisplayFilter filter, List<UserActionSequenceItemI> items, List<UserActionSequenceItemKeyOption> keyOptions) {
        final int itemCountBeforeCurrent = keyOptions.size() / 2;

        int currentItemIndex = findLastCurrentActionProp(items);

        // Prepare items to show with shift
        List<UserActionSequenceItemI> itemsToBeDisplayed = new ArrayList<>();
        for (int i = Math.max(currentItemIndex - itemCountBeforeCurrent, 0); i < items.size(); i++) {
            itemsToBeDisplayed.add(items.get(i));
        }
        // If not enough item to be displayed, add previous items
        for (int i = Math.max(currentItemIndex - itemCountBeforeCurrent, 0) - 1; i >= 0 && itemsToBeDisplayed.size() < keyOptions.size(); i--) {
            itemsToBeDisplayed.add(0, items.get(i));
        }
        // Display them
        for (int i = 0; i < keyOptions.size(); i++) {
            keyOptions.get(i).currentSimplerKeyContentContainerProperty().set(i < itemsToBeDisplayed.size() ? itemsToBeDisplayed.get(i) : null);
        }
    }

    private int findLastCurrentActionProp(List<UserActionSequenceItemI> items) {
        int currentItemIndex = -1;
        for (int i = items.size() - 1; i >= 0 && currentItemIndex < 0; i--) {
            if (items.get(i).currentActionProperty().get()) {
                currentItemIndex = i;
            }
        }
        return currentItemIndex;
    }

    private int getCurrentItemIndex(UserActionSequenceI sequence) {
        return currentItem.get() != null ? sequence.getItems().indexOf(currentItem.get()) : 0;
    }
    //========================================================================


    // START/STOP
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        currentConfiguration = configuration;

        LCUtils.findKeyOptionsByGrid(UserActionSequenceCurrentKeyOption.class, configuration, currentItemKeyOptions, null);
        LCUtils.findKeyOptionsByGrid(UserActionSequenceItemKeyOption.class, configuration, currentSequenceItemKeyOptions, null);

        // Set the parent for every sub items in every sequences
        for (UserActionSequenceI userActionSequence : configuration.userActionSequencesProperty().get().getUserActionSequences()) {
            UserActionSequenceItemI parent = null;
            final ObservableList<UserActionSequenceItemI> sequenceItems = userActionSequence.getItems();
            for (final UserActionSequenceItemI currentItem : sequenceItems) {
                if (currentItem.subItemProperty().get()) {
                    currentItem.itemParentProperty().set(parent);
                } else {
                    currentItem.itemParentProperty().set(null);
                    parent = currentItem;
                }
            }
        }
    }

    @Override
    public void modeStop(LCConfigurationI configuration) {
        this.currentConfiguration = null;
        currentItemKeyOptions.clear();
        currentSequenceItemKeyOptions.clear();
        this.currentSequence.set(null);
        this.currentItem.set(null);
    }
    //========================================================================
}
