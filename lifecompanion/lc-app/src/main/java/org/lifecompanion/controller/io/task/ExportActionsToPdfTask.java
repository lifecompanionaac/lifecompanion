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
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

public class ExportActionsToPdfTask extends LCTask<Void> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportActionsToPdfTask.class);

    private static final float MAIN_TITLE_FONT_SIZE = 15, MAIN_DESCRIPTION_FONT_SIZE =12, SUB_TITLE_FONT_SIZE = 10, BODY_TITLE_FONT_SIZE = 12, BODY_DESCRIPTION_FONT_SIZE = 12, BODY_EXEMPLE_FONT_SIZE = 12, BODY_ID_FONT_SIZE = 12, FOOTER_FONT_SIZE = 9,
            RIGHT_OFFSET_FROM_TEXT = 50, OFFSET_IMAGE_TO_TEXT = 8, HEADER_MARGIN = 15f, LATERAL_MARIN = 35f, FOOTER_MARGIN = 30f, SPACE_BETWEEN_BODY = 20,
             ICON_SIZE = 32, RECTANGLE_SIZE = 40, CERCLE_DIAMETER = 46, KAPPA= 0.552284749831f, RECTANGLE_BORDER_RADIUS = 10f,
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

    private PDDocument pdfDoc;
    private PDPage pdfPage;
    private PDPageContentStream pageContentStream;
    private float pageWidthF, pageHeightF;
    private float currentYPosition;
    private float spaceWidth;
    private String currentTypeCategory;
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
        ObservableList<UseActionMainCategoryI> mainCategoriesAction  = AvailableUseActionController.INSTANCE.getMainCategories();
        ObservableList<UseEventMainCategoryI> mainCategoriesEvent  = AvailableUseEventController.INSTANCE.getMainCategories();
        ObservableList<UseVariableDefinitionI> variables = UseVariableController.INSTANCE.getPossibleVariables();
        int numberOfActions = calculateNumberOfActions(mainCategoriesAction);
        numberOfActions += calculateNumberOfActions(mainCategoriesEvent);
        numberOfActions += variables.size();
        totalWork = numberOfActions;
        updateProgress(0, totalWork);

        // Try to save a PDF
        try (PDDocument pdf = new PDDocument()) {
            pdfDoc = pdf;
            updateProgress(progress.get(), totalWork);
            initLcLogo();
            currentTypeCategory = Translation.getText("export.lists.pdf.name.action.category");
            newCategories(mainCategoriesAction, currentTypeCategory);
            currentTypeCategory = Translation.getText("export.lists.pdf.name.event.category");
            newCategories(mainCategoriesEvent, currentTypeCategory);
            currentTypeCategory = Translation.getText("export.lists.pdf.name.variable.category");
            initPageLayout();
            try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
                this.pageContentStream = page;
                initContentStream();

                String[][] texts = {new String [] {currentTypeCategory}, Translation.getText("use.variable.select.dialog.header.text").split("\\s+")};
                newSection(texts, null, null, true);

                for (UseVariableDefinitionI var : variables) {
                    texts = new String[][] {var.getName().split("\\s+"), var.getDescription().split("\\s+"), (Translation.getText("export.lists.pdf.example")+var.getExampleValueToString()).split("\\s+"), (Translation.getText("export.lists.pdf.id") + "{"+var.getId()+"}").split("\\s+")};
                    newSection(texts, null, null, false);
                }

                // FOOTER
                insertFooter();
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

    private void initPageLayout() {
        pdfPage = new PDPage(PDRectangle.A4);
        pdfDoc.addPage(pdfPage);
        PDRectangle pageSize = pdfPage.getMediaBox();
        pageWidthF = pageSize.getWidth();
        pageHeightF = pageSize.getHeight();
        updateProgress(progress.incrementAndGet(), totalWork);
    }

    private void initContentStream() throws IOException {
        if (this.pageContentStream != null) {
            insertFooter();
        }
        this.pageContentStream = new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true);
        this.currentYPosition = pdfPage.getMediaBox().getHeight() - HEADER_MARGIN;
    }

    private void checkAvailableSpace(String[][] texts, boolean main, boolean img) throws IOException {
        float size = BODY_TITLE_FONT_SIZE * 1.5f;
        PDFont font = main ? MAIN_TITLE_FONT : BODY_TITLE_FONT;
        float fontSize = main ? MAIN_TITLE_FONT_SIZE : BODY_TITLE_FONT_SIZE;
        float colorSize = main ? RECTANGLE_SIZE : CERCLE_DIAMETER;
        float startX = img ? LATERAL_MARIN+colorSize+OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float tx = startX;
        initSpaceWidth(font, fontSize);
        for (String[] text : texts) {
            for (String word :  text) {
                float wordWidth = BODY_TITLE_FONT.getStringWidth(word) / 1000 * BODY_TITLE_FONT_SIZE;
                if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                    tx = startX;
                    size += BODY_TITLE_FONT_SIZE * 1.5f;
                }
                tx += wordWidth + this.spaceWidth;
            }
            tx = startX;
            size += BODY_TITLE_FONT_SIZE * 1.5f;
        }
        if (this.currentYPosition - size <= FOOTER_MARGIN) {
            initPageLayout();
            initContentStream();
        }
    }

    private void initSpaceWidth(PDFont font, float fontSize) throws IOException {
        this.spaceWidth = font.getStringWidth(" ") / 1000 * fontSize;
    }

    private void initLcLogo() throws IOException {
        File logoFile = new File(exportedImageDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        this.logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();
        this.logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDoc);
    }

    private void insertText(String[] words, PDFont font, float fontSize, float startX, float startY) throws IOException {
        float tx = startX;
        float ty = startY;
        initSpaceWidth(font, fontSize);
        for (String word : words) {
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
            if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                tx = startX;
                ty =  fontSize* 1.5f;
            }
            if (this.currentYPosition - fontSize < FOOTER_MARGIN) {
                initPageLayout();
                initContentStream();
            } else {
                this.currentYPosition -= ty;
            }
            this.pageContentStream.beginText();
            this.pageContentStream.newLineAtOffset(tx, this.currentYPosition);
            this.pageContentStream.setFont(font, fontSize);
            this.pageContentStream.showText(word);
            this.pageContentStream.endText();
            tx += wordWidth + this.spaceWidth;
            ty = 0;
        }
    }

    /**
     * This method is used to add the title section and the action section for items that are linked to the MainCategoryI interface.
     */
    private void newCategories(ObservableList <? extends MainCategoryI<? extends SubCategoryI<? extends MainCategoryI, ?>>> mainCategories, String titleType) throws IOException {
        for (MainCategoryI mainCategory : mainCategories) {
            initPageLayout();
            try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage, PDPageContentStream.AppendMode.APPEND, true, true)) {
                this.pageContentStream = page;
                initContentStream();

                // MAIN
                String[][] texts = {(titleType + mainCategory.getName()).split("\\s+"), mainCategory.getStaticDescription().split("\\s+")};
                newSection(texts, mainCategory.getConfigIconPath(), mainCategory.getColor(), true);
                if ( currentYPosition > pageHeightF - HEADER_MARGIN - RECTANGLE_SIZE) currentYPosition = pageHeightF - HEADER_MARGIN - RECTANGLE_SIZE;
                //SUB
                ObservableList<? extends SubCategoryI> subCategories = mainCategory.getSubCategories();
                for (SubCategoryI subCategory : subCategories) {
                    newSubTitle(subCategory.getName().split("\\s+"));

                    // ACTIONS
                    ObservableList<? extends CategorizedElementI> actions = subCategory.getContent();
                    for (CategorizedElementI event : actions) {
                        texts = new String[][] {event.getName().split("\\s+"),event.getStaticDescription().split("\\s+")};
                        newSection(texts, event.getConfigIconPath(), event.getCategory().getColor(), false);
                    }
                }
                // FOOTER
                insertFooter();
            }
        }
    }

    private void newSection(String[][] word, String iconPath, Color backgroundColor, boolean main) throws IOException {
        boolean boolImg = iconPath != null;
        checkAvailableSpace(word, main, boolImg);
         if (boolImg) insertIcon(iconPath, backgroundColor, main);
        for ( int i = 0; i < word.length; i++) {
            if ( i == 0) newTitle(word[i], main,boolImg);
            else if ( i == 1) newDescription(word[i], main, boolImg);
            else if ( i == 2) newExemple(word[i]);
            else if ( i == 3) newId(word[i]);
        }
        currentYPosition -= SPACE_BETWEEN_BODY;
    }

    private void newTitle(String[] word, boolean main, boolean boolImg) throws IOException {
        PDFont font = main ? MAIN_TITLE_FONT : BODY_TITLE_FONT;
        float fontSize = main ? MAIN_TITLE_FONT_SIZE : BODY_TITLE_FONT_SIZE;
        float startX = boolImg ? LATERAL_MARIN+CERCLE_DIAMETER+OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float startY = main ? HEADER_MARGIN : fontSize ;
        insertText(word, font, fontSize, startX, startY);
    }

    private void newSubTitle(String[] word) throws IOException {
        if (currentYPosition - SUB_TITLE_FONT_SIZE < FOOTER_MARGIN) {
            initPageLayout();
            initContentStream();
        }
        if (pageHeightF - currentYPosition >= pageHeightF - HEADER_MARGIN - SUB_TITLE_FONT_SIZE * 2) {
            currentYPosition -= SUB_TITLE_FONT_SIZE * 2;
        }
        this.pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);
         insertText(word, SUB_TITLE_FONT, SUB_TITLE_FONT_SIZE, LATERAL_MARIN, SUB_TITLE_FONT_SIZE * 1.5f);
         currentYPosition -= SUB_TITLE_FONT_SIZE *0.5f;
        this.pageContentStream.addRect(LATERAL_MARIN, currentYPosition, pageWidthF-LATERAL_MARIN-LATERAL_MARIN, LINE_SIZE);
        this.pageContentStream.fill();
        this.pageContentStream.setNonStrokingColor(COLOR_BLACK, COLOR_BLACK, COLOR_BLACK);
        currentYPosition -= SUB_TITLE_FONT_SIZE * 1.5f;
    }

    private void newDescription(String[] word, boolean main, boolean boolImg) throws IOException {
        PDFont font = main ? MAIN_DESCRIPTION_FONT : BODY_DESCRIPTION_FONT;
        float startX = boolImg ? LATERAL_MARIN+CERCLE_DIAMETER+OFFSET_IMAGE_TO_TEXT : LATERAL_MARIN;
        float fontSize = main ? MAIN_DESCRIPTION_FONT_SIZE : BODY_DESCRIPTION_FONT_SIZE;
        insertText(word, font, fontSize, startX, fontSize * 1.5f);
    }
    private void newExemple(String[] word) throws IOException {
        insertText(word, BODY_EXEMPLE_FONT, BODY_EXEMPLE_FONT_SIZE, LATERAL_MARIN, BODY_EXEMPLE_FONT_SIZE * 1.5f);
    }

    private void newId(String[] word) throws IOException {
        insertText(word, BODY_ID_FONT, BODY_ID_FONT_SIZE, LATERAL_MARIN, BODY_ID_FONT_SIZE * 1.5f);
    }

    private void insertIcon(String iconPath, Color backgroundColor, boolean mainIcon) {
        try {
            File iconFile = new File(exportedImageDir.getPath() + File.separator + "icon.png");
            BufferedImage iconBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(iconPath), null);
            ImageIO.write(iconBuffImage, "png", iconFile);

            PDImageXObject pdImage = PDImageXObject.createFromFile(iconFile.getAbsolutePath(), pdfDoc);

            float imageWidth = pdImage.getWidth() > ICON_SIZE ? ICON_SIZE : pdImage.getWidth();
            float imageHeight = pdImage.getHeight() > ICON_SIZE ? ICON_SIZE : pdImage.getHeight();

            float startX = mainIcon ? LATERAL_MARIN + (RECTANGLE_SIZE - imageWidth) / 2 : LATERAL_MARIN + (CERCLE_DIAMETER - imageWidth) / 2;
            float startY = mainIcon ? this.currentYPosition - RECTANGLE_SIZE + (RECTANGLE_SIZE -  imageHeight)/2 : this.currentYPosition - CERCLE_DIAMETER + (CERCLE_DIAMETER - imageHeight)/2;

            this.pageContentStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
            if (mainIcon) drawRect();
            else drawCircle();
            this.pageContentStream.fill();
            this.pageContentStream.drawImage(pdImage, startX, startY, imageWidth, imageHeight);
            this.pageContentStream.setNonStrokingColor(0f, 0f, 0f);
        } catch (IOException e) {
            LOGGER.error("Error while adding icon to PDF", e);
        }
    }

    private void drawRect() throws IOException {
        float roundness = RECTANGLE_BORDER_RADIUS;
        float x = LATERAL_MARIN;
        float y = this.currentYPosition - RECTANGLE_SIZE;
        float w = RECTANGLE_SIZE;
        float h = RECTANGLE_SIZE;

        // Start at the top left corner and move clockwise
        this.pageContentStream.moveTo(x + roundness, y + h);
        this.pageContentStream.lineTo(x + w - roundness, y + h);
        this.pageContentStream.curveTo(x + w, y + h, x + w, y + h, x + w, y + h - roundness);
        this.pageContentStream.lineTo(x + w, y + roundness);
        this.pageContentStream.curveTo(x + w, y, x + w, y, x + w - roundness, y);
        this.pageContentStream.lineTo(x + roundness, y);
        this.pageContentStream.curveTo(x, y, x, y, x, y + roundness);
        this.pageContentStream.lineTo(x, y + h - roundness);
        this.pageContentStream.curveTo(x, y + h, x, y + h, x + roundness, y + h);
        this.pageContentStream.closePath();
    }

    /**
    * The circle is created using cubic Bézier curves to approximate the shape of a circle.
    * Four curve segments are used to form a complete circle by connecting four control points.
    */
    public void drawCircle() throws IOException {
        float radius = CERCLE_DIAMETER/2;
        float centerX = LATERAL_MARIN + radius;
        float centerY = this.currentYPosition - radius;

        float controlDistance = KAPPA * radius;

        float[][] points = {
            {centerX + controlDistance, centerY + radius, centerX + radius, centerY + controlDistance, centerX + radius, centerY}, // Top right
            {centerX + radius, centerY - controlDistance, centerX + controlDistance, centerY - radius, centerX, centerY - radius}, // Bottom right
            {centerX - controlDistance, centerY - radius, centerX - radius, centerY - controlDistance, centerX - radius, centerY}, // Bottom left
            {centerX - radius, centerY + controlDistance, centerX - controlDistance, centerY + radius, centerX, centerY + radius}  // Top left
        };

        this.pageContentStream.moveTo(centerX, centerY + radius);
        for (float[] point : points) {
            this.pageContentStream.curveTo(point[0], point[1], point[2], point[3], point[4], point[5]);
        }
        this.pageContentStream.closePath();
        this.pageContentStream.fill();
    }

    private void insertFooter() throws IOException {
        // FOOTER
        this.pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);
        this.pageContentStream.addRect(0, FOOTER_MARGIN, pageHeightF, LINE_SIZE);
        this.pageContentStream.fill();
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(RIGHT_OFFSET_FROM_TEXT, FOOTER_MARGIN - FOOTER_LINE_HEIGHT);
        this.pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
        this.pageContentStream.showText(currentTypeCategory + StringUtils.dateToStringDateWithHour(new Date()));
        this.pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
        this.pageContentStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties().getAppServerUrl());
        this.pageContentStream.endText();
        this.pageContentStream.setNonStrokingColor(COLOR_BLACK, COLOR_BLACK, COLOR_BLACK);

        // Temp save LC logo
        this.pageContentStream.drawImage(this.logoImage, pageWidthF - this.logoDrawWidth - RIGHT_OFFSET_FROM_TEXT, FOOTER_MARGIN / 2f - LOGO_HEIGHT / 2f, this.logoDrawWidth, LOGO_HEIGHT);
        this.pageContentStream.close();
    }
}
