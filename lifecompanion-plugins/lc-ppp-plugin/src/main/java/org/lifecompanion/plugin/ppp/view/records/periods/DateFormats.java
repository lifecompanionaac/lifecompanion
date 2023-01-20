package org.lifecompanion.plugin.ppp.view.records.periods;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateFormats {
    public static final DateTimeFormatter SHORT_DATE = DateTimeFormatter.ofPattern("dd/MM/yy");

    public static final DateTimeFormatter SHORT_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    public static final DateTimeFormatter SHORT_HOUR = DateTimeFormatter.ofPattern("H'h'");

    public static final DateTimeFormatter SHORT_WEEK_DAY_DATE = DateTimeFormatter.ofPattern("E dd", Locale.getDefault());

    public static final DateTimeFormatter SHORT_MONTH_DAY_DATE = DateTimeFormatter.ofPattern("dd/MM", Locale.getDefault());

    public static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm", Locale.getDefault());
}
