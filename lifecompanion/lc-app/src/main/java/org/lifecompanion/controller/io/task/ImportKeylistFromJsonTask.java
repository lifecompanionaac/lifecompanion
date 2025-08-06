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
import org.lifecompanion.controller.editmode.LCStateController;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.controller.userconfiguration.UserConfigurationController;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListLeaf;
import org.lifecompanion.model.impl.configurationcomponent.dynamickey.KeyListNode;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 */
public class ImportKeylistFromJsonTask extends LCTask<List<KeyListNodeI>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ImportKeylistFromJsonTask.class);
    private final File jsonFile;

    public ImportKeylistFromJsonTask(final File jsonFile) {
        super("task.import.keylist.from.json.title");
        this.jsonFile = jsonFile;
    }

    @Override
    protected List<KeyListNodeI> call() throws Exception {
        ImageDictionaryI imageDictionary = ImageDictionaries.INSTANCE.getDictionaries().stream().sorted((d1, d2) ->
                Boolean.compare(LCStateController.INSTANCE.getFavoriteImageDictionaries().contains(d2.getName()),
                        LCStateController.INSTANCE.getFavoriteImageDictionaries().contains(d1.getName()))).findFirst().orElse(null);

        try (Reader is = new BufferedReader(new InputStreamReader(new FileInputStream(jsonFile), StandardCharsets.UTF_8))) {
            KeyListNodeInJson[] keyListNodeInJson = JsonHelper.GSON.fromJson(is, KeyListNodeInJson[].class);
            return Arrays.stream(keyListNodeInJson).map(k -> k.toKeyListNode(imageDictionary)).collect(Collectors.toList());
        }
    }

    private static class KeyListNodeInJson {
        String text;
        List<KeyListNodeInJson> children;

        KeyListNodeI toKeyListNode(ImageDictionaryI imageDictionary) {
            KeyListNodeI node;
            if (CollectionUtils.isEmpty(children)) {
                node = new KeyListLeaf();
            } else {
                node = new KeyListNode();
                node.getChildren().addAll(children.stream().map(k -> k.toKeyListNode(imageDictionary)).collect(Collectors.toList()));
            }
            node.textProperty().set(StringUtils.toLowerCase(text));
            node.textToWriteProperty().set(text);
            node.textToSpeakProperty().set(text);

            if (StringUtils.isNotBlank(text)) {
                Pair<ImageElementI, Double> firstImageToMatch = ImageDictionaries.INSTANCE.getFirstImageToMatch(text, imageDictionary);
                if (firstImageToMatch != null) {
                    node.textPositionProperty().setValue(UserConfigurationController.INSTANCE.defaultTextPositionOnImageSelectionProperty().get());
                    node.imageVTwoProperty().set(firstImageToMatch.getKey());
                }
            }

            return node;
        }
    }
}
