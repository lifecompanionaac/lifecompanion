package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;

public class AutoEvsAssessmentChartData extends EvsAssessmentChartData {
    public AutoEvsAssessmentChartData(RecordsChart chart, long date, List<AssessmentRecord> records, boolean isDetailed) {
        super(chart, date, records, isDetailed);
    }

    @Override
    public String getTitle() {
        if (this.recordsSize < 2) {
            return Translation.getText("ppp.plugin.view.records.details.assessments.auto_evs.single.title");
        }

        return Translation.getText("ppp.plugin.view.records.details.assessments.auto_evs.multiple.title");
    }

    @Override
    protected List<? extends ChartData> getAllData() {
        return this.chart.getAutoEvsAssessmentsData();
    }
}
