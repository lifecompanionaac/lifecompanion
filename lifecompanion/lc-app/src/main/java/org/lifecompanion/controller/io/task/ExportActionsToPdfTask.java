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

import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.categorizedelement.useaction.AvailableUseActionController;
import org.lifecompanion.controller.categorizedelement.useevent.AvailableUseEventController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.resource.ResourceHelper;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.CategorizedElementI;
import org.lifecompanion.model.api.categorizedelement.MainCategoryI;
import org.lifecompanion.model.api.categorizedelement.SubCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.usevariable.UseVariableDefinitionI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Oscar PAVOINE
 */
public class ExportActionsToPdfTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportActionsToPdfTask.class);

    private static final float MAIN_TITLE_FONT_SIZE = 13, MAIN_DESCRIPTION_FONT_SIZE = 10, SUB_TITLE_FONT_SIZE = 9, BODY_TITLE_FONT_SIZE = 10, BODY_DESCRIPTION_FONT_SIZE = 9, BODY_EXEMPLE_FONT_SIZE = 9, BODY_ID_FONT_SIZE = 9, FOOTER_FONT_SIZE = 8,
            RIGHT_OFFSET_FROM_TEXT = 50, OFFSET_IMAGE_TO_TEXT = 8, HEADER_MARGIN = 15f, LATERAL_MARIN = 35f, FOOTER_MARGIN = 30f, SPACE_BETWEEN_BODY = 12,
            ICON_SIZE = 20, RECTANGLE_SIZE = 32, CERCLE_DIAMETER = 34, KAPPA = 0.552284749831f, RECTANGLE_BORDER_RADIUS = 10f,
            FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f,
            COLOR_GRAY = 0.4f, COLOR_BLACK = 0f;
    private static final PDFont MAIN_TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont MAIN_DESCRIPTION_FONT = PDType1Font.HELVETICA;
    private static final PDFont SUB_TITLE_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont BODY_TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont BODY_DESCRIPTION_FONT = PDType1Font.HELVETICA;
    private static final PDFont BODY_EXEMPLE_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont BODY_ID_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont FOOTER_FONT = PDType1Font.HELVETICA;

    private final File exportedImageDir;
    private final AtomicInteger progress;
    private final File destPdf;

    private int totalWork;

    private float pageWidthF, pageHeightF;
    private float currentYPosition;
    private PDImageXObject logoImage;
    private float logoDrawWidth;


    public ExportActionsToPdfTask(File destPdf) {
        super("task.export.lists.pdf.name");
        this.exportedImageDir = IOUtils.getTempDir("export-images-for-config");
        this.exportedImageDir.mkdirs();
        this.destPdf = destPdf;
        this.progress = new AtomicInteger(0);
    }

    @Override
    protected Void call() throws Exception {
        // Lists containing all actions, events and variables
        ObservableList<UseActionMainCategoryI> mainCategoriesAction = AvailableUseActionController.INSTANCE.getMainCategories();
        ObservableList<UseEventMainCategoryI> mainCategoriesEvent = AvailableUseEventController.INSTANCE.getMainCategories();
        ObservableList<UseVariableDefinitionI> variables = UseVariableController.INSTANCE.getPossibleVariables();
        int numberOfActions = calculateNumberOfActions(mainCategoriesAction);
        numberOfActions += calculateNumberOfActions(mainCategoriesEvent);
        numberOfActions += variables.size();
        totalWork = numberOfActions;
        updateProgress(0, totalWork);

        // Try to save a PDF
        try (PDDocument pdfDoc = new PDDocument()) {
            updateProgress(progress.get(), totalWork);
            initLcLogo(pdfDoc);
            newCategories(mainCategoriesAction, Translation.getText("export.lists.pdf.name.action.category"), pdfDoc);
            newCategories(mainCategoriesEvent, Translation.getText("export.lists.pdf.name.event.category"), pdfDoc);
            try {
                PDPage pdfPage = initPageLayout(pdfDoc);
                PDPageContentStream pageStream = new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true);
                pageStream = initContentStream(Translation.getText("export.lists.pdf.name.variable.category"), pageStream, pdfDoc, pdfPage);

                String[][] texts = {new String[]{Translation.getText("export.lists.pdf.name.variable.category")}, Translation.getText("use.variable.select.dialog.header.text").split("\\s+")};
                pageStream = newSection(texts, null, null, true, Translation.getText("export.lists.pdf.name.variable.category"), pageStream, pdfDoc, pdfPage);

                for (UseVariableDefinitionI var : variables) {
                    texts = new String[][]{var.getName().split("\\s+"), var.getDescription().split("\\s+"), (Translation.getText("export.lists.pdf.example") + var.getExampleValueToString()).split(
                            "\\s+"), (Translation.getText("export.lists.pdf.id") + "{" + var.getId() + "}").split("\\s+")};
                    pageStream = newSection(texts, null, null, false, Translation.getText("export.lists.pdf.name.variable.category"), pageStream, pdfDoc, pdfPage);
                }

                // FOOTER
                insertFooter(Translation.getText("export.lists.pdf.name.variable.category"), pageStream);
            } catch (IOException e) {
                LOGGER.error("Error while adding variable section to PDF", e);
            }
            PDDocumentInformation pdi = pdfDoc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText("pdf.export.lists.file.title"));
            pdi.setCreator(LCConstant.NAME);
            pdfDoc.save(destPdf);
        }
        return null;
    }

    private int calculateNumberOfActions(ObservableList<? extends MainCategoryI<? extends SubCategoryI<? extends MainCategoryI, ?>>> mainCategories) {
        int numberOfActions = mainCategories.size();
        for (MainCategoryI mainCategory : mainCategories) {
            ObservableList<? extends SubCategoryI> subCategories = mainCategory.getSubCategories();
            numberOfActions += subCategories.size();
            for (SubCategoryI subCategory : subCategories) {
                ObservableList<? extends CategorizedElementI> actions = subCategory.getContent();
                numberOfActions += actions.size();
            }
        }
        return numberOfActions;
    }

    private PDPage initPageLayout(PDDocument pdfDoc) {
        PDPage pdfPage = new PDPage(PDRectangle.A4);
        pdfDoc.addPage(pdfPage);
        PDRectangle pageSize = pdfPage.getMediaBox();
        pageWidthF = pageSize.getWidth();
        pageHeightF = pageSize.getHeight();
        updateProgress(progress.incrementAndGet(), totalWork);
        return pdfPage;
    }

    private PDPageContentStream initContentStream(String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws IOException {
        if (pageStream != null) {
            insertFooter(typeCategory, pageStream);
        }
        this.currentYPosition = pdfPage.getMediaBox().getHeight() - HEADER_MARGIN;
        return new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true);
    }

    private PageStreamAndPdfPage checkAvailableSpace(String[][] texts, boolean main, boolean img, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws
                                                                                                                                                                                          IOException {
        float size = BODY_TITLE_FONT_SIZE * 1.5f;
        PDFont font = main ? MAIN_TITLE_FONT : BODY_TITLE_FONT;
        float fontSize = main ? MAIN_TITLE_FONT_SIZE : BODY_TITLE_FONT_SIZE;
        float colorSize = main ? RECTANGLE_SIZE : CERCLE_DIAMETER;
        float startX = img ? LATERAL_MARIN + colorSize + OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float tx = startX;
        float spaceWidth = initSpaceWidth(font, fontSize);
        for (String[] text : texts) {
            for (String word : text) {
                float wordWidth = BODY_TITLE_FONT.getStringWidth(word) / 1000 * BODY_TITLE_FONT_SIZE;
                if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                    tx = startX;
                    size += BODY_TITLE_FONT_SIZE * 1.5f;
                }
                tx += wordWidth + spaceWidth;
            }
            tx = startX;
            size += BODY_TITLE_FONT_SIZE * 1.5f;
        }
        if (this.currentYPosition - size <= FOOTER_MARGIN) {
            pdfPage = initPageLayout(pdfDoc);
            pageStream = initContentStream(typeCategory, pageStream, pdfDoc, pdfPage);
        }
        return new PageStreamAndPdfPage(pageStream, pdfPage);
    }

    private float initSpaceWidth(PDFont font, float fontSize) throws IOException {
        return font.getStringWidth(" ") / 1000 * fontSize;
    }

    private void initLcLogo(PDDocument pdfDoc) throws IOException {
        File logoFile = new File(exportedImageDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        this.logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();
        this.logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDoc);
    }

    private PDPageContentStream insertText(String[] words,
                                           PDFont font,
                                           float fontSize,
                                           float startX,
                                           float startY,
                                           String typeCategory,
                                           PDPageContentStream pageStream,
                                           PDDocument pdfDoc,
                                           PDPage pdfPage) throws IOException {
        float tx = startX;
        float ty = startY;
        float spaceWidth = initSpaceWidth(font, fontSize);
        for (String word : words) {
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
            if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                tx = startX;
                ty = fontSize * 1.5f;
            }
            if (this.currentYPosition - fontSize < FOOTER_MARGIN) {
                initPageLayout(pdfDoc);
                pageStream = initContentStream(typeCategory, pageStream, pdfDoc, pdfPage);
            } else {
                this.currentYPosition -= ty;
            }
            pageStream.beginText();
            pageStream.newLineAtOffset(tx, this.currentYPosition);
            pageStream.setFont(font, fontSize);
            pageStream.showText(word);
            pageStream.endText();
            tx += wordWidth + spaceWidth;
            ty = 0;
        }
        return pageStream;
    }

    /**
     * This method is used to add the title section and the action section for items that are linked to the MainCategoryI interface.
     */
    private void newCategories(ObservableList<? extends MainCategoryI<? extends SubCategoryI<? extends MainCategoryI, ?>>> mainCategories, String typeCategory, PDDocument pdfDoc) {
        for (MainCategoryI mainCategory : mainCategories) {
            PDPage pdfPage = initPageLayout(pdfDoc);
            try {
                PDPageContentStream pageStream = new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true);
                pageStream = initContentStream(typeCategory, pageStream, pdfDoc, pdfPage);

                // MAIN
                String[][] texts = {(typeCategory + mainCategory.getName()).split("\\s+"), mainCategory.getStaticDescription().split("\\s+")};
                pageStream = newSection(texts, mainCategory.getConfigIconPath(), mainCategory.getColor(), true, typeCategory, pageStream, pdfDoc, pdfPage);
                if (currentYPosition > pageHeightF - HEADER_MARGIN - RECTANGLE_SIZE) currentYPosition = pageHeightF - HEADER_MARGIN - RECTANGLE_SIZE;
                //SUB
                ObservableList<? extends SubCategoryI> subCategories = mainCategory.getSubCategories();
                for (SubCategoryI subCategory : subCategories) {
                    pageStream = newSubTitle(subCategory.getName().split("\\s+"), typeCategory, pageStream, pdfDoc, pdfPage);

                    // ACTIONS
                    ObservableList<? extends CategorizedElementI> actions = subCategory.getContent();
                    for (CategorizedElementI event : actions) {
                        texts = new String[][]{event.getName().split("\\s+"), event.getStaticDescription().split("\\s+")};
                        pageStream = newSection(texts, event.getConfigIconPath(), event.getCategory().getColor(), false, typeCategory, pageStream, pdfDoc, pdfPage);
                    }
                }
                // FOOTER
                insertFooter(typeCategory, pageStream);
            } catch (IOException e) {
                LOGGER.error("Error while adding category section to PDF", e);
            }
        }
    }

    private PDPageContentStream newSection(String[][] word,
                                           String iconPath,
                                           Color backgroundColor,
                                           boolean main,
                                           String typeCategory,
                                           PDPageContentStream pageStream,
                                           PDDocument pdfDoc,
                                           PDPage pdfPage) throws IOException {
        boolean boolImg = iconPath != null;
        PageStreamAndPdfPage pageAndPdfPage = checkAvailableSpace(word, main, boolImg, typeCategory, pageStream, pdfDoc, pdfPage);
        pageStream = pageAndPdfPage.getPageStream();
        pdfPage = pageAndPdfPage.getPdfPage();
        if (boolImg) insertIcon(iconPath, backgroundColor, main, pageStream, pdfDoc);
        for (int i = 0; i < word.length; i++) {
            if (i == 0) pageStream = newTitle(word[i], main, boolImg, typeCategory, pageStream, pdfDoc, pdfPage);
            else if (i == 1) pageStream = newDescription(word[i], main, boolImg, typeCategory, pageStream, pdfDoc, pdfPage);
            else if (i == 2) pageStream = newExemple(word[i], typeCategory, pageStream, pdfDoc, pdfPage);
            else if (i == 3) pageStream = newId(word[i], typeCategory, pageStream, pdfDoc, pdfPage);
        }
        currentYPosition -= SPACE_BETWEEN_BODY;
        return pageStream;
    }

    private PDPageContentStream newTitle(String[] word, boolean main, boolean boolImg, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws IOException {
        PDFont font = main ? MAIN_TITLE_FONT : BODY_TITLE_FONT;
        float fontSize = main ? MAIN_TITLE_FONT_SIZE : BODY_TITLE_FONT_SIZE;
        float startX = boolImg ? LATERAL_MARIN + CERCLE_DIAMETER + OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float startY = main ? HEADER_MARGIN : fontSize * 1.2f;
        pageStream = insertText(word, font, fontSize, startX, startY, typeCategory, pageStream, pdfDoc, pdfPage);
        return pageStream;
    }

    private PDPageContentStream newSubTitle(String[] word, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws IOException {
        if (currentYPosition - SUB_TITLE_FONT_SIZE < FOOTER_MARGIN) {
            pdfPage = initPageLayout(pdfDoc);
            pageStream = initContentStream(typeCategory, pageStream, pdfDoc, pdfPage);
        }
        if (pageHeightF - currentYPosition >= pageHeightF - HEADER_MARGIN - SUB_TITLE_FONT_SIZE * 2) {
            currentYPosition -= SUB_TITLE_FONT_SIZE * 2;
        }
        pageStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);
        pageStream = insertText(word, SUB_TITLE_FONT, SUB_TITLE_FONT_SIZE, LATERAL_MARIN, SUB_TITLE_FONT_SIZE * 1.5f, typeCategory, pageStream, pdfDoc, pdfPage);
        currentYPosition -= SUB_TITLE_FONT_SIZE * 0.5f;
        pageStream.addRect(LATERAL_MARIN, currentYPosition, pageWidthF - LATERAL_MARIN - LATERAL_MARIN, LINE_SIZE);
        pageStream.fill();
        pageStream.setNonStrokingColor(COLOR_BLACK, COLOR_BLACK, COLOR_BLACK);
        currentYPosition -= SUB_TITLE_FONT_SIZE * 1.5f;
        return pageStream;
    }

    private PDPageContentStream newDescription(String[] word, boolean main, boolean boolImg, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws
                                                                                                                                                                                     IOException {
        PDFont font = main ? MAIN_DESCRIPTION_FONT : BODY_DESCRIPTION_FONT;
        float startX = boolImg ? LATERAL_MARIN + CERCLE_DIAMETER + OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float fontSize = main ? MAIN_DESCRIPTION_FONT_SIZE : BODY_DESCRIPTION_FONT_SIZE;
        pageStream = insertText(word, font, fontSize, startX, fontSize * 1.5f, typeCategory, pageStream, pdfDoc, pdfPage);
        return pageStream;
    }

    private PDPageContentStream newExemple(String[] word, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws IOException {
        pageStream = insertText(word, BODY_EXEMPLE_FONT, BODY_EXEMPLE_FONT_SIZE, LATERAL_MARIN, BODY_EXEMPLE_FONT_SIZE * 1.5f, typeCategory, pageStream, pdfDoc, pdfPage);
        return pageStream;
    }

    private PDPageContentStream newId(String[] word, String typeCategory, PDPageContentStream pageStream, PDDocument pdfDoc, PDPage pdfPage) throws IOException {
        pageStream = insertText(word, BODY_ID_FONT, BODY_ID_FONT_SIZE, LATERAL_MARIN, BODY_ID_FONT_SIZE * 1.5f, typeCategory, pageStream, pdfDoc, pdfPage);
        return pageStream;
    }

    private void insertIcon(String iconPath, Color backgroundColor, boolean mainIcon, PDPageContentStream pageStream, PDDocument pdfDoc) {
        try {
            File iconFile = new File(exportedImageDir.getPath() + File.separator + "icon.png");
            try (InputStream is = ResourceHelper.getInputStreamForPath(LCConstant.INT_PATH_ICONS + iconPath)) {
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(iconFile))) {
                    org.lifecompanion.framework.commons.utils.io.IOUtils.copyStream(is, bos);
                }
            }

            PDImageXObject pdImage = PDImageXObject.createFromFile(iconFile.getAbsolutePath(), pdfDoc);

            double whRatio = (1.0 * pdImage.getWidth()) / (1.0 * pdImage.getHeight());
            float imageWidth = (float) (whRatio > 1.0 ? ICON_SIZE : ICON_SIZE * whRatio);
            float imageHeight = (float) (whRatio > 1.0 ? ICON_SIZE / whRatio : ICON_SIZE);

            float startX = mainIcon ? LATERAL_MARIN + (RECTANGLE_SIZE - imageWidth) / 2 : LATERAL_MARIN + (CERCLE_DIAMETER - imageWidth) / 2;
            float startY = mainIcon ? this.currentYPosition - RECTANGLE_SIZE + (RECTANGLE_SIZE - imageHeight) / 2 : this.currentYPosition - CERCLE_DIAMETER + (CERCLE_DIAMETER - imageHeight) / 2;

            pageStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
            if (mainIcon) drawRect(pageStream);
            else drawCircle(pageStream);
            pageStream.fill();
            pageStream.drawImage(pdImage, startX, startY, imageWidth, imageHeight);
            pageStream.setNonStrokingColor(0f, 0f, 0f);
        } catch (IOException e) {
            LOGGER.error("Error while adding icon to PDF", e);
        }
    }

    private void drawRect(PDPageContentStream pageStream) throws IOException {
        float roundness = RECTANGLE_BORDER_RADIUS;
        float x = LATERAL_MARIN;
        float y = this.currentYPosition - RECTANGLE_SIZE;
        float w = RECTANGLE_SIZE;
        float h = RECTANGLE_SIZE;

        // Start at the top left corner and move clockwise
        pageStream.moveTo(x + roundness, y + h);
        pageStream.lineTo(x + w - roundness, y + h);
        pageStream.curveTo(x + w, y + h, x + w, y + h, x + w, y + h - roundness);
        pageStream.lineTo(x + w, y + roundness);
        pageStream.curveTo(x + w, y, x + w, y, x + w - roundness, y);
        pageStream.lineTo(x + roundness, y);
        pageStream.curveTo(x, y, x, y, x, y + roundness);
        pageStream.lineTo(x, y + h - roundness);
        pageStream.curveTo(x, y + h, x, y + h, x + roundness, y + h);
        pageStream.closePath();
    }

    /**
     * The circle is created using cubic Bézier curves to approximate the shape of a circle.
     * Four curve segments are used to form a complete circle by connecting four control points.
     */
    public void drawCircle(PDPageContentStream pageStream) throws IOException {
        float radius = CERCLE_DIAMETER / 2;
        float centerX = LATERAL_MARIN + radius;
        float centerY = this.currentYPosition - radius;

        float controlDistance = KAPPA * radius;

        float[][] points = {
                {centerX + controlDistance, centerY + radius, centerX + radius, centerY + controlDistance, centerX + radius, centerY}, // Top right
                {centerX + radius, centerY - controlDistance, centerX + controlDistance, centerY - radius, centerX, centerY - radius}, // Bottom right
                {centerX - controlDistance, centerY - radius, centerX - radius, centerY - controlDistance, centerX - radius, centerY}, // Bottom left
                {centerX - radius, centerY + controlDistance, centerX - controlDistance, centerY + radius, centerX, centerY + radius}  // Top left
        };

        pageStream.moveTo(centerX, centerY + radius);
        for (float[] point : points) {
            pageStream.curveTo(point[0], point[1], point[2], point[3], point[4], point[5]);
        }
        pageStream.closePath();
        pageStream.fill();
    }

    private void insertFooter(String typeCategory, PDPageContentStream pageStream) throws IOException {
        // FOOTER
        pageStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);
        pageStream.addRect(0, FOOTER_MARGIN, pageHeightF, LINE_SIZE);
        pageStream.fill();
        pageStream.beginText();
        pageStream.newLineAtOffset(RIGHT_OFFSET_FROM_TEXT, FOOTER_MARGIN - FOOTER_LINE_HEIGHT);
        pageStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
        pageStream.showText(typeCategory + StringUtils.dateToStringDateWithHour(new Date()));
        pageStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
        pageStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties()
                .getAppServerUrl());
        pageStream.endText();
        pageStream.setNonStrokingColor(COLOR_BLACK, COLOR_BLACK, COLOR_BLACK);

        // Temp save LC logo
        pageStream.drawImage(this.logoImage, pageWidthF - this.logoDrawWidth - RIGHT_OFFSET_FROM_TEXT, FOOTER_MARGIN / 2f - LOGO_HEIGHT / 2f, this.logoDrawWidth, LOGO_HEIGHT);
        pageStream.close();
    }

    public class PageStreamAndPdfPage {
        private PDPageContentStream pageStream;
        private PDPage pdfPage;

        public PageStreamAndPdfPage(PDPageContentStream pageStream, PDPage pdfPage) {
            this.pageStream = pageStream;
            this.pdfPage = pdfPage;
        }

        public PDPageContentStream getPageStream() {
            return pageStream;
        }

        public PDPage getPdfPage() {
            return pdfPage;
        }
    }
}


