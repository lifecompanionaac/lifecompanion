package org.lifecompanion.util.pdf;

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
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.util.IOUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.function.BiConsumer;

public class PdfUtils {

    private static final float HEADER_SIZE = 35f, FOOTER_SIZE = 30f, IMAGE_BORDER = 20f, BACKGROUND_COLOR_BORDER = 3f, HEADER_FONT_SIZE = 16, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, COLOR_GRAY = 0.4f;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont FOOTER_FONT = PDType1Font.HELVETICA;


    public static void createPdfDocument(DocumentConfiguration documentConfiguration, File destinationFile, List<DocumentImagePage> documentImagePages) throws Exception {
        createPdfDocument(documentConfiguration, destinationFile, documentImagePages, (p, t) -> {
        });
    }

    public static void createPdfDocument(DocumentConfiguration documentConfiguration, File destinationFile, List<DocumentImagePage> documentImagePages, BiConsumer<Integer, Integer> progressIndicator) throws Exception {
        final Date exportDate = new Date();

        // Temp save LC logo
        File tempDir = IOUtils.getTempDir("lifecompanion-logo");
        tempDir.mkdirs();
        File logoFile = new File(tempDir.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        float logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();

        // Try to save a PDF
        int progress = 0;
        progressIndicator.accept(0, documentImagePages.size());
        try (PDDocument doc = new PDDocument()) {
            PDImageXObject logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), doc);

            for (DocumentImagePage documentImagePage : documentImagePages) {

                PDPage gridPage = new PDPage(documentConfiguration.getPageSize());
                doc.addPage(gridPage);
                if (documentImagePage.isLandscape()) gridPage.setRotation(90);
                PDRectangle pageSize = gridPage.getMediaBox();
                float pageWidth = pageSize.getWidth();

                float pageWidthF = documentImagePage.isLandscape() ? pageSize.getHeight() : pageSize.getWidth();
                float pageHeightF = documentImagePage.isLandscape() ? pageSize.getWidth() : pageSize.getHeight();

                try (PDPageContentStream pageContentStream = new PDPageContentStream(doc, gridPage)) {
                    if (documentImagePage.isLandscape())
                        pageContentStream.transform(new Matrix(0, 1, -1, 0, pageWidth, 0));

                    pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);

                    // HEADER
                    pageContentStream.addRect(0, pageHeightF - HEADER_SIZE, pageWidthF, LINE_SIZE);
                    pageContentStream.fill();
                    pageContentStream.beginText();
                    pageContentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
                    pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, pageHeightF - HEADER_SIZE / 1.5f);
                    pageContentStream.showText(documentImagePage.getTitle());
                    pageContentStream.endText();

                    // GRID IMAGE
                    Color color = documentConfiguration.getBackgroundColor();
                    if (color != null) {
                        pageContentStream.setNonStrokingColor((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue());
                        float contentWidth = pageWidthF - BACKGROUND_COLOR_BORDER * 2f, contentHeight = pageHeightF - HEADER_SIZE - FOOTER_SIZE - BACKGROUND_COLOR_BORDER * 2f;
                        pageContentStream.addRect((float) (BACKGROUND_COLOR_BORDER + (pageWidthF - 2 * BACKGROUND_COLOR_BORDER) / 2.0 - contentWidth / 2.0),
                                pageHeightF - HEADER_SIZE - BACKGROUND_COLOR_BORDER - contentHeight - ((pageHeightF - HEADER_SIZE - FOOTER_SIZE - 2f * BACKGROUND_COLOR_BORDER) - contentHeight) / 2f,
                                contentWidth, contentHeight);
                        pageContentStream.fill();
                    }

                    PDImageXObject pdImage = PDImageXObject.createFromFile(documentImagePage.getImageFile().getAbsolutePath(), doc);
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
                    pageContentStream.showText(documentConfiguration.getProfileName() + " - " + documentConfiguration.getConfigurationName() + " - " + StringUtils.dateToStringDateWithHour(exportDate));
                    pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
                    pageContentStream.showText(LCConstant.NAME + " v" + InstallationController.INSTANCE.getBuildProperties()
                            .getVersionLabel() + " - " + InstallationController.INSTANCE.getBuildProperties()
                            .getAppServerUrl());
                    pageContentStream.endText();
                    pageContentStream.drawImage(logoImage, pageWidthF - logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_SIZE / 2f - LOGO_HEIGHT / 2f, logoDrawWidth, LOGO_HEIGHT);
                }
                progressIndicator.accept(++progress, documentImagePages.size());
            }
            // Document info and save
            PDDocumentInformation pdi = doc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText(documentConfiguration.getDocumentNameTranslationId(), documentConfiguration.getProfileName(), documentConfiguration.getConfigurationName()));
            pdi.setCreator(LCConstant.NAME);
            doc.save(destinationFile);
        }
    }
}
