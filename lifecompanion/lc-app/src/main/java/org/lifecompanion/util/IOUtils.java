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

import org.jetbrains.annotations.NotNull;
import org.lifecompanion.controller.lifecycle.AppModeController;
import org.lifecompanion.controller.profile.ProfileController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.io.FileNameUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.impl.constant.LCConstant;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class IOUtils {
    public static void silentClose(Closeable closaeable) {
        try {
            closaeable.close();
        } catch (IOException e) {
            // Silent
        }
    }

    private static final SimpleDateFormat DATE_FORMAT_FILENAME = new SimpleDateFormat("dd-MM-yyyy_HH-mm");

    public static File getUserUseModeDestination(String destinationFolderPath, String defaultFolderName, String extension) {
        StringBuilder fileName = new StringBuilder(DATE_FORMAT_FILENAME.format(new Date()));
        LCProfileI profile = ProfileController.INSTANCE.currentProfileProperty().get();
        if (profile != null) {
            fileName.append("_").append(IOUtils.getValidFileName(profile.nameProperty().get()));
        }
        LCConfigurationDescriptionI configDescription = AppModeController.INSTANCE.getUseModeContext().getConfigurationDescription();
        if (configDescription != null) {
            fileName.append("_").append(IOUtils.getValidFileName(configDescription.configurationNameProperty().get()));
        }
        fileName.append("." + extension);
        File correctDestinationFolder;
        if (StringUtils.isBlank(destinationFolderPath) || !new File(destinationFolderPath).exists()) {
            correctDestinationFolder = getDefaultDestinationFolder(defaultFolderName);
        } else {
            correctDestinationFolder = new File(destinationFolderPath);
        }
        correctDestinationFolder.mkdirs();
        return new File(correctDestinationFolder + File.separator + fileName);
    }

    public static File getDefaultDestinationFolder(String defaultFolderName) {
        return new File(System.getProperty("user.home") + File.separator + "Documents" + File.separator + defaultFolderName + File.separator);
    }

    public static String getValidFileName(String fileName, String replacingChar) {
        return StringUtils.isBlank(fileName) ? "" : fileName.replaceAll("[^-_. A-zÀ-ú0-9]", replacingChar);
    }

    public static String getValidFileName(String fileName) {
        return getValidFileName(fileName, "_");
    }

    public static boolean isSupportedImage(final File imgFile) {
        String ext = FileNameUtils.getExtension(imgFile);
        for (int i = 0; i < LCConstant.IMAGE_EXTENSIONS.length; i++) {
            if (StringUtils.endsWithIgnoreCase(ext, LCConstant.IMAGE_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSupportedVideo(final File imgFile) {
        String ext = FileNameUtils.getExtension(imgFile);
        for (int i = 0; i < LCConstant.VIDEO_EXTENSIONS.length; i++) {
            if (StringUtils.endsWithIgnoreCase(ext, LCConstant.VIDEO_EXTENSIONS[i])) {
                return true;
            }
        }
        return false;
    }

    public static File getTempDir(String name) {
        return new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + name + "-" + System.currentTimeMillis() + File.separator);
    }

    public static File getTempFile(String dirName, String suffix) {
        File file = new File(System.getProperty("java.io.tmpdir") + File.separator + "LifeCompanion" + File.separator + "tmp" + File.separator + dirName + File.separator + UUID.randomUUID() + suffix);
        file.getParentFile().mkdirs();
        return file;
    }
}
