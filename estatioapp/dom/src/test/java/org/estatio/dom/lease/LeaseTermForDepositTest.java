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
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.valuetypes.LocalDateInterval;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LeaseTermForDepositTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);


    @Mock
    Lease mockLease;

    LeaseItem depositItem;

    LeaseTermForDeposit term;

    LocalDate startDate;


    @Before
    public void setup() {
        startDate = new LocalDate(2013,1,1);
        depositItem = new LeaseItem();
        depositItem.setType(LeaseItemType.DEPOSIT);
        depositItem.setLease(mockLease);
        term = new LeaseTermForDeposit();
        term.setLeaseItem(depositItem);
        term.setStartDate(startDate);
    }

    public static class VerifyUntilTest extends LeaseTermForDepositTest {

        private LeaseItem rentItem;
        private LeaseTermForIndexable leaseTermForIndexable;
        private Tax vat;

        @Before
        public void setup() {
            super.setup();

            leaseTermForIndexable = new LeaseTermForIndexable();
            leaseTermForIndexable.setBaseValue(new BigDecimal("99500.00"));
            vat = new Tax(){
                @Override
                public BigDecimal percentageFor(final LocalDate date){
                    return new BigDecimal("21.00");
                }
            };
            rentItem = new LeaseItem() {
                @Override
                public LeaseTerm currentTerm(LocalDate date) {
                    return leaseTermForIndexable;
                }

                @Override
                public BigDecimal valueForDate(LocalDate date) {
                    return new BigDecimal("100000.00");
                }

            };
            rentItem.setTax(vat);

            context.checking(new Expectations() {
                {
                    allowing(mockLease).getEffectiveInterval();
                    will(returnValue(new LocalDateInterval(new LocalDate(2013,01,01), new LocalDate(2014,01,01))));
                    allowing(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(rentItem);
                        }
                    }));
                }
            });
        }

        @Test
        public void testHalfYear() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.INDEXED_MGR_EXCLUDING_VAT, "0.00", null, "50000.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.INDEXED_MGR_INCLUDING_VAT, "0.00", null, "60500.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.BASE_MGR_EXCLUDING_VAT, "0.00", null, "49750.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.BASE_MGR_INCLUDING_VAT, "0.00", null, "60197.50");
        }

        @Test
        public void testHalfYearWithExcludingAmount() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.INDEXED_MGR_EXCLUDING_VAT, "10000.00", null,  "40000.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.INDEXED_MGR_INCLUDING_VAT, "10000.00", null,  "50500.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.BASE_MGR_EXCLUDING_VAT, "10000.00", null,  "39750.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M6, DepositType.BASE_MGR_INCLUDING_VAT, "10000.00", null,  "50197.50");
        }

        @Test
        public void testQuarter() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M3, DepositType.INDEXED_MGR_EXCLUDING_VAT, "0.00", null, "25000.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M3, DepositType.INDEXED_MGR_INCLUDING_VAT, "0.00", null, "30250.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M3, DepositType.BASE_MGR_EXCLUDING_VAT, "0.00", null, "24875.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M3, DepositType.BASE_MGR_INCLUDING_VAT, "0.00", null, "30098.75");
        }

        @Test
        public void testMonth() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.INDEXED_MGR_EXCLUDING_VAT, "0.00", null, "8333.33");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.INDEXED_MGR_INCLUDING_VAT, "0.00", null, "10083.33");
        }

        @Test
        public void testTypeManual() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.MANUAL, DepositType.MANUAL, "0.00", null , "0.00");
        }

        @Test
        public void testManualDepositValueForFractionManual() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.MANUAL, DepositType.MANUAL, "0.00", "123.00", "123.00");
        }

        @Test
        public void testManualDepositValueForFractionM1() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.MANUAL, "0.00", "123.00", "123.00");
        }

        @Test
        public void testManualDepositValueZero() {
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.MANUAL, "0.00", "0.00", "0.00");
        }

        @Test
        public void testMonthWhenTerminated() {
            term.terminate(new LocalDate(2013,01,01));
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.INDEXED_MGR_EXCLUDING_VAT, "0.00", null, "0.00");
            super.verifyUntil(new LocalDate(2013,01,01), Fraction.M1, DepositType.INDEXED_MGR_INCLUDING_VAT, "0.00", null, "0.00");
        }

    }

    public static class VerifyWhenTerminated extends LeaseTermForDepositTest {

        @Before
        public void setup() {
            super.setup();

            context.checking(new Expectations() {
                {
                    allowing(mockLease).getEffectiveInterval();
                    will(returnValue(new LocalDateInterval(new LocalDate(2013,01,01), new LocalDate(2014,01,01))));
                }
            });
        }

        @Test
        public void testMonthWhenTerminatedBefore() {
            term.terminate(new LocalDate(2013,01,01));
            super.verifyUntil(new LocalDate(2013,01,02), Fraction.M1, DepositType.INDEXED_MGR_EXCLUDING_VAT, "0.00", null, "0.00");
            super.verifyUntil(new LocalDate(2013,01,02), Fraction.M1, DepositType.INDEXED_MGR_INCLUDING_VAT, "0.00", null, "0.00");
        }

    }

    private void verifyUntil(
            final LocalDate date,
            final Fraction fraction,
            final DepositType depositType,
            final String excludedAmountStr,
            final String manualDepositValueStr,
            final String expectedValueStr
    ) {
        term.setFraction(fraction);
        term.setDepositType(depositType);
        term.setExcludedAmount(parseBigDecimal(excludedAmountStr));
        term.setManualDepositValue(parseBigDecimal(manualDepositValueStr));
        term.verifyUntil(date);

        assertThat(term.getEffectiveValue(), is(parseBigDecimal(expectedValueStr)));
    }

    private BigDecimal parseBigDecimal(final String input) {
        if (input == null) {
            return null;
        }
        return new BigDecimal(input);
    }

    public static class Initialize extends LeaseTermForDepositTest {

        @Test
        public void test(){
            //when
            term.doInitialize();

            //then
            assertThat(term.getCalculatedDepositValue(), is(BigDecimal.ZERO));
            assertThat(term.getExcludedAmount(), is(BigDecimal.ZERO));
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
                    .exercise(new LeaseTermForDeposit());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static FixtureDatumFactory<LeaseTermStatus> statii() {
            return new FixtureDatumFactory(LeaseTermStatus.class, (Object[]) LeaseTermStatus.values());
        }

    }

    public static class ValidateChangeParameters {


        LeaseTermForDeposit leaseTermForDeposit = new LeaseTermForDeposit();
        Fraction fraction;
        DepositType depositType;
        BigDecimal excludedAmount = new BigDecimal("-0.01");


        @Test
        public void test(){
            assertThat(leaseTermForDeposit.validateChangeParameters(fraction, depositType, excludedAmount), is("Excluded amount should not be negative"));
        }

    }

    public static class InvoiceCalculationTest extends LeaseTermForDepositTest {

        private List<InvoiceCalculationService.CalculationResult> results;
        private InvoiceCalculationService invoiceCalculationService;

        private LeaseItem rentItem;
        private LeaseTermForIndexable leaseTermForIndexable;

        @Before
        public void setup() {
            startDate = new LocalDate(2013,1,1);
            depositItem = new LeaseItem();
            depositItem.setType(LeaseItemType.DEPOSIT);
            depositItem.setLease(mockLease);
            depositItem.setInvoicingFrequency(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            term = new LeaseTermForDeposit();
            term.setLeaseItem(depositItem);
            term.setStartDate(startDate);
            term.setFraction(Fraction.M6);
            term.setDepositType(DepositType.INDEXED_MGR_EXCLUDING_VAT);
            term.setExcludedAmount(new BigDecimal("500.00"));
            invoiceCalculationService = new InvoiceCalculationService();

            leaseTermForIndexable = new LeaseTermForIndexable();
            leaseTermForIndexable.setIndexedValue(new BigDecimal("10000.00"));
            rentItem = new LeaseItem() {
                @Override
                public LeaseTerm currentTerm(LocalDate date) {
                    return leaseTermForIndexable;
                }

                @Override
                public BigDecimal valueForDate(LocalDate date) {
                    return new BigDecimal("10000.00");
                }

            };

            context.checking(new Expectations() {
                {
                    allowing(mockLease).getEffectiveInterval();
                    will(returnValue(new LocalDateInterval(new LocalDate(2013,01,01), new LocalDate(2014,01,01))));
                    allowing(mockLease).getStartDate();
                    will(returnValue(new LocalDate(2013,01,01)));
                    allowing(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(rentItem);
                        }
                    }));
                }
            });
        }

        @Test
        public void testAtStartTermRetro() {

            testOneResultExpected(InvoiceRunType.RETRO_RUN, startDate, "2013-01-01/2013-04-01:2013-01-01", "2013-01-01/2013-04-01", "4500.00");

        }

        @Test
        public void testAfterStartTermRetro() {

            testOneResultExpected(InvoiceRunType.RETRO_RUN, startDate.plusDays(1), "2013-01-01/2013-04-01:2013-01-01", "2013-01-01/2013-04-01", "4500.00");

        }

        @Test
        public void testBeforeStartTermRetro() {

            testNoResultsExpected(InvoiceRunType.RETRO_RUN, startDate.minusDays(1));

        }

        @Test
        public void testAtStartTermNormal() {

            testOneResultExpected(InvoiceRunType.NORMAL_RUN, startDate, "2013-01-01/2013-04-01:2013-01-01", "2013-01-01/2013-04-01", "4500.00");

        }

        @Test
        public void testAfterStartTermNormal() {

            testNoResultsExpected(InvoiceRunType.NORMAL_RUN, startDate.plusDays(1));

        }

        @Test
        public void testTerminatedBeforeDueDate() {

            term.terminate(startDate.plusMonths(3).minusDays(1));

            testTwoResultsExpected(
                    InvoiceRunType.RETRO_RUN, startDate.plusMonths(3),
                    "2013-01-01/2013-04-01:2013-01-01",
                    "2013-01-01/2013-04-01",
                    "0.00",
                    "2013-04-01/2013-07-01:2013-04-01",
                    "2013-04-01/2013-07-01",
                    "0.00");

        }

        private void testOneResultExpected(InvoiceRunType invoiceRunType, LocalDate start, String invoicingIntervalExpected, String effectiveIntervalExpected, String valueExpected) {
            // given
            InvoiceCalculationParameters parameters = new InvoiceCalculationParameters(invoiceRunType, start, start, start.plusDays(1));
            term.verifyUntil(start.plusDays(2));

            // when
            results = invoiceCalculationService.calculateDueDateRange(term, parameters);

            // then
            assertThat(results.size(), is(1));
            assertThat(results.get(0).invoicingInterval().toString(), is(invoicingIntervalExpected));
            assertThat(results.get(0).effectiveInterval().toString(), is(effectiveIntervalExpected));
            assertThat(results.get(0).value(), is(new BigDecimal(valueExpected)));
        }

        private void testTwoResultsExpected(
                InvoiceRunType invoiceRunType,
                LocalDate start,
                String invoicingIntervalExpected0,
                String effectiveIntervalExpected0,
                String valueExpected0,
                String invoicingIntervalExpected1,
                String effectiveIntervalExpected1,
                String valueExpected1) {
            // given
            InvoiceCalculationParameters parameters = new InvoiceCalculationParameters(invoiceRunType, start, start, start.plusDays(1));
            term.verifyUntil(start.plusDays(2));

            // when
            results = invoiceCalculationService.calculateDueDateRange(term, parameters);

            // then
            assertThat(results.size(), is(2));
            assertThat(results.get(0).invoicingInterval().toString(), is(invoicingIntervalExpected0));
            assertThat(results.get(0).effectiveInterval().toString(), is(effectiveIntervalExpected0));
            assertThat(results.get(0).value(), is(new BigDecimal(valueExpected0)));
            assertThat(results.get(1).invoicingInterval().toString(), is(invoicingIntervalExpected1));
            assertThat(results.get(1).effectiveInterval().toString(), is(effectiveIntervalExpected1));
            assertThat(results.get(1).value(), is(new BigDecimal(valueExpected1)));
        }


        private void testNoResultsExpected(InvoiceRunType invoiceRunType, LocalDate start) {
            // given
            InvoiceCalculationParameters parameters = new InvoiceCalculationParameters(invoiceRunType, start, start, start.plusDays(1));
            term.verifyUntil(start.plusDays(2));

            // when
            results = invoiceCalculationService.calculateDueDateRange(term, parameters);

            // then
            assertThat(results.size(), is(0));
        }

    }

}