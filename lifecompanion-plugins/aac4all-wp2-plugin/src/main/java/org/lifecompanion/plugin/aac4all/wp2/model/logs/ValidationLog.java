package org.lifecompanion.plugin.aac4all.wp2.model.logs;

public class ValidationLog {
    private String row;
    private int position;

    public ValidationLog(String row, int position) {
        this.row = row;
        this.position = position;
    }
}
