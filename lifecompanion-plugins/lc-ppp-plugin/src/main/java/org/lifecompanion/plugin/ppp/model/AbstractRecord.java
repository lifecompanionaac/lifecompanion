package org.lifecompanion.plugin.ppp.model;

import java.time.ZonedDateTime;
import java.util.UUID;

public abstract class AbstractRecord implements JsonRecordI {
    public static final String DIRECTORY = "records";

    protected String id;

    protected Evaluator evaluator;

    protected ZonedDateTime recordedAt;

    protected String comment;

    AbstractRecord(Evaluator evaluator) {
        this.id = UUID.randomUUID().toString();
        this.evaluator = evaluator;
        this.recordedAt = ZonedDateTime.now();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public ZonedDateTime getRecordedAt() {
        return this.recordedAt;
    }

    @Override
    public Evaluator getEvaluator() {
        return this.evaluator;
    }

    public void setEvaluator(Evaluator evaluator) {
        this.evaluator = evaluator;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
