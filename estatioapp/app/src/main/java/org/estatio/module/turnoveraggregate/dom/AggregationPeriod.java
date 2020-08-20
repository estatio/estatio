package org.estatio.module.turnoveraggregate.dom;

import org.joda.time.LocalDate;

public enum AggregationPeriod {

    P_1M("1M", 1){
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            return firstDayOfMonthFor(periodEndDate);
        }
    },
    P_2M("2M", 2) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus1Month = periodEndDate.minusMonths(1);
            return firstDayOfMonthFor(dateMinus1Month);
        }
    },
    P_3M("3M", 3) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus2Months = periodEndDate.minusMonths(2);
            return firstDayOfMonthFor(dateMinus2Months);
        }
    },
    P_6M("6M", 6) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus5Months = periodEndDate.minusMonths(5);
            return firstDayOfMonthFor(dateMinus5Months);
        }
    },
    P_9M("9M", 9) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus8Months = periodEndDate.minusMonths(8);
            return firstDayOfMonthFor(dateMinus8Months);
        }
    },
    P_12M("12M", 12) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus11Months = periodEndDate.minusMonths(11);
            return firstDayOfMonthFor(dateMinus11Months);
        }
    },
    P_12M_COVID("12M_COVID", 9) {
        @Override public LocalDate periodStartDateFor(final LocalDate periodEndDate) {
            final LocalDate dateMinus11Months = periodEndDate.minusMonths(11);
            return firstDayOfMonthFor(dateMinus11Months);
        }
    };

    private static LocalDate firstDayOfMonthFor(final LocalDate date) {
        return new LocalDate(date.getYear(), date.getMonthOfYear(), 1);
    }

    private final String name;
    private final int minNumberOfTurnovers;

    AggregationPeriod(final String name, final int minNumberOfTurnovers) {
        this.name = name;
        this.minNumberOfTurnovers = minNumberOfTurnovers;
    }

    public String getName(){
        return name;
    };

    public int getMinNumberOfTurnovers(){
        return minNumberOfTurnovers;
    }

    public abstract LocalDate periodStartDateFor(final LocalDate periodEndDate);

}
