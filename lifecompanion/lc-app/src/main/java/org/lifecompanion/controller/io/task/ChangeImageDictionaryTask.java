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

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.layout.Region;
import javafx.scene.transform.Scale;
import javafx.util.Pair;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.DisplayableComponentI;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.ImageUseComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.imagedictionary.ImageDictionaryI;
import org.lifecompanion.model.api.imagedictionary.ImageElementI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.imagedictionary.ImageDictionaries;
import org.lifecompanion.model.impl.ui.configurationcomponent.UseViewProvider;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ConfigurationComponentUtils;
import org.lifecompanion.util.model.ImageDictionaryUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ChangeImageDictionaryTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeImageDictionaryTask.class);

    private final LCConfigurationI configuration;
    private final String srcDictionaryId, destDictionaryId;

    private final AtomicInteger progress;


    public ChangeImageDictionaryTask(LCConfigurationI configuration, String srcDictionaryId, String destDictionaryId) {
        super("task.change.image.dictionary.name");
        this.configuration = configuration;
        this.progress = new AtomicInteger(0);
        this.srcDictionaryId = srcDictionaryId;
        this.destDictionaryId = destDictionaryId;
    }

    @Override
    protected Void call() throws Exception {
        final KeyListNodeI rootKeyListNode = configuration.rootKeyListNodeProperty().get();

        // Define total work to do
        AtomicInteger nodeCount = new AtomicInteger();
        rootKeyListNode.traverseTreeToBottom(n -> nodeCount.incrementAndGet());
        int totalWork = configuration.getAllComponent().values().size() + nodeCount.get();

        // Change on configuration component
        for (DisplayableComponentI comp : configuration.getAllComponent().values()) {
            changeImageOn(comp);
            updateProgress(progress.incrementAndGet(), totalWork);
        }
        // Change on nodes
        rootKeyListNode.traverseTreeToBottom(node -> {
            changeImageOn(node);
            updateProgress(progress.incrementAndGet(), totalWork);
        });
        return null;
    }

    private void changeImageOn(Object obj) {
        if (obj instanceof ImageUseComponentI imageUseComponent) {
            ImageElementI imageElement = imageUseComponent.imageVTwoProperty().get();
            if (imageElement != null) {
                LOGGER.info("Not null, image detected");
                //if ("ARASAAC".equals(imageElement.getDictionary().getName())) {
                    String[] keywords = imageElement.getKeywords();
                    List<Pair<ImageDictionaryI, List<List<ImageElementI>>>> searchResult = ImageDictionaries.INSTANCE.searchImage(String.join(" ", keywords),
                            false,
                            ConfigurationComponentUtils.SIMILARITY_CONTAINS);
                    if (!searchResult.isEmpty()) {
                        Optional<Pair<ImageDictionaryI, List<List<ImageElementI>>>> found = searchResult.stream().filter(val -> "SCLERA".equals(val.getKey().getName())).findAny();
                        if (found.isPresent()) {
                            Pair<ImageDictionaryI, List<List<ImageElementI>>> firstDictResult = found.get();
                            if (!CollectionUtils.isEmpty(firstDictResult.getValue())) {
                                List<ImageElementI> firstImages = firstDictResult.getValue().get(0);
                                if (!CollectionUtils.isEmpty(firstImages)) {
                                    ImageElementI bestMatchingImage = firstImages.get(0);
                                    imageUseComponent.imageVTwoProperty().set(bestMatchingImage);
                                }
                            }
                        }
                    }
                //}
            }
        }
    }
}