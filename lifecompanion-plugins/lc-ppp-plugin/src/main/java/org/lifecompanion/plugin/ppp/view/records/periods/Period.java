package org.lifecompanion.plugin.ppp.view.records.periods;

import org.lifecompanion.framework.commons.utils.lang.CollectionUtils;
import org.lifecompanion.plugin.ppp.model.ActionRecord;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

public class Period {
    private final ZonedDateTime start;
    private final ZonedDateTime end;
    private List<ActionRecord> actions;
    private List<AssessmentRecord> pppAssessments;
    private List<AssessmentRecord> evsAssessments;
    private List<AssessmentRecord> autoEvsAssessments;

    public Period(ZonedDateTime start, ZonedDateTime end) {
        this.start = start.with(LocalTime.of(0, 0, 0));
        this.end = end.with(LocalTime.of(0, 0, 0));
    }

    public ZonedDateTime getStart() {
        return this.start;
    }

    public ZonedDateTime getEnd() {
        return this.end;
    }

    public List<ActionRecord> getActions() {
        return this.actions;
    }

    public void setActions(List<ActionRecord> actions) {
        this.actions = actions;
    }

    public List<AssessmentRecord> getPppAssessments() {
        return this.pppAssessments;
    }

    public void setPppAssessments(List<AssessmentRecord> pppAssessments) {
        this.pppAssessments = pppAssessments;
    }

    public List<AssessmentRecord> getEvsAssessments() {
        return evsAssessments;
    }

    public void setEvsAssessments(List<AssessmentRecord> evsAssessments) {
        this.evsAssessments = evsAssessments;
    }

    public List<AssessmentRecord> getAutoEvsAssessments() {
        return autoEvsAssessments;
    }

    public void setAutoEvsAssessments(List<AssessmentRecord> autoEvsAssessments) {
        this.autoEvsAssessments = autoEvsAssessments;
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(actions) && CollectionUtils.isEmpty(pppAssessments) && CollectionUtils.isEmpty(evsAssessments);
    }

}
