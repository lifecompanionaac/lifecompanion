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

package org.lifecompanion.util;

import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

public class IOUtils {
    public static void silentClose(Closeable closaeable) {
        try {
            closaeable.close();
        } catch (IOException e) {
            // Silent
        }
    }

    public static String getValidFileName(String fileName, String replacingChar) {
        return StringUtils.isBlank(fileName) ? "" : fileName.replaceAll("[^-_. A-zÀ-ú0-9]", replacingChar);
    }

    public static String getValidFileName(String fileName) {
        return getValidFileName(fileName, "_");
    }

    public static boolean isSupportedImage(final File imgFile) {
        for (int i = 0; i < LCConstant.IMAGE_EXTENSIONS.length; i++) {
            if (StringUtils.endsWithIgnoreCase(FileNameUtils.getExtension(imgFile), LCConstant.IMAGE_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }

    public static File getTempDir(String name) {
        return new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + name + "-" + System.currentTimeMillis() + File.separator);
    }
}
