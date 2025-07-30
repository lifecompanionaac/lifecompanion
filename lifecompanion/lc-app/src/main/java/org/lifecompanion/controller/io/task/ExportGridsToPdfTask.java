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
import javafx.util.Pair;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.encoding.WinAnsiEncoding;
import org.lifecompanion.controller.configurationcomponent.dynamickey.KeyListController;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.GridComponentI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.configurationcomponent.dynamickey.KeyListNodeI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
import org.lifecompanion.model.api.ui.configurationcomponent.ComponentViewI;
import org.lifecompanion.model.impl.io.PdfConfig;
import org.lifecompanion.model.impl.ui.configurationcomponent.UseViewProvider;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.model.ImageDictionaryUtils;
import org.lifecompanion.util.model.LCTask;
import org.lifecompanion.util.pdf.DocumentConfiguration;
import org.lifecompanion.util.pdf.DocumentImagePage;
import org.lifecompanion.util.pdf.PdfUtils;
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

    private static final String EXPORT_IMAGE_LOADING_KEY = "export-to-pdf";
    private static final long MAX_IMAGE_LOADING_TIME = 10_000;
    private static final String NO_STACK_ID = "nostack";

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
    private final PdfConfig pdfConfig;

    public ExportGridsToPdfTask(LCConfigurationI configuration, File destPdf, LCProfileI profile, LCConfigurationDescriptionI configurationDescription, PdfConfig pdfConfig) {
        super("task.export.grids.pdf.name");
        this.configuration = configuration;
        this.profile = profile;
        this.configurationDescription = configurationDescription;
        this.pdfConfig = pdfConfig;
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
                gridsToSnap.addAll(grids.stream().sorted(Comparator.comparingInt(grid -> grid.stackParentProperty().get().getComponentList().indexOf(grid))).toList());
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
        maxImageSize = pdfConfig.getPageSize().getHeight() * 2 - (pdfConfig.isEnableHeaderFooter() ? PdfUtils.IMAGE_BORDER_FULL : PdfUtils.IMAGE_BORDER_SMALL) * 2.0;

        totalWork = gridPrintTasks.stream().mapToInt(pt -> pt.pageCount).sum();
        updateProgress(0, totalWork);

        List<DocumentImagePage> exportResults = new ArrayList<>();
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
        totalWork += exportResults.size();

        final String profileName = profile != null ? profile.nameProperty().get() : "PROFILE?";
        final String configName = configurationDescription != null ? configurationDescription.configurationNameProperty().get() : "CONFIGURATION?";

        DocumentConfiguration documentConfiguration = new DocumentConfiguration(configuration.backgroundColorProperty().get(), pdfConfig.getPageSize(), profileName, configName, "pdf.export.file.title");
        documentConfiguration.setEnableFooter(pdfConfig.isEnableHeaderFooter());
        documentConfiguration.setEnableHeader(pdfConfig.isEnableHeaderFooter());
        PdfUtils.createPdfDocument(
                documentConfiguration,
                destPdf, exportResults, (prog, total) -> updateProgress(progress.get() + prog, totalWork));

        return null;
    }


    private DocumentImagePage executePrint(GridPrintTask gridPrintTask, int taskIndex, int pageIndex) {
        final ComponentViewI<?> viewForGrid = gridPrintTask.gridToSnap.getDisplay(useViewProviderToSnap, false);
        Pair<Double, Boolean> scaleAndLandscape = getScaleAndLandscape(viewForGrid);
        ImageDictionaryUtils.loadAllImagesIn(EXPORT_IMAGE_LOADING_KEY, scaleAndLandscape.getKey(), MAX_IMAGE_LOADING_TIME, gridPrintTask.gridToSnap);
        DocumentImagePage result = printGrid(gridPrintTask, taskIndex, viewForGrid, scaleAndLandscape.getKey(), scaleAndLandscape.getValue(), pageIndex);
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

    private DocumentImagePage printGrid(GridPrintTask printTask, int taskIndex, ComponentViewI<?> viewForGrid, double scale, boolean landscape, int pageIndex) {
        String gridName = cleanTextForPdf(printTask.gridToSnap.nameProperty().get() + (printTask.nodeToSelect != null ? (" - " + StringUtils.trimToEmpty(printTask.nodeToSelect.textProperty()
                .get()) + " (" + (pageIndex + 1) + "/" + printTask.pageCount + ")") : ""));
        final String fileName = IOUtils.getValidFileName(taskIndex + "_" + printTask.gridToSnap.nameProperty().get() + "_" + (printTask.nodeToSelect != null ? (printTask.nodeToSelect.textProperty()
                .get() + "_" + pageIndex) : ""));
        File imageFile = new File(exportedImageDir + File.separator + fileName + ".png");
        final Image image = FXThreadUtils.runOnFXThreadAndWaitFor(() -> {
            try {
                final Region regionForGrid = viewForGrid.getView();
                fxGroupAttachedToScene.getChildren().add(regionForGrid);
                SnapshotParameters snapParams = new SnapshotParameters();
                snapParams.setFill(Color.TRANSPARENT);
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
        return new DocumentImagePage(gridName, imageFile, landscape);
    }

    private Pair<Double, Boolean> getScaleAndLandscape(ComponentViewI<?> componentView) {
        Bounds regionBounds = componentView.getView().getBoundsInParent();
        boolean landscape = false;
        double scale;
        if (regionBounds.getWidth() > regionBounds.getHeight()) {
            landscape = true;
            scale = maxImageSize / regionBounds.getWidth();
        } else {
            scale = maxImageSize / regionBounds.getHeight();
        }
        return new Pair<>(scale, landscape);
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
