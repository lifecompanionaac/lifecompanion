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

package org.lifecompanion.controller.io.task;

import javafx.util.Pair;
import org.lifecompanion.controller.editaction.KeyActions;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridPartKeyComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.SimplerKeyContentContainerI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.util.model.ImageDictionaryUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ChangeImageDictionaryTask extends LCTask<List<KeyActions.ChangeImageAction>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeImageDictionaryTask.class);

    private final LCConfigurationI configuration;
    private final ImageDictionaryI srcDictionary, destDictionary;

    private final AtomicInteger progress;


    public ChangeImageDictionaryTask(LCConfigurationI configuration, ImageDictionaryI srcDictionary, ImageDictionaryI destDictionary) {
        super("task.change.image.dictionary.name");
        this.configuration = configuration;
        this.progress = new AtomicInteger(0);
        this.srcDictionary = srcDictionary;
        this.destDictionary = destDictionary;
    }

    @Override
    protected List<KeyActions.ChangeImageAction> call() throws Exception {
        // Define total work to do
        AtomicInteger totalWork = new AtomicInteger();
        ImageDictionaryUtils.forEachImageUseComponentWithImage(configuration, (comp, imgElement) -> totalWork.incrementAndGet());

        // Change on configuration component
        List<KeyActions.ChangeImageAction> changeImageActions = new ArrayList<>();
        ImageDictionaryUtils.forEachImageUseComponentWithImage(configuration, (comp, imgElement) -> {
            prepareChangeAction(comp, imgElement, changeImageActions);
            updateProgress(progress.incrementAndGet(), totalWork.get());
        });
        // MultiActionWrapperAction + ChangeImageAction

        return changeImageActions;
    }


    private void prepareChangeAction(ImageUseComponentI imageUseComponent, ImageElementI imageElement, List<KeyActions.ChangeImageAction> changeImageActions) {
        if (srcDictionary == null || imageElement.getDictionary() == srcDictionary) {
            // Mix original image keywords + text if possible
            String[] keywords = imageElement.getKeywords();

            // Fire search
            List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult =
                    ImageDictionaries.INSTANCE.searchImage(
                            String.join(" ", keywords) + " " + StringUtils.trimToEmpty(getComponentTextIfPossible(imageUseComponent)),
                            false,
                            ConfigurationComponentUtils.SIMILARITY_CONTAINS,
                            imageDictionary -> imageDictionary == destDictionary);

            // Get the first result as the results are sorted
            if (!searchResult.isEmpty()) {
                Pair<ImageDictionaryI, List<List<ImageElementI>>> resultForDestDictionary = searchResult.getFirst();
                if (!CollectionUtils.isEmpty(resultForDestDictionary.getValue())) {
                    List<ImageElementI> firstPage = resultForDestDictionary.getValue().getFirst();
                    if (!CollectionUtils.isEmpty(firstPage)) {
                        ImageElementI bestMatchingImage = firstPage.getFirst();
                        changeImageActions.add(new KeyActions.ChangeImageAction(imageUseComponent, bestMatchingImage, false));
                    }
                }
            }
        }
    }

    private String getComponentTextIfPossible(ImageUseComponentI imageUseComponent) {
        if (imageUseComponent instanceof GridPartKeyComponentI)
            return ((GridPartKeyComponentI) imageUseComponent).textContentProperty().get();
        if (imageUseComponent instanceof SimplerKeyContentContainerI)
            return ((SimplerKeyContentContainerI) imageUseComponent).textProperty().get();
        return "";
    }
}