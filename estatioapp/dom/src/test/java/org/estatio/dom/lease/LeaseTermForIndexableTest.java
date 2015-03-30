/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
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

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexBase;
import org.estatio.dom.index.IndexValue;
import org.estatio.dom.index.IndexValues;
import org.estatio.dom.index.IndexationService;
import org.estatio.services.clock.ClockService;

public class LeaseTermForIndexableTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Lease lease;
    LeaseItem item;
    LeaseTermForIndexable term;

    Index i;

    IndexBase ib1;
    IndexBase ib2;
    IndexValue iv1;
    IndexValue iv2;

    IndexationService indexationService;

    final LocalDate now = LocalDate.now();

    @Mock
    ClockService mockClockService;

    @Mock
    LeaseTerms mockLeaseTerms;

    @Mock
    IndexValues mockIndexValues;

    @Before
    public void setup() {

        i = new Index();

        i.injectIndexValues(mockIndexValues);

        ib1 = new IndexBase();
        ib1.setStartDate(new LocalDate(2000, 1, 1));

        ib1.setIndex(i);

        ib2 = new IndexBase();
        ib2.setFactor(BigDecimal.valueOf(1.373));
        ib2.modifyPrevious(ib1);
        ib2.setStartDate(new LocalDate(2011, 1, 1));

        ib2.setIndex(i);

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

        lease.getItems().add(item);
        item.setLease(lease);

        item.setType(LeaseItemType.RENT);
        item.leaseTerms = mockLeaseTerms;

        term = new LeaseTermForIndexable();
        term.injectClockService(mockClockService);
        term.injectIndexationService(new IndexationService());
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.setBaseIndexStartDate(iv1.getStartDate());
        term.setNextIndexStartDate(iv2.getStartDate());
        term.setBaseValue(BigDecimal.valueOf(23456.78));
        term.setIndex(i);

        item.getTerms().add(term);
        term.setLeaseItem(item);

        term.setStartDate(new LocalDate(2011, 1, 1));
        term.doInitialize();

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
            }
        });

    }

    public static class Align extends LeaseTermForIndexableTest {

        @Test
        public void happyCase() {
            context.checking(new Expectations() {
                {
                    allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2010, 1, 1)));
                    will(returnValue(iv1));
                    allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2011, 1, 1)));
                    will(returnValue(iv2));
                }
            });
            term.align();
            Assert.assertEquals(new BigDecimal("23691.35"), term.getIndexedValue());
        }

        @Test
        public void whenEmptyIndex() {
            context.checking(new Expectations() {
                {
                    allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2010, 1, 1)));
                    will(returnValue(iv1));
                    allowing(mockIndexValues).findIndexValueByIndexAndStartDate(with(i), with(new LocalDate(2011, 1, 1)));
                    will(returnValue(iv2));
                }
            });
            term.align();
            Assert.assertEquals(new BigDecimal("23691.35"), term.getIndexedValue());
        }

    }

    public static class ValueForDueDate extends LeaseTermForIndexableTest {

        @Test
        public void happyCase() throws Exception {
            LeaseTermForIndexable term = new LeaseTermForIndexable();
            term.setStartDate(new LocalDate(2011, 1, 1));
            term.setBaseValue(BigDecimal.valueOf(20000));
            term.setIndexedValue(BigDecimal.valueOf(30000));
            term.setEffectiveDate(null);
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));

            term.setStartDate(new LocalDate(2011, 2, 1));
            term.setEffectiveDate(new LocalDate(2011, 2, 1));

            assertThat(term.valueForDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(20000)));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));

            term.setStartDate(new LocalDate(2011, 1, 1));
            term.setEffectiveDate(new LocalDate(2012, 4, 1));

            assertThat(term.valueForDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(20000)));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(20000)));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(30000)));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(30000)));

            term.setSettledValue(BigDecimal.valueOf(31000));
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1)), is(BigDecimal.valueOf(20000)));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31)), is(BigDecimal.valueOf(20000)));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1)), is(BigDecimal.valueOf(31000)));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31)), is(BigDecimal.valueOf(31000)));
        }
    }

    public static class DoInitialize extends LeaseTermForIndexableTest {

        @Ignore // incomplete, null pointer exception
        @Test
        public void initialize_ok() throws Exception {
            LeaseTermForIndexable nextTerm = new LeaseTermForIndexable();
            term.setNext(nextTerm);

            nextTerm.doInitialize();

            assertThat(nextTerm.getBaseIndexStartDate(), is(term.getNextIndexStartDate()));
            assertThat(nextTerm.getNextIndexStartDate(), is(term.getNextIndexStartDate().plusYears(1)));
            assertThat(nextTerm.getEffectiveDate(), is(term.getEffectiveDate().plusYears(1)));

        }
    }
    
    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(LeaseItem.class))
                    .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
                    .withFixture(pojos(Index.class))
                    .withFixture(statii())
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new LeaseTermForIndexable());
        }


        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static PojoTester.FixtureDatumFactory<LeaseTermStatus> statii() {
            return new PojoTester.FixtureDatumFactory(LeaseTermStatus.class, (Object[])LeaseTermStatus.values());
        }

    }

}
