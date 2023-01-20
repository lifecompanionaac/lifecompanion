package org.lifecompanion.plugin.ppp.tasks;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.lifecompanion.controller.appinstallation.InstallationController;
import org.lifecompanion.controller.plugin.PluginController;
import org.lifecompanion.controller.resource.IconHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.utils.lang.StringUtils;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.plugin.PluginInfo;
import org.lifecompanion.plugin.ppp.PediatricPainProfilePlugin;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.ProfileService;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;
import org.lifecompanion.plugin.ppp.view.records.RecordsView;
import org.lifecompanion.plugin.ppp.view.records.data.ChartData;
import org.lifecompanion.plugin.ppp.view.records.periods.DateFormats;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.FXUtils;
import org.lifecompanion.util.javafx.SnapshotUtils;
import org.lifecompanion.util.model.LCTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PdfExportTask extends LCTask<Void> {
    public static final Logger LOGGER = LoggerFactory.getLogger(PdfExportTask.class);

    private static final float HEADER_SIZE = 65f, FOOTER_SIZE = 30f, IMAGE_Y_OFFSET = 10f, HEADER_FONT_SIZE = 16, FOOTER_FONT_SIZE = 9, TEXT_LEFT_OFFSET = 50, DATA_LINE_HEIGHT = 16f, FOOTER_LINE_HEIGHT = 12f, LOGO_HEIGHT = 25f, LINE_SIZE = 1f, COLOR_DATA = 0.95f, COLOR_GRAY = 0.4f;
    private static final PDFont HEADER_FONT = PDType1Font.HELVETICA_BOLD;
    private static final PDFont FOOTER_FONT = PDType1Font.HELVETICA;

    private final LCConfigurationI config;
    private final File dataDirectory;
    private final File destinationFile;
    private final LocalDate start;
    private final LocalDate end;
    private final File tempImagesDirectory;

    private PDDocument doc;
    private String profileName;
    private Date exportDate;
    private PDImageXObject logoImage;
    private float logoDrawWidth;

    public PdfExportTask(final LCConfigurationI config, final File dataDirectory, final File destinationFile, LocalDate start, LocalDate end) {
        super("ppp.plugin.task.pdf.export.title");
        this.config = config;
        this.dataDirectory = dataDirectory;
        this.destinationFile = destinationFile;
        this.start = start;
        this.end = end;

        this.tempImagesDirectory = IOUtils.getTempDir("ppp-images-pdf-export");
        this.tempImagesDirectory.mkdirs();
    }

    @Override
    protected Void call() throws Exception {
        final long daysNumber = this.start.until(this.end, ChronoUnit.DAYS);
        final long totalProgress = daysNumber + 1;
        long progress = 0;

        this.updateProgress(progress, totalProgress);

        UserProfile userProfile = ProfileService.INSTANCE.loadProfile(this.dataDirectory.getAbsolutePath());
        this.profileName = userProfile.getUserId();
        this.exportDate = new Date();

        // Temp save LC logo
        File logoFile = new File(this.tempImagesDirectory.getPath() + File.separator + "lc_logo.png");
        BufferedImage logoBuffImage = SwingFXUtils.fromFXImage(IconHelper.get(LCConstant.LC_BIG_ICON_PATH), null);
        ImageIO.write(logoBuffImage, "png", logoFile);
        this.logoDrawWidth = LOGO_HEIGHT / logoBuffImage.getHeight() * logoBuffImage.getWidth();

        // Try to save a PDF
        try (PDDocument doc = new PDDocument()) {
            this.doc = doc;
            this.logoImage = PDImageXObject.createFromFile(logoFile.getAbsolutePath(), doc);

            SummaryExportResult summaryResult = new SummaryExportResult(this.config, this.dataDirectory,
                    this.tempImagesDirectory, this.start, this.start.plusDays(daysNumber));
            summaryResult.build();

            this.createPage(Translation.getText("ppp.plugin.pdf.header.title.summary",
                    summaryResult.getFrom().format(DateFormats.SHORT_DATE), summaryResult.getTo().format(DateFormats.SHORT_DATE), profileName),
                    (pageContentStream, pageWidth, pageHeight) -> {
                        // CHART IMAGE
                        PDImageXObject pdImage = PDImageXObject.createFromFile(summaryResult.getChartImage().getAbsolutePath(), doc);
                        float imageWidth = pageWidth - TEXT_LEFT_OFFSET * 2f;
                        float imageHeight = imageWidth / 2f;
                        float imageY = pageHeight - HEADER_SIZE - imageHeight - IMAGE_Y_OFFSET;
                        pageContentStream.drawImage(pdImage, TEXT_LEFT_OFFSET, imageY, imageWidth, imageHeight);
                    });

            this.updateProgress(progress++, totalProgress);

            for (int daysToAdd = 0; daysToAdd < daysNumber; daysToAdd++) {
                DayExportResult dayResult = new DayExportResult(this.config, this.dataDirectory,
                        this.tempImagesDirectory, this.start.plusDays(daysToAdd));
                dayResult.build();

                this.createPage(Translation.getText("ppp.plugin.pdf.header.title.day",
                        dayResult.getDay().format(DateFormats.SHORT_DATE), profileName), (pageContentStream, pageWidth, pageHeight) -> {
                    // CHART IMAGE
                    PDImageXObject pdImage = PDImageXObject.createFromFile(dayResult.getChartImage().getAbsolutePath(), doc);
                    float imageWidth = pageWidth - TEXT_LEFT_OFFSET * 2f;
                    float imageHeight = imageWidth / 2f;
                    float imageY = pageHeight - HEADER_SIZE - imageHeight - IMAGE_Y_OFFSET;
                    pageContentStream.drawImage(pdImage, TEXT_LEFT_OFFSET, imageY, imageWidth, imageHeight);

                    // DETAILS.
                    pageContentStream.beginText();
                    pageContentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                    pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, imageY - IMAGE_Y_OFFSET * 2f);
                    pageContentStream.showText(Translation.getText("ppp.plugin.pdf.data.title"));
                    pageContentStream.endText();

                    List<ChartData> allChartData = Stream.concat(dayResult.getChart().getActionsData().stream(),
                            Stream.concat(dayResult.getChart().getPppAssessmentsData().stream(), dayResult.getChart().getEvsAssessmentsData().stream()))
                            .sorted(Comparator.comparing(c -> c.detailedRecord().getRecordedAt()))
                            .collect(Collectors.toList());

                    float rowY = imageY - IMAGE_Y_OFFSET * 5f;

                    if (allChartData.isEmpty()) {
                        pageContentStream.beginText();
                        pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
                        pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, rowY + DATA_LINE_HEIGHT / 2.5f);
                        pageContentStream.showText(Translation.getText("ppp.plugin.pdf.data.no_data"));
                        pageContentStream.endText();
                    }

                    for (int index = 0; index < allChartData.size(); index++) {
                        ChartData chartData = allChartData.get(index);

                        String comment = chartData.detailedRecord().getComment();
                        if (index % 2 == 0) {
                            float backgroundHeight = (comment == null ? 1f : 2f) * DATA_LINE_HEIGHT;
                            float backgroundY = comment == null ? rowY : rowY - DATA_LINE_HEIGHT;

                            pageContentStream.setNonStrokingColor(COLOR_DATA, COLOR_DATA, COLOR_DATA);
                            pageContentStream.addRect(TEXT_LEFT_OFFSET, backgroundY, pageWidth - TEXT_LEFT_OFFSET * 2f, backgroundHeight);
                            pageContentStream.fill();
                            pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);
                        }

                        pageContentStream.beginText();
                        pageContentStream.setFont(HEADER_FONT, FOOTER_FONT_SIZE);
                        pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET + 5f, rowY + DATA_LINE_HEIGHT / 2.5f);
                        pageContentStream.showText(Translation.getText("ppp.plugin.pdf.data.date_and_type",
                                DateFormats.TIME.format(chartData.detailedRecord().getRecordedAt()),
                                chartData.getTitle()));
                        pageContentStream.endText();

                        pageContentStream.beginText();
                        pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
                        pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET + 75f, rowY + DATA_LINE_HEIGHT / 2.5f);
                        pageContentStream.showText(Translation.getText("ppp.plugin.pdf.data.score_and_evaluator",
                                chartData.getDetailsValue(), chartData.detailedRecord().getEvaluator().getName()));
                        pageContentStream.endText();

                        if (comment != null) {
                            rowY -= DATA_LINE_HEIGHT;

                            pageContentStream.beginText();
                            pageContentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, FOOTER_FONT_SIZE);
                            pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET + 75f, rowY + DATA_LINE_HEIGHT / 2.5f);
                            pageContentStream.showText(chartData.detailedRecord().getComment().replace("\n", ". "));
                            pageContentStream.endText();
                        }

                        rowY -= DATA_LINE_HEIGHT;
                    }
                });

                this.updateProgress(progress++, totalProgress);
            }

            // Document info and save
            PDDocumentInformation pdi = doc.getDocumentInformation();
            pdi.setAuthor(LCConstant.NAME);
            pdi.setTitle(Translation.getText("ppp.plugin.pdf.file.title", this.start.format(DateFormats.SHORT_DATE), this.end.format(DateFormats.SHORT_DATE), profileName));
            pdi.setCreator(LCConstant.NAME);
            doc.save(this.destinationFile);
            doc.save(new File(this.tempImagesDirectory.getPath() + File.separator + "result.pdf"));
        }

        return null;
    }

    private void createPage(String title, PageConsumer pageConsumer) throws Exception {
        PDPage gridPage = new PDPage(PDRectangle.A4);
        this.doc.addPage(gridPage);

        PDRectangle pageSize = gridPage.getMediaBox();

        float pageWidth = pageSize.getWidth();
        float pageHeight = pageSize.getHeight();

        try (PDPageContentStream pageContentStream = new PDPageContentStream(this.doc, gridPage)) {
            pageContentStream.setNonStrokingColor(COLOR_GRAY, COLOR_GRAY, COLOR_GRAY);

            // HEADER
            pageContentStream.addRect(0, pageHeight - HEADER_SIZE, pageWidth, LINE_SIZE);
            pageContentStream.fill();
            pageContentStream.beginText();
            pageContentStream.setFont(HEADER_FONT, HEADER_FONT_SIZE);
            pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, pageHeight - HEADER_SIZE / 1.25f);
            pageContentStream.showText(title);
            pageContentStream.endText();

            pageConsumer.accept(pageContentStream, pageWidth, pageHeight);

            // FOOTER
            Optional<PluginInfo> pluginInfo = PluginController.INSTANCE.getPluginInfoList().stream()
                    .filter(p -> PediatricPainProfilePlugin.PLUGIN_ID.equals(p.getPluginId())
                            && PluginController.INSTANCE.isPluginLoaded(p.getPluginId()))
                    .findAny();

            pageContentStream.addRect(0, FOOTER_SIZE, pageWidth, LINE_SIZE);
            pageContentStream.fill();
            pageContentStream.beginText();
            pageContentStream.setFont(FOOTER_FONT, FOOTER_FONT_SIZE);
            pageContentStream.newLineAtOffset(TEXT_LEFT_OFFSET, FOOTER_SIZE - FOOTER_LINE_HEIGHT);
            pageContentStream.showText(Translation.getText("ppp.plugin.pdf.footer.title",
                    this.profileName, StringUtils.dateToStringDateWithHour(this.exportDate)));
            pageContentStream.newLineAtOffset(0, -FOOTER_LINE_HEIGHT);
            pageContentStream.showText(Translation.getText("ppp.plugin.pdf.footer.subtitle",
                    LCConstant.NAME,
                    InstallationController.INSTANCE.getBuildProperties().getVersionLabel(),
                    pluginInfo.isPresent() ? pluginInfo.get().getPluginVersion() : "Unknown",
                    InstallationController.INSTANCE.getBuildProperties().getAppServerUrl()));
            pageContentStream.endText();
            pageContentStream.drawImage(this.logoImage, pageWidth - this.logoDrawWidth - TEXT_LEFT_OFFSET, FOOTER_SIZE / 2f - LOGO_HEIGHT / 2f, this.logoDrawWidth, LOGO_HEIGHT);
        }
    }

    private static abstract class ExportResult {
        protected final LCConfigurationI config;
        protected final File dataDirectory;
        protected final File tempDirectory;

        private RecordsChart chart;
        private File chartImage;

        public ExportResult(LCConfigurationI config, File dataDirectory, File tempDirectory) {
            this.config = config;
            this.dataDirectory = dataDirectory;
            this.tempDirectory = tempDirectory;
        }

        abstract protected void buildView(Consumer<RecordsView> viewConsumer);

        public void build() {
            AtomicReference<RecordsChart> chartRef = new AtomicReference<>();
            AtomicReference<Image> imageRef = new AtomicReference<>();
            Semaphore semaphore = new Semaphore(0);

            this.buildView((view) -> {
                try {
                    StackPane chartPane = view.createChartPaneForSnapshot();
                    chartPane.setStyle("-fx-background-color: #F4F4F4");
                    FXUtils.setFixedSize(chartPane, 800, 400);

                    chartRef.set(view.getChart());
                    imageRef.set(SnapshotUtils.takeNodeSnapshot(chartPane, 800, 400, true, 0.8));
                } finally {
                    semaphore.release();
                }
            });

            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                LOGGER.warn("Cannot capture image", e);
            }

            this.chart = chartRef.get();
            Image chartFxImage = imageRef.get();
            this.chartImage = new File(tempDirectory.getPath() + File.separator + UUID.randomUUID() + "-chart.png");

            try {
                BufferedImage buffImage = SwingFXUtils.fromFXImage(chartFxImage, null);
                ImageIO.write(buffImage, "png", this.chartImage);
            } catch (Exception e) {
                LOGGER.error("Exception when saving snapshot to {}", this.chartImage, e);
            }
        }

        public RecordsChart getChart() {
            return this.chart;
        }

        public File getChartImage() {
            return this.chartImage;
        }
    }

    private static class DayExportResult extends ExportResult {
        private final LocalDate day;

        public DayExportResult(LCConfigurationI config, File dataDirectory, File tempDirectory, LocalDate day) {
            super(config, dataDirectory, tempDirectory);

            this.day = day;
        }

        @Override
        protected void buildView(Consumer<RecordsView> viewConsumer) {
            new RecordsView(this.config, this.dataDirectory, this.day, viewConsumer);
        }

        public LocalDate getDay() {
            return this.day;
        }
    }

    private static class SummaryExportResult extends ExportResult {
        private final LocalDate from;
        private final LocalDate to;

        public SummaryExportResult(LCConfigurationI config, File dataDirectory, File tempDirectory, LocalDate from, LocalDate to) {
            super(config, dataDirectory, tempDirectory);

            this.from = from;
            this.to = to;
        }

        @Override
        protected void buildView(Consumer<RecordsView> viewConsumer) {
            new RecordsView(this.config, this.dataDirectory, this.from, this.to, viewConsumer);
        }

        public LocalDate getFrom() {
            return this.from;
        }

        public LocalDate getTo() {
            return this.to;
        }
    }

    @FunctionalInterface
    public interface PageConsumer {
        void accept(PDPageContentStream page, float width, float height) throws Exception;
    }
}
