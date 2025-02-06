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

import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.TreeDisplayableComponentI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.imagedictionary.ImageElement;
import org.lifecompanion.util.ThreadUtils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ImageDictionaryUtils {

    public static void forEachImageUseComponentWithImage(LCConfigurationI configuration, BiConsumer<ImageUseComponentI, ImageElementI> action) {
        forEachImageUseComponent(configuration, imageUseComponent -> {
            ImageElementI imageElement = imageUseComponent.imageVTwoProperty().get();
            if (imageElement != null) {
                action.accept(imageUseComponent, imageElement);
            }
        });
    }

    public static void forEachImageUseComponent(LCConfigurationI configuration, Consumer<ImageUseComponentI> action) {
        for (DisplayableComponentI comp : configuration.getAllComponent().values()) {
            ifImageUseComponent(comp, action);
        }
        configuration.rootKeyListNodeProperty().get().traverseTreeToBottom(node -> ifImageUseComponent(node, action));
    }

    private static void ifImageUseComponent(Object object, Consumer<ImageUseComponentI> action) {
        if (object instanceof ImageUseComponentI imageUseComponent) {
            action.accept(imageUseComponent);
        }
    }

    public static void loadAllImagesIn(String loadRequestId, long timeout, TreeDisplayableComponentI component) {
        loadAllImagesIn(loadRequestId, null, timeout, component);
    }

    public static void loadAllImagesIn(String loadRequestId, Double scale, long timeout, TreeDisplayableComponentI component) {
        ConfigurationComponentUtils.exploreTree(component, node -> {
            if (node instanceof ImageUseComponentI imageUseComponent) {
                imageUseComponent.addExternalLoadingRequest(loadRequestId, scale != null ? scale : 1.0);
            }
        });
        waitForImageToLoad(timeout);
    }

    public static void unloadAllImagesIn(String loadRequestId, TreeDisplayableComponentI component) {
        ConfigurationComponentUtils.exploreTree(component, node -> {
            if (node instanceof ImageUseComponentI imageUseComponent) {
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
