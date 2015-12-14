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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class LeaseTermForTurnoverRentTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    LeaseTermForTurnoverRent term;

    @Before
    public void setup() {

        term = new LeaseTermForTurnoverRent();
        term.setStartDate(new LocalDate(2013,1,1));
        term.setEndDate(new LocalDate(2013,12,31));
    }

    public static class DoAlign extends LeaseTermForTurnoverRentTest {

        @Mock
        private LeaseItem rentItem;

        @Mock
        private Lease mockLease;

        private LeaseItem torItem;

        @Before
        public void setup() {
            super.setup();

            torItem = new LeaseItem();

            context.checking(new Expectations() {
                {
                    oneOf(mockLease).findItemsOfType(LeaseItemType.RENT);
                    will(returnValue(new ArrayList<LeaseItem>() {
                        private static final long serialVersionUID = 8666017984548841746L;
                        {
                            add(rentItem);
                        }
                    }));
                    oneOf(rentItem).calculationResults(with(any(LocalDateInterval.class)), with(any(LocalDate.class)));
                    will(returnValue(new ArrayList<InvoiceCalculationService.CalculationResult>() {
                        private static final long serialVersionUID = -2212720554866561882L;
                        {
                            add(new InvoiceCalculationService.CalculationResult(
                                    new InvoicingInterval(LocalDateInterval.parseString("2013-01-01/2013-04-01"), new LocalDate(2013, 1, 1)),
                                    LocalDateInterval.parseString("2013-01-01/2013-04-01"),
                                    new BigDecimal("100000.00"),
                                    BigDecimal.ZERO
                            ));
                        }
                    }));
                }
            });
        }

        @Test
        public void testNothing() {
            doAlign(null, null, null, "0");
        }

        @Test
        public void testRule() {
            doAlign("7", null, "1500000.00", "5000.00");
        }

        @Test
        public void testBudget() {
            doAlign(null, "120000.00", null, "20000.00");
        }

        @Test
        public void testBudgetAndRule() {
            doAlign("7", "120000.00", "1500000.00", "5000.00");
        }


        private void doAlign(
                final String turnoverRentRule,
                final String totalBudgetedRentStr,
                final String auditedTurnoverStr,
                final String expectedValueStr
        ) {
            term.setLeaseItem(torItem);
            term.setStartDate(new LocalDate(2013, 1, 1));
            torItem.setLease(mockLease);
            term.setTurnoverRentRule(turnoverRentRule);
            term.setTotalBudgetedRent(parseBigDecimal(totalBudgetedRentStr));
            term.setAuditedTurnover(parseBigDecimal(auditedTurnoverStr));

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

    public static class DoInitialize extends LeaseTermForTurnoverRentTest {

        @Test
        public void endDateIsSetWhenNotProvided() {
            // given
            LeaseTerm term = new LeaseTermForTurnoverRent();
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
            LeaseTerm term = new LeaseTermForTurnoverRent();
            term.setStartDate(new LocalDate(2014,1,1));
            term.setEndDate(term.getStartDate().plusDays(1));
            term.setFrequency(LeaseTermFrequency.YEARLY);
            //when
            term.doInitialize();
            //then
            assertThat(term.getEndDate(), is(term.getStartDate().plusDays(1)));
        }

    }


    public static class ValidateTurnoverRentRule extends LeaseTermForTurnoverRentTest {

        @Before
        public void setup() {
            super.setup();
            term.setTurnoverRentRule("7.00");
        }

        @Test
        public void whenReturnsNull() {
            Assert.assertNull(term.validateTurnoverRentRule("7.00"));
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
                    .exercise(new LeaseTermForTurnoverRent());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        private static FixtureDatumFactory<LeaseTermStatus> statii() {
            return new FixtureDatumFactory(LeaseTermStatus.class, (Object[]) LeaseTermStatus.values());
        }

    }
}