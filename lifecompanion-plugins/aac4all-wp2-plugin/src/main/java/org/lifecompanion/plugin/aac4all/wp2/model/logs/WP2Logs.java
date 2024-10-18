package org.lifecompanion.plugin.aac4all.wp2.model.logs;



import java.time.LocalDateTime;
import java.util.Date;

public class WP2Logs  {
    private LocalDateTime timestamp;
    private LogType type;
    private Object data;

    public WP2Logs(LocalDateTime timestamp, LogType type, Object data) {
        this.timestamp = timestamp;
        this.type = type;
        this.data = data;
    }
}
