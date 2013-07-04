/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.joda.time.LocalDate;

import org.estatio.dom.agreement.AgreementForTesting;
import org.estatio.dom.valuetypes.LocalDateInterval;


public class WithIntervalContractTester<T extends WithInterval<?>> {

    public static class WIInstantiator<T extends WithInterval<?>> {

        private final Class<T> wiClass;

        public WIInstantiator(Class<T> wiClass) {
            this.wiClass = wiClass;
        }

        /**
         * Instantiate the {@link WithInterval}, with NO {@link WithInterval#getParentWithInterval() parent}.
         */
        public T newWithInterval() throws Exception {
            return wiClass.newInstance();
        }

        /**
         * Instantiate the {@link WithInterval}, with a {@link WithInterval#getParentWithInterval() parent}.
         * 
         * <p>
         * If the {@link WithInterval} does not support having a parent, then just return <tt>null</tt>. 
         */
        public T newWithIntervalWithParent() throws Exception {
            return null;
        }

        /**
         * The name of the concrete class that is instantiated.
         */
        public String getClassName() {
            return wiClass.getName();
        }
    }

    private WIInstantiator<T> instantiator;

    public WithIntervalContractTester(final WIInstantiator<T> instantiator) {
        this.instantiator = instantiator;
        System.out.println("WithIntervalContractTester: " + instantiator.getClassName());
    }

    private final LocalDate startDate = new LocalDate(2013,4,1);
    private final LocalDate endDate = new LocalDate(2014,5,2);
    
    private final LocalDate parentStartDate = new LocalDate(2012,3,31);
    private final LocalDate parentEndDate = new LocalDate(2015,6,3);

    public void testAll() {
        whenHasStartDateAndHasEndDate();
        whenHasStartDateButNoEndDateWithNoParent();
        whenNoStartDateButHasEndDateWithNoParent();
        whenNoStartDateAndNoEndDateWithNoParent();
        
        whenHasStartDateButNoEndDateWithParentHasEndDate();
        whenHasStartDateButNoEndDateWithParentNoEndDate();
        whenNoStartDateButHasEndDateWithParentHasStartDate();
        whenNoStartDateButHasEndDateWithParentNoStartDate();
        
        whenNoStartDateAndNoEndDateWithParentNoStartDateAndNoEndDate();
        whenNoStartDateAndNoEndDateWithParentHasStartDateButNoEndDate();
        whenNoStartDateAndNoEndDateWithParentNoStartDateButHasEndDate();
        whenNoStartDateAndNoEndDateWithParentHasStartDateAndHasEndDate();
    }

