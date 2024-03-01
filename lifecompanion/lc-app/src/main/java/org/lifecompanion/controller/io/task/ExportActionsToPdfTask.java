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
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventGeneratorI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useevent.UseEventSubCategoryI;
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

    private static final float MAIN_TITLE_FONT_SIZE = 20, MAIN_DESCRIPTION_FONT_SIZE =15, SUB_TITLE_FONT_SIZE = 10, BODY_TITLE_FONT_SIZE = 12, BODY_DESCRIPTION_FONT_SIZE = 12, BODY_EXEMPLE_FONT_SIZE = 12, BODY_ID_FONT_SIZE = 12, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, OFFSET_IMAGE_TEXT = 10, HEADER_MARGIN = 15f, LATERAL_MARIN = 35f, FOOTER_MARGIN = 30f, SPACE_BETWEEN_BODY = 20, MAIN_ICON_SIZE = 48, ACTION_ICON_SIZE = 32, MAIN_CERCLE_DIAMETER = 67.5f, ACTION_CERCLE_DIAMETER = 44, KAPPA= 0.552284749831f;
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
        int numberOfMainCategoriesAction = mainCategoriesAction.size();
        int numberOfSubCategoriesAction = 0;
        int numberOfActions = 0;
        for (UseActionMainCategoryI mainCategory : mainCategoriesAction) {
            ObservableList<UseActionSubCategoryI> subCategories = mainCategory.getSubCategories();
            numberOfSubCategoriesAction += subCategories.size();
            for (UseActionSubCategoryI subCategory : subCategories) {
                ObservableList<BaseUseActionI<?>> actions = subCategory.getContent();
                numberOfActions += actions.size();
            }
        }
        for (UseEventMainCategoryI mainCategory : mainCategoriesEvent) {
            ObservableList<UseEventSubCategoryI> subCategories = mainCategory.getSubCategories();
            numberOfSubCategoriesAction += subCategories.size();
            for (UseEventSubCategoryI subCategory : subCategories) {
                ObservableList<UseEventGeneratorI> events = subCategory.getContent();
                numberOfActions += events.size();
            }
        }
        numberOfActions += variables.size();
        totalWork = (numberOfMainCategoriesAction + numberOfSubCategoriesAction + numberOfActions)/10;
        updateProgress(0, totalWork);

        // Try to save a PDF
        try (PDDocument pdf = new PDDocument()) {
            pdfDoc = pdf;
            updateProgress(progress.get(), totalWork);
            // LC logo
            setLcLogo();

            // ACTIONS
            currentTypeCategory = "Actions - ";
            addCategories(mainCategoriesAction, currentTypeCategory);
            updateProgress(progress.incrementAndGet(), totalWork);
            // EVENTS
            currentTypeCategory = "Événements - ";
            addCategories(mainCategoriesEvent, currentTypeCategory);
            updateProgress(progress.incrementAndGet(), totalWork);

            // VARIABLES
            currentTypeCategory = "Variables - ";
            setGridPage();
            try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage)) {
                this.pageContentStream = page;
                setPageContentStream();

                String[][] texts = {new String [] {"Variables"}, Translation.getText("use.variable.select.dialog.header.text").split("\\s+")};
                addSection(texts, null, null, true);

                currentYPosition -= SPACE_BETWEEN_BODY;
                for (UseVariableDefinitionI var : variables) {
                    texts = new String[][] {var.getName().split("\\s+"), var.getDescription().split("\\s+"), ("Example : "+var.getExampleValueToString()).split("\\s+"), ("ID : {"+var.getId()+"}").split("\\s+")};
                    addSection(texts, null, null, false);
                }

                // FOOTER
                addFooter();
                updateProgress(progress.incrementAndGet(), totalWork);
            }
            PDDocumentInformation pdi = pdfDoc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText("pdf.export.lists.file.title"));
            pdi.setCreator(LCConstant.NAME);
            pdfDoc.save(destPdf);
        }
        return null;
    }

    private void nextLine(float tx, float ty, String text, PDFont font, float fontSize) throws IOException {
        if (this.currentYPosition - fontSize < FOOTER_MARGIN) {
            setGridPage();
            setPageContentStream();
        } else {
            this.currentYPosition -= ty;
        }
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(tx, this.currentYPosition);
        this.pageContentStream.setFont(font, fontSize);
        this.pageContentStream.showText(text);
        this.pageContentStream.endText();
    }

    private void setGridPage() {
        pdfPage = new PDPage(PDRectangle.A4);
        pdfDoc.addPage(pdfPage);
        PDRectangle pageSize = pdfPage.getMediaBox();
        pageWidthF = pageSize.getWidth();
        pageHeightF = pageSize.getHeight();
    }

    private void setPageContentStream() throws IOException {
        if (this.pageContentStream != null) {
            addFooter();
        }
        this.pageContentStream = new PDPageContentStream(pdfDoc, pdfPage);
        this.currentYPosition = pdfPage.getMediaBox().getHeight() - HEADER_MARGIN;
    }

    private void addIcon(String iconPath, Color backgroundColor, boolean mainIcon) {
        try {
            File iconFile = new File(exportedImageDir.getPath() + File.separator + "icon.png");
            BufferedImage iconBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(iconPath), null);
            ImageIO.write(iconBuffImage, "png", iconFile);

            PDImageXObject pdImage = PDImageXObject.createFromFile(iconFile.getAbsolutePath(), pdfDoc);

            int multiplier = mainIcon ? 2 : 1;
            float imgSize = mainIcon ? (int) MAIN_ICON_SIZE : (int) ACTION_ICON_SIZE;
            float cercleRadius = mainIcon ? MAIN_CERCLE_DIAMETER : ACTION_CERCLE_DIAMETER;
            float imageWidth = pdImage.getWidth()*multiplier > imgSize ? imgSize : pdImage.getWidth() * multiplier;
            float imageHeight = pdImage.getHeight()*multiplier > imgSize ? imgSize : pdImage.getHeight() * multiplier;
            float startX = mainIcon ? LATERAL_MARIN + (cercleRadius/2 - imageWidth) / 2 : LATERAL_MARIN + (cercleRadius - imageWidth) / 2;

            this.pageContentStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
            addCircle(mainIcon);
            this.pageContentStream.fill();
            this.pageContentStream.drawImage(pdImage, startX, this.currentYPosition - cercleRadius + (cercleRadius - imageHeight) / 2, imageWidth, imageHeight);
            this.pageContentStream.setNonStrokingColor(0f, 0f, 0f);
        } catch (IOException e) {
            LOGGER.error("Error while adding icon to PDF", e);
        }
    }

    private void addText(String[] words, PDFont font, float fontSize, float startX, float startY) throws IOException {
        float tx = startX;
        float ty = startY;
        setSpaceWidth(font, fontSize);
        for (String word : words) {
            float wordWidth = font.getStringWidth(word) / 1000 * fontSize;
            if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                tx = startX;
                ty = fontSize * 1.5f;
            }
            nextLine(tx, ty, word, font, fontSize);
            tx += wordWidth + this.spaceWidth;
            ty = 0;
        }
    }

    private void addCategories(ObservableList <? extends MainCategoryI<? extends SubCategoryI<? extends MainCategoryI, ?>>> mainCategories, String titleType) throws IOException {
        for (MainCategoryI mainCategory : mainCategories) {
            setGridPage();
            try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage)) {
                this.pageContentStream = page;
                setPageContentStream();

                // MAIN
                String[][] texts = {(titleType + mainCategory.getName()).split("\\s+"), mainCategory.getStaticDescription().split("\\s+")};
                addSection(texts, mainCategory.getConfigIconPath(), mainCategory.getColor(), true);
                if ( currentYPosition > pageHeightF - HEADER_MARGIN - MAIN_CERCLE_DIAMETER) currentYPosition = pageHeightF - HEADER_MARGIN - MAIN_CERCLE_DIAMETER;
                //SUB
                ObservableList<? extends SubCategoryI> subCategories = mainCategory.getSubCategories();
                for (SubCategoryI subCategory : subCategories) {
                    addSubTitle(subCategory.getName().split("\\s+"));

                    // ACTIONS
                    ObservableList<? extends CategorizedElementI> actions = subCategory.getContent();
                    for (CategorizedElementI event : actions) {
                        texts = new String[][] {event.getName().split("\\s+"),event.getStaticDescription().split("\\s+")};
                        addSection(texts, event.getConfigIconPath(), event.getCategory().getColor(), false);
                    }
                }
                // FOOTER
                addFooter();
            }
        }
    }

    private void addSection(String[][] word, String iconPath, Color backgroundColor, boolean main) throws IOException {
        boolean boolImg = iconPath != null;
        checkAvailableSpace(word, boolImg);
         if (boolImg) addIcon(iconPath, backgroundColor, main);
        for ( int i = 0; i < word.length; i++) {
            if ( i == 0) addTitle(word[i], main,boolImg);
            else if ( i == 1) addDescription(word[i], main, boolImg);
            else if ( i == 2) addExemple(word[i]);
            else if ( i == 3) addId(word[i]);
        }
        currentYPosition -= SPACE_BETWEEN_BODY;
    }

    private void addTitle(String[] word, boolean main, boolean boolImg) throws IOException {
        PDFont font = main ? MAIN_TITLE_FONT : BODY_TITLE_FONT;
        float fontSize = main ? MAIN_TITLE_FONT_SIZE : BODY_TITLE_FONT_SIZE;
        float startX = boolImg ? LATERAL_MARIN+ACTION_CERCLE_DIAMETER+OFFSET_IMAGE_TEXT : LATERAL_MARIN;
        float startY = main ? HEADER_MARGIN : fontSize ;
        addText(word, font, fontSize, startX, startY);
    }

    private void addSubTitle(String[] word) throws IOException {
         addText(word, SUB_TITLE_FONT, SUB_TITLE_FONT_SIZE, LATERAL_MARIN, SUB_TITLE_FONT_SIZE * 1.5f);
        currentYPosition -= SUB_TITLE_FONT_SIZE *0.5f;
        this.pageContentStream.addRect(LATERAL_MARIN, currentYPosition, pageWidthF-LATERAL_MARIN-LATERAL_MARIN, LINE_SIZE);
        this.pageContentStream.fill();
        currentYPosition -= SUB_TITLE_FONT_SIZE * 1.5f;
    }

    private void addDescription(String[] word, boolean main, boolean boolImg) throws IOException {
        PDFont font = main ? MAIN_DESCRIPTION_FONT : BODY_DESCRIPTION_FONT;
        float startX = boolImg ? LATERAL_MARIN+ACTION_CERCLE_DIAMETER+OFFSET_IMAGE_TEXT : LATERAL_MARIN;
        float fontSize = main ? MAIN_DESCRIPTION_FONT_SIZE : BODY_DESCRIPTION_FONT_SIZE;
        addText(word, font, fontSize, startX, fontSize * 1.5f);
    }
    private void addExemple(String[] word) throws IOException {
        addText(word, BODY_EXEMPLE_FONT, BODY_EXEMPLE_FONT_SIZE, LATERAL_MARIN, BODY_EXEMPLE_FONT_SIZE * 1.5f);
    }

    private void addId(String[] word) throws IOException {
        addText(word, BODY_ID_FONT, BODY_ID_FONT_SIZE, LATERAL_MARIN, BODY_ID_FONT_SIZE * 1.5f);
    }

    private void checkAvailableSpace(String[][] texts, boolean img) throws IOException {
        float size = BODY_TITLE_FONT_SIZE * 1.5f;
        float startX = img ? LATERAL_MARIN+ACTION_CERCLE_DIAMETER+OFFSET_IMAGE_TEXT : LATERAL_MARIN;
        float tx = startX;
        setSpaceWidth(BODY_TITLE_FONT, BODY_TITLE_FONT_SIZE);
        for (String[] text : texts) {
            for (String word :  text) {
                float wordWidth = BODY_TITLE_FONT.getStringWidth(word) / 1000 * BODY_TITLE_FONT_SIZE;
                if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                    tx = startX;
                    size += BODY_TITLE_FONT_SIZE * 1.5f;
                }
                tx += wordWidth + this.spaceWidth;
            }
        }
        if (this.currentYPosition - size <= FOOTER_MARGIN) {
            setGridPage();
            setPageContentStream();
        }
    }

    private void setSpaceWidth(PDFont font, float fontSize) throws IOException {
        this.spaceWidth = font.getStringWidth(" ") / 1000 * fontSize;
    }

    /**
    * The circle is created using cubic Bézier curves to approximate the shape of a circle.
    * Four curve segments are used to form a complete circle by connecting four control points.
    */
    public void addCircle(boolean main) throws IOException {
        float radius = main ? MAIN_CERCLE_DIAMETER/2 : ACTION_CERCLE_DIAMETER/2;

        float centerX = main ? LATERAL_MARIN + radius/2 : LATERAL_MARIN + radius;
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

    private void setLcLogo() throws IOException {
        File logoFile = new File(exportedImageDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        this.logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();
        this.logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDoc);
    }

    private void addFooter() throws IOException {
        // FOOTER
        this.pageContentStream.addRect(0, FOOTER_MARGIN, pageHeightF, LINE_SIZE);
        this.pageContentStream.fill();
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, FOOTER_MARGIN - FOOTER_LINE_HEIGHT);
        this.pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
        this.pageContentStream.showText(currentTypeCategory + StringUtils.dateToStringDateWithHour(new Date()));
        this.pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
        this.pageContentStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties().getAppServerUrl());
        this.pageContentStream.endText();

        // Temp save LC logo
        this.pageContentStream.drawImage(this.logoImage, pageWidthF - this.logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_MARGIN / 2f - LOGO_HEIGHT / 2f, this.logoDrawWidth, LOGO_HEIGHT);
        this.pageContentStream.close();
    }
}
