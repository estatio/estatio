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
import java.util.Arrays;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.estatio.dom.tax.Tax;
import org.incode.module.base.dom.valuetypes.AbstractInterval;
import org.incode.module.base.dom.valuetypes.LocalDateInterval;

import static org.assertj.core.api.Assertions.assertThat;

public class LeaseTermForDeposit_Test {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    LeaseItem mockLeaseItem;

    @Mock
    Tax mockTax;

    LeaseTermForDeposit leaseTermForDeposit;
    BigDecimal depositBase;

    @Before
    public void setup() {
        final LeaseItem leaseItem = new LeaseItem() {
            @Override public List<LeaseItemSource> getSourceItems() {
                return Arrays.asList(new LeaseItemSource(null, mockLeaseItem));
            }
        };
        leaseItem.setType(LeaseItemType.DEPOSIT);
        leaseTermForDeposit = new LeaseTermForDeposit();
        leaseTermForDeposit.setLeaseItem(leaseItem);
    }

    public static class ValueForDate extends LeaseTermForDeposit_Test {

        @Test
        public void date_outside_interval_returns_zero() throws Exception {

            final LeaseTermForDeposit leaseTermForDeposit = new LeaseTermForDeposit() {
                @Override public LocalDateInterval getInterval() {
                    return new LocalDateInterval(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1), AbstractInterval.IntervalEnding.EXCLUDING_END_DATE);
                }
                @Override public BigDecimal getCalculatedDepositValue() {
                    return new BigDecimal("123.45");
                }
            };

            assertThat(leaseTermForDeposit.valueForDate(new LocalDate(2013, 1, 1))).isEqualTo(new BigDecimal("123.45"));
            assertThat(leaseTermForDeposit.valueForDate(new LocalDate(2012, 12, 31))).isEqualTo(new BigDecimal("0.00"));
            assertThat(leaseTermForDeposit.valueForDate(new LocalDate(2014, 1, 1))).isEqualTo(new BigDecimal("0.00"));

        }

        @Test
        public void manual_overrides_calculated() throws Exception {
            //Given
            final LeaseTermForDeposit leaseTermForDeposit = new LeaseTermForDeposit() {
                @Override public LocalDateInterval getInterval() {
                    return new LocalDateInterval(new LocalDate(2013, 1, 1), new LocalDate(2014, 1, 1), AbstractInterval.IntervalEnding.EXCLUDING_END_DATE);
                }
                @Override public BigDecimal getCalculatedDepositValue() {
                    return new BigDecimal("123.45");
                }
                @Override public BigDecimal getManualDepositValue() {
                    return new BigDecimal("123.46");
                }
            };
            
            //When,Then
            assertThat(leaseTermForDeposit.valueForDate(new LocalDate(2013, 1, 1))).isEqualTo(new BigDecimal("123.46"));
        }

    }

    public static class IncludeAndExcludeVAT extends LeaseTermForDeposit_Test {

        @Test
        public void including_vat() throws Exception {

            context.checking(new Expectations() {
                {
                    allowing(mockLeaseItem).getEffectiveTax();
                    will(returnValue(mockTax));
                    allowing(mockTax).grossFromNet(with(new BigDecimal("100.00")), with(new LocalDate(2013, 1, 1)));
                    will(returnValue(new BigDecimal("121.00")));
                    allowing(mockLeaseItem).valueForDate(with(new LocalDate(2013, 1, 1)));
                    will(returnValue(new BigDecimal("100.00")));
                }
            });

            // given
            leaseTermForDeposit.setIncludeVat(true);

            // when
            depositBase = leaseTermForDeposit.calculateDepositBaseValue(new LocalDate(2013, 1, 1));

            //then
            assertThat(depositBase).isEqualTo(new BigDecimal("121.00"));

        }

        @Test
        public void excluding_vat() throws Exception {

            context.checking(new Expectations() {
                {
                    allowing(mockLeaseItem).valueForDate(with(new LocalDate(2013, 1, 1)));
                    will(returnValue(new BigDecimal("100.00")));
                }
            });

            // given
            leaseTermForDeposit.setIncludeVat(false);

            // when
            depositBase = leaseTermForDeposit.calculateDepositBaseValue(new LocalDate(2013, 1, 1));

            //then
            assertThat(depositBase).isEqualTo(new BigDecimal("100.00"));

        }

    }

    public static class WithFixedCalculationDate extends LeaseTermForDeposit_Test {

        @Test
        public void useFixedCalculationDateOverCalculationDate() throws Exception{

            LocalDate fixedCalculationDate = new LocalDate(2012, 1, 1);
            LocalDate calculationDate = new LocalDate(2013, 1, 1);

            context.checking(new Expectations() {
                {
                    allowing(mockLeaseItem).valueForDate(with(calculationDate));
                    will(returnValue(new BigDecimal("100.00")));
                    allowing(mockLeaseItem).valueForDate(with(fixedCalculationDate));
                    will(returnValue(new BigDecimal("99.00")));
                }
            });

            // given
            leaseTermForDeposit.setIncludeVat(false);

            // when
            leaseTermForDeposit.setFixedDepositCalculationDate(fixedCalculationDate);
            depositBase = leaseTermForDeposit.calculateDepositBaseValue(calculationDate);

            // then
            assertThat(depositBase).isEqualTo(new BigDecimal("99.00"));


        }

    }

    public static class VerifyUntil extends LeaseTermForDeposit_Test {

        @Test
        public void happyCase() throws Exception {

            context.checking(new Expectations() {
                {
                    allowing(mockLeaseItem).getEffectiveTax();
                    will(returnValue(mockTax));
                    allowing(mockTax).grossFromNet(with(new BigDecimal("100.00")), with(new LocalDate(2013, 1, 1)));
                    will(returnValue(new BigDecimal("121.00")));
                    allowing(mockLeaseItem).valueForDate(with(new LocalDate(2013, 1, 1)));
                    will(returnValue(new BigDecimal("100.00")));
                }
            });

            // given
            leaseTermForDeposit.setFraction(Fraction.M3);
            leaseTermForDeposit.setIncludeVat(true);

            // when
            leaseTermForDeposit.verifyUntil(new LocalDate(2013,1,1));

            // then
            assertThat(leaseTermForDeposit.getCalculatedDepositValue()).isEqualTo(new BigDecimal("30.25"));
            assertThat(leaseTermForDeposit.getEffectiveValue()).isEqualTo(new BigDecimal("30.25"));

        }

    }

    public static class CopyValuesTo extends LeaseTermForDeposit_Test {

        @Test
        public void values_are_copied_correctly() throws Exception {
            // Given
            LeaseTermForDeposit from = new LeaseTermForDeposit();
            from.setFraction(Fraction.M1);
            from.setIncludeVat(true);

            LeaseTermForDeposit to = new LeaseTermForDeposit();

            // When
            from.copyValuesTo(to);

            // Then
            assertThat(from.getFraction()).isEqualTo(to.getFraction());
            assertThat(from.isIncludeVat()).isEqualTo(to.isIncludeVat());
            assertThat(from.getFixedDepositCalculationDate()).isEqualTo(to.getFixedDepositCalculationDate());

            // and when given
            from.setFixedDepositCalculationDate(new LocalDate("2012-01-01"));

            // when
            from.copyValuesTo(to);

            // then
            assertThat(from.getFixedDepositCalculationDate()).isEqualTo(to.getFixedDepositCalculationDate());

        }
    }


    
    

 }
