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
import org.apache.pdfbox.util.Matrix;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.categorizedelement.useaction.AvailableUseActionController;
import org.lifecompanion.controller.categorizedelement.useevent.AvailableUseEventController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.controller.usevariable.UseVariableController;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
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

    private static final float MAIN_TITLE_FONT_SIZE = 40, MAIN_DESCRIPTION_FONT_SIZE =20, SUB_TITLE_FONT_SIZE = 16, BODY_TITLE_FONT_SIZE = 12, BODY_DESCRIPTION_FONT_SIZE = 12, BODY_EXEMPLE_FONT_SIZE = 12, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, MAIN_TITLE_OFFSET = 5, HEADER_MARGIN = 30f, LATERAL_MARIN = 45f, FOOTER_MARGIN = 30f, spaceBetweenBody = 20, MAIN_ICON_COLOR_SIZE = 64, ACTION_ICON_COLOR_SIZE = 32;
    private static final PDFont MAIN_TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont MAIN_DESCRIPTION_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont SUB_TITLE_FONT = PDType1Font.HELVETICA_BOLD_OBLIQUE;
    private static final PDFont BODY_TITLE_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont BODY_DESCRIPTION_FONT = PDType1Font.HELVETICA;
    private static final PDFont BODY_EXEMPLE_FONT = PDType1Font.HELVETICA_OBLIQUE;
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
    private final String imageDirectory;
    private float spaceWidth;

    public ExportActionsToPdfTask(File destPdf) {
        super("task.export.lists.pdf.name");
        this.exportedImageDir = IOUtils.getTempDir("export-images-for-config");
        this.exportedImageDir.mkdirs();
        this.destPdf = destPdf;
        this.progress = new AtomicInteger(0);
        this.imageDirectory = new File( "").getAbsolutePath()+ "\\src\\main\\resources\\icons\\";
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

            for (UseActionMainCategoryI mainCategory : mainCategoriesAction) {
                setGridPage();
                try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage)) {
                    this.pageContentStream = page;
                    setPageContentStream();

                    // MAIN ICON
                    addIcon(mainCategory.getConfigIconPath(), mainCategory.getColor(), true);

                    // MAIN TITLE
                    addText(("Action - "+mainCategory.getName()).split("\\s+"), MAIN_TITLE_FONT, MAIN_TITLE_FONT_SIZE, LATERAL_MARIN+MAIN_ICON_COLOR_SIZE+MAIN_TITLE_OFFSET, HEADER_MARGIN+MAIN_ICON_COLOR_SIZE-MAIN_TITLE_FONT_SIZE);

                    // MAIN DESCRIPTION
                    addText(mainCategory.getStaticDescription().split("\\s+"), MAIN_DESCRIPTION_FONT, MAIN_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, MAIN_DESCRIPTION_FONT_SIZE * 1.5f);

                    //SUB TITLE
                    ObservableList<UseActionSubCategoryI> subCategories = mainCategory.getSubCategories();
                    for (UseActionSubCategoryI subCategory : subCategories) {
                        addText(subCategory.getName().split("\\s+"), SUB_TITLE_FONT, SUB_TITLE_FONT_SIZE, (pageWidthF - SUB_TITLE_FONT.getStringWidth(subCategory.getName()) / 1000 * SUB_TITLE_FONT_SIZE) / 2, MAIN_DESCRIPTION_FONT_SIZE * 1.5f);

                        ObservableList<BaseUseActionI<?>> actions = subCategory.getContent();
                        for (BaseUseActionI<?> action : actions) {
                            // ACTIONS SIZE
                            PDImageXObject actionImage = PDImageXObject.createFromFile(this.imageDirectory + action.getConfigIconPath().replace("/", "\\"), pdfDoc);
                            String[] words = action.getStaticDescription().split("\\s+");
                            checkAvailableSpace(actionImage.getHeight(), words);

                            // ACTIONS ICON
                            addIcon(action.getConfigIconPath(), action.getCategory().getColor(), false);

                            // ACTIONS TITLE
                            addText(action.getName().split("\\s+"), BODY_TITLE_FONT, BODY_TITLE_FONT_SIZE, LATERAL_MARIN+ACTION_ICON_COLOR_SIZE+MAIN_TITLE_OFFSET, ACTION_ICON_COLOR_SIZE);

                            // ACTIONS DESCRIPTION
                            addText(words, BODY_DESCRIPTION_FONT, BODY_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, BODY_DESCRIPTION_FONT_SIZE * 1.5f);

                            currentYPosition -= spaceBetweenBody;
                        }
                    }

                    // FOOTER
                    addFooter();
                }
                updateProgress(progress.incrementAndGet(), totalWork);
            }
            for (UseEventMainCategoryI mainCategory : mainCategoriesEvent) {
                setGridPage();
                try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage)) {
                    this.pageContentStream = page;
                    setPageContentStream();

                    // MAIN ICON
                    addIcon(mainCategory.getConfigIconPath(), mainCategory.getColor(), true);

                    // MAIN TITLE
                    addText(("Event - " + mainCategory.getName()).split("\\s+"), MAIN_TITLE_FONT, MAIN_TITLE_FONT_SIZE, LATERAL_MARIN+MAIN_ICON_COLOR_SIZE+MAIN_TITLE_OFFSET, HEADER_MARGIN+MAIN_ICON_COLOR_SIZE-MAIN_TITLE_FONT_SIZE);

                    // MAIN DESCRIPTION
                    addText(mainCategory.getStaticDescription().split("\\s+"), MAIN_DESCRIPTION_FONT, MAIN_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, MAIN_DESCRIPTION_FONT_SIZE * 1.5f);

                    //SUB TITLE
                    ObservableList<UseEventSubCategoryI> subCategories = mainCategory.getSubCategories();
                    for (UseEventSubCategoryI subCategory : subCategories) {
                        addText(new String[]{subCategory.getName()}, SUB_TITLE_FONT, SUB_TITLE_FONT_SIZE, (pageWidthF - SUB_TITLE_FONT.getStringWidth(subCategory.getName()) / 1000 * SUB_TITLE_FONT_SIZE) / 2, MAIN_DESCRIPTION_FONT_SIZE * 1.5f);

                        ObservableList<UseEventGeneratorI> events = subCategory.getContent();
                        for (UseEventGeneratorI event : events) {
                            // ACTIONS SIZE
                            PDImageXObject actionImage = PDImageXObject.createFromFile(this.imageDirectory + event.getConfigIconPath().replace("/", "\\"), pdfDoc);
                            String[] words = event.getStaticDescription().split("\\s+");
                            checkAvailableSpace(actionImage.getHeight(), words);

                            // ACTIONS ICON
                            addIcon(event.getConfigIconPath(), event.getCategory().getColor(), false);

                            // ACTIONS TITLE
                            addText(new String[]{event.getName()}, BODY_TITLE_FONT, BODY_TITLE_FONT_SIZE, LATERAL_MARIN + ACTION_ICON_COLOR_SIZE + MAIN_TITLE_OFFSET, ACTION_ICON_COLOR_SIZE);

                            // ACTIONS DESCRIPTION
                            addText(words, BODY_DESCRIPTION_FONT, BODY_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, BODY_DESCRIPTION_FONT_SIZE * 1.5f);
                            currentYPosition -= spaceBetweenBody;
                        }
                    }
                    // FOOTER
                    addFooter();
                    updateProgress(progress.incrementAndGet(), totalWork);
                }
            }
            setGridPage();
            try (PDPageContentStream page = new PDPageContentStream(pdfDoc, pdfPage)) {
                this.pageContentStream = page;
                setPageContentStream();

                //TITLE
                addText(new String[]{"Variable"}, MAIN_TITLE_FONT, MAIN_TITLE_FONT_SIZE, LATERAL_MARIN, HEADER_MARGIN);

                // MAIN DESCRIPTION
                addText(Translation.getText("use.variable.select.dialog.header.text").split("\\s+"), MAIN_DESCRIPTION_FONT, MAIN_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, MAIN_DESCRIPTION_FONT_SIZE * 1.5f);

                currentYPosition -= spaceBetweenBody;
                for (UseVariableDefinitionI var : variables) {
                    String[] words = var.getDescription().split("\\s+");

                    // VARIABLE SIZE
                    checkAvailableSpace((int) (BODY_DESCRIPTION_FONT_SIZE * 1.5f), words);

                    // VARIABLE NAME
                    addText(new String[]{var.getName()}, BODY_TITLE_FONT, BODY_TITLE_FONT_SIZE, LATERAL_MARIN, BODY_TITLE_FONT_SIZE * 1.5f);

                    // VARIABLE DESCRIPTION
                    addText(words, BODY_DESCRIPTION_FONT, BODY_DESCRIPTION_FONT_SIZE, LATERAL_MARIN, BODY_DESCRIPTION_FONT_SIZE * 1.5f);

                    // VARIABLE EXAMPLE
                    addText(new String[]{"Example : "+var.getExampleValueToString()}, BODY_EXEMPLE_FONT, BODY_EXEMPLE_FONT_SIZE, LATERAL_MARIN, BODY_EXEMPLE_FONT_SIZE * 1.5f);

                    currentYPosition -= spaceBetweenBody;
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
        pdfPage.setRotation(90);
        PDRectangle pageSize = pdfPage.getMediaBox();
        pageWidthF = pageSize.getHeight();
        pageHeightF =pageSize.getWidth();
    }

    private void setPageContentStream() throws IOException {
        if (this.pageContentStream != null) {
            addFooter();
        }
        this.pageContentStream = new PDPageContentStream(pdfDoc, pdfPage);
        this.pageContentStream.transform(new Matrix(0, 1, -1, 0, pdfPage.getMediaBox().getWidth(), 0));
        this.currentYPosition = pdfPage.getMediaBox().getWidth() - HEADER_MARGIN;
    }

    private void addIcon(String iconPath, Color backgroundColor, boolean mainIcon) {
        try {
            PDImageXObject pdImage = PDImageXObject.createFromFile(this.imageDirectory + iconPath.replace("/", "\\"), pdfDoc);
            int multiplicator = mainIcon ? 2 : 1;
            float size = mainIcon ? (int) MAIN_ICON_COLOR_SIZE : (int) ACTION_ICON_COLOR_SIZE;
            float imageWidth = pdImage.getWidth()*multiplicator > size ? size : pdImage.getWidth() * multiplicator;
            float imageHeight = pdImage.getHeight()*multiplicator > size ? size : pdImage.getHeight() * multiplicator;

            this.pageContentStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
            this.pageContentStream.addRect(LATERAL_MARIN, this.currentYPosition - size, size, size);
            this.pageContentStream.fill();
            this.pageContentStream.drawImage(pdImage, LATERAL_MARIN + (size - imageWidth) / 2, this.currentYPosition - size + (size - imageHeight) / 2, imageWidth, imageHeight);
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

    private void checkAvailableSpace(int para1, String[] para2) throws IOException {
        float size = para1 + BODY_DESCRIPTION_FONT_SIZE * 1.5f , tx = LATERAL_MARIN;
        setSpaceWidth(BODY_DESCRIPTION_FONT, BODY_DESCRIPTION_FONT_SIZE);
        for (String word : para2) {
            float wordWidth = BODY_DESCRIPTION_FONT.getStringWidth(word) / 1000 * BODY_DESCRIPTION_FONT_SIZE;
            if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                tx = LATERAL_MARIN;
                size += BODY_DESCRIPTION_FONT_SIZE * 1.5f;
            }
            tx += wordWidth + this.spaceWidth;
        }
        size = ACTION_ICON_COLOR_SIZE + size;
        if (this.currentYPosition - size <= FOOTER_MARGIN) {
            setGridPage();
            setPageContentStream();
        }
    }

    private void setSpaceWidth(PDFont font, float fontSize) throws IOException {
        this.spaceWidth = font.getStringWidth(" ") / 1000 * fontSize;
    }

    private void addFooter() throws IOException {
        // FOOTER
        this.pageContentStream.addRect(0, FOOTER_MARGIN, pdfPage.getMediaBox().getHeight(), LINE_SIZE);
        this.pageContentStream.fill();
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, FOOTER_MARGIN - FOOTER_LINE_HEIGHT);
        this.pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
        this.pageContentStream.showText(StringUtils.dateToStringDateWithHour(new Date()));
        this.pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
        this.pageContentStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties().getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties().getAppServerUrl());
        this.pageContentStream.endText();

        // Temp save LC logo
        File logoFile = new File(exportedImageDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        float logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), pdfDoc);
        this.pageContentStream.drawImage(logoImage, pdfPage.getMediaBox().getHeight() - logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_MARGIN / 2f - LOGO_HEIGHT / 2f, logoDrawWidth, LOGO_HEIGHT);
        this.pageContentStream.close();
    }
}
