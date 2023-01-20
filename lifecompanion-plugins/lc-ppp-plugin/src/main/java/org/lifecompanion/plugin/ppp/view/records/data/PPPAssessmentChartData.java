package org.lifecompanion.plugin.ppp.view.records.data;

import javafx.scene.paint.Color;
import org.lifecompanion.framework.commons.translation.Translation;
import org.lifecompanion.model.impl.constant.LCGraphicStyle;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.view.records.RecordsChart;

import java.util.List;

public class PPPAssessmentChartData extends AbstractAssessmentChartData {
    public PPPAssessmentChartData(RecordsChart chart, long date, List<AssessmentRecord> records, boolean isDetailed) {
        super(chart, date, records, isDetailed);
    }

    @Override
    public String getTitle() {
        if (this.recordsSize < 2) {
            return Translation.getText("ppp.plugin.view.records.details.assessments.ppp.single.title");
        }

        return Translation.getText("ppp.plugin.view.records.details.assessments.ppp.multiple.title");
    }

    @Override
    protected List<? extends ChartData> getAllData() {
        return this.chart.getPppAssessmentsData();
    }

    @Override
    protected Color getColor() {
        return LCGraphicStyle.MAIN_PRIMARY;
    }
}