    public void whenHasStartDateAndHasEndDate() {

        WithInterval<?> t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(endDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has start date", interval.startDate(), is(startDate));
        assertThat(instantiator.getClassName() + ": has end date", interval.endDateExcluding(), is(endDate.plusDays(1)));
    }
    
    public void whenHasStartDateButNoEndDateWithNoParent() {

        WithInterval<?> t = newWithInterval();
        
        t.setStartDate(startDate);
        t.setEndDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has start date", interval.startDate(), is(startDate));
        assertThat(instantiator.getClassName() + ": no end date and no parent", interval.endDateExcluding(), is(nullValue()));
    }
    
    public void whenNoStartDateButHasEndDateWithNoParent() {

        WithInterval<?> t = newWithInterval();
        
        t.setStartDate(null);
        t.setEndDate(endDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date and no parent", interval.startDate(), is(nullValue()));
        assertThat(instantiator.getClassName() + ": has end date", interval.endDateExcluding(), is(endDate.plusDays(1)));
    }
    
    public void whenNoStartDateAndNoEndDateWithNoParent() {
        
        WithInterval<?> t = newWithInterval();
        
        t.setStartDate(null);
        t.setEndDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date and no parent", interval.startDate(), is(nullValue()));
        assertThat(instantiator.getClassName() + ": no end date and no parent", interval.endDateExcluding(), is(nullValue()));
    }
    
    public void whenHasStartDateButNoEndDateWithParentHasEndDate() {

        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(startDate);
        t.setEndDate(null);
        
        t.getParentWithInterval().setEndDate(parentEndDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has start date", interval.startDate(), is(startDate));
        assertThat(instantiator.getClassName() + ": no end date but parent has end date", interval.endDateExcluding(), is(parentEndDate.plusDays(1)));
    }
    
    public void whenHasStartDateButNoEndDateWithParentNoEndDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(startDate);
        t.setEndDate(null);
        
        t.getParentWithInterval().setEndDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has start date", interval.startDate(), is(startDate));
        assertThat(instantiator.getClassName() + ": no end date and parent has no end date", interval.endDateExcluding(), is(nullValue()));
    }
    
    public void whenNoStartDateButHasEndDateWithParentHasStartDate() {

        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }

        t.setStartDate(null);
        t.setEndDate(endDate);
        
        t.getParentWithInterval().setStartDate(parentStartDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has no start date but parent has start date", interval.startDate(), is(parentStartDate));
        assertThat(instantiator.getClassName() + ": has end date", interval.endDateExcluding(), is(endDate.plusDays(1)));
    }
    
    public void whenNoStartDateButHasEndDateWithParentNoStartDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(null);
        t.setEndDate(endDate);
        
        t.getParentWithInterval().setStartDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date and parent has no start date", interval.startDate(), is(nullValue()));
        assertThat(instantiator.getClassName() + ": has end date", interval.endDateExcluding(), is(endDate.plusDays(1)));
    }
    
    public void whenNoStartDateAndNoEndDateWithParentNoStartDateAndNoEndDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(null);
        t.setEndDate(null);
        
        t.getParentWithInterval().setStartDate(null);
        t.getParentWithInterval().setEndDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date and parent has no start date", interval.startDate(), is(nullValue()));
        assertThat(instantiator.getClassName() + ": no end date and parent has no end date", interval.endDateExcluding(), is(nullValue()));
    }
    
    public void whenNoStartDateAndNoEndDateWithParentHasStartDateButNoEndDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(null);
        t.setEndDate(null);
        
        t.getParentWithInterval().setStartDate(parentStartDate);
        t.getParentWithInterval().setEndDate(null);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date but parent has start date", interval.startDate(), is(parentStartDate));
        assertThat(instantiator.getClassName() + ": has no end date", interval.endDateExcluding(), is(nullValue()));
    }
    
    public void whenNoStartDateAndNoEndDateWithParentNoStartDateButHasEndDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(null);
        t.setEndDate(null);
        
        t.getParentWithInterval().setStartDate(null);
        t.getParentWithInterval().setEndDate(parentEndDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": has no start date and parent has no start date", interval.startDate(), is(nullValue()));
        assertThat(instantiator.getClassName() + ": has no end date but parent has end date", interval.endDateExcluding(), is(parentEndDate.plusDays(1)));
    }
    
    public void whenNoStartDateAndNoEndDateWithParentHasStartDateAndHasEndDate() {
        
        WithInterval<?> t = newWithIntervalAndHasParent();
        if(t == null) {
            return;
        }
        
        t.setStartDate(null);
        t.setEndDate(null);
        
        t.getParentWithInterval().setStartDate(parentStartDate);
        t.getParentWithInterval().setEndDate(parentEndDate);
        
        final LocalDateInterval interval = t.getInterval();
        assertThat(instantiator.getClassName() + ": no start date but parent has start date", interval.startDate(), is(parentStartDate));
        assertThat(instantiator.getClassName() + ": no start date but parent has end date", interval.endDateExcluding(), is(parentEndDate.plusDays(1)));
    }
    
    private T newWithInterval() {
        try {
            final T wi = instantiator.newWithInterval();
            assertThat(wi.getParentWithInterval(), is(nullValue()));
            return wi;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private T newWithIntervalAndHasParent() {
        try {
            final T wi = instantiator.newWithIntervalWithParent();
            if(wi != null) {
                assertThat(wi.toString(), wi.getParentWithInterval(), is(not(nullValue())));
            }
            return wi;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
