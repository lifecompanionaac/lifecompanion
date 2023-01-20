package org.lifecompanion.plugin.ppp.view.records;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.controlsfx.glyphfont.FontAwesome;
import org.lifecompanion.controller.editaction.AsyncExecutorController;
import org.lifecompanion.controller.editmode.FileChooserType;
import org.lifecompanion.controller.editmode.LCFileChoosers;
import org.lifecompanion.controller.io.IOHelper;
import org.lifecompanion.controller.resource.GlyphFontHelper;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.framework.commons.ui.LCViewInitHelper;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.constant.LCConstant;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.ppp.model.JsonRecordI;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.services.FilesService;
import org.lifecompanion.plugin.ppp.services.RecordsService;
import org.lifecompanion.plugin.ppp.tasks.*;
import org.lifecompanion.plugin.ppp.view.commons.FormatterListCell;
import org.lifecompanion.plugin.ppp.view.records.data.ChartData;
import org.lifecompanion.plugin.ppp.view.records.periods.DateFormats;
import org.lifecompanion.plugin.ppp.view.records.periods.Period;
import org.lifecompanion.plugin.ppp.view.records.periods.PeriodType;
import org.lifecompanion.plugin.ppp.view.records.series.SeriesGroup;
import org.lifecompanion.util.IOUtils;
import org.lifecompanion.util.javafx.DialogUtils;
import org.lifecompanion.util.javafx.FXControlUtils;
import org.lifecompanion.util.javafx.FXUtils;

import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.lifecompanion.controller.editmode.FileChooserType.OTHER_MISC_EXTERNAL;

public class RecordsView extends BorderPane implements LCViewInitHelper {
    private final LCConfigurationI config;

    private final BooleanProperty loading;
    private final ObjectProperty<UserProfile> profile;
    private final ObjectProperty<PeriodType> periodType;
    private final ObjectProperty<SeriesGroup> seriesGroup;
    private final ObjectProperty<Period> period;
    private final ObjectProperty<File> zipFile;
    private final ObjectProperty<File> dataDirectory;

    private RecordsChart chart;
    private LineChart<Number, Number> backgroundChart;

    private Label dataLabel;
    private Button btnImport;
    private Button btnExport;
    private Button btnPdf;
    private Button btnRemoveImported;
    private ComboBox<PeriodType> comboPeriodTypes;
    private ComboBox<SeriesGroup> comboSeriesGroup;
    private Button btnPrevPeriod;
    private Button btnNextPeriod;

    private Label detailsTitle;
    private Button detailsPrevBtn;
    private Button detailsNextBtn;
    private GridPane detailsDescription;
    private Button detailsDayViewBtn;
    private Button detailedRecordDeleteBtn;
    private JsonRecordI detailedRecord;

    private final Consumer<RecordsView> onLoadCallback;

    private final boolean hideTaskFromNotifications;

    public RecordsView(LCConfigurationI config) {
        this(config, null, PeriodType.DAY, PeriodType.DAY.getDefaultPeriod(), false, null);

        this.handleZipFileChange();
    }

    public RecordsView(LCConfigurationI config, File dataDirectory, LocalDate day, Consumer<RecordsView> onLoadCallback) {
        this(config, dataDirectory, PeriodType.DAY, PeriodType.DAY.getPeriodForEnd(day.plusDays(1).atStartOfDay(ZoneId.systemDefault())), true, onLoadCallback);

        this.handleDataDirectoryChange();
    }

    public RecordsView(LCConfigurationI config, File dataDirectory, LocalDate from, LocalDate to, Consumer<RecordsView> onLoadCallback) {
        this(config, dataDirectory, PeriodType.MONTH, new Period(from.atStartOfDay(ZoneId.systemDefault()), to.atStartOfDay(ZoneId.systemDefault())), true, onLoadCallback);

        this.handleDataDirectoryChange();
    }

