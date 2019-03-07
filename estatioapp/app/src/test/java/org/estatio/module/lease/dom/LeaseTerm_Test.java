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
package org.estatio.module.lease.dom;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

import org.hamcrest.Description;
import org.jmock.Expectations;
import org.jmock.api.Action;
import org.jmock.api.Invocation;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.clock.ClockService;
import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2.Mode;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;
import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.bean.PojoTester;
import org.incode.module.unittestsupport.dom.with.WithIntervalMutableContractTestAbstract_changeDates;

import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.BDDAssertions.then;

public class LeaseTerm_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(Mode.INTERFACES_AND_CLASSES);

    Lease lease;
    LeaseTermForTesting term;
    LeaseItem item;

    @Mock
    LeaseTermRepository mockLeaseTermRepository;

    @Mock
    ClockService mockClockService;

    private final LocalDate now = LocalDate.now();

    @Before
    public void setUp() throws Exception {
        context.checking(new Expectations() {
            {
                allowing(mockClockService).now();
                will(returnValue(now));
                allowing(mockLeaseTermRepository).newLeaseTerm(
                        with(any(LeaseItem.class)),
                        with(any(LeaseTerm.class)),
                        with(any(LocalDate.class)),
                        with(aNull(LocalDate.class)));
                will(returnLeaseTerm());
            }
        });

        lease = new Lease();
        lease.setStartDate(new LocalDate(2012, 1, 1));

        item = new LeaseItem();
        item.setEndDate(new LocalDate(2013, 6, 30));
        lease.getItems().add(item);
        item.setLease(lease);

        item.leaseTermRepository = mockLeaseTermRepository;
        item.clockService = mockClockService;

        term = new LeaseTermForTesting();

        item.getTerms().add(term);
        term.setLeaseItem(item);

        term.setStartDate(new LocalDate(2012, 1, 1));
        term.setFrequency(LeaseTermFrequency.YEARLY);
        term.clockService = mockClockService;
        term.leaseTermRepository = mockLeaseTermRepository;
        term.initialize();
    }

    private Action returnLeaseTerm() {
        return new Action() {
            @Override
            public Object invoke(Invocation invocation) throws Throwable {
                LeaseItem leaseItem = (LeaseItem) invocation.getParameter(0);
                LeaseTerm leaseTerm = (LeaseTerm) invocation.getParameter(1);
                LocalDate startDate = (LocalDate) invocation.getParameter(2);
                LocalDate endDate = (LocalDate) invocation.getParameter(3);
                LeaseTermForTesting ltt = new LeaseTermForTesting();
                // relationships
                ltt.setLeaseItem(leaseItem);
                leaseItem.getTerms().add(ltt);
                ltt.setPrevious(leaseTerm);
                leaseTerm.setNext(ltt);
                // set values
                ltt.modifyStartDate(startDate);
                ltt.modifyEndDate(endDate);
                ltt.clockService = mockClockService;
                return ltt;
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("new Lease Term under item and with previous term");
            }
        };
    }

    public static class CreateNext extends LeaseTerm_Test {

        @Test
        public void createNext_ok() {
            final LeaseTermForTesting anotherTerm = new LeaseTermForTesting();
            item.getTerms().add(anotherTerm);
            anotherTerm.setLeaseItem(item);

            LeaseTermForTesting next = (LeaseTermForTesting) term.createNext(new LocalDate(2013, 1, 1), null);
            assertThat(this.term.getEndDate()).isEqualTo(new LocalDate(2012, 12, 31));
            assertThat(next.getStartDate()).isEqualTo(new LocalDate(2013, 1, 1));
            Assert.assertNull(next.getEndDate());
        }

        @Test
        public void hide_when_no_autocreate() throws Exception {
            //given
            final LeaseTermForTesting term = new LeaseTermForTesting();
            final LeaseItemType leaseItemType = LeaseItemType.RENT_DISCOUNT;
            final LeaseItem item = new LeaseItem();
            item.setType(leaseItemType);
            term.setLeaseItem(item);
            //when, then
            then(leaseItemType.autoCreateTerms()).isFalse();
            then(term.hideCreateNext()).isTrue();
        }

    }

    public static class Split extends LeaseTerm_Test {

        @Test
        public void split_ok() {

            LeaseTermRepository ltr = new LeaseTermRepository() {
                @Override public LeaseTerm newLeaseTerm(final LeaseItem leaseItem, final LeaseTerm previous, final LocalDate startDate, final LocalDate endDate) {
                    final LeaseTermForTesting leaseTermForTesting = new LeaseTermForTesting(leaseItem, startDate, endDate, null);
                    previous.setNext(leaseTermForTesting);
                    leaseTermForTesting.setPrevious(previous);
                    return leaseTermForTesting;
                }
            };

            final LeaseItem leaseItem = new LeaseItem();

            final LocalDate termStartDate = new LocalDate(2018, 1, 1);
            final LocalDate termEndDate = new LocalDate(2019, 12, 31);

            final LocalDate splitStartDate = new LocalDate(2019, 1, 1);

            //given
            term = new LeaseTermForTesting(
                    leaseItem,
                    new LocalDateInterval(termStartDate, termEndDate, IntervalEnding.INCLUDING_END_DATE),
                    new BigDecimal(100));

            LeaseTerm nextTerm = new LeaseTermForTesting();

            term.setNext(nextTerm);

            term.leaseTermRepository = ltr;

            //when
            final LeaseTerm newTerm = term.split(splitStartDate);

            //then
            assertThat(term.getEndDate()).isEqualTo(LocalDateInterval.endDateFromStartDate(splitStartDate));
            assertThat(newTerm.getStartDate()).isEqualTo(splitStartDate);
            assertThat(newTerm.getEndDate()).isEqualTo(termEndDate);
            assertThat(newTerm.getPrevious()).isEqualTo(term);
            assertThat(newTerm.getNext()).isEqualTo(nextTerm);
            assertThat(term.getNext()).isEqualTo(newTerm);
            assertThat(term.getEffectiveValue()).isEqualTo(newTerm.getEffectiveValue());

        }

        @Test
        public void validate_when_date_equal_or_before_start_date() {
            //given

            term = new LeaseTermForTesting(
                    new LeaseItem(),
                    new LocalDateInterval(new LocalDate(2018, 1, 1),
                            new LocalDate(2019, 12, 31), IntervalEnding.INCLUDING_END_DATE),
                    new BigDecimal(100));

            //when
            //then
            assertThat(term.validateSplit(new LocalDate(2018, 1, 2))).isNull();
            assertThat(term.validateSplit(new LocalDate(2019, 1, 1))).isNull();
            assertThat(term.validateSplit(new LocalDate(2019, 12, 30))).isNull();

            assertThat(term.validateSplit(new LocalDate(2017, 1, 1))).isNotNull();
            assertThat(term.validateSplit(new LocalDate(2018, 1, 1))).isNotNull();
            assertThat(term.validateSplit(new LocalDate(2019, 12, 31))).isNotNull();
            assertThat(term.validateSplit(new LocalDate(2020, 1, 1))).isNotNull();

        }


    }



    public static class Update extends LeaseTerm_Test {

        // TODO: the call to update is actually commented out ???
        @Test
        public void update_ok() {
            LeaseTermForTesting nextTerm = new LeaseTermForTesting();
            nextTerm.setPrevious(term);
            term.setNext(nextTerm);
            nextTerm.modifyStartDate(new LocalDate(2013, 1, 1));
            // term.update();
            assertThat(term.getEndDate()).isEqualTo(new LocalDate(2012, 12, 31));
        }
    }

    public static class InvoicedValueFor extends LeaseTerm_Test {

        // TODO: We moved the retrieval to the repository so this is broken.
        @Ignore
        @Test
        public void invoicedValueFor_ok() throws Exception {
            LocalDateInterval interval = new LocalDateInterval(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1), IntervalEnding.EXCLUDING_END_DATE);
            LeaseTermForTesting term = new LeaseTermForTesting();
            InvoiceForLease invoice = new InvoiceForLease();
            invoice.setStatus(InvoiceStatus.APPROVED);
            InvoiceItemForLease item1 = new InvoiceItemForLease();

            invoice.getItems().add(item1);
            item1.setInvoice(invoice);

            item1.setLeaseTerm(term);
            item1.setStartDate(interval.startDate());
            item1.setNetAmount(BigDecimal.valueOf(1234.45));

            InvoiceItemForLease item2 = new InvoiceItemForLease();
            invoice.getItems().add(item2);
            item2.setInvoice(invoice);

            item2.setNetAmount(BigDecimal.valueOf(1234.45));
            item2.setLeaseTerm(term);
            item2.setStartDate(interval.startDate());

        }
    }

    public static class EffectiveInterval extends LeaseTerm_Test {

        @Test
        public void testEffectiveInterval() throws Exception {
            term.align();
            assertThat(term.getEffectiveInterval().endDate()).isEqualTo(new LocalDate(2013, 6, 30));
            lease.setTenancyEndDate(new LocalDate(2012, 3, 31));
            assertThat(term.getEffectiveInterval().endDate()).isEqualTo(new LocalDate(2012, 3, 31));
        }

        @Test
        public void testEI() throws Exception {
            assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, null, "2011-02-01", null, "2011-01-01", "2011-12-31").toString()).isEqualTo("2011-02-01/2012-01-01");
            assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, "2012-06-30", "2011-02-01", null, "2012-01-01", "2012-12-31").toString()).isEqualTo("2012-01-01/2012-07-01");
            assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, null, "2011-02-01", null, "2011-01-01", null).toString()).isEqualTo("2011-02-01/----------");
            assertThat(effectiveIntervalWith("2011-01-01", "2012-12-31", null, "2013-12-31", "2011-02-01", null, "2014-01-01", null)).isNull();
        }

        // //////////////////////////////////////

        private LocalDateInterval effectiveIntervalWith(
                String leaseStartDate,
                String leaseEndDate,
                String leaseTenancyStartDate,
                String leaseTenacyEndDate,
                String itemStartDate,
                String itemEndDate,
                String termStartDate, String termEndDate
        ) {

            Lease lease = new Lease();
            lease.setStartDate(parseDate(leaseStartDate));
            lease.setEndDate(parseDate(leaseEndDate));
            lease.setTenancyEndDate(parseDate(leaseTenacyEndDate));

            LeaseItem item = new LeaseItem();
            item.setStartDate(parseDate(itemStartDate));
            item.setEndDate(parseDate(itemEndDate));
            lease.getItems().add(item);
            item.setLease(lease);

            item.leaseTermRepository = mockLeaseTermRepository;
            item.clockService = mockClockService;

            LeaseTerm term = new LeaseTermForTesting();

            item.getTerms().add(term);
            term.setLeaseItem(item);

            term.setStartDate(parseDate(termStartDate));
            term.setEndDate(parseDate(termEndDate));
            term.setFrequency(LeaseTermFrequency.YEARLY);
            term.clockService = mockClockService;
            term.initialize();

            return term.getEffectiveInterval();

        }

        private LocalDate parseDate(String input) {
            if (input == null)
                return null;
            return LocalDate.parse(input);

        }

    }

    public static class NextStartDate extends LeaseTerm_Test {

        @Test
        public void nextStartDate() throws Exception {
            assertThat(term.nextStartDate()).isEqualTo(new LocalDate(2013, 1, 1));
        }
    }

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(LeaseItem.class))
                    .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
                    .withFixture(statii())
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new LeaseTermForTesting());
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        private static PojoTester.FixtureDatumFactory<LeaseTermStatus> statii() {
            return new PojoTester.FixtureDatumFactory(LeaseTermStatus.class, (Object[]) LeaseTermStatus.values());
        }

    }

    public static class ChangeDatesDelegate extends WithIntervalMutableContractTestAbstract_changeDates<LeaseTerm> {

        private LeaseTerm leaseTerm;

        @Before
        public void setUp() throws Exception {
            leaseTerm = withIntervalMutable;
        }

        protected LeaseTerm doCreateWithIntervalMutable(final WithIntervalMutable.Helper<LeaseTerm> mockChangeDates) {

            return new MyLeaseTerm(mockChangeDates);
        }

        // //////////////////////////////////////

        @Test
        public void changeDatesDelegate() {
            leaseTerm = new LeaseTerm() {
                @Override
                public BigDecimal getEffectiveValue() {
                    return null;
                }

                @Override
                public BigDecimal valueForDate(LocalDate dueDate) {
                    return null;
                }
            };
            assertThat(leaseTerm.getChangeDates()).isNotNull();
        }

        @Programmatic
        private static class MyLeaseTerm extends LeaseTerm {
            private final Helper<LeaseTerm> mockChangeDates;

            public MyLeaseTerm(final Helper<LeaseTerm> mockChangeDates) {
                this.mockChangeDates = mockChangeDates;
            }

            @Override Helper<LeaseTerm> getChangeDates() {
                return mockChangeDates;
            }

            @Override
            public BigDecimal getEffectiveValue() {
                return null;
            }

            @Override
            public BigDecimal valueForDate(LocalDate dueDate) {
                return null;
            }
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<LeaseTerm> {

        private LeaseItem leaseItem1;
        private LeaseItem leaseItem2;

        @Before
        public void setUp() throws Exception {
            leaseItem1 = new LeaseItem();
            leaseItem2 = new LeaseItem();

            leaseItem1.setType(LeaseItemType.RENT);
            leaseItem2.setType(LeaseItemType.SERVICE_CHARGE);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<LeaseTerm>> orderedTuples() {
            return listOf(
                    listOf(
                            newLeaseTerm(null, null),
                            newLeaseTerm(leaseItem1, null),
                            newLeaseTerm(leaseItem1, null),
                            newLeaseTerm(leaseItem2, null)
                    ),
                    listOf(
                            newLeaseTerm(leaseItem1, null),
                            newLeaseTerm(leaseItem1, 1),
                            newLeaseTerm(leaseItem1, 1),
                            newLeaseTerm(leaseItem1, 2)
                    ));
        }

        private LeaseTerm newLeaseTerm(
                LeaseItem leaseItem, Integer sequence) {
            final LeaseTerm lt = new LeaseTermForTesting();
            lt.setLeaseItem(leaseItem);
            lt.setSequence(sequence != null ? BigInteger.valueOf(sequence.longValue()) : null);
            return lt;
        }

    }

    public static class ValidateCreateNext extends LeaseTerm_Test {

        private LeaseItem leaseItem;

        @Before
        public void setUp() throws Exception {
            leaseItem = new LeaseItem();
            leaseItem.setType(LeaseItemType.RENT); // or any other type except deposit
            term = new LeaseTermForTesting();
            term.setLeaseItem(leaseItem);
            term.setStartDate(new LocalDate(2014, 6, 1));
            term.setEndDate(new LocalDate(2014, 8, 31));
            term.leaseTermRepository = new LeaseTermRepository();
        }

        @Test
        public void happy() throws Exception {
            assertThat(term.validateCreateNext(new LocalDate(2014, 7, 1), null)).isNull();
        }

        @Test
        public void canStartOnStartDateAsThis() throws Exception {
            assertThat(term.validateCreateNext(new LocalDate(2014, 6, 1), null)).isNull();
        }

        @Test
        public void canEndOnEndDateAsThis() throws Exception {
            assertThat(term.validateCreateNext(new LocalDate(2014, 8, 31), null)).isNull();
        }

        @Test
        public void tooEarly() throws Exception {
            assertThat(term.validateCreateNext(new LocalDate(2014, 5, 31), null)).isEqualTo("Start date must be on or after 2014-06-01");
        }

        @Test
        public void canStartAfterThisEnds() throws Exception {
            // because the action itself will auto-align
            assertThat(term.validateCreateNext(new LocalDate(2014, 9, 1), null)).isNull();
        }

    }

    public static class ChangeDates extends LeaseTerm_Test {

        private LeaseTerm term;
        private LeaseTerm prev;
        private LeaseTerm next;

        @Before
        public void setUp() throws Exception {
            term = new MyLeaseTermForTesting();
            prev = new MyLeaseTermForTesting2();
            next = new LeaseTermForTesting(null, new LocalDate(2011, 1, 1), new LocalDate(2011, 12, 31), null);

        }

        @Test
        public void validate() throws Exception {
            // Before start date prev
            assertThat(term.validateChangeDates(new LocalDate(2008, 12, 31), new LocalDate(2010, 12, 31))).isEqualTo("New start date can't be before start date of previous term");
            // Invalid interval
            assertThat(term.validateChangeDates(new LocalDate(2011, 1, 2), new LocalDate(2010, 12, 31))).isEqualTo("End date must be after start date");
            // Can't change end date when there's a next term
            assertThat(term.validateChangeDates(new LocalDate(2010, 1, 1), new LocalDate(2011, 1, 1))).isEqualTo("The end date of this term is set by the start date of the next term");
        }

        @Test
        public void changeEndDateOfPreviousTerm() throws Exception {
            // Given, when
            term.changeDates(term.getStartDate().plusMonths(1), term.getEndDate());
            // then
            assertThat(prev.getEndDate()).isEqualTo(LocalDateInterval.endDateFromStartDate(term.getStartDate()));

        }

        @Programmatic
        private class MyLeaseTermForTesting extends LeaseTermForTesting {
            public MyLeaseTermForTesting() {
                super(null, new LocalDate(2010, 1, 1), new LocalDate(2010, 12, 31), null);
            }

            @Override
            public LeaseTerm getPrevious() {
                return prev;
            }

            @Override
            public LeaseTerm getNext() {
                return next;
            }
        }

        @Programmatic
        private class MyLeaseTermForTesting2 extends LeaseTermForTesting {
            public MyLeaseTermForTesting2() {
                super(null, new LocalDate(2009, 1, 1), new LocalDate(2009, 12, 31), null);
            }

            @Override
            public LeaseTerm getNext() {
                return term;
            }
        }
    }

}
