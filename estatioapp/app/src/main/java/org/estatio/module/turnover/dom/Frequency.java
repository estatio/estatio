package org.estatio.module.turnover.dom;

import org.joda.time.LocalDate;

public enum Frequency {
    DAILY{
        @Override public boolean hasStartDate(final LocalDate date) {
            return date.getDayOfWeek()<=5;
        }
    },
    MONTHLY{
        @Override public boolean hasStartDate(final LocalDate date) {
            return date.getDayOfMonth()==1;
        }
    },
    YEARLY{
        @Override public boolean hasStartDate(final LocalDate date) {
            return date.getMonthOfYear()==1 && date.getDayOfMonth()==1;
        }
    };

    abstract public boolean hasStartDate(final LocalDate date);
}
