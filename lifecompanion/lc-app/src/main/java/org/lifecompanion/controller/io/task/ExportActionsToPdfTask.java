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
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.categorizedelement.useaction.BaseUseActionI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionMainCategoryI;
import org.lifecompanion.model.api.categorizedelement.useaction.UseActionSubCategoryI;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.api.profile.LCConfigurationDescriptionI;
import org.lifecompanion.model.api.profile.LCProfileI;
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

    private static final float HEADER_SIZE = 35f, FOOTER_SIZE = 30f,  MAIN_COLOR_SIZE = 32 * 2, ACTION_COLOR_SIZE = 32, MAIN_T_FONT_SIZE = 40, MAIN_D_FONT_SIZE =20,SUB_FONT_SIZE = 16, BODY_TITEL_FONT_SIZE = 12, BODY_FONT_SIZE = 12, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, COLOR_GRAY = 0.4f, LATERAL_MARIN = 45, TOP_MARGIN = 20;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont HEADER_DESCRIPTION_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final PDFont SUB_FONT = PDType1Font.HELVETICA_BOLD_OBLIQUE;
    private static final PDFont BODY_TITEL_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont BODY_FONT = PDType1Font.HELVETICA;
    private static final PDFont FOOTER_FONT = PDType1Font.HELVETICA;


    private static final String EXPORT_IMAGE_LOADING_KEY = "export-to-pdf";
    private static final long MAX_IMAGE_LOADING_TIME = 10_000;
    private static final String NO_STACK_ID = "nostack";
    private static final int MAIN_TITLE_OFFSET = 5;

    private final File exportedImageDir;
    private final AtomicInteger progress;
    private final File destPdf;
    private final LCProfileI profile;
    private final LCConfigurationDescriptionI configurationDescription;
    private final LCConfigurationI configuration;

    private int totalWork;

    private static float currentYPosition;

    private String currentDirectory;
    private PDDocument doc;
    private static PDPage gridPage;
    private PDPageContentStream pageContentStream;

    public ExportActionsToPdfTask(LCConfigurationI configuration, File destPdf, LCProfileI profile, LCConfigurationDescriptionI configurationDescription) {
        super("task.export.actions.pdf.name");
        this.configuration = configuration;
        this.profile = profile;
        this.configurationDescription = configurationDescription;
        this.exportedImageDir = IOUtils.getTempDir("export-images-for-config");
        this.exportedImageDir.mkdirs();
        this.destPdf = destPdf;
        this.progress = new AtomicInteger(0);
        this.currentDirectory = new File( "").getAbsolutePath()+ "\\src\\main\\resources\\icons\\";
    }

    @Override
    protected Void call() throws Exception {
        // List that contains every action main category and sort
        ObservableList<UseActionMainCategoryI> mainCategories  = AvailableUseActionController.INSTANCE.getMainCategories();
        int numberOfMainCategories = mainCategories.size();
        int numberOfSubCategories = 0;
        int numberOfActions = 0;
        for (UseActionMainCategoryI mainCategory : mainCategories) {
            ObservableList<UseActionSubCategoryI> subCategories = mainCategory.getSubCategories();
            numberOfSubCategories += subCategories.size();
            for (UseActionSubCategoryI subCategory : subCategories) {
                ObservableList<BaseUseActionI<?>> actions = subCategory.getContent();
                numberOfActions += actions.size();
            }
        }
        totalWork = (numberOfMainCategories + numberOfSubCategories + numberOfActions)/10;
        updateProgress(0, totalWork);

        // TODO : default names
        final String profileName = profile != null ? profile.nameProperty().get() : "PROFILE?";
        final String configName = configurationDescription != null ? configurationDescription.configurationNameProperty().get() : "CONFIGURATION?";
        final Date exportDate = new Date();

        // Try to save a PDF
        try (PDDocument pdf = new PDDocument()) {
            doc = pdf;
            updateProgress(progress.get(), totalWork);
            for (UseActionMainCategoryI mainCategory : mainCategories) {
                    setGridPage();
                    PDRectangle pageSize = gridPage.getMediaBox();
                    float pageWidth = pageSize.getWidth();

                    float pageWidthF = pageSize.getHeight();
                    float pageHeightF =pageSize.getWidth();

                    try (PDPageContentStream page = new PDPageContentStream(doc, gridPage)) {
                        this.pageContentStream = page;
                        this.pageContentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));
                        this.currentYPosition = pageHeightF - TOP_MARGIN;

                        // MAIN ICON
                        Color backgroundColor = mainCategory.getColor();
                        PDImageXObject pdImage = PDImageXObject.createFromFile(this.currentDirectory + mainCategory.getConfigIconPath().replace("/", "\\"), doc);
                        int imageWidth = pdImage.getWidth() > 32 ? 32*2: pdImage.getWidth()*2;
                        int imageHeight = pdImage.getHeight() > 32 ? 32*2 : pdImage.getHeight()*2;
                        this.pageContentStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
                        this.pageContentStream.addRect(LATERAL_MARIN, this.currentYPosition-MAIN_COLOR_SIZE, MAIN_COLOR_SIZE,  MAIN_COLOR_SIZE);
                        this.pageContentStream.fill();
                        this.pageContentStream.drawImage(pdImage, LATERAL_MARIN+(MAIN_COLOR_SIZE -  imageWidth) / 2, this.currentYPosition-MAIN_COLOR_SIZE+(MAIN_COLOR_SIZE - imageHeight) / 2, imageWidth,  imageHeight);
                        this.pageContentStream.setNonStrokingColor(0, 0, 0);

                        // MAIN TITLE
                        nextLine(LATERAL_MARIN+MAIN_COLOR_SIZE+MAIN_TITLE_OFFSET, MAIN_T_FONT_SIZE+TOP_MARGIN, mainCategory.getName(), HEADER_FONT, MAIN_T_FONT_SIZE);

                        // MAIN DESCRIPTION
                        String[] words = mainCategory.getStaticDescription().split("\\s+");
                        float tx = LATERAL_MARIN;
                        float ty = MAIN_D_FONT_SIZE * 1.5f;
                        float spaceWidth = HEADER_DESCRIPTION_FONT.getStringWidth(" ") / 1000 * MAIN_D_FONT_SIZE;
                        for (String word : words) {
                            float wordWidth = HEADER_DESCRIPTION_FONT.getStringWidth(word) / 1000 * MAIN_D_FONT_SIZE;
                            if (tx + wordWidth > pageWidthF - LATERAL_MARIN * 2) {
                                tx = LATERAL_MARIN;
                                ty = MAIN_D_FONT_SIZE * 1.5f;
                            }
                            nextLine(tx, ty, word, HEADER_DESCRIPTION_FONT, MAIN_D_FONT_SIZE);
                            tx += wordWidth + spaceWidth;
                            ty = 0;
                        }

                        //SUB TITLE
                        ObservableList<UseActionSubCategoryI> subCategories = mainCategory.getSubCategories();
                        for (UseActionSubCategoryI subCategory : subCategories) {
                            float textWidth = SUB_FONT.getStringWidth(subCategory.getName()) / 1000 * SUB_FONT_SIZE;
                            float centeredTextPositionX = (pageWidthF - textWidth) / 2;
                            nextLine(centeredTextPositionX, MAIN_D_FONT_SIZE*1.5f, subCategory.getName(), SUB_FONT, SUB_FONT_SIZE);

                            ObservableList<BaseUseActionI<?>> actions = subCategory.getContent();
                            for (BaseUseActionI<?> action : actions) {
                                // ACTIONS SIZE
                                PDImageXObject actionImage = PDImageXObject.createFromFile(this.currentDirectory + action.getConfigIconPath().replace("/", "\\"), doc);
                                words = action.getStaticDescription().split("\\s+");
                                float size = BODY_FONT_SIZE * 1.5f+10;
                                tx = LATERAL_MARIN;
                                spaceWidth = BODY_FONT.getStringWidth(" ") / 1000 * BODY_FONT_SIZE;
                                for (String word : words) {
                                    float wordWidth = BODY_FONT.getStringWidth(word) / 1000 * BODY_FONT_SIZE;
                                    if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                                        tx = LATERAL_MARIN;
                                        size += BODY_FONT_SIZE * 1.5f;
                                    }
                                    tx += wordWidth + spaceWidth;
                                }
                                size = ACTION_COLOR_SIZE + size;
                                if (this.currentYPosition - size < FOOTER_SIZE) {
                                    setPageContentStream();
                                }

                                // ACTIONS ICON
                                backgroundColor = action.getCategory().getColor();
                                imageWidth = actionImage.getWidth() > 32 ?  32 : actionImage.getWidth();
                                imageHeight = actionImage.getHeight() > 32 ? 32 : actionImage.getHeight();
                                this.pageContentStream.setNonStrokingColor((float) backgroundColor.getRed(), (float) backgroundColor.getGreen(), (float) backgroundColor.getBlue());
                                this.pageContentStream.addRect(LATERAL_MARIN, this.currentYPosition - ACTION_COLOR_SIZE, ACTION_COLOR_SIZE, ACTION_COLOR_SIZE);
                                this.pageContentStream.fill();
                                this.pageContentStream.drawImage(actionImage, LATERAL_MARIN+(ACTION_COLOR_SIZE-imageWidth)/2, this.currentYPosition-ACTION_COLOR_SIZE+(ACTION_COLOR_SIZE-imageHeight)/2, imageWidth, imageHeight);
                                this.pageContentStream.setNonStrokingColor(0, 0, 0);

                                // ACTIONS TITLE
                                nextLine(LATERAL_MARIN+ACTION_COLOR_SIZE+MAIN_TITLE_OFFSET, ACTION_COLOR_SIZE, action.getName(), BODY_TITEL_FONT, BODY_FONT_SIZE);

                                // ACTIONS DESCRIPTION
                                tx = LATERAL_MARIN;
                                ty = BODY_FONT_SIZE * 1.5f;
                                spaceWidth = BODY_FONT.getStringWidth(" ") / 1000 * BODY_FONT_SIZE;
                                for (String word : words) {
                                    float wordWidth = BODY_FONT.getStringWidth(word) / 1000 * BODY_FONT_SIZE;
                                    if (tx + wordWidth > pageWidthF - LATERAL_MARIN) {
                                        tx = LATERAL_MARIN;
                                        ty = BODY_FONT_SIZE * 1.5f;
                                    }
                                    nextLine(tx, ty, word, BODY_FONT, BODY_FONT_SIZE);
                                    tx += wordWidth + spaceWidth;
                                    ty = 0;
                                }
                                currentYPosition -= 10;
                            }
                        }

                        // FOOTER
                        addFooter();
                    }
                    updateProgress(progress.incrementAndGet(), totalWork);
                }
            PDDocumentInformation pdi = doc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText("pdf.export.file.title", profileName, configName));
            pdi.setCreator(LCConstant.NAME);
            doc.save(destPdf);
        }
        return null;
    }

    private void nextLine(float tx, float ty, String text, PDFont font, float fontSize) throws IOException {
        if (this.currentYPosition - fontSize < FOOTER_SIZE) {
            setPageContentStream();
        } else {
            this.currentYPosition -= ty;
        }
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(tx, this.currentYPosition);
        writeLine(text, font, fontSize);
        this.pageContentStream.endText();
    }

    private void writeLine(String text, PDFont font, float fontSize) throws IOException {
        this.pageContentStream.setFont(font, fontSize);
        this.pageContentStream.showText(text);
    }

    private void setGridPage() {
        gridPage = new PDPage(PDRectangle.A4);
        doc.addPage(gridPage);
        gridPage.setRotation(90);
    }

    private void setPageContentStream() throws IOException {
        addFooter();
        setGridPage();
        this.pageContentStream = new PDPageContentStream(doc, gridPage);
        this.pageContentStream.transform(new Matrix(0, 1, -1, 0, gridPage.getMediaBox().getWidth(), 0));
        this.currentYPosition = gridPage.getMediaBox().getWidth() - TOP_MARGIN;
    }

    private void addFooter() throws IOException {
        this.pageContentStream.addRect(0, FOOTER_SIZE, gridPage.getMediaBox().getHeight(), LINE_SIZE);
        this.pageContentStream.fill();
        this.pageContentStream.beginText();
        this.pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, FOOTER_SIZE - FOOTER_LINE_HEIGHT);
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
        PDImageXObject logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), doc);
        this.pageContentStream.drawImage(logoImage, gridPage.getMediaBox().getHeight() - logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_SIZE / 2f - LOGO_HEIGHT / 2f, logoDrawWidth, LOGO_HEIGHT);
        this.pageContentStream.close();
    }
}
