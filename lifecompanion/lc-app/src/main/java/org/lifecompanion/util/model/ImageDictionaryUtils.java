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

package org.lifecompanion.util.model;

import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.ThreadUtils;

public class ImageDictionaryUtils {
    public static void loadAllImagesIn(String loadRequestId, long timeout, TreeDisplayableComponentI component) {
        ConfigurationComponentUtils.exploreTree(component, node -> {
            if (node instanceof ImageUseComponentI) {
                final ImageUseComponentI imageUseComponent = (ImageUseComponentI) node;
                imageUseComponent.addExternalLoadingRequest(loadRequestId);
            }
        });
        waitForImageToLoad(timeout);
    }

    public static void unloadAllImagesIn(String loadRequestId, TreeDisplayableComponentI component) {
        ConfigurationComponentUtils.exploreTree(component, node -> {
            if (node instanceof ImageUseComponentI) {
                final ImageUseComponentI imageUseComponent = (ImageUseComponentI) node;
                imageUseComponent.removeExternalLoadingRequest(loadRequestId);
            }
        });
    }

    private static void waitForImageToLoad(long timeout) {
        ThreadUtils.safeSleep(100);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < timeout && ImageDictionaries.INSTANCE.isRunningImageLoadingTask())
            ThreadUtils.safeSleep(100);
    }
}
