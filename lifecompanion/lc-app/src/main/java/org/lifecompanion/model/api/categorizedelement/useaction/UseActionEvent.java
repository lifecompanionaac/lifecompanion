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

/**
 * Define the different way that could trigger a user action.<br>
 * <ul>
 * <li>UseActionEven.ACTIVATION : Fired on activation of the parent component (basically when the user select a key...)</li>
 * <li>UseActionEven.OVER : Fire on parent over/scan</li>
 * <li>UseActionEven.EVENT : Fire on use event fired</li>
 * </ul>
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum UseActionEvent {
    ACTIVATION("use.action.event.type.activation"), //Fired on activation of the parent component
    OVER("use.action.event.type.over"), //Fire on parent over/scan
    EVENT("use.action.event.type.event.fired"),//Fire on use event fired
    INTERNAL("use.action.event.type.event.system")//Fire on internal activation
    ;

    /**
     * Label ID of the event type
     */
    private String eventLabelId;

    UseActionEvent(final String eventLabelIdP) {
        this.eventLabelId = eventLabelIdP;
    }

    public String getEventLabelId() {
        return this.eventLabelId;
    }

}
