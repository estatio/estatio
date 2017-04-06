package org.estatio.capex.dom.time;

import lombok.Getter;

public enum CalendarType {
    NATURAL(1),
    FINANCIAL(7);

    /**
     * Which month in the year is the first month of this calendar.
     *
     * <p>
     *     For example, our financial calendar starts in July each year, so that is the 7th month of the year.
     * </p>
     */
    @Getter
    private final int startMonth;

    CalendarType(final int startMonth) {

        this.startMonth = startMonth;
    }
}
