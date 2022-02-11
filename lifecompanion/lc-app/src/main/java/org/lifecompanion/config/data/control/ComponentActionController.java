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
package org.lifecompanion.config.data.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableList;
import org.lifecompanion.model.api.configurationcomponent.*;
import org.lifecompanion.model.api.editaction.UndoRedoActionI;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.config.data.action.impl.GridStackActions;
import org.lifecompanion.config.data.action.impl.OptionActions.PasteComponentAction;
import org.lifecompanion.config.data.action.impl.RemoveActions.RemoveGridPartAction;
import org.lifecompanion.config.data.action.impl.RemoveActions.RemoveMultipleKeyAction;
import org.lifecompanion.config.data.action.impl.RemoveActions.RemoveRootComponentAction;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * The controller to manage the copy/paste actions.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ComponentActionController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(ComponentActionController.class);

    /**
     * The current copied component
     */
    private final ObjectProperty<ConfigurationChildComponentI> copiedComponent;

    /**
     * Source for style copy on keys
     */
    private final ObjectProperty<DisplayableComponentI> styleCopySource;

    /**
     * Private singleton constructor
     */
    private ComponentActionController() {
        this.copiedComponent = new SimpleObjectProperty<>();
        this.styleCopySource = new SimpleObjectProperty<>(this, "styleCopySource");
        AppModeController.INSTANCE.getEditModeContext().configurationProperty().addListener((obs, ov, nv) -> {
            this.clearCopiedComponent();
            this.styleCopySource.set(null);
        });
    }

    // Class part : "Basic getter"
    //========================================================================
    public ReadOnlyObjectProperty<ConfigurationChildComponentI> copiedComponentProperty() {
        return this.copiedComponent;
    }

    public ObjectProperty<DisplayableComponentI> styleCopySourceProperty() {
        return this.styleCopySource;
    }
    //========================================================================

    // Class part : "Copy/paste method"
    //========================================================================

    /**
     * Prepare the copy for a component
     *
     * @param component the component to be copied
     */
    public void copyComponent(final ConfigurationChildComponentI component) {
        this.copiedComponent.set(component);
    }

    /**
     * Paste the copied component on the given target.<br>
     * Target is optional, it has a use only in grid and key.
     *
     * @param targetConfiguration the target configuration where the component will be copied, must never be null (=> a component can't be paste in "no configuration")
     * @param target              the target where the copied component should be paste, can be null
     * @param keys                the selected keys
     */
    public void pasteComponent(final LCConfigurationI targetConfiguration, final ConfigurationChildComponentI target,
                               final List<GridPartKeyComponentI> keys) {
        ConfigurationChildComponentI copied = this.copiedComponent.get();
        if (copied != null) {
            this.LOGGER.info("Will try to copy the element {} to {}", copied.getID(), keys);
            ConfigurationChildComponentI cloned = (ConfigurationChildComponentI) copied.duplicate(true);
            // Issue #117 : name should change on paste
            if (cloned instanceof UserNamedComponentI) {
                UserNamedComponentI userNamedComp = (UserNamedComponentI) cloned;
                if (!StringUtils.isBlank(userNamedComp.userNameProperty().get())) {
                    userNamedComp.userNameProperty()
                            .set(Translation.getText("action.paste.component.comp.renamed.copy.of") + " " + userNamedComp.userNameProperty().get());
                }
            }
            PasteComponentAction pasteAction = new PasteComponentAction(targetConfiguration, cloned, target, keys);
            ConfigActionController.INSTANCE.executeAction(pasteAction);
        }
    }

    /**
     * Clear the current copied component
     */
    public void clearCopiedComponent() {
        this.copiedComponent.set(null);
    }
    //========================================================================

    // Class part : "Delete method"
    //========================================================================

    /**
     * This will delete the given component.<br>
     * This method will change with the given component type and location in configuration.
     *
     * @param component    the component to delete.
     * @param selectedKeys the selected key list
     */
    public void removeComponent(final ConfigurationChildComponentI component, ObservableList<GridPartKeyComponentI> selectedKeys) {
        UndoRedoActionI action = null;
        //Root graphics removed
        if (component instanceof RootGraphicComponentI) {
            action = new RemoveRootComponentAction((RootGraphicComponentI) component);
        }
        //Grid removed
        else if (component instanceof GridComponentI) {
            GridComponentI gridComp = (GridComponentI) component;
            StackComponentI stackParent = gridComp.stackParentProperty().get();
            if (stackParent != null && stackParent.isDirectStackChild(gridComp)) {
                action = new GridStackActions.RemoveGridInStackAction(stackParent, gridComp);
            }
        }
        //Grid part removed (not else if because GridPartComponentI can also be a GridComponentI)
        if (action == null && component instanceof GridPartComponentI && selectedKeys.isEmpty()) {
            action = new RemoveGridPartAction((GridPartComponentI) component);
        }
        // Multiple key removed
        if (action == null && !selectedKeys.isEmpty()) {
            action = new RemoveMultipleKeyAction(new ArrayList<>(selectedKeys));
        }
        if (action != null) {
            ConfigActionController.INSTANCE.executeAction(action);
        } else {
            this.LOGGER.warn("Couldn't find the needed remove action to delete component {}", component.getClass().getSimpleName());
        }
    }
    //========================================================================

}
