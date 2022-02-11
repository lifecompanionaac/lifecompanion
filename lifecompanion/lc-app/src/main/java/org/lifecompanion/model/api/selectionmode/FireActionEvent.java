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
package org.lifecompanion.model.api.selectionmode;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Enum to determine when a activation event should be sent to the selection mode.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum FireActionEvent {
    ON_PRESS("fire.action.event.onpress", false), ON_RELEASE("fire.action.event.onrelease", true);

    private final String nameId;
    private final boolean enableTimeToFireAction;

    FireActionEvent(final String nameIdP, final boolean enableTimeToFireActionP) {
        this.nameId = nameIdP;
        this.enableTimeToFireAction = enableTimeToFireActionP;
    }

    public String getName() {
        return Translation.getText(this.nameId);
    }

    public boolean isEnableTimeToFireAction() {
        return enableTimeToFireAction;
    }
}
