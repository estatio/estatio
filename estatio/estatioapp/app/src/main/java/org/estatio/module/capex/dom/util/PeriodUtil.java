package org.estatio.module.capex.dom.util;

import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import org.incode.module.base.dom.valuetypes.LocalDateInterval;

public class PeriodUtil {

    public static Pattern financialYearPattern = Pattern.compile("^F\\d{4}.*");
    public static Pattern yearPattern = Pattern.compile("^\\d{4}.*");

    public static LocalDate startDateFromPeriod(final String period) {
        LocalDateInterval localDateInterval = fromPeriod(period);
        return localDateInterval != null ? localDateInterval.startDate() : null;
    }

    public static LocalDate endDateFromPeriod(final String period) {
        LocalDateInterval localDateInterval = fromPeriod(period);
        return localDateInterval != null ? localDateInterval.endDate() : null;
    }

    public static LocalDateInterval fromPeriod(final String period) {
        return period != null
                ? PeriodUtil.yearFromPeriod(period)
                : null;
    }

    public static LocalDateInterval yearFromPeriod(final String period){
        if (period == null) {
            return new LocalDateInterval(); // Interval with open start and end date
        }
        LocalDate startDate = null;
        LocalDate endDate = null;
        if (financialYearPattern.matcher(period).matches()){
            Integer year = Integer.valueOf(period.substring(1,5));
            // F2021 has 6 months; After 2021 financial year equals calendar year
            if (year<=2021) {
                startDate = new LocalDate(year - 1, 7, 1);
                // ECP-1335 : the users may want to extend again to 31-12-2021 but for the moment we extend to 30-6-2021, so a 'regular' financial year
//                endDate = year == 2021 ? new LocalDate(year - 1, 12, 31) : new LocalDate(year, 6, 30);
                endDate = new LocalDate(year, 6, 30);
            } else {
                //
            }
        }
        if (yearPattern.matcher(period).matches()){
            Integer year = Integer.valueOf(period.substring(0,4));
            startDate = new LocalDate(year, 1, 1);
            endDate = new LocalDate(year, 12, 31);
        }
        return new LocalDateInterval(startDate, endDate);
    }

    public static String periodFromInterval(@NotNull final LocalDateInterval interval){
        if(interval.isInfinite()) {
            return null;
        }
        LocalDate endDate = interval.endDate();
        if (matchesCalendarYear(interval)){
            return String.valueOf(endDate.getYear());
        }
        if (matchesFinancialYear(interval)){
//            if (endDate.equals(new LocalDate(2020,12,31))) return "F2021"; // this is the only 6 months financial year
            return "F".concat(String.valueOf(endDate.getYear()));
        }
        return null;
    }

    public static boolean isValidPeriod(final String period){
        if (period!=null && !period.equals("") && !yearFromPeriod(period).equals(new LocalDateInterval(null, null))){
            return true;
        }
        return false;
    }

    public static String reasonInvalidPeriod(final String period){
        if (period.equals("F2022")) return "Financial periods are not supported after F2021";
        return isValidPeriod(period) ? null : "Not a valid period; use four digits of the year with optional prefix F for a financial year (for example: F2017)";
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
        if (date.getYear()>2020) return false; // the last financial year is starts at 2020-7-1
        return date.getDayOfMonth() == 1 && date.getMonthOfYear() == 7 ? true : false;
    }

    private static boolean matchesEndFinancialYear(final LocalDate date){
        if (date.equals(new LocalDate(2021,6,30))) return true; // the last financial year end date that we can recognize as such ...
        if (date.getYear()>2021) return false;
        return date.getDayOfMonth() == 30 && date.getMonthOfYear() == 6 ? true : false;
    }

}
