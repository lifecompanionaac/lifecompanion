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
package org.lifecompanion.controller.configurationcomponent.dynamickey;

import javafx.beans.property.*;
import javafx.collections.ObservableList;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.controller.lifecycle.AppMode;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.selectionmode.SelectionModeController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.framework.utils.Pair;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionEvent;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.keyoption.KeyOptionI;
import org.lifecompanion.model.api.lifecycle.ModeListenerI;
import org.lifecompanion.model.impl.categorizedelement.useaction.available.*;
import org.lifecompanion.model.impl.configurationcomponent.keyoption.dynamickey.KeyListNodeKeyOption;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum KeyListController implements ModeListenerI {
    INSTANCE;
    private final static Logger LOGGER = LoggerFactory.getLogger(KeyListController.class);

    private final ObjectProperty<KeyListNodeI> currentNode;

    /**
     * Useful to know the possible display size of each node per grid + to update display for a category
     */
    private final Map<Pair<GridComponentI, String>, StatusForGridAndCategory> statusForGridAndCategoryMap;
    private final Map<GridComponentI, List<KeyListNodeKeyOption>> keyOptionsPerGrid;
    private KeyListNodeI rootKeyListNode;

    private final List<KeyListNodeI> nodeHistory;

    private final Set<Runnable> goParentKeyNodeNoParentListener;
    private final Set<Runnable> nextWithoutLoopEndReachedListener;
    private final Set<Runnable> previousWithoutLoopStartReachedListener;


    KeyListController() {
        currentNode = new SimpleObjectProperty<>();
        keyOptionsPerGrid = new HashMap<>();
        statusForGridAndCategoryMap = new HashMap<>();
        goParentKeyNodeNoParentListener = new HashSet<>();
        nextWithoutLoopEndReachedListener = new HashSet<>();
        previousWithoutLoopStartReachedListener = new HashSet<>();
        nodeHistory = new ArrayList<>();

        // In config mode : refresh from scratch on every changes in config (but try to restore state)
        AppModeController.INSTANCE.getEditModeContext().configurationUnsavedActionProperty().addListener((obs, ov, nv) -> {
            final LCConfigurationI configuration = AppModeController.INSTANCE.getEditModeContext().getConfiguration();
            if (configuration != null) {
                refreshKeyListFromScratch(configuration, true);
            }
        });
        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                refreshKeyListFromScratch(nv, true);
            } else {
                clearCurrentConfiguration();
            }
        });
    }

    // LISTENER
    //========================================================================
    public Set<Runnable> getGoParentKeyNodeNoParentListener() {
        return goParentKeyNodeNoParentListener;
    }

    public Set<Runnable> getNextWithoutLoopEndReachedListener() {
        return nextWithoutLoopEndReachedListener;
    }

    public Set<Runnable> getPreviousWithoutLoopStartReachedListener() {
        return previousWithoutLoopStartReachedListener;
    }
    //========================================================================

    // PUBLIC API
    //========================================================================
    public void selectNode(KeyListNodeI node) {
        selectNode(node, false);
    }

    public void selectNode(KeyListNodeI node, boolean skipHistory) {
        if (node != null && !node.isLeafNode()) {
            // Reset the destination node page index to first page
            this.forEach(node, (statusForGridAndCategory, grid, keyOptions, nodeChildren, pageIndex, pageSize, maxPageCount) -> statusForGridAndCategory.pageIndex.set(0));
            currentNode.set(node);
            if (!skipHistory) {
                if (node == rootKeyListNode) {
                    nodeHistory.clear();
                }
                nodeHistory.add(node);
            }
            updateDisplayedKeys(currentNode.get());
        }
    }

    public void goRootNode() {
        selectNode(rootKeyListNode);
    }

    public void goParentKeyNode() {
        if (nodeHistory.size() >= 2) {
            nodeHistory.remove(nodeHistory.size() - 1);
            final KeyListNodeI lastNodeBeforeCurrent = nodeHistory.remove(nodeHistory.size() - 1);
            selectNode(lastNodeBeforeCurrent);
        } else if (currentNode.get() != null && currentNode.get().parentProperty().get() != null) {
            selectNode(currentNode.get().parentProperty().get(), true);
        } else {
            goParentKeyNodeNoParentListener.forEach(Runnable::run);
        }
    }

    public void nextInCurrent() {
        this.nextIn(currentNode.get(), true);
    }

    public void nextInCurrentWithoutLoop() {
        this.nextIn(currentNode.get(), false);
    }

    public void previousInCurrent() {
        this.previousIn(currentNode.get(), true);
    }

    public void previousInCurrentWithoutLoop() {
        this.previousIn(currentNode.get(), false);
    }


    public void nextIn(KeyListNodeI node, boolean enableLoop) {
        this.forEach(node, (statusForGridAndCategory, grid, keyOptions, nodeChildren, pageIndex, pageSize, maxPageCount) -> {
            if (pageIndex + 1 < maxPageCount) {
                statusForGridAndCategory.pageIndex.set(pageIndex + 1);
            } else if (enableLoop) {
                statusForGridAndCategory.pageIndex.set(0);
            } else {
                nextWithoutLoopEndReachedListener.forEach(Runnable::run);
            }
        });
        updateDisplayedKeys(node);
    }

    public void previousIn(KeyListNodeI node, boolean enableLoop) {
        this.forEach(node, (statusForGridAndCategory, grid, keyOptions, nodeChildren, pageIndex, pageSize, maxPageCount) -> {
            if (pageIndex - 1 >= 0) {
                statusForGridAndCategory.pageIndex.set(pageIndex - 1);
            } else if (enableLoop) {
                statusForGridAndCategory.pageIndex.set(maxPageCount - 1);
            } else {
                previousWithoutLoopStartReachedListener.forEach(Runnable::run);
            }
        });
        updateDisplayedKeys(node);
    }

    public void nextOnLevel(int level) {
        findAllNodeForExactLevel(level).forEach(n -> this.nextIn(n, true));
    }

    private Set<KeyListNodeI> findAllNodeForExactLevel(int level) {
        Set<KeyListNodeI> nodeToExecuteNextOn = new HashSet<>();
        this.statusForGridAndCategoryMap.forEach((grid, statusForGridAndCategoryMap) -> {
            if (statusForGridAndCategoryMap.node.levelProperty().get() == level) {
                nodeToExecuteNextOn.add(statusForGridAndCategoryMap.node);
            }
        });
        return nodeToExecuteNextOn;
    }

    public void previousOnLevel(int level) {
        findAllNodeForExactLevel(level).forEach(n -> this.previousIn(n, true));
    }
    //========================================================================


    // PRIVATE - STATUS
    //========================================================================
    private void updateDisplayedKeys(KeyListNodeI node) {
        this.forEach(node, (statusForGridAndCategory, grid, keyOptions, nodeChildren, pageIndex, pageSize, maxPageCount) -> {
            for (int i = 0; i < pageSize; i++) {
                final KeyListNodeKeyOption keyOption = statusForGridAndCategory.keyOptions.get(i);
                int keyIndex = pageIndex * pageSize + i;
                FXThreadUtils.runOnFXThread(() -> keyOption.currentSimplerKeyContentContainerProperty().set(keyIndex >= 0 && keyIndex < nodeChildren.size() ? nodeChildren.get(keyIndex) : null));
            }
        });
        if (AppModeController.INSTANCE.modeProperty().get() == AppMode.USE) {
            FXThreadUtils.runOnFXThread(SelectionModeController.INSTANCE::generateScanningForCurrentGridAndRestart);
        }
    }


    private void forEach(KeyListNodeI forNode, ForEachKeyAndStatusConsumer consumer) {
        this.keyOptionsPerGrid.forEach((grid, keyOptions) -> {
            final StatusForGridAndCategory statusForGridAndCategory = getKeyOptionForNodeAndGridIncludeNonSpecificNodeDisplayer(forNode, grid);
            if (statusForGridAndCategory != null) {
                final ObservableList<KeyListNodeI> nodeChildren = forNode.getChildren();
                int pageIndex = statusForGridAndCategory.pageIndex.get();
                int pageSize = statusForGridAndCategory.keyOptions.size();
                int maxPageCount = (int) Math.ceil((1.0 * nodeChildren.size()) / (1.0 * pageSize));
                consumer.accept(statusForGridAndCategory, grid, keyOptions, nodeChildren, pageIndex, pageSize, maxPageCount);
            }
        });
    }

    public int getPageCount(GridComponentI grid, KeyListNodeI node) {
        final StatusForGridAndCategory status = this.getKeyOptionForNodeAndGridIncludeNonSpecificNodeDisplayer(node, grid);
        return status != null ? (int) Math.ceil((1.0 * node.getChildren().size()) / (1.0 * status.keyOptions.size())) : -1;
    }

    public void selectNodeById(String nodeId) {
        final KeyListNodeI nodeToSelect = findNodeByIdInSubtree(rootKeyListNode, nodeId);
        if (nodeToSelect != null) {
            selectNode(nodeToSelect);
        }
    }

    public static KeyListNodeI findNodeByIdInSubtree(KeyListNodeI node, String nodeId) {
        AtomicReference<KeyListNodeI> found = new AtomicReference<>();
        node.traverseTreeToBottom(childNode -> {
            if (StringUtils.isEquals(nodeId, childNode.getID())) {
                found.set(childNode);
            }
        });
        return found.get();
    }


    private interface ForEachKeyAndStatusConsumer {
        void accept(StatusForGridAndCategory statusForGridAndCategory,
                    GridComponentI grid,
                    List<KeyListNodeKeyOption> keyOptions,
                    List<KeyListNodeI> nodeChildren,
                    int pageIndex,
                    int pageSize,
                    int maxPageCount);
    }

    private StatusForGridAndCategory getKeyOptionForNodeAndGridIncludeNonSpecificNodeDisplayer(KeyListNodeI currentNodeValue, GridComponentI grid) {
        return statusForGridAndCategoryMap.get(Pair.of(grid, currentNodeValue.getID()));
    }

    public Map<Pair<GridComponentI, String>, StatusForGridAndCategory> getStatusForGridAndCategoryMap() {
        return statusForGridAndCategoryMap;
    }

    public String getCurrentNodeId() {
        return currentNode.get() != null ? currentNode.get().getID() : null;
    }
    //========================================================================


    // MODE
    //========================================================================
    @Override
    public void modeStart(LCConfigurationI configuration) {
        refreshKeyListFromScratch(configuration, false);
    }

    private void refreshKeyListFromScratch(LCConfigurationI configuration, boolean tryToRestoreState) {
        String previouslySelectedNodeIdToRestore = tryToRestoreState && currentNode.get() != null ? currentNode.get().getID() : null;

        clearCurrentConfiguration();
        rootKeyListNode = configuration.rootKeyListNodeProperty().get();

        // Find all keys that display key list
        ConfigurationComponentUtils.findKeyOptionsByGrid(KeyListNodeKeyOption.class, configuration, keyOptionsPerGrid, null);

        // Sort these keys options by their possibles displayed categories
        keyOptionsPerGrid.forEach((grid, keyOptions) -> {
            for (KeyListNodeKeyOption keyOption : keyOptions) {
                rootKeyListNode.traverseTreeToBottom(node -> {
                    final int nodeLevel = node.levelProperty().get();
                    if (!node.isLeafNode() && (!keyOption.specificLevelProperty().get() || keyOption.selectedLevelProperty().get() == nodeLevel || (keyOption.displayLevelBellowProperty()
                            .get() && nodeLevel > keyOption.selectedLevelProperty().get()))) {
                        statusForGridAndCategoryMap.computeIfAbsent(Pair.of(grid, node.getID()), key -> new StatusForGridAndCategory(grid, node)).keyOptions.add(keyOption);
                    }
                });
            }
        });

        // TODO : should never leave blank keys ? (sublevel...)
        KeyListNodeI nodeToSetAsCurrent = previouslySelectedNodeIdToRestore != null ? findNodeByIdInSubtree(rootKeyListNode, previouslySelectedNodeIdToRestore) : null;
        selectNode(nodeToSetAsCurrent != null ? nodeToSetAsCurrent : rootKeyListNode);
    }


    @Override
    public void modeStop(LCConfigurationI configuration) {
        clearCurrentConfiguration();
    }

    private void clearCurrentConfiguration() {
        this.currentNode.set(null);
        this.statusForGridAndCategoryMap.clear();
        this.keyOptionsPerGrid.clear();
        rootKeyListNode = null;
        nodeHistory.clear();
    }
    //========================================================================

    // CONFIG MODE SIMULATION
    //========================================================================
    public void simulateKeyListKeyActions(GridPartKeyComponentI key) {
        if (isKeylistKeyOptionWithValidSelectAction(key)) {
            selectKeyNodeAction(key);
        } else {
            NextInCurrentKeyListAction nextInCurrentKeyListAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextInCurrentKeyListAction.class);
            NextInCurrentKeyListNoLoopAction nextInCurrentKeyListNoLoopAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextInCurrentKeyListNoLoopAction.class);
            PreviousInCurrentKeyListAction previousInCurrentKeyListAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousInCurrentKeyListAction.class);
            PreviousInCurrentKeyListNoLoopAction previousInCurrentKeyListNoLoopAction = key.getActionManager()
                    .getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousInCurrentKeyListNoLoopAction.class);
            GoParentCurrentKeyNodeAction goParentCurrentKeyNodeAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, GoParentCurrentKeyNodeAction.class);
            GoRootKeyNodeAction goRootKeyNodeAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, GoRootKeyNodeAction.class);
            NextKeysOnSpecificLevelAction nextKeysOnSpecificLevelAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextKeysOnSpecificLevelAction.class);
            PreviousKeysOnSpecificLevelAction previousKeysOnSpecificLevelAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousKeysOnSpecificLevelAction.class);
            SelectSpecificKeyListAction selectSpecificKeyListAction = key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, SelectSpecificKeyListAction.class);
            if (nextInCurrentKeyListAction != null) {
                nextInCurrent();
            } else if (previousInCurrentKeyListAction != null) {
                previousInCurrent();
            } else if (nextInCurrentKeyListNoLoopAction != null) {
                nextInCurrentWithoutLoop();
            } else if (previousInCurrentKeyListNoLoopAction != null) {
                previousInCurrentWithoutLoop();
            } else if (goParentCurrentKeyNodeAction != null) {
                goParentKeyNode();
            } else if (goRootKeyNodeAction != null) {
                goRootNode();
            } else if (nextKeysOnSpecificLevelAction != null) {
                nextOnLevel(nextKeysOnSpecificLevelAction.selectedLevelProperty().get());
            } else if (previousKeysOnSpecificLevelAction != null) {
                previousOnLevel(previousKeysOnSpecificLevelAction.selectedLevelProperty().get());
            } else if (selectSpecificKeyListAction != null) {
                if (StringUtils.isNotBlank(selectSpecificKeyListAction.linkedNodeIdProperty().get())) {
                    selectNodeById(selectSpecificKeyListAction.linkedNodeIdProperty().get());
                }
            }
        }
    }


    public boolean isKeySimulatedAsKeyListActions(GridPartKeyComponentI key) {
        return isKeylistKeyOptionWithValidSelectAction(key)
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextInCurrentKeyListAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextInCurrentKeyListNoLoopAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousInCurrentKeyListAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousInCurrentKeyListNoLoopAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, GoParentCurrentKeyNodeAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, GoRootKeyNodeAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, NextKeysOnSpecificLevelAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, PreviousKeysOnSpecificLevelAction.class) != null
                || key.getActionManager().getFirstActionOfType(UseActionEvent.ACTIVATION, SelectSpecificKeyListAction.class) != null;
    }

    private boolean isKeylistKeyOptionWithValidSelectAction(GridPartKeyComponentI key) {
        if (key != null) {
            final KeyOptionI keyOption = key.keyOptionProperty().get();
            if (keyOption instanceof KeyListNodeKeyOption) {
                KeyListNodeKeyOption keyListNodeKeyOption = (KeyListNodeKeyOption) keyOption;
                KeyListNodeI node = keyListNodeKeyOption.currentSimplerKeyContentContainerProperty().get();
                return node != null && (!node.isLeafNode() || StringUtils.isNotBlank(node.linkedNodeIdProperty().get()) || StringUtils.isNotBlank(node.linkedGridIdProperty().get()));
            }
        }
        return false;
    }
    //========================================================================

    // KEY ACTIONS
    //========================================================================
    public void selectKeyNodeAction(GridPartKeyComponentI key) {
        if (key != null) {
            final KeyOptionI keyOption = key.keyOptionProperty().get();
            if (keyOption instanceof KeyListNodeKeyOption) {
                KeyListNodeKeyOption keyListNodeKeyOption = (KeyListNodeKeyOption) keyOption;
                KeyListNodeI node = keyListNodeKeyOption.currentSimplerKeyContentContainerProperty().get();
                if (StringUtils.isBlank(node.linkedNodeIdProperty().get()) && StringUtils.isBlank(node.linkedGridIdProperty().get())) {
                    selectNode(node);
                } else {
                    if (StringUtils.isNotBlank(node.linkedNodeIdProperty().get())) {
                        selectNodeById(node.linkedNodeIdProperty().get());
                    } else if (StringUtils.isNotBlank(node.linkedGridIdProperty().get())) {
                        if (AppModeController.INSTANCE.isUseMode()) {
                            GridComponentI targetGrid = ConfigurationComponentUtils.findById(AppModeController.INSTANCE.getUseModeContext().getConfiguration(),
                                    node.linkedGridIdProperty().get(),
                                    GridComponentI.class);
                            if (targetGrid != null) {
                                SelectionModeController.INSTANCE.goToGridPart(targetGrid);
                            }
                        } else if (AppModeController.INSTANCE.isEditMode()) {
                            GridComponentI targetGrid = ConfigurationComponentUtils.findById(AppModeController.INSTANCE.getEditModeContext().getConfiguration(),
                                    node.linkedGridIdProperty().get(),
                                    GridComponentI.class);
                            SelectionController.INSTANCE.selectDisplayableComponent(targetGrid, true);
                        }
                    }
                }
            }
        }
    }
    //========================================================================

    // INTERNAL DATA CLASS
    //========================================================================
    public static class StatusForGridAndCategory {
        private final GridComponentI grid;
        private final KeyListNodeI node;
        private final List<KeyListNodeKeyOption> keyOptions;
        private final IntegerProperty pageIndex;

        StatusForGridAndCategory(GridComponentI grid, KeyListNodeI node) {
            this.grid = grid;
            this.node = node;
            this.keyOptions = new ArrayList<>();
            this.pageIndex = new SimpleIntegerProperty();
        }

        @Override
        public String toString() {
            return "StatusForGridAndCategory{" +
                    "grid=" + grid +
                    ", node=" + node +
                    ", keyOptions=" + keyOptions +
                    ", pageIndex=" + pageIndex +
                    '}';
        }
    }
    //========================================================================


}
