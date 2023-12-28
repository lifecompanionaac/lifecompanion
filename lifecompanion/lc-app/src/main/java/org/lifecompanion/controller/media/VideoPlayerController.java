/*
 * LifeCompanion AAC and its sub projects
 *
 * Copyright (C) 2014 to 2019 Mathieu THEBAUD
 * Copyright (C) 2020 to 2023 CMRRF KERPAPE (Lorient, France) and CoWork'HIT (Lorient, France)
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

package org.lifecompanion.controller.media;

import org.lifecompanion.framework.commons.utils.io.IOUtils;
import org.lifecompanion.model.api.configurationcomponent.VideoElementI;
import org.lifecompanion.model.impl.configurationcomponent.VideoElement;

import java.io.File;
import java.io.IOException;

public enum VideoPlayerController {
    INSTANCE;

    // cf private ImageElementI getOrAdd(File imagePath, ImageDictionaryI dictionary) {
    public VideoElementI getOrAddVideo(File videoPath) {
        try {
            // TODO : optimize hash for size
            final String id = IOUtils.fileSha256HexToString(videoPath);

            // TODO : Check if exist in current configuration

            // Generate the element
            VideoElementI videoElement = new VideoElement(id, videoPath);

            return videoElement;
        } catch (Exception e) {
            return null;
        }
    }
}
