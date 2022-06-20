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
package org.lifecompanion.model.api.categorizedelement.useaction;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import org.lifecompanion.model.api.configurationcomponent.DuplicableComponentI;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;

/**
 * Base use action : a use action is an action that can be done while using the components.<br>
 * A use action will have some parameter to be executed. There will be one instance per associated component.<br>
 * An use action should never launch any new Thread (except on really needed case).<br>
 * Note that most of the common action will simply use the {@link SimpleUseActionI} because it's simpler and more efficient to implements.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public interface BaseUseActionI<T extends UseActionTriggerComponentI> extends CategorizedElementI<UseActionSubCategoryI>, DuplicableComponentI {

    // Class part : "Configuration"
    //========================================================================

    /**
     * @return the class of the allowed parent type.
     * For example for an action : "Write key label", the action will only allow parent type that have a label.<br>
     * The check must be on inheritance and not on strict equals between parent class and this allowed parent class.<br>
     * A null value means that this allow every object to be parent.
     */
    Class<T> allowedParent();

    /**
     * @return a array that contains every trigger that could fire this action.<br>
     * Note that if this action allow a event type, the {@link #eventStarts(UseActionEvent)} or {@link #eventEnds(UseActionEvent)} should act with the allowed event.<br>
     */
    UseActionEvent[] allowedActionEvent();
    //========================================================================

    // Class part : "Usage"
    //========================================================================
    /**
     * @return true if this command is async.<br>
     * Note if this action is async, other action will be able to be executed at the same time.<br>
     * <strong>Most of the commons action should not be async</strong> to allow user to create scenario (action 1 then action 2...)
     */
    //	public boolean isAsync();

    /**
     * @return if the scanning must be paused while this command is executed.<br>
     * If return true, scanning will be automatically pause before execution and resumed after.<br>
     * <strong>Most of the commons action should pause the scanning while executing</strong>
     */
    //	public boolean isPauseScanning();

    /**
     * To indicates if this action is an action that will execute changes in the current selection part/grid.<br>
     * If this return true, the scanning or selection will not be restarted after the action is done.
     *
     * @return true if the action is a moving action.
     */
    boolean isMovingAction();

    /**
     * @return should return true if this action doesn't need to receive start and end event and just need a basic behavior.<br>
     * Typically, the {@link SimpleUseActionI} actions subclass will return true to this method.
     */
    boolean isSimple();

    /**
     * @param eventType the event type that started
     */
    void eventStarts(UseActionEvent eventType);

    /**
     * @param eventType the event type that ended
     */
    void eventEnds(UseActionEvent eventType);

    /**
     * @return the parent of this command : the action is set on a component so the parent is this component.<br>
     * Each time the action is created and associate to a {@link UseActionTriggerComponentI}, this property must be updated
     */
    ObjectProperty<T> parentComponentProperty();

    /**
     * To disable modification/delete on this action if it's attached to a key option.
     *
     * @return true if delete is disabled for this action.<br>
     * This mean that the user will get a error message if he try to remove/modify the action.
     */
    BooleanProperty attachedToKeyOptionProperty();
    //========================================================================

    String NODE_ACTION = "Act";
}
