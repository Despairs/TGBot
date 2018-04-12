package com.despairs.bot.utils;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Calendar;
import java.util.Locale;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;

/**
 * @author EKovtunenko
 */
public class DateUtils {

    private static final String PERIOD_PATTERN = "LLLL yyyy";
    private static final Locale LOCALE = new Locale("ru");

    private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
            .appendPattern(PERIOD_PATTERN)
            .parseDefaulting(DAY_OF_MONTH, 1)
            .toFormatter(LOCALE);

    public static String toPeriod(Calendar cal) {
        return new SimpleDateFormat(PERIOD_PATTERN, LOCALE).format(cal.getTime());
    }

    public static LocalDate fromPeriod(String period) {
        return LocalDate.parse(period, FORMATTER);
    }
}
