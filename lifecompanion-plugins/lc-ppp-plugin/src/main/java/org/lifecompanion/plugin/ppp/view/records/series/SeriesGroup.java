package org.lifecompanion.plugin.ppp.view.records.series;

import org.lifecompanion.framework.commons.translation.Translation;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum SeriesGroup {
    HETERO("ppp.plugin.view.records.chart.series.groups.hetero.name", Arrays.asList(
            SeriesType.ACTIONS,
            SeriesType.PPP_ASSESSMENTS,
            SeriesType.EVS_ASSESSMENTS), true, true),
    AUTO("ppp.plugin.view.records.chart.series.groups.auto.name", Collections.singletonList(
            SeriesType.AUTO_EVS_ASSESSMENTS), false, true);

    private final String textId;
    private final List<SeriesType> visibleSeries;
    private final boolean leftAxisVisible;
    private final boolean rightAxisVisible;

    SeriesGroup(String textId, List<SeriesType> visibleSeries, boolean leftAxisVisible, boolean rightAxisVisible) {
        this.textId = textId;
        this.visibleSeries = visibleSeries;
        this.leftAxisVisible = leftAxisVisible;
        this.rightAxisVisible = rightAxisVisible;
    }

    public String getText() {
        return Translation.getText(this.textId);
    }

    public List<SeriesType> getVisibleSeries() {
        return this.visibleSeries;
    }

    public boolean isLeftAxisVisible() {
        return this.leftAxisVisible;
    }

    public boolean isRightAxisVisible() {
        return this.rightAxisVisible;
    }
}
