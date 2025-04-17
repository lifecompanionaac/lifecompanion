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

package org.lifecompanion.controller.editmode;

public enum FileChooserType {

    CONFIG_EXPORT(true),
    CONFIG_MOBILE_EXPORT(true),
    CONFIG_IMPORT(true),

    KEYLIST_EXPORT(true),
    KEYLIST_IMPORT(true),

    PROFILE_EXPORT(true),
    PROFILE_IMPORT(true),

    EXPORT_PDF(false),

    EXPORT_OTHER_MISC(true),

    PLUGIN_ADD(false),

    PRIORIZED_WORD(false),

    SELECT_IMAGES(false),
    SELECT_VIDEOS(false),
    SELECT_SOUND(false),

    SAVE_USER_TEXT(false),
    RUN_PROGRAM(false),
    OPEN_WITH_DEFAULT_APP(false),
    OPEN_FOLDER(false),

    OTHER_MISC_NO_EXTERNAL(false),
    OTHER_MISC_EXTERNAL(true),

    ;

    private final boolean useExternalDevice;

    FileChooserType(boolean useExternalDevice) {
        this.useExternalDevice = useExternalDevice;
    }

    public boolean isUseExternalDevice() {
        return useExternalDevice;
    }
}
