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

package org.lifecompanion.controller.editaction;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.lifecompanion.controller.editaction.GridActions.ReplaceComponentAction;
import org.lifecompanion.controller.editaction.GridActions.ReplaceMultiCompAction;
import org.lifecompanion.controller.editaction.GridStackActions.AddGridInStackAction;
import org.lifecompanion.controller.editmode.SelectionController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.model.PositionSize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class that keep actions related to the option on component, like move, resize, etc...
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class OptionActions {
    private static final Logger LOGGER = LoggerFactory.getLogger(OptionActions.class);

    public static final KeyCombination KEY_COMBINATION_COPY = new KeyCodeCombination(KeyCode.C, KeyCodeCombination.SHORTCUT_DOWN);
    public static final KeyCombination KEY_COMBINATION_PASTE = new KeyCodeCombination(KeyCode.V, KeyCodeCombination.SHORTCUT_DOWN);

    /**
     * When a component is resized
     */
    public static class ResizeAction<T extends ResizableComponentI & MovableComponentI> implements UndoRedoActionI {
        private final PositionSize initState, finalState;
        private final T component;

        public ResizeAction(final PositionSize initStateP, final PositionSize finalStateP, final T componentP) {
            this.initState = initStateP;
            this.finalState = finalStateP;
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            this.finalState.setPositionAndSizeOn(this.component);
        }

        @Override
        public String getNameID() {
            return "action.resize.name";
        }

        @Override
        public void undoAction() throws LCException {
            this.initState.setPositionAndSizeOn(this.component);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    /**
     * When a component is moved
     */
    public static class MoveAction<T extends MovableComponentI> implements UndoRedoActionI {
        private final PositionSize initState, finalState;
        private final T component;

        public MoveAction(final PositionSize initStateP, final PositionSize finalStateP, final T componentP) {
            this.initState = initStateP;
            this.finalState = finalStateP;
            this.component = componentP;
        }

        @Override
        public void doAction() throws LCException {
            this.finalState.setPositionOn(this.component);
        }

        @Override
        public String getNameID() {
            return "action.move.name";
        }

        @Override
        public void undoAction() throws LCException {
            this.initState.setPositionOn(this.component);
        }

        @Override
        public void redoAction() throws LCException {
            this.doAction();
        }
    }

    /**
     * When a component is added to a configuration.
     */
    public static class AddRootComponentAction implements UndoRedoActionI {
        private final LCConfigurationI configuration;
        private final RootGraphicComponentI added;

        public AddRootComponentAction(final LCConfigurationI configurationP, final RootGraphicComponentI addedP) {
            this.configuration = configurationP;
            this.added = addedP;
        }

        @Override
        public void doAction() throws LCException {
            this.addComponent(true);
        }

        private void addComponent(final boolean selectAndNotify) {
            this.added.dispatchRemovedPropertyValue(false);
            this.configuration.getChildren().add(this.added);
            if (selectAndNotify) {
                SelectionController.INSTANCE.selectDisplayableComponent(this.added, true);
                LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.root.component.added", added.getDisplayableTypeName())));
            }
        }

        @Override
        public String getNameID() {
            return "action.add.root.component.name";
        }

        @Override
        public void undoAction() throws LCException {
            //Remove added
            this.configuration.getChildren().remove(this.added);
        }

        @Override
        public void redoAction() throws LCException {
            this.addComponent(false);
        }
    }

    /**
     * Action to replace a grid in stack
     */
    public static class ReplaceGridInStackAction implements UndoRedoActionI {

        private final StackComponentI stackComponent;
        private final GridComponentI toReplace;
        private final GridComponentI component;
        private boolean select;

        public ReplaceGridInStackAction(final StackComponentI stackComponent, final GridComponentI toReplace, final GridComponentI component,
                                        final boolean select) {
            this.stackComponent = stackComponent;
            this.toReplace = toReplace;
            this.component = component;
            this.select = select;
        }

        @Override
        public void doAction() throws LCException {
            this.stackComponent.replace(this.toReplace, this.component);
            //Delete replace, and undelete component
            this.toReplace.dispatchRemovedPropertyValue(true);
            this.component.dispatchRemovedPropertyValue(false);
            //Select
            if (this.select) {
                SelectionController.INSTANCE.selectDisplayableComponent(this.component, true);
            }
        }

        @Override
        public String getNameID() {
            return "action.replace.grid.in.stack";
        }

        @Override
        public void undoAction() throws LCException {
            this.stackComponent.replace(this.component, this.toReplace);
            //Delete comp, and undelete replace
            this.component.dispatchRemovedPropertyValue(true);
            this.toReplace.dispatchRemovedPropertyValue(false);
        }

        @Override
        public void redoAction() throws LCException {
            this.select = false;
            this.doAction();
        }
    }

    /**
     * When we copy a component.<br>
     */
    public static class PasteComponentAction implements UndoRedoActionI {
        private final LCConfigurationI targetConfiguration;
        private final ConfigurationChildComponentI toPasteComponent;
        private final ConfigurationChildComponentI targetComponent;
        private final List<UndoRedoActionI> pasteActions;
        private final List<GridPartKeyComponentI> keys;
        private final Set<GridPartKeyComponentI> toPasteKeys;

        public PasteComponentAction(final LCConfigurationI targetConfiguration, final ConfigurationChildComponentI toPasteComponent, final Set<GridPartKeyComponentI> toPasteKeys,
                                    final ConfigurationChildComponentI targetComponent, final List<GridPartKeyComponentI> targetKeys) {
            this.pasteActions = new ArrayList<>();
            this.toPasteKeys = toPasteKeys;
            this.keys = targetKeys;
            this.targetConfiguration = targetConfiguration;
            this.toPasteComponent = toPasteComponent;
            this.targetComponent = targetComponent;
        }

        @Override
        public void doAction() throws LCException {
            //Create the action relative the component type and target component

            //Root component
            if (this.toPasteComponent instanceof RootGraphicComponentI) {
                this.pasteActions.add(new AddRootComponentAction(this.targetConfiguration, (RootGraphicComponentI) this.toPasteComponent));
            }
            if (this.toPasteComponent instanceof GridPartComponentI && this.targetComponent != null) {
                //Grid part into a stack : add the stack
                if (this.targetComponent instanceof StackComponentI && this.toPasteComponent instanceof GridComponentI) {
                    this.pasteActions.add(new AddGridInStackAction((StackComponentI) this.targetComponent, (GridComponentI) this.toPasteComponent, true, true));
                }
                // Keys copied to another keys
                else if (this.targetComponent instanceof GridPartKeyComponentI && toPasteKeys.size() > 1) {
                    /*
                     * Algo :
                     * 2) take the min top-left position as the reference for source keys and always compute their position relative to this min
                     * 3) in the target grid, start from the target key position and go through the grid end, searching for corresponding source key positions (ignore duplicates)
                     * 4) if found, replace the component at the position with the component to copy (never do it twice)
                     */
                    GridPartKeyComponentI targetKey = (GridPartKeyComponentI) targetComponent;
                    GridComponentI gridComponent = targetKey.gridParentProperty().get();
                    if (gridComponent != null) {
                        int rowMin = toPasteKeys.stream().mapToInt(k -> k.rowProperty().get()).min().orElse(0);
                        int columMin = toPasteKeys.stream().mapToInt(k -> k.columnProperty().get()).min().orElse(0);
                        Set<GridPartComponentI> replaced = new HashSet<>();
                        int targetRowStart = targetKey.rowProperty().get();
                        int targetColumnStart = targetKey.columnProperty().get();
                        for (int r = targetRowStart; r < gridComponent.rowCountProperty().get(); r++) {
                            for (int c = targetColumnStart; c < gridComponent.columnCountProperty().get(); c++) {
                                GridPartComponentI targetComp = gridComponent.getGrid().getComponent(r, c);
                                if (!replaced.contains(targetComp)) {
                                    replaced.add(targetComp);
                                    int searchedRow = r - targetRowStart;
                                    int searchedColumn = c - targetColumnStart;
                                    if (searchedRow >= 0 && searchedColumn >= 0) {
                                        toPasteKeys.stream().filter(k ->
                                                k.rowProperty().get() - rowMin == searchedRow && k.columnProperty().get() - columMin == searchedColumn).findAny().ifPresent(k -> {
                                            toPasteKeys.remove(k);
                                            pasteActions.add(new ReplaceComponentAction(gridComponent.getGrid(), targetComp, k, false));
                                        });
                                    }
                                }
                            }
                        }
                    }
                }
                //Grid part copied into another grid part
                else if (this.targetComponent instanceof GridPartComponentI) {
                    GridPartComponentI gridPasteComponent = (GridPartComponentI) this.toPasteComponent;
                    GridPartComponentI gridTargetComponent = (GridPartComponentI) this.targetComponent;
                    GridComponentI targetParent = gridTargetComponent.gridParentProperty().get();
                    //Target is a key list
                    if (!CollectionUtils.isEmpty(this.keys) && this.keys.size() > 1) {
                        this.pasteActions.add(new ReplaceMultiCompAction(gridPasteComponent, this.keys));
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.key.list.paste"));
                    }
                    //Replace in a grid
                    else if (targetParent != null) {
                        this.pasteActions.add(new ReplaceComponentAction(targetParent.getGrid(), gridTargetComponent, gridPasteComponent, true));
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.grid.part.copied.in.another.part",
                                gridPasteComponent.getDisplayableTypeName())));
                    }
                    //Replace in stack
                    else {
                        StackComponentI stackParent = gridTargetComponent.stackParentProperty().get();
                        if (stackParent != null && gridPasteComponent instanceof GridComponentI) {
                            this.pasteActions.add(new ReplaceGridInStackAction(stackParent, (GridComponentI) gridTargetComponent,
                                    (GridComponentI) gridPasteComponent, true));
                            LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.grid.replaced.previous.notification",
                                    gridPasteComponent.getDisplayableTypeName(),
                                    gridTargetComponent.getDisplayableTypeName())));
                        } else {
                            throw LCException.newException().withMessageId("use.action.impossible.paste.grid.stack.action").build();
                        }
                    }
                }
            }

            if (!this.pasteActions.isEmpty()) {
                OptionActions.LOGGER.info("Will execute the paste action for copied component type {} : {}",
                        this.toPasteComponent.getClass().getSimpleName(), this.pasteActions.stream().map(a -> a.getClass().getSimpleName()).collect(Collectors.joining(", ")));
                for (UndoRedoActionI pasteAction : this.pasteActions) {
                    pasteAction.doAction();
                }
            } else {
                OptionActions.LOGGER.warn("Didn't found any correct paste action for copied component {} (target component {})",
                        this.toPasteComponent.getClass(), this.targetComponent != null ? this.targetComponent.getClass() : "null");
            }
        }

        @Override
        public String getNameID() {
            return "paste.action.name";
        }

        @Override
        public void undoAction() throws LCException {
            if (!this.pasteActions.isEmpty()) {
                for (int i = this.pasteActions.size() - 1; i >= 0; i--) {
                    this.pasteActions.get(i).undoAction();
                }
            } else {
                OptionActions.LOGGER.warn("Can't undo paste action because there is no executed action");
            }
        }

        @Override
        public void redoAction() throws LCException {
            if (!this.pasteActions.isEmpty()) {
                for (UndoRedoActionI pasteAction : this.pasteActions) {
                    pasteAction.redoAction();
                }
            } else {
                OptionActions.LOGGER.warn("Can't redo paste action because there is no executed action");
            }
        }
    }
}
