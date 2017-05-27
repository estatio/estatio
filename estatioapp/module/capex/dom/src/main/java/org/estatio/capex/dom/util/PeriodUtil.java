package org.estatio.capex.dom.util;

import java.util.regex.Pattern;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

public class PeriodUtil {

    private static Pattern financialYearPattern = Pattern.compile("^F\\d{4}.*");
    private static Pattern yearPattern = Pattern.compile("^\\d{4}.*");

    @Nullable
    public static LocalDateInterval yearFromPeriod(final String period){
        if (period == null) {
            return new LocalDateInterval(); // Interval with open start and end date
        }
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (financialYearPattern.matcher(period).matches()){
            Integer year = Integer.valueOf(period.substring(1,5));
            startDate = new LocalDate(year-1, 07, 01);
            endDate = new LocalDate(year, 06, 30);
        }
        if (yearPattern.matcher(period).matches()){
            Integer year = Integer.valueOf(period.substring(0,4));
            startDate = new LocalDate(year, 01, 01);
            endDate = new LocalDate(year, 12, 31);
        }
        return new LocalDateInterval(startDate, endDate);
    }

    public static String periodFromInterval(@NotNull final LocalDateInterval interval){
        LocalDate endDate = interval.endDate();
        if (matchesCalendarYear(interval)){
            return String.valueOf(endDate.getYear());
        }
        if (matchesFinancialYear(interval)){
            return "F".concat(String.valueOf(endDate.getYear()));
        }
        return null;
    }

    public static boolean isValidPeriod(final String period){
        if (period!=null && !yearFromPeriod(period).equals(new LocalDateInterval(null, null))){
            return true;
        }
        return false;
    }


    private static boolean matchesCalendarYear(final LocalDateInterval interval){
        if (interval.isValid()){
            return matchesStartCalendarYear(interval.startDate()) && matchesEndCalendarYear(interval.endDate());
        }
        return false;
    }

    private static boolean matchesFinancialYear(final LocalDateInterval interval){
        if (interval.isValid()){
            return matchesStartFinancialYear(interval.startDate()) && matchesEndFinancialYear(interval.endDate());
        }
        return false;
    }

    private static boolean matchesStartCalendarYear(final LocalDate date){
        return date.getDayOfMonth() == 1 && date.getMonthOfYear() == 1 ? true : false;
    }

    private static boolean matchesEndCalendarYear(final LocalDate date){
        return date.getDayOfMonth() == 31 && date.getMonthOfYear() == 12 ? true : false;
    }

    private static boolean matchesStartFinancialYear(final LocalDate date){
        return date.getDayOfMonth() == 1 && date.getMonthOfYear() == 7 ? true : false;
    }

    private static boolean matchesEndFinancialYear(final LocalDate date){
        return date.getDayOfMonth() == 30 && date.getMonthOfYear() == 6 ? true : false;
    }

}
