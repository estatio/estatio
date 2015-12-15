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
import org.estatio.dom.invoice.InvoicingInterval;
import org.estatio.dom.lease.invoicing.InvoiceCalculationService;
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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class LeaseTermForPercentageTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    LeaseTermForPercentage term;

    @Before
    public void setup() {

        term = new LeaseTermForPercentage();
        term.setStartDate(new LocalDate(2013,1,1));
        term.setEndDate(new LocalDate(2013,12,31));
    }

    public static class DoAlign extends LeaseTermForPercentageTest {

        @Mock
        private LeaseItem rentItem;

        @Mock
        private LeaseItem torItem;

        @Mock
        private Lease mockLease;

        private LeaseItem percentageItem;

        @Before
        public void setup() {
            super.setup();

            percentageItem = new LeaseItem();

            context.checking(new Expectations() {
                {
                    oneOf(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(rentItem);
                        }
                    }));
                    oneOf(mockLease).findItemsOfType(LeaseItemType.TURNOVER_RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        {
                            add(torItem);
                        }
                    }));
                    oneOf(rentItem).calculationResults(with(any(LocalDateInterval.class)), with(any(LocalDate.class)));
                    will(returnValue(new ArrayList<InvoiceCalculationService.CalculationResult>() {
                        {
                            add(new InvoiceCalculationService.CalculationResult(
                                    new InvoicingInterval(LocalDateInterval.parseString("2013-01-01/2013-04-01"), new LocalDate(2013, 1, 1)),
                                    LocalDateInterval.parseString("2013-01-01/2013-04-01"),
                                    new BigDecimal("222222.22"),
                                    BigDecimal.ZERO
                            ));
                        }
                    }));
                    oneOf(torItem).calculationResults(with(any(LocalDateInterval.class)), with(any(LocalDate.class)));
                    will(returnValue(new ArrayList<InvoiceCalculationService.CalculationResult>() {
                        {
                            add(new InvoiceCalculationService.CalculationResult(
                                    new InvoicingInterval(LocalDateInterval.parseString("2013-01-01/2013-04-01"), new LocalDate(2013, 1, 1)),
                                    LocalDateInterval.parseString("2013-01-01/2013-04-01"),
                                    new BigDecimal("111111.11"),
                                    BigDecimal.ZERO
                            ));
                        }
                    }));
                }
            });
        }

        @Test
        public void testNothing() {
            doAlign("0", "0.00");
        }

        @Test
        public void testPercentageCalculation() {
            doAlign("0.33", "1100.00");
        }

        @Test
        public void testPercentageCalculation2() {
            doAlign("1.33", "4433.33");
        }

        private void doAlign(
                final String percentageStr,
                final String expectedValueStr
        ) {
            term.setLeaseItem(percentageItem);
            term.setStartDate(new LocalDate(2013, 1, 1));
            percentageItem.setLease(mockLease);
            term.setPercentage(parseBigDecimal(percentageStr));

            term.doAlign();

            assertThat(term.getEffectiveValue(), is(parseBigDecimal(expectedValueStr)));
        }

        private BigDecimal parseBigDecimal(final String input) {
            if (input == null) {
                return null;
            }
            return new BigDecimal(input);
        }
    }

    public static class ValidateChangePercentage extends LeaseTermForPercentageTest{

        LeaseTermForPercentage term = new LeaseTermForPercentage();

        @Test
        public void testValidate(){
            assertThat(term.validateChangeParameters(new BigDecimal(-0.00001)), is("Percentage should be between 0 and 100"));
            assertThat(term.validateChangeParameters(new BigDecimal(100.00001)), is("Percentage should be between 0 and 100"));
            assertNull(term.validateChangeParameters(new BigDecimal(0)));
            assertNull(term.validateChangeParameters(new BigDecimal(100)));
        }

    }

    public static class DoInitialize extends LeaseTermForPercentageTest {

        @Test
        public void endDateIsSetWhenNotProvided() {
            // given
            LeaseTerm term = new LeaseTermForPercentage();
            term.setStartDate(new LocalDate(2014,1,1));
            term.setFrequency(LeaseTermFrequency.YEARLY);
            //when
            term.doInitialize();
            //then
            assertThat(term.getEndDate(), is(new LocalDate(2014,12,31)));
        }

        @Test
        public void useGivenEndDate() {
            // given
            LeaseTerm term = new LeaseTermForPercentage();
            term.setStartDate(new LocalDate(2014,1,1));
            term.setEndDate(term.getStartDate().plusDays(1));
            term.setFrequency(LeaseTermFrequency.YEARLY);
            //when
            term.doInitialize();
            //then
            assertThat(term.getEndDate(), is(term.getStartDate().plusDays(1)));
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
                    .exercise(new LeaseTermForPercentage());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static FixtureDatumFactory<LeaseTermStatus> statii() {
            return new FixtureDatumFactory(LeaseTermStatus.class, (Object[]) LeaseTermStatus.values());
        }

    }
}