package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;

public class EvsAssessmentChartData extends AbstractAssessmentChartData {
    private static final Color EVS_COLOR = Color.web("#8BC34A");

    public EvsAssessmentChartData(RecordsChart chart, long date, List<AssessmentRecord> records, boolean isDetailed) {
        super(chart, date, records, isDetailed);
    }

    @Override
    protected Number computeScoreForAxis() {
        return (this.score.doubleValue() / 4.0) * 60.0;
    }

    @Override
    public String getTitle() {
        if (this.recordsSize < 2) {
            return Translation.getText("ppp.plugin.view.records.details.assessments.evs.single.title");
        }

        return Translation.getText("ppp.plugin.view.records.details.assessments.evs.multiple.title");
    }

    @Override
    protected List<? extends ChartData> getAllData() {
        return this.chart.getEvsAssessmentsData();
    }

    @Override
    protected Color getColor() {
        return EVS_COLOR;
    }
}
