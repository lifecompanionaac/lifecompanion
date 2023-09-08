package org.lifecompanion.plugin.ppp.services;

import javafx.scene.Scene;
import org.lifecompanion.controller.metrics.SessionStatsController;
import org.lifecompanion.controller.systemvk.SystemVirtualKeyboardController;
import org.lifecompanion.framework.utils.FluentHashMap;
import org.lifecompanion.model.api.configurationcomponent.LCConfigurationI;
import org.lifecompanion.model.impl.notification.LCNotification;
import org.lifecompanion.plugin.ppp.model.ActionRecord;
import org.lifecompanion.plugin.ppp.model.AssessmentRecord;
import org.lifecompanion.plugin.ppp.model.AssessmentType;
import org.lifecompanion.plugin.ppp.model.JsonRecordI;
import org.lifecompanion.plugin.ppp.view.records.RecordsStage;
import org.lifecompanion.plugin.ppp.view.records.RecordsView;
import org.lifecompanion.ui.notification.LCNotificationController;
import org.lifecompanion.util.javafx.FXThreadUtils;
import org.lifecompanion.util.javafx.StageUtils;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.List;

public enum RecordsService {
    INSTANCE;

    public void showRecordStage(LCConfigurationI config) {
        FXThreadUtils.runOnFXThread(() -> {
            RecordsStage stage = new RecordsStage(StageUtils.getEditOrUseStageVisible());
            final Scene scene = new Scene(new RecordsView(config));
            SystemVirtualKeyboardController.INSTANCE.registerScene(scene);
            SessionStatsController.INSTANCE.registerScene(scene);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.setOnHidden(s -> {
                SystemVirtualKeyboardController.INSTANCE.unregisterScene(scene);
                SessionStatsController.INSTANCE.unregisterScene(scene);
            });
            stage.show();
        });
    }

    public void save(LCConfigurationI config, JsonRecordI record) {
        this.save(config, record, true);
    }

    public void save(LCConfigurationI config, JsonRecordI record, boolean showNotification) {
        FilesService.INSTANCE.jsonSave(record, this.computeRecordPath(config, record));
        if (showNotification) {
            LCNotificationController.INSTANCE.showNotification(LCNotification.createInfo("ppp.plugin.record.save.info.notification"));
        }
    }

    public void delete(LCConfigurationI config, JsonRecordI record) {
        FilesService.INSTANCE.jsonDelete(this.computeRecordPath(config, record));
        if (record instanceof AssessmentRecord) {
            SessionStatsController.INSTANCE.pushEvent("ppp.assessment.deleted",
                    FluentHashMap.mapStrObj("id", record.getId())
            );
        }
    }

    private String computeRecordPath(LCConfigurationI config, JsonRecordI record) {
        return FilesService.INSTANCE.getPluginDirectoryPath(config) + record.getRecordsDirectory() + File.separator
                + record.getRecordedAt().toEpochSecond() + ".json";
    }

    public List<ActionRecord> loadActions(String recordsDirectory, ZonedDateTime start, ZonedDateTime end) {
        return this.load(recordsDirectory + File.separator + ActionRecord.DIRECTORY, ActionRecord.class, start, end);
    }

    public List<AssessmentRecord> loadAssessments(String recordsDirectory, AssessmentType assessmentType, ZonedDateTime start, ZonedDateTime end) {
        List<AssessmentRecord> assessmentRecords = this.load(recordsDirectory + File.separator + assessmentType.getDirectory(), AssessmentRecord.class, start, end);

        for (AssessmentRecord assessmentRecord : assessmentRecords) {
            assessmentRecord.setAssessmentType(assessmentType);
        }

        return assessmentRecords;
    }

    public <T extends JsonRecordI> List<T> load(String directory, Class<T> classOfT, ZonedDateTime start, ZonedDateTime end) {
        final long fromEpoch = start.toEpochSecond();
        final long toEpoch = end.toEpochSecond();

        return FilesService.INSTANCE.jsonLoadMany(classOfT, directory, name -> {
            try {
                long epochSecond = Long.parseLong(name.replace(".json", ""));

                return epochSecond >= fromEpoch && epochSecond <= toEpoch;
            } catch (NumberFormatException exception) {
                return false;
            }
        });
    }
}
