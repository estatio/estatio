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
package org.estatio.dom.lease;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.Indices;
import org.estatio.services.clock.ClockService;

public class LeaseTermForIndexableRentTest {

    private Lease lease; 
    private LeaseItem item;
    private LeaseTermForIndexableRent term;

    public Index i;

    private IndexBase ib1;
    private IndexBase ib2;
    private IndexValue iv1;
    private IndexValue iv2;

    private final LocalDate now = LocalDate.now();

    @Mock
    private ClockService mockClockService;

    @Mock
    LeaseTerms mockLeaseTerms;

    @Mock
    IndexValues mockIndexValues;

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    @Before
    public void setup() {

        i = new Index();

        i.injectIndexValues(mockIndexValues);

        ib1 = new IndexBase();
        ib1.setStartDate(new LocalDate(2000, 1, 1));

        i.addToIndexBases(ib1);

        ib2 = new IndexBase();
        ib2.setFactor(BigDecimal.valueOf(1.373));
        ib2.modifyPreviousBase(ib1);
        ib2.setStartDate(new LocalDate(2011, 1, 1));

        i.addToIndexBases(ib2);

        iv1 = new IndexValue();
        iv1.setStartDate(new LocalDate(2010, 1, 1));
        iv1.setValue(BigDecimal.valueOf(137.6));
        ib1.addToValues(iv1);

        iv2 = new IndexValue();
        iv2.setStartDate(new LocalDate(2011, 1, 1));
        iv2.setValue(BigDecimal.valueOf(101.2));
        ib2.addToValues(iv2);
        
        lease = new Lease();
        lease.setStartDate(new LocalDate(2011,1,1));
        lease.setEndDate(new LocalDate(2020,12,31));
        
        item = new LeaseItem();
        item.injectClockService(mockClockService);
        item.modifyLease(lease);
        item.setType(LeaseItemType.RENT);
        item.injectLeaseTerms(mockLeaseTerms);

        term = new LeaseTermForIndexableRent();
        term.injectClockService(mockClockService);
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.setBaseIndexStartDate(iv1.getStartDate());
        term.setNextIndexStartDate(iv2.getStartDate());
        term.setBaseValue(BigDecimal.valueOf(23456.78));
        term.setIndex(i);
        term.modifyLeaseItem(item);
        term.setStartDate(new LocalDate(2011, 1, 1));
        term.initialize();
        
        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
            }
        });

    }

    @Test
    public void update_ok() {
        context.checking(new Expectations() {
            {
                allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2010, 1, 1)));
                will(returnValue(iv1));
                allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2011, 1, 1)));
                will(returnValue(iv2));
            }
        });
        term.update();
        Assert.assertEquals(new BigDecimal("23691.3500"), term.getIndexedValue());
    }

    @Test
    public void update_whenEmptyIndex_ok() {
        context.checking(new Expectations() {
            {
                allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2010, 1, 1)));
                will(returnValue(iv1));
                allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2011, 1, 1)));
                will(returnValue(iv2));
            }
        });
        term.update();
        Assert.assertEquals(new BigDecimal("23691.3500"), term.getIndexedValue());
    }


    @Test
    public void valueForDueDate_ok() throws Exception {
        LeaseTermForIndexableRent term = new LeaseTermForIndexableRent();
        term.setStartDate(new LocalDate(2011,1,1));
        term.setBaseValue(BigDecimal.valueOf(20000));
        term.setIndexedValue(BigDecimal.valueOf(30000));
        term.setEffectiveDate(null);
        assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));

        term.setStartDate(new LocalDate(2011, 2, 1));
        term.setEffectiveDate(new LocalDate(2011, 2, 1));
        
        assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));

        term.setStartDate(new LocalDate(2011, 1, 1));
        term.setEffectiveDate(new LocalDate(2012, 4, 1));
        
        assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(20000)));
        assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(20000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));
        
        term.setSettledValue(BigDecimal.valueOf(31000));
        assertThat(term.valueForDueDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(20000)));
        assertThat(term.valueForDueDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(20000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(31000)));
        assertThat(term.valueForDueDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(31000)));
    }

    @Ignore // incomplete, null pointer exception
    @Test
    public void initialize_ok() throws Exception {
        LeaseTermForIndexableRent nextTerm = new LeaseTermForIndexableRent();
        term.modifyNext(nextTerm);
        
        nextTerm.initialize();
        
        assertThat(nextTerm.getBaseIndexStartDate(), is(term.getNextIndexStartDate()));
        assertThat(nextTerm.getNextIndexStartDate(), is(term.getNextIndexStartDate().plusYears(1)));
        assertThat(nextTerm.getEffectiveDate(), is(term.getEffectiveDate().plusYears(1)));
        
    }
}
