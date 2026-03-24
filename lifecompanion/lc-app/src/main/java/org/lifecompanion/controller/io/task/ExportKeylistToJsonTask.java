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

import org.apache.commons.io.FileUtils;
import org.lifecompanion.controller.io.JsonHelper;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.LinkType;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.util.javafx.ColorUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Mathieu THEBAUD <math.thebaud@gmail.com>
 * @author Oscar PAVOINE <oscar.pavoine@coworkhit.com>
 */
public class ExportKeylistToJsonTask extends LCTask<List<KeyListNodeI>> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExportKeylistToJsonTask.class);
    private final File jsonFile;
    private final KeyListNodeI root;

    public ExportKeylistToJsonTask(final File jsonFile, KeyListNodeI root) {
        super("task.export.keylist.to.json.title");
        this.jsonFile = jsonFile;
        this.root = root;
    }

    /*
     * TODO :
     *  - link with ID instead of children
     *  - null write/speak when not enable
     *  - image in base64
     *
     */

    @Override
    protected List<KeyListNodeI> call() throws Exception {
        final List<KeyListNodeI> rootChildren = this.root != null
                ? new ArrayList<>(this.root.getChildren())
                : Collections.emptyList();

        final List<KeyListNodeToJson> keyListToExport = rootChildren.stream()
                .map(KeyListNodeToJson::from)
                .collect(Collectors.toList());

        try (Writer os = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.jsonFile), StandardCharsets.UTF_8))) {
            JsonHelper.GSON.toJson(keyListToExport, os);
        }

        return rootChildren;
    }

    private static class KeyListNodeToJson {
        String id;
        String text;
        String textToSpeak;
        String textToWrite;
        String speakOnOver;
        String imageBase64;
        String imageName;
        String backgroundColor;
        String textPosition;
        String shapeStyle;
        String strokeColor;
        String textColor;
        List<KeyListNodeToJson> children;
        String linkTo;

        static KeyListNodeToJson from(final KeyListNodeI sourceNode) {
            KeyListNodeToJson targetNode = new KeyListNodeToJson();

            targetNode.id = sourceNode.getID();
            targetNode.text = sourceNode.textProperty().get();
            targetNode.textToSpeak = sourceNode.enableSpeakProperty().get() ? sourceNode.textToSpeakProperty().get() : null;
            targetNode.textToWrite = sourceNode.enableWriteProperty().get() ? sourceNode.textToWriteProperty().get() : null;
            targetNode.speakOnOver = sourceNode.enableSpeakOnOverProperty().get() ? sourceNode.textSpeakOnOverProperty().get() : null;

            final ImageElementI image = sourceNode.imageVTwoProperty().get();
            if (image != null) {
                File srcImage = image.getRealFilePath();
                if (srcImage.exists()) {
                    try {
                        byte[] fileContent = FileUtils.readFileToByteArray(srcImage);
                        targetNode.imageBase64 = Base64.getEncoder().encodeToString(fileContent);
                        targetNode.imageName = image.getName();
                    } catch (Exception e) {
                        LOGGER.error("Can't export image content", e);
                    }
                }
            }

            targetNode.backgroundColor = ColorUtils.toWebColorWithAlpha(sourceNode.backgroundColorProperty().get());
            targetNode.textPosition = sourceNode.textPositionProperty().get() != null ? sourceNode.textPositionProperty().get().name() : null;
            targetNode.shapeStyle = sourceNode.shapeStyleProperty().get() != null ? sourceNode.shapeStyleProperty().get().name() : null;
            targetNode.strokeColor = ColorUtils.toWebColorWithAlpha(sourceNode.strokeColorProperty().get());
            targetNode.textColor = ColorUtils.toWebColorWithAlpha(sourceNode.textColorProperty().get());

            if (sourceNode.isLinkNode()) {
                if (sourceNode.linkTypeProperty().get() == LinkType.KEYLIST) {
                    targetNode.linkTo = sourceNode.linkedNodeIdProperty().get();
                }
            } else if (!sourceNode.isLeafNode()) {
                if (!CollectionUtils.isEmpty(sourceNode.getChildren())) {
                    targetNode.children = sourceNode.getChildren()
                            .stream()
                            .map(KeyListNodeToJson::from)
                            .collect(Collectors.toList());
                }
            }

            return targetNode;
        }

    }
}
