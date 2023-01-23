package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.scene.chart.XYChart;
import org.lifecompanion.plugin.ppp.model.JsonRecordI;

public interface ChartData {
    boolean isDetailed();

    JsonRecordI firstRecord();

    JsonRecordI detailedRecord();

    XYChart.Data<Number, Number> getData();

    String getTitle();

    String getDetailsName();

    String getDetailsValue();

    ChartData getPrev();

    ChartData getNext();
}
