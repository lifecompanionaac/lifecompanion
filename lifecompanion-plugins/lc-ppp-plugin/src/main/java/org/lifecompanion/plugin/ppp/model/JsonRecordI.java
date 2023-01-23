package org.lifecompanion.plugin.ppp.model;

import java.time.ZonedDateTime;

public interface JsonRecordI {
    String getId();

    String getRecordsDirectory();

    Evaluator getEvaluator();

    ZonedDateTime getRecordedAt();

    void setComment(String comment);

    String getComment();
}
