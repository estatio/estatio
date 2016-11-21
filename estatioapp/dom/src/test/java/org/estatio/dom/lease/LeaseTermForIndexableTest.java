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

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;
import org.estatio.dom.index.Index;
import org.estatio.dom.index.IndexValueRepository;
import org.estatio.dom.index.Indexable;
import org.estatio.dom.lease.indexation.IndexationMethod;
import org.estatio.dom.lease.indexation.IndexationService;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForIndexableTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Lease lease;
    LeaseItem item;
    LeaseTermForIndexable term;

    final LocalDate now = LocalDate.now();

    @Mock
    ClockService mockClockService;

    @Mock
    LeaseTermRepository mockLeaseTermRepository;

    @Mock
    IndexValueRepository mockIndexValueRepository;

    @Mock
    Index mockIndex;

    @Before
    public void setup() {

        lease = new Lease();
        lease.setStartDate(new LocalDate(2011, 1, 1));
        lease.setEndDate(new LocalDate(2020, 12, 31));

        item = new LeaseItem();
        item.clockService = mockClockService;

        lease.getItems().add(item);
        item.setLease(lease);

        item.setType(LeaseItemType.RENT);
        item.leaseTermRepository = mockLeaseTermRepository;

        term = new LeaseTermForIndexable();
        term.clockService = mockClockService;
        term.indexationService = new IndexationService();
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.setBaseIndexStartDate(new LocalDate(2010, 1, 1));
        term.setNextIndexStartDate(new LocalDate(2011, 1, 1));
        term.setBaseValue(BigDecimal.valueOf(23456.78));
        term.setIndex(mockIndex);
        term.setBaseIndexValue(BigDecimal.valueOf(137.6));
        term.setNextIndexValue(BigDecimal.valueOf(101.2));
        term.setRebaseFactor(BigDecimal.valueOf(1.373));
        term.setIndexationMethod(IndexationMethod.LAST_KNOWN_INDEX);

        item.getTerms().add(term);
        term.setLeaseItem(item);

        term.setStartDate(new LocalDate(2011, 1, 1));
        term.initialize();

        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
            }
        });

    }

    public static class DoAlign extends LeaseTermForIndexableTest {

        @Test
        public void happyCase() {
            context.checking(new Expectations() {
                {
                    allowing(mockIndex).initialize(with(any(Indexable.class)));
                }
            });

            term.align();
            Assert.assertEquals(new BigDecimal("23691.35"), term.getIndexedValue());
        }

        @Test
        public void whenEmptyIndex() {
            context.checking(new Expectations() {
                {
                    allowing(mockIndex).initialize(with(any(Indexable.class)));
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
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31))).isEqualTo(BigDecimal.valueOf(30000));

            term.setStartDate(new LocalDate(2011, 2, 1));
            term.setEffectiveDate(new LocalDate(2011, 2, 1));

            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(20000));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31))).isEqualTo(BigDecimal.valueOf(30000));

            term.setStartDate(new LocalDate(2011, 1, 1));
            term.setEffectiveDate(new LocalDate(2012, 4, 1));

            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(20000));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31))).isEqualTo(BigDecimal.valueOf(20000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(30000));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31))).isEqualTo(BigDecimal.valueOf(30000));

            term.setSettledValue(BigDecimal.valueOf(31000));
            assertThat(term.valueForDate(new LocalDate(2011, 1, 1))).isEqualTo(BigDecimal.valueOf(20000));
            assertThat(term.valueForDate(new LocalDate(2011, 12, 31))).isEqualTo(BigDecimal.valueOf(20000));
            assertThat(term.valueForDate(new LocalDate(2012, 4, 1))).isEqualTo(BigDecimal.valueOf(31000));
            assertThat(term.valueForDate(new LocalDate(2012, 7, 31))).isEqualTo(BigDecimal.valueOf(31000));
        }
    }

    public static class DoInitialize extends LeaseTermForIndexableTest {

        @Test
        public void initializeWithoutIndexationMethod() throws Exception {
            // given
            LeaseTermForIndexable nextTerm = new LeaseTermForIndexable();
            term.setNext(nextTerm);
            nextTerm.setPrevious(term);
            // when
            nextTerm.initialize();
            // then
            assertThat(nextTerm.getBaseIndexStartDate()).isEqualTo(term.getNextIndexStartDate());
            assertThat(nextTerm.getNextIndexStartDate()).isEqualTo(term.getNextIndexStartDate().plusYears(1));
        }

        @Test
        public void initializeWithBaseIndexIndexationMethod() throws Exception {
            // given
            LeaseTermForIndexable nextTerm = new LeaseTermForIndexable();
            term.setIndexationMethod(IndexationMethod.BASE_INDEX);
            term.setNext(nextTerm);
            nextTerm.setPrevious(term);
            // when
            nextTerm.initialize();
            // then
            assertThat(nextTerm.getBaseIndexStartDate()).isEqualTo(term.getBaseIndexStartDate());
            assertThat(nextTerm.getNextIndexStartDate()).isEqualTo(term.getNextIndexStartDate().plusYears(1));
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
            return new PojoTester.FixtureDatumFactory(LeaseTermStatus.class, (Object[]) LeaseTermStatus.values());
        }

    }

}
