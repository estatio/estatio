/**
 *  Licensed to the Apache Software Foundation (ASF) under one or more
 *  contributor license agreements.  See the NOTICE file distributed with
 *  this work for additional information regarding copyright ownership.
 *  The ASF licenses this file to You under the Apache License, Version 2.0
 *  (the "License"); you may not use this file except in compliance with
 *  the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.estatio.dom;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.util.SortedSet;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.estatio.dom.WithIntervalContiguous.Factory;
import org.estatio.dom.valuetypes.LocalDateInterval;

public class WithIntervalContiguous_HelperTest {
    
    static class SomeDomainObject implements WithIntervalContiguous<SomeDomainObject> {

        public SomeDomainObject() {
        }
        
        public SomeDomainObject(final LocalDate startDate, final LocalDate endDate) {
            setStartDate(startDate);
            this.endDate = endDate;
        }


        // //////////////////////////////////////

        private LocalDate startDate;

        @Override
        public LocalDate getStartDate() {
            return startDate;
        }

        @Override
        public void setStartDate(LocalDate startDate) {
            this.startDate = startDate;
        }

        
        private LocalDate endDate;
        @Override
        public LocalDate getEndDate() {
            return endDate;
        }

        @Override
        public void setEndDate(LocalDate endDate) {
            this.endDate = endDate;
        }

        // //////////////////////////////////////

        
        @Override
        public WithInterval<?> getWithIntervalParent() {
            return null;
        }

        @Override
        public LocalDate getEffectiveStartDate() {
            return null;
        }

        @Override
        public LocalDate getEffectiveEndDate() {
            return null;
        }

        @Override
        public LocalDateInterval getInterval() {
            return null;
        }

        @Override
        public boolean isCurrent() {
            return false;
        }

        @Override
        public SomeDomainObject changeDates(LocalDate startDate, LocalDate endDate) {
            return null;
        }

        @Override
        public LocalDate default0ChangeDates() {
            return null;
        }

        @Override
        public LocalDate default1ChangeDates() {
            return null;
        }

        @Override
        public String validateChangeDates(LocalDate startDate, LocalDate endDate) {
            return null;
        }

        public SomeDomainObject predecessor;
        @Override
        public SomeDomainObject getPredecessor() {
            return predecessor;
        }

        public SomeDomainObject successor;
        @Override
        public SomeDomainObject getSuccessor() {
            return successor;
        }

        @Override
        public SortedSet<SomeDomainObject> getTimeline() {
            return null;
        }
        
        @Override
        public int compareTo(SomeDomainObject o) {
            return 0;
        }
    }

    private LocalDate d20130401;
    private LocalDate d20140531;
    private LocalDate d20150615;
    private LocalDate d20160801;
    
    private Factory<SomeDomainObject> factory;
    
    private SomeDomainObject target;
    private WithIntervalContiguous.Helper<SomeDomainObject> helper;
    
    // //////////////////////////////////////

    @Before
    public void setUp() throws Exception {
        
        d20130401 = new LocalDate(2013,4,1);
        d20140531 = new LocalDate(2014,5,31);
        d20150615 = new LocalDate(2015,6,15);
        d20160801 = new LocalDate(2016,8,1);
        
        factory = new Factory<WithIntervalContiguous_HelperTest.SomeDomainObject>() {
            
            @Override
            public SomeDomainObject newRole(LocalDate startDate, LocalDate endDate) {
                return new SomeDomainObject(startDate, endDate);
            }
        };
        
        target = new SomeDomainObject();
        helper = new WithIntervalContiguous.Helper<SomeDomainObject>(target);
    }
    
    // //////////////////////////////////////

    @Test
    public void succeededBy_whenNoSuccessor() {
        
        target.setStartDate(d20130401);
        target.setEndDate(d20140531);
        final SomeDomainObject successor = helper.succeededBy(d20130401, d20140531, factory);
        
        assertThat(target.getEndDate(), is(d20130401.minusDays(1)));
        assertThat(successor.getStartDate(), is(d20130401));
        assertThat(successor.getEndDate(), is(d20140531));
    }
    
    @Test
    public void succeededBy_whenSuccessorExists() {

        // given
        final SomeDomainObject existingSuccessor = helper.succeededBy(d20130401, d20160801, factory);
        target.successor = existingSuccessor;
        target.setEndDate(d20130401.minusDays(1));

        // when
        final SomeDomainObject newSuccessor = helper.succeededBy(d20130401, d20140531, factory);
        
        // then
        assertThat(target.getEndDate(), is(d20130401.minusDays(1)));
        
        assertThat(newSuccessor.getStartDate(), is(d20130401));
        assertThat(newSuccessor.getEndDate(), is(d20140531));
        
        assertThat(existingSuccessor.getStartDate(), is(d20140531.plusDays(1)));
        assertThat(existingSuccessor.getEndDate(), is(d20160801));
    }

    // //////////////////////////////////////

    @Test
    public void validateSucceededBy_startDate_after_endDate() {
        target.setStartDate(d20130401);
        target.setEndDate(d20140531);
        
        String reason = helper.validateSucceededBy(d20140531, d20140531.minusDays(1));
        
        assertThat(reason, is("End date cannot be earlier than start date"));
    }

    @Test
    public void validateSucceededBy_startDate_not_before_existing_startDate() {
        target.setStartDate(d20130401);

        String reason = helper.validateSucceededBy(d20130401, d20140531);
        assertThat(reason, is("Successor must start after existing"));
    }

    @Test
    public void validateSucceededBy_withStartDate_noSuccessor_happyCase() {
        target.setStartDate(d20130401);

        String reason = helper.validateSucceededBy(d20130401.plusDays(1), d20140531);
        assertThat(reason, is(nullValue()));
    }

    @Test
    public void validateSucceededBy_noStartDate_noSuccessor_happyCase() {
        target.setStartDate(null);
        String reason = helper.validateSucceededBy(d20130401, d20140531);
        assertThat(reason, is(nullValue()));
    }
    
    @Test
    public void validateSucceededBy_whenSuccessor_noEndDate_provided() {
        final SomeDomainObject existingSuccessor = helper.succeededBy(d20130401, d20160801, factory);
        target.successor = existingSuccessor;

        String reason = helper.validateSucceededBy(d20140531, null);
        assertThat(reason, is("An end date is required because a successor already exists"));
    }
    
    @Test
    public void validateSucceededBy_whenSuccessor_endDate_not_prior_to_existing_successors_endDate() {
        final SomeDomainObject existingSuccessor = helper.succeededBy(d20130401, d20160801, factory);
        target.successor = existingSuccessor;
        
        String reason = helper.validateSucceededBy(d20140531, d20160801);
        assertThat(reason, is("Successor must end prior to existing successor"));
    }
    
    @Test
    public void validateSucceededBy_whenSuccessor_happyCase() {
        final SomeDomainObject existingSuccessor = helper.succeededBy(d20130401, d20160801, factory);
        target.successor = existingSuccessor;
        
        String reason = helper.validateSucceededBy(d20140531, d20160801.minusDays(1));
        assertThat(reason, is(nullValue()));
    }
    
    @Test
    public void default1SucceededBy_whenNoEndDate() {
        LocalDate dflt = helper.default1SucceededBy();
        assertThat(dflt, is(nullValue()));
    }

    // //////////////////////////////////////

    @Test
    public void default1SucceededBy_whenEndDate() {
        target.setEndDate(d20140531);
        LocalDate dflt = helper.default1SucceededBy();
        assertThat(dflt, is(d20140531.plusDays(1)));
    }

    // //////////////////////////////////////

    @Test
    public void precededBy_whenNoPredecessor() {
        
        target.setStartDate(d20130401);
        target.setEndDate(d20140531);
        final SomeDomainObject predecessor = helper.precededBy(d20130401, d20140531, factory);
        
        assertThat(target.getStartDate(), is(d20140531.plusDays(1)));
        assertThat(predecessor.getStartDate(), is(d20130401));
        assertThat(predecessor.getEndDate(), is(d20140531));
    }
    
    @Test
    public void precededBy_whenPredecessorExists() {

        // given
        final SomeDomainObject existingPredecessor = helper.precededBy(d20130401, d20160801, factory);
        target.predecessor = existingPredecessor;
        target.setStartDate(d20160801.plusDays(1));

        // when
        final SomeDomainObject newPredecessor = helper.precededBy(d20140531, d20160801, factory);
        
        // then
        assertThat(target.getStartDate(), is(d20160801.plusDays(1)));
        
        assertThat(newPredecessor.getStartDate(), is(d20140531));
        assertThat(newPredecessor.getEndDate(), is(d20160801));
        
        assertThat(existingPredecessor.getStartDate(), is(d20130401));
        assertThat(existingPredecessor.getEndDate(), is(d20140531.minusDays(1)));
    }

    // //////////////////////////////////////

    @Test
    public void validatePrecededBy_startDate_after_endDate() {
        target.setStartDate(d20140531);
        target.setEndDate(d20150615);
        
        String reason = helper.validatePrecededBy(d20150615, d20150615.minusDays(1));
        
        assertThat(reason, is("End date cannot be earlier than start date"));
    }

    @Test
    public void validatePrecededBy_endDate_not_after_existing_endDate() {
        target.setEndDate(d20150615);

        String reason = helper.validatePrecededBy(d20130401, d20150615);
        assertThat(reason, is("Predecessor must end before existing"));
    }

    @Test
    public void validatePrecededBy_withEndDate_noPredecessor_happyCase() {
        target.setStartDate(d20160801);

        String reason = helper.validatePrecededBy(d20140531, d20160801.minusDays(1));
        assertThat(reason, is(nullValue()));
    }

    @Test
    public void validatePrecededBy_noEndDate_noPredecessor_happyCase() {
        target.setEndDate(null);
        String reason = helper.validatePrecededBy(d20130401, d20140531);
        assertThat(reason, is(nullValue()));
    }
    
    @Test
    public void validatePrecededBy_whenPredecessor_noStartDate_provided() {
        final SomeDomainObject existingPredecessor = helper.precededBy(d20130401, d20160801, factory);
        target.predecessor = existingPredecessor;

        String reason = helper.validatePrecededBy(null, d20150615);
        assertThat(reason, is("A start date is required because a predecessor already exists"));
    }
    
    @Test
    public void validatePrecededBy_whenPredecessor_startDate_not_after_existing_predecessors_startDate() {
        final SomeDomainObject existingPredecessor = helper.precededBy(d20130401, d20160801, factory);
        target.predecessor = existingPredecessor;
        
        String reason = helper.validatePrecededBy(d20130401, d20150615);
        assertThat(reason, is("Predecessor must start after existing predecessor"));
    }
    
    @Test
    public void validatePrecededBy_whenPredecessor_happyCase() {
        final SomeDomainObject existingPredecessor = helper.precededBy(d20130401, d20160801, factory);
        target.predecessor = existingPredecessor;
        
        String reason = helper.validatePrecededBy(d20130401.plusDays(1), d20150615);
        assertThat(reason, is(nullValue()));
    }
    
    @Test
    public void default2PrecededBy_whenNoStartDate() {
        LocalDate dflt = helper.default2PrecededBy();
        assertThat(dflt, is(nullValue()));
    }

    // //////////////////////////////////////

    @Test
    public void default2PrecededBy_whenStartDate() {
        target.setStartDate(d20140531);
        LocalDate dflt = helper.default2PrecededBy();
        assertThat(dflt, is(d20140531.minusDays(1)));
    }

    
}