    private RecordsView(LCConfigurationI config, File dataDirectory, PeriodType periodType, Period period, boolean hideTaskFromNotifications, Consumer<RecordsView> onLoadCallback) {
        this.config = config;
        this.hideTaskFromNotifications = hideTaskFromNotifications;
        this.loading = new SimpleBooleanProperty(true);
        this.profile = new SimpleObjectProperty<>();
        this.periodType = new SimpleObjectProperty<>(periodType);
        this.seriesGroup = new SimpleObjectProperty<>(SeriesGroup.HETERO);
        this.period = new SimpleObjectProperty<>(period);
        //this.period = new SimpleObjectProperty<>(PeriodType.DAY.getPeriodForEnd(day.plusDays(1).atStartOfDay(ZoneId.systemDefault())));
        this.zipFile = new SimpleObjectProperty<>();
        this.dataDirectory = new SimpleObjectProperty<>(dataDirectory);
        this.onLoadCallback = onLoadCallback;

        this.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        this.getStylesheets().add(this.getClass().getResource("/styles/ppp_plugin_chart.css").toExternalForm());
        this.initAll();
    }

    public StackPane createChartPaneForSnapshot() {
        StackPane chartPane = new StackPane(this.createChartBackground(), this.chart);
        chartPane.getStylesheets().addAll(LCConstant.CSS_STYLE_PATH);
        chartPane.getStylesheets().add(this.getClass().getResource("/styles/ppp_plugin_chart.css").toExternalForm());

        return chartPane;
    }

    public RecordsChart getChart() {
        return this.chart;
    }

