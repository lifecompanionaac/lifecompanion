package org.lifecompanion.plugin.ppp.view.records.series;

import org.lifecompanion.framework.commons.translation.Translation;

public enum SeriesType {
    ACTIONS("ppp.plugin.view.records.chart.series.actions.name"),
    PPP_ASSESSMENTS("ppp.plugin.view.records.chart.series.assessments.ppp.name"),
    EVS_ASSESSMENTS("ppp.plugin.view.records.chart.series.assessments.evs.name"),
    AUTO_EVS_ASSESSMENTS("ppp.plugin.view.records.chart.series.assessments.auto_evs.name");

    private final String textId;

    SeriesType(String textId) {
        this.textId = textId;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }
}
