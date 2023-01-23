package org.lifecompanion.plugin.ppp.view.records;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.TilePane;
import javafx.scene.shape.Rectangle;
import javafx.util.StringConverter;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.ppp.model.JsonRecordI;
import org.lifecompanion.plugin.ppp.model.UserProfile;
import org.lifecompanion.plugin.ppp.view.records.data.*;
import org.lifecompanion.plugin.ppp.view.records.periods.Period;
import org.lifecompanion.plugin.ppp.view.records.periods.PeriodType;
import org.lifecompanion.plugin.ppp.view.records.series.SeriesGroup;
import org.lifecompanion.plugin.ppp.view.records.series.SeriesType;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class RecordsChart extends LineChart<Number, Number> {
    private final Series<Number, Number> scoreSeries;
    private final Series<Number, Number> actionsSeries;
    private final Series<Number, Number> pppAssessmentsSeries;
    private final Series<Number, Number> evsAssessmentsSeries;
    private final Series<Number, Number> autoEvsAssessmentsSeries;
    private final List<ChartData> actionsData;
    private final List<ChartData> pppAssessmentsData;
    private final List<ChartData> evsAssessmentsData;
    private final List<ChartData> autoEvsAssessmentsData;

    private final ObjectProperty<ChartData> selectedData;

    private TilePane legendAlias;
    private FlowPane legendReplacement;

    public RecordsChart() {
        super(createDateAxis(), createScoreAxis());

        this.setTitle(Translation.getText("ppp.plugin.view.records.chart.title"));
        this.setAnimated(false);
        this.setCreateSymbols(false);
        this.getStyleClass().add("records-chart");

        this.scoreSeries = new Series<>();
        this.scoreSeries.setName(Translation.getText("ppp.plugin.view.records.chart.series.score.name"));
        this.actionsSeries = new Series<>();
        this.actionsSeries.setName(SeriesType.ACTIONS.getText());
        this.pppAssessmentsSeries = new Series<>();
        this.pppAssessmentsSeries.setName(SeriesType.PPP_ASSESSMENTS.getText());
        this.evsAssessmentsSeries = new Series<>();
        this.evsAssessmentsSeries.setName(SeriesType.EVS_ASSESSMENTS.getText());
        this.autoEvsAssessmentsSeries = new Series<>();
        this.autoEvsAssessmentsSeries.setName(SeriesType.AUTO_EVS_ASSESSMENTS.getText());

        this.actionsData = new ArrayList<>();
        this.pppAssessmentsData = new ArrayList<>();
        this.evsAssessmentsData = new ArrayList<>();
        this.autoEvsAssessmentsData = new ArrayList<>();

        this.selectedData = new SimpleObjectProperty<>();

        this.setAllSeries();
    }

    private static Axis<Number> createDateAxis() {
        NumberAxis dateAxis = new NumberAxis();

        dateAxis.setAutoRanging(false);

        return dateAxis;
    }

    private static Axis<Number> createScoreAxis() {
        NumberAxis scoreAxis = new NumberAxis();

        scoreAxis.setLabel(Translation.getText("ppp.plugin.view.records.chart.axes.score.ppp.label"));
        scoreAxis.setAutoRanging(false);
        scoreAxis.setLowerBound(0);
        scoreAxis.setUpperBound(60);
        scoreAxis.setPrefWidth(55.0);

        return scoreAxis;
    }

    @Override
    protected void updateLegend() {
        super.updateLegend();

        Node legend = getLegend();
        if (legend instanceof TilePane) {
            this.legendAlias = (TilePane) legend;
            this.legendReplacement = new FlowPane(10, 10);
            this.legendReplacement.setPadding(new Insets(10, 0, 0, 0));
            this.legendReplacement.setAlignment(Pos.CENTER);
            this.legendReplacement.getStyleClass().add("records-chart-legend");
            setLegend(this.legendReplacement);
        }

        if (this.legendAlias != null && this.legendAlias.getChildren().size() > 0) {
            this.legendReplacement.getChildren().setAll(this.legendAlias.getChildren());
            this.legendAlias.getChildren().clear();
            setLegend(this.legendReplacement);
        }
    }

    public List<ChartData> getActionsData() {
        return actionsData;
    }

    public List<ChartData> getPppAssessmentsData() {
        return pppAssessmentsData;
    }

    public List<ChartData> getEvsAssessmentsData() {
        return evsAssessmentsData;
    }

    public List<ChartData> getAutoEvsAssessmentsData() {
        return autoEvsAssessmentsData;
    }

    public ObjectProperty<ChartData> selectedDataProperty() {
        return selectedData;
    }

    public void apply(UserProfile profile, PeriodType periodType, Period period, SeriesGroup seriesGroup) {
        this.selectedData.set(null);

        this.setTitle(periodType.getTitleValue(period.getStart(), period.getEnd()));

        NumberAxis dateAxis = (NumberAxis) this.getXAxis();
        dateAxis.setLowerBound(periodType.getRealValue(period.getStart()));
        dateAxis.setUpperBound(periodType.getRealValue(period.getEnd()));
        dateAxis.setTickUnit(periodType.getTickUnit());
        dateAxis.setMinorTickVisible(false);
        dateAxis.setTickLabelFormatter(new StringConverter<>() {
            @Override
            public String toString(Number number) {
                return periodType.getAxisValue(number.longValue());
            }

            @Override
            public Number fromString(String string) {
                return null;
            }
        });

        this.applyToScoreSeries(profile, periodType, period);
        this.applyToActionsSeries(periodType, period);
        this.applyToPPPAssessmentsSeries(periodType, period);
        this.applyToEvsAssessmentsSeries(periodType, period);
        this.applyToAutoEvsAssessmentsSeries(periodType, period);

        this.setAllSeries();

        for (SeriesType seriesType : SeriesType.values()) {
            this.setVisibilityOfSeries(seriesType, seriesGroup.getVisibleSeries().contains(seriesType));
        }
    }

    private void setAllSeries() {
        // Reset all data to force a re-render of axis and series nodes.
        this.getData().setAll(Arrays.asList(this.scoreSeries, this.actionsSeries,
                this.pppAssessmentsSeries, this.evsAssessmentsSeries, this.autoEvsAssessmentsSeries));
    }

    private void setVisibilityOfSeries(SeriesType seriesType, boolean visible) {
        Series<Number, Number> series = this.getData().stream()
                .filter(s -> s.getName().equals(seriesType.getText()))
                .findFirst().orElse(null);
        if (series == null) {
            return;
        }

        series.getNode().setVisible(visible);
        series.getData().forEach(d -> d.getNode().setVisible(visible));

        this.legendReplacement.getChildren().forEach(c -> {
            Label label = (Label) c;
            if (label.getText().equals(series.getName())) {
                label.setVisible(visible);
                label.setManaged(visible);
            }
        });
    }

    private void applyToScoreSeries(UserProfile profile, PeriodType periodType, Period period) {
        Series<Number, Number> scoreSeries = new Series<>();
        scoreSeries.setName("Score de base");

        Function<ZonedDateTime, Data<Number, Number>> createScoreData = date -> {
            XYChart.Data<Number, Number> data = new XYChart.Data<>(
                    periodType.getRealValue(date), profile.getBaseScore());
            Node invisibleRectangle = new Rectangle();
            invisibleRectangle.setVisible(false);
            data.setNode(invisibleRectangle);
            return data;
        };

        this.scoreSeries.getData().setAll(Arrays.asList(
                createScoreData.apply(period.getStart()), createScoreData.apply(period.getEnd())));
    }

    private void applyToActionsSeries(PeriodType periodType, Period period) {
        this.applyRecordsToSeries(
                periodType, period.getActions(), this.actionsData, this.actionsSeries,
                dateRecords -> new ActionChartData(this,
                        dateRecords.getKey(), dateRecords.getValue(), periodType.isDetailed()));
    }

    private void applyToPPPAssessmentsSeries(PeriodType periodType, Period period) {
        this.applyRecordsToSeries(
                periodType, period.getPppAssessments(), this.pppAssessmentsData, this.pppAssessmentsSeries,
                dateRecords -> new PPPAssessmentChartData(this,
                        dateRecords.getKey(), dateRecords.getValue(), periodType.isDetailed()));
    }

    private void applyToEvsAssessmentsSeries(PeriodType periodType, Period period) {
        this.applyRecordsToSeries(
                periodType, period.getEvsAssessments(), this.evsAssessmentsData, this.evsAssessmentsSeries,
                dateRecords -> new EvsAssessmentChartData(this,
                        dateRecords.getKey(), dateRecords.getValue(), periodType.isDetailed()));
    }

    private void applyToAutoEvsAssessmentsSeries(PeriodType periodType, Period period) {
        this.applyRecordsToSeries(
                periodType, period.getAutoEvsAssessments(), this.autoEvsAssessmentsData, this.autoEvsAssessmentsSeries,
                dateRecords -> new AutoEvsAssessmentChartData(this,
                        dateRecords.getKey(), dateRecords.getValue(), periodType.isDetailed()));
    }

    private <T extends JsonRecordI> void applyRecordsToSeries(PeriodType periodType, List<T> records,
                                                              List<ChartData> dataList, Series<Number, Number> dataSeries,
                                                              Function<Map.Entry<Long, List<T>>, ChartData> makeData) {
        dataList.clear();

        SortedMap<Long, List<T>> dateRecordsMap = new TreeMap<>();
        records.forEach(record -> dateRecordsMap.compute(
                periodType.getRealValue(record.getRecordedAt()),
                (date, recordLists) -> {
                    recordLists = recordLists != null ? recordLists : new ArrayList<>();
                    recordLists.add(record);

                    return recordLists;
                }));


        for (Map.Entry<Long, List<T>> dateRecords : dateRecordsMap.entrySet()) {
            dataList.add(makeData.apply(dateRecords));
        }

        dataSeries.getData().setAll(
                dataList.stream().map(ChartData::getData).collect(Collectors.toList()));
    }
}