    @Override
    public void initUI() {
        this.chart = new RecordsChart();

        this.dataLabel = new Label();
        this.dataLabel.setMaxHeight(Double.MAX_VALUE);
        this.dataLabel.setMaxWidth(Double.MAX_VALUE);
        this.dataLabel.getStyleClass().add("text-weight-bold");
        this.btnPdf = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.period.data.pdf.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.FILE).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.btnImport = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.period.data.import.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.DOWNLOAD).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.btnExport = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.period.data.export.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.UPLOAD).size(20).color(LCGraphicStyle.MAIN_DARK),
                null);
        this.btnRemoveImported = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.period.data.remove.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CLOSE).size(20).color(
                        LCGraphicStyle.SECOND_PRIMARY), null);

        HBox periodDataLayout = new HBox(5.0, this.dataLabel, this.btnImport, this.btnExport, this.btnRemoveImported, this.btnPdf);
        HBox.setHgrow(this.dataLabel, Priority.ALWAYS);
        periodDataLayout.getStyleClass().add("border-bottom-gray");

        this.comboPeriodTypes = new ComboBox<>(FXCollections.observableArrayList(PeriodType.values()));
        this.comboPeriodTypes.setButtonCell(new FormatterListCell<>(PeriodType::getName));
        this.comboPeriodTypes.setCellFactory((lv) -> new FormatterListCell<>(PeriodType::getName));

        this.comboSeriesGroup = new ComboBox<>(FXCollections.observableArrayList(SeriesGroup.values()));
        this.comboSeriesGroup.setButtonCell(new FormatterListCell<>(SeriesGroup::getText));
        this.comboSeriesGroup.setCellFactory((lv) -> new FormatterListCell<>(SeriesGroup::getText));

        this.btnPrevPeriod = FXControlUtils.createLeftTextButton(
                Translation.getText("ppp.plugin.view.records.period.previous.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(20).color(
                        LCGraphicStyle.MAIN_DARK), null);
        this.btnNextPeriod = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.period.next.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(20).color(
                        LCGraphicStyle.MAIN_DARK), null);

        Pane fillerPane1 = new Pane();
        HBox.setHgrow(fillerPane1, Priority.ALWAYS);
        Pane fillerPane2 = new Pane();
        HBox.setHgrow(fillerPane2, Priority.ALWAYS);
        HBox periodActionsLayout = new HBox(10, btnPrevPeriod, fillerPane1, comboPeriodTypes, comboSeriesGroup,
                fillerPane2, btnNextPeriod);

        VBox periodLayout = new VBox(10, periodDataLayout, periodActionsLayout);

        this.detailsPrevBtn = FXControlUtils.createLeftTextButton(
                Translation.getText("ppp.plugin.view.records.details.previous.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_LEFT).size(20).color(
                        LCGraphicStyle.MAIN_DARK), null);
        this.detailsNextBtn = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.details.next.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.CHEVRON_RIGHT).size(20).color(
                        LCGraphicStyle.MAIN_DARK), null);
        this.detailedRecordDeleteBtn = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.details.delete.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.TRASH_ALT).size(20).color(
                        LCGraphicStyle.SECOND_DARK), null);
        this.detailsTitle = new Label();
        this.detailsTitle.setMaxHeight(Double.MAX_VALUE);
        this.detailsTitle.setMaxWidth(Double.MAX_VALUE);
        this.detailsTitle.setAlignment(Pos.CENTER);
        this.detailsTitle.setTextAlignment(TextAlignment.CENTER);
        this.detailsTitle.getStyleClass().add("text-weight-bold");
        HBox.setHgrow(this.detailsTitle, Priority.ALWAYS);
        HBox detailsTitleLayout = new HBox(this.detailsPrevBtn, this.detailsTitle, this.detailsNextBtn);

        this.detailsDescription = new GridPane();
        this.detailsDescription.setAlignment(Pos.CENTER);
        this.detailsDescription.setHgap(15.0);
        this.detailsDescription.setVgap(5.0);
        this.detailsDescription.add(
                new Label(Translation.getText("ppp.plugin.view.records.details.no_data.description")), 0, 0);

        this.detailsDayViewBtn = FXControlUtils.createRightTextButton(
                Translation.getText("ppp.plugin.view.records.details.day_view.name"),
                GlyphFontHelper.FONT_AWESOME.create(FontAwesome.Glyph.SHARE).size(16).color(
                        LCGraphicStyle.MAIN_DARK),
                null);
        GridPane.setHalignment(this.detailsDayViewBtn, HPos.CENTER);
        GridPane.setHalignment(this.detailedRecordDeleteBtn, HPos.CENTER);

        VBox detailsLayout = new VBox(10, detailsTitleLayout, this.detailsDescription);
        detailsLayout.setFillWidth(true);
        detailsLayout.setMinHeight(160.0);
        detailsLayout.setAlignment(Pos.CENTER);
        detailsLayout.getStyleClass().add("border-top-gray");

        this.backgroundChart = this.createChartBackground();
        StackPane chartStackPane = new StackPane(this.backgroundChart, this.chart);

        BorderPane container = new BorderPane();
        container.setTop(periodLayout);
        container.setCenter(chartStackPane);
        container.setBottom(detailsLayout);
        container.setPadding(new Insets(5));

        ScrollPane containerScroller = new ScrollPane(container);
        containerScroller.setFitToWidth(true);
        containerScroller.setFitToHeight(true);
        this.setCenter(containerScroller);
    }

    @Override
    public void initBinding() {
        this.dataLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            if (this.profile.get() == null) {
                return "";
            }

            return this.zipFile.get() == null
                    ? Translation.getText("ppp.plugin.view.records.period.data.label.from_config",
                    this.profile.get().getUserId())
                    : Translation.getText("ppp.plugin.view.records.period.data.label.from_import",
                    this.profile.get().getUserId(), this.zipFile.get().getName());
        }, this.profile));

        this.btnImport.disableProperty().bind(this.loading);
        this.btnImport.visibleProperty().bind(this.zipFile.isNull());
        this.btnImport.managedProperty().bind(this.zipFile.isNull());
        this.btnExport.disableProperty().bind(this.loading);
        this.btnExport.visibleProperty().bind(this.zipFile.isNull());
        this.btnExport.managedProperty().bind(this.zipFile.isNull());
        this.btnPdf.disableProperty().bind(this.loading);
        this.btnRemoveImported.disableProperty().bind(this.loading);
        this.btnRemoveImported.visibleProperty().bind(this.zipFile.isNotNull());
        this.btnRemoveImported.managedProperty().bind(this.zipFile.isNotNull());
        this.btnPrevPeriod.disableProperty().bind(this.loading);
        this.btnNextPeriod.disableProperty().bind(this.loading);

        this.comboPeriodTypes.disableProperty().bind(this.loading);
        this.comboPeriodTypes.getSelectionModel().select(this.periodType.get());

        this.comboSeriesGroup.disableProperty().bind(this.loading);
        this.comboSeriesGroup.valueProperty().bindBidirectional(this.seriesGroup);

        this.detailsPrevBtn.disableProperty().bind(
                Bindings.createBooleanBinding(() -> this.chart.selectedDataProperty().get() == null
                                || this.chart.selectedDataProperty().get().getPrev() == null,
                        this.chart.selectedDataProperty()).or(this.loading));
        this.detailsNextBtn.disableProperty().bind(
                Bindings.createBooleanBinding(() -> this.chart.selectedDataProperty().get() == null
                                || this.chart.selectedDataProperty().get().getNext() == null,
                        this.chart.selectedDataProperty()).or(this.loading));
        this.detailsTitle.textProperty().bind(Bindings.createStringBinding(() -> {
            ChartData selectedData = this.chart.selectedDataProperty().get();
            if (selectedData == null) {
                return Translation.getText("ppp.plugin.view.records.details.no_data.title");
            }

            return selectedData.getTitle();
        }, this.chart.selectedDataProperty()));

        this.detailsDayViewBtn.disableProperty().bind(this.loading);
        this.detailedRecordDeleteBtn.disableProperty().bind(this.loading.or(this.zipFile.isNotNull()));
    }

    @Override
    public void initListener() {
        this.btnImport.setOnAction(e -> {
            FileChooser fileChooser = LCFileChoosers.getOtherFileChooser(
                    Translation.getText("ppp.plugin.view.records.period.data.import.chooser.title"),
                    FilesService.DATA_EXTENSION_FILTER, OTHER_MISC_EXTERNAL);

            File dataZipFile = fileChooser.showOpenDialog(FXUtils.getSourceWindow(btnImport));
            if (dataZipFile != null) {
                this.zipFile.set(dataZipFile);
            }
        });
        this.btnExport.setOnAction(event -> {
            FileChooser fileChooser = LCFileChoosers.getOtherFileChooser(
                    Translation.getText("ppp.plugin.view.records.period.data.export.chooser.title"),
                    FilesService.DATA_EXTENSION_FILTER, OTHER_MISC_EXTERNAL);
            fileChooser.setInitialFileName(IOHelper.DATE_FORMAT_FILENAME_WITHOUT_TIME.format(new Date()) + "-"
                    + IOUtils.getValidFileName(this.profile.get().getUserId()) + "-ppp");

            File destinationZipFile = fileChooser.showSaveDialog(FXUtils.getSourceWindow(btnExport));
            if (destinationZipFile != null) {
                Task<Void> task = new ExportDataTask(this.config, destinationZipFile);
                AsyncExecutorController.INSTANCE.addAndExecute(true, false, task);
            }
        });
        this.btnPdf.setOnAction(event -> {
            // Contains (from,to)
            final DateRangerPickerDialog dateRangerPickerDialog = new DateRangerPickerDialog();
            dateRangerPickerDialog.initOwner(FXUtils.getSourceWindow(btnPdf));
            final Optional<Pair<LocalDate, LocalDate>> dateRange = dateRangerPickerDialog.showAndWait();
            if (dateRange.isPresent()) {
                FileChooser fileChooser = LCFileChoosers.getOtherFileChooser(
                        Translation.getText("ppp.plugin.view.records.period.data.pdf.chooser.title"),
                        FilesService.PDF_EXTENSION_FILTER, FileChooserType.EXPORT_PDF);

                fileChooser.setInitialFileName(IOHelper.DATE_FORMAT_FILENAME_WITHOUT_TIME.format(new Date()) + "-"
                        + IOUtils.getValidFileName(this.profile.get().getUserId()) + "-ppp");

                File destinationFile = fileChooser.showSaveDialog(FXUtils.getSourceWindow(btnPdf));
                if (destinationFile != null) {
                    Task<Void> task = new PdfExportTask(this.config, this.dataDirectory.get(), destinationFile,
                            dateRange.get().getKey(), dateRange.get().getValue());
                    AsyncExecutorController.INSTANCE.addAndExecute(true, false, task);
                }
            }
        });
        this.btnRemoveImported.setOnAction(e -> this.zipFile.set(null));
        this.btnPrevPeriod.setOnAction(e -> this.period.set(this.periodType.get().getPrevious(this.period.get())));
        this.btnNextPeriod.setOnAction(e -> this.period.set(this.periodType.get().getNext(this.period.get())));
        this.comboPeriodTypes.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null && nv != ov && nv != this.periodType.get()) {
                this.periodType.set(nv);
                this.period.set(nv.getDefaultPeriod());
            }
        });

        this.zipFile.addListener((obs, ov, nv) -> this.handleZipFileChange());
        this.dataDirectory.addListener((obs, ov, nv) -> this.handleDataDirectoryChange());

        this.profile.addListener((obs, ov, nv) -> this.handleProfileOrPeriodChange());
        this.period.addListener((obs, ov, nv) -> this.handleProfileOrPeriodChange());
        this.seriesGroup.addListener((obs, ov, nv) -> this.handleProfileOrPeriodChange());

        this.detailsPrevBtn.setOnAction(e -> this.handleDetailsChangeUsing(ChartData::getPrev));
        this.detailsNextBtn.setOnAction(e -> this.handleDetailsChangeUsing(ChartData::getNext));
        this.detailsDayViewBtn.setOnAction(e -> {
            ChartData selectedData = this.chart.selectedDataProperty().get();
            if (selectedData == null) {
                return;
            }

            JsonRecordI firstRecord = selectedData.firstRecord();
            if (firstRecord == null) {
                return;
            }

            this.periodType.set(PeriodType.DAY);
            this.comboPeriodTypes.getSelectionModel().select(this.periodType.get());
            this.period.set(PeriodType.DAY.getPeriodForEnd(firstRecord.getRecordedAt().plusDays(1)));
        });
        this.detailedRecordDeleteBtn.setOnAction(e -> {
            if (DialogUtils.alertWithSourceAndType(this.detailedRecordDeleteBtn, Alert.AlertType.CONFIRMATION)
                    .withContentText(Translation.getText("ppp.plugin.view.records.details.delete.confirmDialog.content"))
                    .withHeaderText(Translation.getText("ppp.plugin.view.records.details.delete.confirmDialog.header"))
                    .showAndWait() == ButtonType.OK
            ) {
                RecordsService.INSTANCE.delete(this.config, this.detailedRecord);
                // Trigger chart reload.
                this.handleDataDirectoryChange();
            }
        });

        this.chart.selectedDataProperty().addListener((obs, ov, nv) -> {
            this.detailsDescription.getChildren().clear();

            if (nv == null) {
                this.detailsDescription.add(
                        new Label(Translation.getText("ppp.plugin.view.records.details.no_data.description")),
                        0, 0, 2, 1);

                return;
            }

            this.detailedRecord = nv.detailedRecord();
            if (this.detailedRecord == null) {
                this.createDetailsName(Translation.getText("ppp.plugin.view.records.details.data.date"), 0);
                this.createDetailsValue(this.periodType.get().getAxisValue(nv.getData().getXValue().longValue()), 0);
                this.createDetailsName(nv.getDetailsName(), 1);
                this.createDetailsValue(nv.getDetailsValue(), 1);
                this.detailsDescription.add(this.detailsDayViewBtn, 0, 2, 2, 1);

                return;
            }

            this.createDetailsName(Translation.getText("ppp.plugin.view.records.details.data.date"), 0);
            this.createDetailsValue(DateFormats.SHORT_DATETIME.format(this.detailedRecord.getRecordedAt()), 0);
            this.createDetailsName(Translation.getText("ppp.plugin.view.records.details.data.evaluator"), 1);
            this.createDetailsValue(this.detailedRecord.getEvaluator() == null
                    ? Translation.getText("ppp.plugin.view.records.details.data.auto_assessment")
                    : this.detailedRecord.getEvaluator().getName(), 1);
            this.createDetailsName(nv.getDetailsName(), 2);
            this.createDetailsValue(nv.getDetailsValue(), 2);
            this.createDetailsName(Translation.getText("ppp.plugin.view.records.details.data.comment"), 3);
            this.createDetailsValue(this.detailedRecord.getComment() == null ? "-" : this.detailedRecord.getComment(), 3);
            this.detailsDescription.add(this.detailedRecordDeleteBtn, 0, 4, 2, 1);
        });

        this.seriesGroup.addListener((obs, ov, nv) -> {
            if (nv != null) {
                this.setAxisVisibility((NumberAxis) this.chart.getYAxis(), nv.isLeftAxisVisible());
                this.setAxisVisibility((NumberAxis) this.backgroundChart.getYAxis(), nv.isRightAxisVisible());
            }
        });
    }

    private void setAxisVisibility(NumberAxis axis, boolean visible) {
        axis.setTickLabelsVisible(visible);
        axis.setTickMarkVisible(visible);
        axis.setMinorTickVisible(visible);
    }

    private void createDetailsName(String name, int rowIndex) {
        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("text-weight-bold");

        this.detailsDescription.add(nameLabel, 0, rowIndex);
    }

    private void createDetailsValue(String value, int rowIndex) {
        Label valueLabel = new Label(value);
        GridPane.setHalignment(valueLabel, HPos.RIGHT);

        this.detailsDescription.add(valueLabel, 1, rowIndex);
    }

    private void handleZipFileChange() {
        this.loading.set(true);

        File zipFile = this.zipFile.get();
        if (zipFile != null) {
            ImportDataTask task = new ImportDataTask(zipFile);
            task.setOnSucceeded(e -> this.dataDirectory.set(task.getValue()));

            AsyncExecutorController.INSTANCE.addAndExecute(true, hideTaskFromNotifications, task);
        } else {
            this.dataDirectory.set(new File(FilesService.INSTANCE.getPluginDirectoryPath(this.config)));
        }
    }

    private void handleDataDirectoryChange() {
        this.loading.set(true);

        LoadDirProfileTask task = new LoadDirProfileTask(this.dataDirectory.get());
        task.setOnSucceeded(e -> this.profile.set(task.getValue()));

        AsyncExecutorController.INSTANCE.addAndExecute(true, hideTaskFromNotifications, task);
    }

    private void handleProfileOrPeriodChange() {
        this.loading.set(true);

        UserProfile profile = this.profile.get();
        Period period = this.period.get();
        File dataDirectory = this.dataDirectory.get();
        if (profile != null && periodType != null && period != null && dataDirectory != null) {
            LoadDataForPeriodTask task = new LoadDataForPeriodTask(this.dataDirectory.get(), period);
            task.setOnSucceeded(e -> {
                this.chart.apply(profile, this.periodType.get(), period, this.seriesGroup.get());

                this.loading.set(false);

                if (this.onLoadCallback != null) {
                    this.onLoadCallback.accept(this);
                }
            });
            AsyncExecutorController.INSTANCE.addAndExecute(true, true, task);
        }
    }

    private void handleDetailsChangeUsing(Function<ChartData, ChartData> resolver) {
        ChartData selectedData = this.chart.selectedDataProperty().get();
        if (selectedData == null) {
            return;
        }

        ChartData newChartData = resolver.apply(selectedData);
        if (newChartData != null) {
            this.chart.selectedDataProperty().set(newChartData);
        }
    }

    private LineChart<Number, Number> createChartBackground() {
        NumberAxis dateAxis = new NumberAxis();
        dateAxis.setOpacity(0.0);

        NumberAxis scoreAxis = new NumberAxis();
        scoreAxis.setSide(Side.RIGHT);
        scoreAxis.setLabel(Translation.getText("ppp.plugin.view.records.chart.axes.score.evs.label"));
        scoreAxis.setPrefWidth(55.0);
        scoreAxis.setLowerBound(0);
        scoreAxis.setUpperBound(4);
        scoreAxis.setTickUnit(1);
        scoreAxis.setMinorTickVisible(false);
        scoreAxis.setAutoRanging(false);

        LineChart<Number, Number> chartBg = new LineChart<>(dateAxis, scoreAxis);
        chartBg.setAnimated(false);
        chartBg.setCreateSymbols(false);
        chartBg.setVerticalZeroLineVisible(false);
        chartBg.setHorizontalZeroLineVisible(false);
        chartBg.setVerticalGridLinesVisible(false);
        chartBg.setHorizontalGridLinesVisible(false);
        chartBg.getStyleClass().add("background-chart");

        return chartBg;
    }
}
