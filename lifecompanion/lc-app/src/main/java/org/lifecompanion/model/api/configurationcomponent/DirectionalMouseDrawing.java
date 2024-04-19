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

package org.lifecompanion.model.api.configurationcomponent;

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Represent the different way of drawing the virtual mouse.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum DirectionalMouseDrawing {
    SIMPLE_CIRCLE("virtual.mouse.drawing.simple.circle"),
    TARGET("virtual.mouse.drawing.target");

    private final String text;

    DirectionalMouseDrawing(final String textP) {
        this.text = textP;
    }

    public String getText() {
        return Translation.getText(this.text);
    }

    public String getImagePath() {
        return "mouse-drawing/" + this.name().toLowerCase() + ".png";
    }
}
