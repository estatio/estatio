package org.estatio.dom.utils;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class DateTimeUtils {

    public static Period stringToPeriod(String inputString) {
        inputString = inputString.replaceAll(" ", "").toLowerCase();
        PeriodFormatter formatter = new PeriodFormatterBuilder().
                appendYears().appendSuffix("y").
                appendMonths().appendSuffix("m").
                appendDays().appendSuffix("d").
                appendHours().appendSuffix("h").
                appendMinutes().appendSuffix("min").
                toFormatter();
        try {
            Period p = formatter.parsePeriod(inputString);
            return p;
        } catch (Exception e) {
            return null;
        }
    }

    public static String periodToString(Period period) {
        StringBuilder sb = new StringBuilder();
        Period leftOver = period;
        int y = leftOver.getYears();
        if (y > 0) {
            sb.append(String.format("%1$d year(s) ", y));
            leftOver.minusYears(y);
        }
        int m = leftOver.getMonths();
        if (m > 0) {
            sb.append(String.format("%1$d month(s) ", m));
            leftOver.minusMonths(y);
        }
        int d = leftOver.getDays();
        if (d > 0) {
            sb.append(String.format("%1$d day(s) ", d));
            leftOver.minusDays(y);
        }
        return sb.toString().trim();
    }
}
