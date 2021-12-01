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

package org.lifecompanion.framework.commons.utils.io;

import java.io.File;
import java.text.DecimalFormat;

public class FileNameUtils {
    private FileNameUtils() {
    }

    /**
     * @param size file size to convert into string
     * @return the string that represent the size
     */
    public static String getFileSize(final long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "kB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        if (digitGroups > 0 && digitGroups < units.length) {
            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
        } else {
            return new DecimalFormat("#,##0.#").format(size);
        }
    }

    /**
     * Shortcut for {@link #getExtension(String)} using as path
     *
     * <pre>
     * file.getName()
     * </pre>
     */
    public static String getExtension(final File file) {
        return getExtension(file.getName());
    }

    /**
     * @param path the path that contains the wanted extension
     * @return the extension without the ".", will return an empty string if doesn't have any extension
     */
    public static String getExtension(final String path) {
        int lastIndexOf = path.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return "";
        }
        return path.substring(lastIndexOf + 1);
    }

    /**
     * @param file file to get the name
     * @return the file name with its extension removed
     */
    public static String getNameWithoutExtension(final File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return name;
        }
        return name.substring(0, lastIndexOf);
    }
}
