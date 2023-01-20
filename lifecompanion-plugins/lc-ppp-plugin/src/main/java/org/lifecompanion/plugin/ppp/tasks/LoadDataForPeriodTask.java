package org.lifecompanion.plugin.ppp.tasks;

import org.lifecompanion.plugin.ppp.model.AssessmentType;
import org.lifecompanion.plugin.ppp.services.RecordsService;
import org.lifecompanion.plugin.ppp.view.records.periods.Period;
import org.lifecompanion.util.model.LCTask;

import java.io.File;

public class LoadDataForPeriodTask extends LCTask<Void> {
    private final File dataDirectory;
    private final Period period;

    public LoadDataForPeriodTask(final File dataDirectory, Period period) {
        super("ppp.plugin.task.data.load.directory.title");
        this.dataDirectory = dataDirectory;
        this.period = period;
    }

    @Override
    protected Void call() throws Exception {
        this.period.setActions(RecordsService.INSTANCE.loadActions(
                this.dataDirectory.getAbsolutePath(), this.period.getStart(), this.period.getEnd()));
        this.period.setPppAssessments(RecordsService.INSTANCE.loadAssessments(
                this.dataDirectory.getAbsolutePath(), AssessmentType.PPP,
                this.period.getStart(), this.period.getEnd()));
        this.period.setEvsAssessments(RecordsService.INSTANCE.loadAssessments(
                this.dataDirectory.getAbsolutePath(), AssessmentType.EVS,
                this.period.getStart(), this.period.getEnd()));
        this.period.setAutoEvsAssessments(RecordsService.INSTANCE.loadAssessments(
                this.dataDirectory.getAbsolutePath(), AssessmentType.AUTO_EVS,
                this.period.getStart(), this.period.getEnd()));

        return null;
    }
}
