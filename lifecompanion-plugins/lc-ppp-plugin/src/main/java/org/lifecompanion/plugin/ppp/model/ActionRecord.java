package org.lifecompanion.plugin.ppp.model;

import java.io.File;

public class ActionRecord extends AbstractRecord {
    public static final String DIRECTORY = AbstractRecord.DIRECTORY + File.separator + "actions";

    private final Action action;

    public ActionRecord(Evaluator evaluator, Action action) {
        super(evaluator);
        this.action = action;
    }

    @Override
    public String getRecordsDirectory() {
        return DIRECTORY;
    }

    public Action getAction() {
        return action;
    }
}
