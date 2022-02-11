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
package org.lifecompanion.controller.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.lifecompanion.model.impl.constant.LCConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.image.Image;

/**
 * Class to load just once each configuration icon.
 *
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class IconManager {
    private final static Logger LOGGER = LoggerFactory.getLogger(IconManager.class);
    /**
     * Map that contains each loaded icons
     */
    private static final Map<String, Image> icons = new HashMap<>();

    public static Image get(final String path) {
        return get(path,0,0,false,false);
    }

    public static Image get(final String path, final int width, final int height, final boolean preserveRatio, final boolean smooth) {
        if (IconManager.icons.containsKey(path)) {
            return IconManager.icons.get(path);
        } else {
            String iconPath = LCConstant.INT_PATH_ICONS + path;
            IconManager.LOGGER.debug("Will try to load a icon without resize or change : {}", iconPath);
            try (InputStream is = ResourceHelper.getInputStreamForPath(iconPath)) {
                Image img = new Image(is, width, height, preserveRatio, smooth);
                IconManager.icons.put(path, img);
                return img;
            } catch (IOException e) {
                LOGGER.error("Error while loading image resource from {}", iconPath, e);
                return null;
            }
        }
    }

    /**
     * To remove a image cached into {@link IconManager}.<br>
     * Noop if the image was not cached.
     *
     * @param path the image to remove.
     */
    public static void removeFromCache(final String path) {
        icons.remove(path);
    }
}
