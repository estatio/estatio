package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;
import org.junit.Assume;
import org.junit.Test;

import org.estatio.dom.valuetypes.LocalDateInterval;


public class WithIntervalContractTest_getInterval<T extends WithInterval> {

    private Class<T> cls;

    /**
     * For {@link WithDescriptionContractAutoTest_compareTo auto-testing}.
     */
    public WithIntervalContractTest_getInterval<T> with(Class<T> cls) {
        this.cls = cls;
        return this;
    }

    private final LocalDate startDate = new LocalDate(1900,1,1);
    private final LocalDate endDateInclusive = new LocalDate(2900,1,1);
    
    @Test
    public void closedInterval() {
        Assume.assumeThat(cls, is(not(nullValue())));
        WithInterval t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(endDateInclusive);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(interval.getStartDate(), is(startDate));
        assertThat(interval.getEndDate(), is(endDateInclusive.plusDays(1)));
    }
    
    /**
     * Manual tests should override this method.
     */
    protected T newWithInterval() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
