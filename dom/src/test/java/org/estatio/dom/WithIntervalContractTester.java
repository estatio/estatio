package org.estatio.dom;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import org.estatio.dom.valuetypes.LocalDateInterval;


public class WithIntervalContractTester<T extends WithInterval> {

    private Class<T> cls;

    public WithIntervalContractTester(Class<T> cls) {
        this.cls = cls;
    }

    private final LocalDate startDate = new LocalDate(1900,1,1);
    private final LocalDate endDateInclusive = new LocalDate(2900,1,1);
    
    public void test() {
        System.out.println("WithIntervalContractTester: " + cls.getName());

        WithInterval t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(endDateInclusive);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(interval.startDate(), is(startDate));
        assertThat(interval.endDateExcluding(), is(endDateInclusive.plusDays(1)));
    }
    
    private T newWithInterval() {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
