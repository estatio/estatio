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

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.estatio.dom.AbstractBeanPropertiesTest;
import org.estatio.dom.PojoTester.FixtureDatumFactory;
import org.estatio.dom.lease.invoicing.InvoiceCalculationParameters;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
import org.estatio.dom.lease.invoicing.InvoiceRunType;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

        @Mock
        private LeaseItem rentItem;

        @Before
        public void setup() {
            super.setup();
            context.checking(new Expectations() {
                {
                    oneOf(mockLease).getEffectiveInterval();
                    will(returnValue(new LocalDateInterval(new LocalDate(2013,01,01), new LocalDate(2014,01,01))));
                    oneOf(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(rentItem);
                        }
                    }));
                    oneOf(rentItem).valueForDate(with(any(LocalDate.class)));
                    will(returnValue(new BigDecimal("100000.00")));
                }
            });
        }

        @Test
        public void testHalfYear() {
            verifyUntil(new LocalDate(2013,01,01), DepositType.HALF_YEAR, "0.00", "50000.00");
        }

        @Test
        public void testHalfYearWithExcludingAmount() {
            verifyUntil(new LocalDate(2013,01,01), DepositType.HALF_YEAR, "10000.00", "40000.00");
        }

        @Test
        public void testQuarter() {
            verifyUntil(new LocalDate(2013,01,01), DepositType.QUARTER, "0.00", "25000.00");
        }

        @Test
        public void testMonth() {
            verifyUntil(new LocalDate(2013,01,01), DepositType.MONTH, "0.00", "8333.33");
        }

        private void verifyUntil(
                final LocalDate date,
                final DepositType depositType,
                final String excludedAmountStr,
                final String expectedValueStr
        ) {
            term.setDepositType(depositType);
            term.setExcludedAmount(parseBigDecimal(excludedAmountStr));

            term.verifyUntil(date);

            assertThat(term.getEffectiveValue(), is(parseBigDecimal(expectedValueStr)));
        }

        private BigDecimal parseBigDecimal(final String input) {
            if (input == null) {
                return null;
            }
            return new BigDecimal(input);
        }

    }

    public static class Initialize extends LeaseTermForDepositTest {

        @Test
        public void test(){
            //when
            term.doInitialize();

            //then
            assertThat(term.getDepositValue(), is(BigDecimal.ZERO));
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
        DepositType depositType;
        BigDecimal excludedAmount = new BigDecimal("-0.01");


        @Test
        public void test(){
            assertThat(leaseTermForDeposit.validateChangeParameters(depositType, excludedAmount), is("Excluded amount should not be negative"));
        }

    }

    public static class InvoiceCalculationTest extends LeaseTermForDepositTest {

        private List<InvoiceCalculationService.CalculationResult> results;
        private InvoiceCalculationService invoiceCalculationService;

        @Mock
        private LeaseItem rentItem;

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
            term.setDepositType(DepositType.HALF_YEAR);
            term.setExcludedAmount(new BigDecimal("500.00"));
            invoiceCalculationService = new InvoiceCalculationService();

            context.checking(new Expectations() {
                {
                    allowing(mockLease).getEffectiveInterval();
                    will(returnValue(new LocalDateInterval(new LocalDate(2013,01,01), new LocalDate(2014,01,01))));
                    allowing(mockLease).getStartDate();
                    will(returnValue(new LocalDate(2013,01,01)));
                    oneOf(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(rentItem);
                        }
                    }));
                    oneOf(rentItem).valueForDate(with(any(LocalDate.class)));
                    will(returnValue(new BigDecimal("10000.00")));
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