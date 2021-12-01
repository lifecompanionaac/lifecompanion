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

import org.lifecompanion.framework.commons.translation.Translation;

/**
 * Enum for available scanning modes : auto is the default scanning mode (for one switch input), manual is useful if the user wants to manually go to next element
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public enum ScanningMode {
    AUTO("selection.mode.scanning.mode.auto"), MANUAL("selection.mode.scanning.mode.manual");

    private final String nameId;

    ScanningMode(final String nameIdP) {
        this.nameId = nameIdP;
    }

    public String getName() {
        return Translation.getText(this.nameId);
    }
}
