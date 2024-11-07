package org.lifecompanion.plugin.aac4all.wp2.model.logs;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class WP2Evaluation {
    private LocalDateTime date;
    private String id;
    private int scanPause,scanFirstPause, maxScanBeforeStop;
    private List<WP2KeyboardEvaluation> evaluations = new ArrayList<>();

    public WP2Evaluation(LocalDateTime date, String id,int scanPause,int scanFirstPause,int maxScanBeforeStop) {
        this.date = date;
        this.id = id;
        this.scanPause = scanPause;
        this.scanFirstPause = scanFirstPause;
        this.maxScanBeforeStop = maxScanBeforeStop;
    }

    public List<WP2KeyboardEvaluation> getEvaluations() {
        return evaluations;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getScanPause() {
        return scanPause;
    }

    public int getScanFirstPause() {
        return scanFirstPause;
    }

    public int getMaxScanBeforeStop() {
        return maxScanBeforeStop;
    }
}
