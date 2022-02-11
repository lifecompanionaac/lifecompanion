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

package org.lifecompanion.config.data.action.impl;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.model.impl.exception.LCException;
import org.lifecompanion.util.PositionSize;
import org.lifecompanion.config.data.action.impl.GridActions.ReplaceComponentAction;
import org.lifecompanion.config.data.action.impl.GridActions.ReplaceMultiCompAction;
import org.lifecompanion.config.data.action.impl.GridStackActions.AddGridInStackAction;
import org.lifecompanion.config.data.control.SelectionController;
import org.lifecompanion.config.data.notif.LCNotification;
import org.lifecompanion.config.view.pane.main.notification2.LCNotificationController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
        private LCConfigurationI configuration;
        private RootGraphicComponentI added;
        private final Node source;

        public AddRootComponentAction(Node source, final LCConfigurationI configurationP, final RootGraphicComponentI addedP) {
            this.configuration = configurationP;
            this.added = addedP;
            this.source = source;
        }

        @Override
        public void doAction() throws LCException {
            this.addComponent(true);
        }

        private void addComponent(final boolean selectAndNotify) {
            this.added.dispatchRemovedPropertyValue(false);
            this.configuration.getChildren().add(this.added);
            if (selectAndNotify) {
                SelectionController.INSTANCE.setSelectedRoot(this.added);
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

        private StackComponentI stackComponent;
        private GridComponentI toReplace, component;
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
                SelectionController.INSTANCE.setSelectedPart(this.component);
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
        private LCConfigurationI targetConfiguration;
        private ConfigurationChildComponentI toPasteComponent;
        private ConfigurationChildComponentI targetComponent;
        private UndoRedoActionI pasteAction;
        private List<GridPartKeyComponentI> keys;

        public PasteComponentAction(final LCConfigurationI targetConfiguration, final ConfigurationChildComponentI toPasteComponent,
                                    final ConfigurationChildComponentI targetComponent, final List<GridPartKeyComponentI> keys) {
            this.keys = keys;
            this.targetConfiguration = targetConfiguration;
            this.toPasteComponent = toPasteComponent;
            this.targetComponent = targetComponent;
        }

        @Override
        public void doAction() throws LCException {
            //Create the action relative the component type and target component
            //Root component
            if (this.toPasteComponent instanceof RootGraphicComponentI) {
                this.pasteAction = new AddRootComponentAction(null, this.targetConfiguration, (RootGraphicComponentI) this.toPasteComponent);//FIXME : null source node
            }
            if (this.toPasteComponent instanceof GridPartComponentI && this.targetComponent != null) {
                //Grid part into a stack : add the stack
                if (this.targetComponent instanceof StackComponentI && this.toPasteComponent instanceof GridComponentI) {
                    this.pasteAction = new AddGridInStackAction((StackComponentI) this.targetComponent, (GridComponentI) this.toPasteComponent, true, true);
                }
                //Grid part copied into another grid part
                else if (this.targetComponent instanceof GridPartComponentI) {
                    GridPartComponentI gridPasteComponent = (GridPartComponentI) this.toPasteComponent;
                    GridPartComponentI gridTargetComponent = (GridPartComponentI) this.targetComponent;
                    GridComponentI targetParent = gridTargetComponent.gridParentProperty().get();
                    //Target is a key list
                    if (!CollectionUtils.isEmpty(this.keys) && this.keys.size() > 1) {
                        this.pasteAction = new ReplaceMultiCompAction(gridPasteComponent, this.keys);
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("notification.key.list.paste"));
                    }
                    //Replace in a grid
                    else if (targetParent != null) {
                        this.pasteAction = new ReplaceComponentAction(targetParent.getGrid(), gridTargetComponent, gridPasteComponent, true);
                        LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.grid.part.copied.in.another.part", gridPasteComponent.getDisplayableTypeName())));
                    }
                    //Replace in stack
                    else {
                        StackComponentI stackParent = gridTargetComponent.stackParentProperty().get();
                        if (stackParent != null && gridPasteComponent instanceof GridComponentI) {
                            this.pasteAction = new ReplaceGridInStackAction(stackParent, (GridComponentI) gridTargetComponent,
                                    (GridComponentI) gridPasteComponent, true);
                            LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo(Translation.getText("notification.grid.replaced.previous.notification", gridPasteComponent.getDisplayableTypeName(), gridTargetComponent.getDisplayableTypeName())));
                        } else {
                            throw LCException.newException().withMessageId("use.action.impossible.paste.grid.stack.action").build();
                        }
                    }
                }
            }

            if (this.pasteAction != null) {
                OptionActions.LOGGER.info("Will execute the paste action for copied component type {} : {}",
                        this.toPasteComponent.getClass().getSimpleName(), this.pasteAction.getClass().getSimpleName());
                this.pasteAction.doAction();
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
            if (this.pasteAction != null) {
                this.pasteAction.undoAction();
            } else {
                OptionActions.LOGGER.warn("Can't undo paste action because there is no executed action");
            }
        }

        @Override
        public void redoAction() throws LCException {
            if (this.pasteAction != null) {
                this.pasteAction.redoAction();
            } else {
                OptionActions.LOGGER.warn("Can't redo paste action because there is no executed action");
            }
        }
    }
}
