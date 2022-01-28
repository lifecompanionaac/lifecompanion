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
import javafx.beans.property.SimpleObjectProperty;
import org.lifecompanion.api.action.definition.BaseConfigActionI;
import org.lifecompanion.api.component.definition.GridComponentI;
import org.lifecompanion.api.component.definition.GridPartKeyComponentI;
import org.lifecompanion.api.component.definition.StackComponentI;
import org.lifecompanion.api.component.definition.usercomp.UserCompDescriptionI;
import org.lifecompanion.api.ui.AddTypeEnum;
import org.lifecompanion.api.ui.PossibleAddComponentI;
import org.lifecompanion.base.data.component.simple.GridPartKeyComponent;
import org.lifecompanion.base.data.control.refacto.AppModeController;
import org.lifecompanion.base.data.ui.PossibleAddComponents;
import org.lifecompanion.config.data.action.impl.GridStackActions.AddGridInStackAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller that control every drag and drop action.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DragController {
    INSTANCE;
    private final Logger LOGGER = LoggerFactory.getLogger(DragController.class);
    /**
     * The current dragged possible add
     */
    private ObjectProperty<PossibleAddComponentI<?>> currentDraggedPossibleAdd;

    /**
     * The current dragged user comp
     */
    private ObjectProperty<UserCompDescriptionI> currentDraggedUserComp;

    /**
     * Current dragged key, to invert key
     */
    private ObjectProperty<GridPartKeyComponentI> currentDraggedKey;

    private DragController() {
        this.currentDraggedPossibleAdd = new SimpleObjectProperty<>();
        this.currentDraggedUserComp = new SimpleObjectProperty<>();
        this.currentDraggedKey = new SimpleObjectProperty<>();
    }

    // Class part : "Public properties"
    //========================================================================
    public ObjectProperty<PossibleAddComponentI<?>> currentDraggedPossibleAddProperty() {
        return this.currentDraggedPossibleAdd;
    }

    public ObjectProperty<UserCompDescriptionI> currentDraggedUserCompProperty() {
        return this.currentDraggedUserComp;
    }

    public ObjectProperty<GridPartKeyComponentI> currentDraggedKeyProperty() {
        return this.currentDraggedKey;
    }
    //========================================================================

    // Class part : "Utils"
    //========================================================================
    public boolean isDragShouldBeAcceptedOn(final AddTypeEnum addType, final boolean onKey) {
        return AppModeController.INSTANCE.getEditModeContext().getConfiguration() != null &&
                (onKey && (this.currentDraggedKey.get() != null)
                        || DragController.isAllowedTarget(addType, this.currentDraggedPossibleAdd.get())
                        || this.currentDraggedUserComp.get() != null && addType == this.currentDraggedUserComp.get().getTargetType());
    }

    public boolean isDragComponentIsPresentOn(final AddTypeEnum addType) {
        return DragController.isAllowedTarget(addType, this.currentDraggedPossibleAdd.get())
                || this.currentDraggedUserComp.get() != null && addType == this.currentDraggedUserComp.get().getTargetType();
    }

    @SuppressWarnings("unchecked")
    public <T> T createNewCompFor(final AddTypeEnum addTypeEnum, final Object... params) {
        try {
            if (this.currentDraggedPossibleAdd.get() != null) {
                return (T) this.currentDraggedPossibleAdd.get().getNewComponent(addTypeEnum, params);
            } else if (!this.isAddInStackUserComponent() && this.currentDraggedUserComp.get() != null) {
                this.LOGGER.info("Current drag user comp for " + addTypeEnum);
                return this.currentDraggedUserComp.get().getUserComponent().createNewComponent();
            }
        } catch (Exception e) {
            this.LOGGER.warn("Couldn't create the current dragged component", e);
        }
        return null;
    }

    //TODO : should be interface
    public boolean isAddInStackComponent() {
        return this.currentDraggedPossibleAdd.get() instanceof PossibleAddComponents.AddGridInStack;
    }

    //TODO : should be interface
    public boolean isAddInStackUserComponent() {
        return this.currentDraggedUserComp.get() != null
                && this.currentDraggedUserComp.get().getUserComponent().getLoadedComponent() instanceof GridComponentI;
    }

    public BaseConfigActionI createAddInStackAction(final GridPartKeyComponent model) {
        GridComponentI gridParent = model.gridParentProperty().get();
        if (gridParent != null && gridParent.stackParentProperty().get() != null) {
            StackComponentI stackParent = gridParent.stackParentProperty().get();
            return new AddGridInStackAction(stackParent, true, true);
        } else {
            this.LOGGER.warn("Didn't find any stack parent for key {}", model);
        }
        return null;
    }

    public BaseConfigActionI createAddUserCompInStackAction(final GridPartKeyComponent model) {
        GridComponentI gridParent = model.gridParentProperty().get();
        if (this.currentDraggedUserComp.get() != null && gridParent != null && gridParent.stackParentProperty().get() != null) {
            StackComponentI stackParent = gridParent.stackParentProperty().get();
            GridComponentI gridToAdd = (GridComponentI) this.currentDraggedUserComp.get().getUserComponent().createNewComponent();
            return new AddGridInStackAction(stackParent, gridToAdd, true, true);
        } else {
            this.LOGGER.warn("Didn't find any stack parent for key {}", model);
        }
        return null;
    }

    public void resetCurrentDraggedComp() {
        this.currentDraggedPossibleAdd.set(null);
        this.currentDraggedUserComp.set(null);
    }
    //========================================================================

    /**
     * Check if a target type should accept a possible add
     *
     * @param targetType  the target type
     * @param possibleAdd the possible add that must be checked
     * @return true if the target could accept possible added component type
     */
    public static boolean isAllowedTarget(final AddTypeEnum targetType, final PossibleAddComponentI<?> possibleAdd) {
        if (possibleAdd != null) {
            AddTypeEnum[] allowedAddType = possibleAdd.getAllowedAddType();
            for (AddTypeEnum addType : allowedAddType) {
                if (addType == targetType) {
                    return true;
                }
            }
        }
        return false;
    }

}
