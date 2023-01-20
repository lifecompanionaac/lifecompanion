package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.scene.chart.XYChart;
import org.lifecompanion.plugin.ppp.model.JsonRecordI;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;

public abstract class AbstractChartData<T extends JsonRecordI> implements ChartData {
    protected final RecordsChart chart;
    protected final long date;
    protected final List<T> records;
    protected final int recordsSize;
    private final boolean isDetailed;
    private final String detailsName;
    private final String detailsValue;
    private final XYChart.Data<Number, Number> data;

    public AbstractChartData(RecordsChart chart, long date, List<T> records, boolean isDetailed) {
        this.chart = chart;
        this.date = date;
        this.records = records;
        this.recordsSize = records.size();
        this.isDetailed = isDetailed;

        this.data = this.computeData();
        this.detailsName = this.computeDetailsName();
        this.detailsValue = this.computeDetailsValue();
    }

    @Override
    public boolean isDetailed() {
        return this.isDetailed && this.recordsSize == 1;
    }

    @Override
    public JsonRecordI firstRecord() {
        return this.recordsSize == 0 ? null : this.records.get(0);
    }

    @Override
    public JsonRecordI detailedRecord() {
        return this.isDetailed() ? this.records.get(0) : null;
    }

    @Override
    public XYChart.Data<Number, Number> getData() {
        return data;
    }

    @Override
    public String getDetailsName() {
        return detailsName;
    }

    @Override
    public String getDetailsValue() {
        return detailsValue;
    }

    @Override
    public ChartData getPrev() {
        int prevIndex = this.getAllData().indexOf(this) - 1;

        return prevIndex >= 0 ? this.getAllData().get(prevIndex) : null;
    }

    @Override
    public ChartData getNext() {
        int nextIndex = this.getAllData().indexOf(this) + 1;

        return nextIndex < this.getAllData().size() ? this.getAllData().get(nextIndex) : null;
    }

    abstract protected XYChart.Data<Number, Number> computeData();

    abstract protected String computeDetailsName();

    abstract protected String computeDetailsValue();

    abstract protected List<? extends ChartData> getAllData();
}
