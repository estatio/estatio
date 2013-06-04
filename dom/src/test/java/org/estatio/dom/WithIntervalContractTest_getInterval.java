package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Test;

import org.estatio.dom.valuetypes.LocalDateInterval;


public abstract class WithIntervalContractTest_getInterval<T extends WithInterval> {

    private final LocalDate startDate = new LocalDate(1900,1,1);
    private final LocalDate endDateInclusive = new LocalDate(2900,1,1);
    
    @Test
    public void openBound() {
        WithInterval t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(endDateInclusive);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(interval.getStartDate(), is(startDate));
        assertThat(interval.getEndDate(), is(endDateInclusive.plusDays(1)));
    }
    
    protected abstract T newWithInterval();

}
