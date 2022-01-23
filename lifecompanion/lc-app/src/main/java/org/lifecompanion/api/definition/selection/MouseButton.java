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

package org.lifecompanion.api.definition.selection;

import javafx.scene.input.MouseEvent;
import org.lifecompanion.framework.commons.translation.Translation;

import java.util.function.Predicate;

public enum MouseButton {
    ANY("mouse.button.any", me -> true),
    PRIMARY("mouse.button.primary", me -> me.getButton() == javafx.scene.input.MouseButton.PRIMARY),
    SECONDARY("mouse.button.secondary", me -> me.getButton() == javafx.scene.input.MouseButton.SECONDARY),
    MIDDLE("mouse.button.middle", me -> me.getButton() == javafx.scene.input.MouseButton.MIDDLE);

    private final Predicate<MouseEvent> validator;
    private final String nameId;

    MouseButton(String nameId, Predicate<MouseEvent> validator) {
        this.nameId = nameId;
        this.validator = validator;
    }

    public boolean checkEvent(MouseEvent event) {
        return event != null && validator.test(event);
    }

    public String getName() {
        return Translation.getText(nameId);
    }
}
