package org.lifecompanion.plugin.ppp.view.records.periods;

import org.lifecompanion.framework.commons.translation.Translation;

import java.time.*;
import java.util.function.Function;

public enum PeriodType {
    DAY(true, "ppp.plugin.view.records.period_type.day.", Duration.ofDays(1),
            ZonedDateTime.now().plusDays(1), 60 * 60,
            ZonedDateTime::toEpochSecond,
            t -> DateFormats.SHORT_HOUR.format(
                    ZonedDateTime.ofInstant(Instant.ofEpochSecond(t), ZoneId.systemDefault())),
            DateFormats.SHORT_DATE::format),
    WEEK(false, "ppp.plugin.view.records.period_type.week.", Duration.ofDays(7),
            ZonedDateTime.now().with(DayOfWeek.SUNDAY).plusDays(1), 60 * 60 * 24,
            ZonedDateTime::toEpochSecond,
            t -> DateFormats.SHORT_WEEK_DAY_DATE.format(
                    ZonedDateTime.ofInstant(Instant.ofEpochSecond(t), ZoneId.systemDefault())),
            DateFormats.SHORT_DATE::format),
    MONTH(false, "ppp.plugin.view.records.period_type.month.", Duration.ofDays(30),
            ZonedDateTime.now().plusDays(1), 1,
            d -> d.toLocalDate().toEpochDay(),
            t -> DateFormats.SHORT_MONTH_DAY_DATE.format(LocalDate.ofEpochDay(t)),
            DateFormats.SHORT_DATE::format);

    private final boolean isDetailed;
    private final String translationsNamespace;
    private final Duration duration;
    private final ZonedDateTime defaultEnd;
    private final int tickUnit;
    private final Function<ZonedDateTime, Long> realValueConverter;
    private final Function<Long, String> axisValueFormatter;
    private final Function<ZonedDateTime, String> titleValueFormatter;

    PeriodType(boolean isDetailed, String translationsNamespace,
               Duration duration, ZonedDateTime defaultEnd, int tickUnit,
               Function<ZonedDateTime, Long> realValueConverter,
               Function<Long, String> axisValueFormatter,
               Function<ZonedDateTime, String> titleValueFormatter) {
        this.isDetailed = isDetailed;
        this.translationsNamespace = translationsNamespace;
        this.duration = duration;
        this.defaultEnd = defaultEnd;
        this.tickUnit = tickUnit;
        this.realValueConverter = realValueConverter;
        this.axisValueFormatter = axisValueFormatter;
        this.titleValueFormatter = titleValueFormatter;
    }

    public boolean isDetailed() {
        return isDetailed;
    }

    public Period getDefaultPeriod() {
        return this.getPeriodForEnd(this.defaultEnd);
    }

    public Period getNext(Period period) {
        return this.getPeriodForEnd(period.getEnd().plus(this.duration));
    }

    public Period getPrevious(Period period) {
        return this.getPeriodForEnd(period.getEnd().minus(this.duration));
    }

    public Period getPeriodForEnd(ZonedDateTime end) {
        return new Period(end.minus(this.duration), end);
    }

    public String getName() {
        return Translation.getText(this.translationsNamespace + "name");
    }

    public int getTickUnit() {
        return this.tickUnit;
    }

    public long getRealValue(ZonedDateTime dateTime) {
        return this.realValueConverter.apply(dateTime);
    }

    public String getAxisValue(Long dateTime) {
        return this.axisValueFormatter.apply(dateTime);
    }

    public String getTitleValue(ZonedDateTime start, ZonedDateTime end) {
        return Translation.getText(this.translationsNamespace + "title",
                this.titleValueFormatter.apply(start), this.titleValueFormatter.apply(end));
    }
}
