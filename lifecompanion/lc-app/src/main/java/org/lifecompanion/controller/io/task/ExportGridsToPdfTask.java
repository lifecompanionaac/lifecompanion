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
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.util.Matrix;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.ui.configurationcomponent.UseViewProvider;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
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

public class ExportGridsToPdfTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportGridsToPdfTask.class);

    private static final float HEADER_SIZE = 35f, FOOTER_SIZE = 30f, IMAGE_BORDER = 20f, BACKGROUND_COLOR_BORDER = 3f, HEADER_FONT_SIZE = 16, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, COLOR_GRAY = 0.4f;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont FOOTER_FONT = PDType1Font.HELVETICA;
    private static final String EXPORT_IMAGE_LOADING_KEY = "export-to-pdf";
    private static final long MAX_IMAGE_LOADING_TIME = 10_000;
    private static final String NO_STACK_ID = "nostack";
    private static final double IMAGE_OFFSET = 20.0;

    private final File exportedImageDir;
    private final AtomicInteger progress;
    private final File destPdf;
    private final LCProfileI profile;
    private final LCConfigurationDescriptionI configurationDescription;
    private final LCConfigurationI configuration;

    private int totalWork;
    private Group fxGroupAttachedToScene;
    private UseViewProvider useViewProviderToSnap;
    private double maxImageSize;


    public ExportGridsToPdfTask(LCConfigurationI configuration, File destPdf, LCProfileI profile, LCConfigurationDescriptionI configurationDescription) {
        super("task.export.grids.pdf.name");
        this.configuration = configuration;
        this.profile = profile;
        this.configurationDescription = configurationDescription;
        this.exportedImageDir = IOUtils.getTempDir("export-images-for-config");
        this.exportedImageDir.mkdirs();
        this.destPdf = destPdf;
        this.progress = new AtomicInteger(0);
    }

    @Override
    protected Void call() throws Exception {
        // Find all grid by stacks in configuration
        final Map<String, List<GridComponentI>> gridByStack = configuration.getAllComponent()
                .values()
                .stream()
                .filter(comp -> comp instanceof GridComponentI)
                .map(comp -> (GridComponentI) comp)
                .collect(Collectors.groupingBy(g -> g.stackParentProperty().get() != null ? g.stackParentProperty()
                        .get()
                        .getID() : NO_STACK_ID));

        // Sort each grid by its order in stack and make a final lit
        List<GridComponentI> gridsToSnap = new ArrayList<>();
        gridByStack.forEach((stackId, grids) -> {
            if (NO_STACK_ID.equals(stackId)) gridsToSnap.addAll(grids);
            else
                gridsToSnap.addAll(grids.stream().sorted(Comparator.comparingInt(grid -> grid.stackParentProperty().get().getComponentList().indexOf(grid))).collect(Collectors.toList()));
        });


        fxGroupAttachedToScene = new Group();
        final Scene scene = new Scene(fxGroupAttachedToScene);
        scene.setFill(configuration.backgroundColorProperty().getValue());

        this.useViewProviderToSnap = new UseViewProvider();

        List<GridPrintTask> gridPrintTasks = new ArrayList<>();

        final KeyListNodeI rootNode = configuration.rootKeyListNodeProperty().get();
        rootNode.traverseTreeToBottom(childNode -> {
            if (!childNode.isLeafNode()) {
                for (GridComponentI gridToSnap : gridsToSnap) {
                    int pageCount = KeyListController.INSTANCE.getPageCount(gridToSnap, childNode);
                    if (pageCount < 0) {
                        addIfNotContains(gridPrintTasks, new GridPrintTask(gridToSnap, null, 1));
                    } else {
                        addIfNotContains(gridPrintTasks, new GridPrintTask(gridToSnap, childNode, pageCount));
                    }
                }
            }
        });

        // Determine max printing dimensions
        maxImageSize = PDRectangle.A4.getHeight() * 2 - IMAGE_OFFSET * 2.0;

        totalWork = gridPrintTasks.stream().mapToInt(pt -> pt.pageCount).sum();
        updateProgress(0, totalWork);

        List<ImageExportResult> exportResults = new ArrayList<>();
        for (int i = 0; i < gridPrintTasks.size(); i++) {
            GridPrintTask gridPrintTask = gridPrintTasks.get(i);
            if (gridPrintTask.nodeToSelect == null) {
                exportResults.add(executePrint(gridPrintTask, i, 0));
            } else {
                KeyListController.INSTANCE.selectNode(gridPrintTask.nodeToSelect);
                for (int p = 0; p < gridPrintTask.pageCount; p++) {
                    exportResults.add(executePrint(gridPrintTask, i, p));
                    FXThreadUtils.runOnFXThreadAndWaitFor(() -> KeyListController.INSTANCE.nextIn(gridPrintTask.nodeToSelect, true));
                }
            }
        }

        // TODO : default names
        final String profileName = profile != null ? profile.nameProperty().get() : "PROFILE?";
        final String configName = configurationDescription != null ? configurationDescription.configurationNameProperty().get() : "CONFIGURATION?";
        final Date exportDate = new Date();

        // Temp save LC logo
        File logoFile = new File(exportedImageDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        float logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();

        // Try to save a PDF
        try (PDDocument doc = new PDDocument()) {
            PDImageXObject logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), doc);

            updateProgress(progress.get(), totalWork + exportResults.size());
            for (ImageExportResult exportResult : exportResults) {

                PDPage gridPage = new PDPage(PDRectangle.A4);
                doc.addPage(gridPage);
                if (exportResult.landscape) gridPage.setRotation(90);
                PDRectangle pageSize = gridPage.getMediaBox();
                float pageWidth = pageSize.getWidth();

                float pageWidthF = exportResult.landscape ? pageSize.getHeight() : pageSize.getWidth();
                float pageHeightF = exportResult.landscape ? pageSize.getWidth() : pageSize.getHeight();

                try (PDPageContentStream pageContentStream = new PDPageContentStream(doc, gridPage)) {
                    if (exportResult.landscape) pageContentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));

                    pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);

                    // HEADER
                    pageContentStream.addRect(0, pageHeightF - HEADER_SIZE, pageWidthF, LINE_SIZE);
                    pageContentStream.fill();
                    pageContentStream.beginText();
                    pageContentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
                    pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, pageHeightF - HEADER_SIZE / 1.5f);
                    pageContentStream.showText(exportResult.title);
                    pageContentStream.endText();

                    // GRID IMAGE
                    Color color = configuration.backgroundColorProperty().get();
                    if (color != null) {
                        pageContentStream.setNonStrokingColor((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
                        float contentWidth = pageWidthF - BACKGROUND_COLOR_BORDER * 2f, contentHeight = pageHeightF - HEADER_SIZE - FOOTER_SIZE - BACKGROUND_COLOR_BORDER * 2f;
                        pageContentStream.addRect((float) (BACKGROUND_COLOR_BORDER + (pageWidthF - 2 * BACKGROUND_COLOR_BORDER) / 2.0 - contentWidth / 2.0),
                                pageHeightF - HEADER_SIZE - BACKGROUND_COLOR_BORDER - contentHeight - ((pageHeightF - HEADER_SIZE - FOOTER_SIZE - 2f * BACKGROUND_COLOR_BORDER) - contentHeight) / 2f,
                                contentWidth, contentHeight);
                        pageContentStream.fill();
                    }

                    PDImageXObject pdImage = PDImageXObject.createFromFile(exportResult.imageFile.getAbsolutePath(), doc);
                    float imageDestWidth = pageWidthF - IMAGE_BORDER * 2f, imageDestHeight = pageHeightF - HEADER_SIZE - FOOTER_SIZE - IMAGE_BORDER * 2f;
                    float widthRatio = imageDestWidth / pdImage.getWidth(), heightRatio = imageDestHeight / pdImage.getHeight();
                    float bestRatio = Math.min(widthRatio, heightRatio);
                    float imageDrawWidth = bestRatio * pdImage.getWidth(), imageDrawHeight = bestRatio * pdImage.getHeight();
                    pageContentStream.drawImage(pdImage,
                            (float) (IMAGE_BORDER + (pageWidthF - 2 * IMAGE_BORDER) / 2.0 - imageDrawWidth / 2.0),
                            pageHeightF - HEADER_SIZE - IMAGE_BORDER - imageDrawHeight - ((pageHeightF - HEADER_SIZE - FOOTER_SIZE - 2f * IMAGE_BORDER) - imageDrawHeight) / 2f,
                            imageDrawWidth, imageDrawHeight);


                    pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);

                    // FOOTER
                    pageContentStream.addRect(0, FOOTER_SIZE, pageWidthF, LINE_SIZE);
                    pageContentStream.fill();
                    pageContentStream.beginText();
                    pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
                    pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, FOOTER_SIZE - FOOTER_LINE_HEIGHT);
                    pageContentStream.showText(profileName + " - " + configName + " - " + StringUtils.dateToStringDateWithHour(exportDate));
                    pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
                    pageContentStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties()
                            .getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties()
                            .getAppServerUrl());
                    pageContentStream.endText();
                    pageContentStream.drawImage(logoImage, pageWidthF - logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_SIZE / 2f - LOGO_HEIGHT / 2f, logoDrawWidth, LOGO_HEIGHT);
                }
                updateProgress(progress.incrementAndGet(), totalWork + exportResults.size());
            }
            // Document info and save
            PDDocumentInformation pdi = doc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText("pdf.export.file.title", profileName, configName));
            pdi.setCreator(LCConstant.NAME);
            doc.save(destPdf);
        }
        return null;
    }


    private ImageExportResult executePrint(GridPrintTask gridPrintTask, int taskIndex, int pageIndex) {
        ImageDictionaryUtils.loadAllImagesIn(EXPORT_IMAGE_LOADING_KEY, MAX_IMAGE_LOADING_TIME, gridPrintTask.gridToSnap);
        ImageExportResult result = printGrid(gridPrintTask, taskIndex, pageIndex);
        ImageDictionaryUtils.unloadAllImagesIn(EXPORT_IMAGE_LOADING_KEY, gridPrintTask.gridToSnap);
        updateProgress(progress.incrementAndGet(), totalWork);
        return result;
    }

    private static <T> void addIfNotContains(Collection<T> collection, T val) {
        if (!collection.contains(val)) collection.add(val);
    }

    private static class GridPrintTask {
        private final GridComponentI gridToSnap;
        private final KeyListNodeI nodeToSelect;
        private final int pageCount;

        public GridPrintTask(GridComponentI gridToSnap, KeyListNodeI nodeToSelect, int pageCount) {
            this.gridToSnap = gridToSnap;
            this.nodeToSelect = nodeToSelect;
            this.pageCount = pageCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GridPrintTask that = (GridPrintTask) o;
            return pageCount == that.pageCount && Objects.equals(gridToSnap, that.gridToSnap) && Objects.equals(nodeToSelect, that.nodeToSelect);
        }

        @Override
        public int hashCode() {
            return Objects.hash(gridToSnap, nodeToSelect, pageCount);
        }
    }

    private static class ImageExportResult {
        private final String title;
        private final File imageFile;
        private final boolean landscape;

        public ImageExportResult(String title, File imageFile, boolean landscape) {
            this.title = title;
            this.imageFile = imageFile;
            this.landscape = landscape;
        }
    }

    private ImageExportResult printGrid(GridPrintTask printTask, int taskIndex, int pageIndex) {
        String gridName = cleanTextForPdf(printTask.gridToSnap.nameProperty().get() + (printTask.nodeToSelect != null ? (" - " + StringUtils.trimToEmpty(printTask.nodeToSelect.textProperty()
                .get()) + " (" + (pageIndex + 1) + "/" + printTask.pageCount + ")") : ""));
        final String fileName = IOUtils.getValidFileName(taskIndex + "_" + printTask.gridToSnap.nameProperty().get() + "_" + (printTask.nodeToSelect != null ? (printTask.nodeToSelect.textProperty()
                .get() + "_" + pageIndex) : ""));
        File imageFile = new File(exportedImageDir + File.separator + fileName + ".png");
        AtomicBoolean landscape = new AtomicBoolean();
        final Image image = FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            try {
                final ComponentViewI<?> viewForGrid = printTask.gridToSnap.getDisplay(useViewProviderToSnap, false);
                final Region regionForGrid = viewForGrid.getView();
                Bounds regionBounds = regionForGrid.getBoundsInParent();
                double scale;
                if (regionBounds.getWidth() > regionBounds.getHeight()) {
                    landscape.set(true);
                    scale = maxImageSize / regionBounds.getWidth();
                } else {
                    scale = maxImageSize / regionBounds.getHeight();
                }
                fxGroupAttachedToScene.getChildren().add(regionForGrid);
                SnapshotParameters snapParams = new SnapshotParameters();
                if (scale > 1.0)
                    snapParams.setTransform(new Scale(scale, scale));
                LOGGER.info("Scale to {}", scale);
                final Image tmpImage = regionForGrid.snapshot(snapParams, null);
                fxGroupAttachedToScene.getChildren().remove(regionForGrid);
                viewForGrid.unbindComponentAndChildren();
                return tmpImage;
            } catch (Exception e) {
                LOGGER.error("Exception while taking snapshot", e);
                return null;
            }
        });
        // Save image to file
        try {
            BufferedImage buffImage = SwingFXUtils.fromFXImage(image, null);
            ImageIO.write(buffImage, "png", imageFile);
            LOGGER.info("Grid image  saved to {}", imageFile);
        } catch (Exception e) {
            LOGGER.error("Exception when saving snapshot to {}", imageFile, e);
        }
        return new ImageExportResult(gridName, imageFile, landscape.get());
    }

    private String cleanTextForPdf(String text) {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char charAt = text.charAt(i);
            if (WinAnsiEncoding.INSTANCE.contains(charAt)) {
                b.append(charAt);
            } else {
                b.append("?");
            }
        }
        return b.toString();
    }
}
