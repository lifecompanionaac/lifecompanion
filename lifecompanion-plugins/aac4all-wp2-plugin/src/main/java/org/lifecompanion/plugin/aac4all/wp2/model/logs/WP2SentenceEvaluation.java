package org.lifecompanion.plugin.aac4all.wp2.model.logs;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WP2SentenceEvaluation {
    private String sentence;
    private Date date;
    private String textEntry;
    private List<WP2Logs> logs = new ArrayList<>();

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTextEntry() {
        return textEntry;
    }

    public void setTextEntry(String textEntry) {
        this.textEntry = textEntry;
    }

    WP2SentenceEvaluation(String sentence, Date date) {
        this.sentence = sentence;
        this.date = date;
    }

    public WP2Logs addAndGetLogs(LocalDateTime timestamp, LogType type, Object data) {
        WP2Logs log = new WP2Logs(timestamp, type, data);
        logs.add(log);
        return log;
    }
}
