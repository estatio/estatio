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
package org.estatio.dom.tax;

import java.util.List;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.dom.with.WithIntervalMutable;
import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.incode.module.unittestsupport.dom.with.WithIntervalMutableContractTestAbstract_changeDates;

import static org.assertj.core.api.Assertions.assertThat;

public class TaxRateTest {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Tax.class))
                    .withFixture(pojos(TaxRate.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new TaxRate());
        }
    }

    public static class ChangeDates extends WithIntervalMutableContractTestAbstract_changeDates<TaxRate> {

        private TaxRate taxRate;

        @Before
        public void setUp() throws Exception {
            taxRate = withIntervalMutable;
        }

        protected TaxRate doCreateWithIntervalMutable(final WithIntervalMutable.Helper<TaxRate> mockChangeDates) {
            return new TaxRate() {
                @Override
                WithIntervalMutable.Helper<TaxRate> getChangeDates() {
                    return mockChangeDates;
                }
            };
        }


        // //////////////////////////////////////

        @Test
        public void changeDatesDelegate() {
            taxRate = new TaxRate();
            assertThat(taxRate.getChangeDates()).isNotNull();
        }


    }

    public static class CompareTo extends ComparableContractTest_compareTo<TaxRate> {

        private Tax tax1;
        private Tax tax2;

        @Before
        public void setUp() throws Exception {
            tax1 = new Tax();
            tax1.setReference("TAX1");

            tax2 = new Tax();
            tax2.setReference("TAX2");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<TaxRate>> orderedTuples() {
            return listOf(
                    listOf(
                            newTaxRate(null, new LocalDate(2012,4,2)),
                            newTaxRate(tax1, new LocalDate(2012,3,1)),
                            newTaxRate(tax1, new LocalDate(2012,3,1)),
                            newTaxRate(tax2, null))
                    ,listOf(
                            newTaxRate(tax1, new LocalDate(2012,4,2)),
                            newTaxRate(tax1, new LocalDate(2012,3,1)),
                            newTaxRate(tax1, new LocalDate(2012,3,1)),
                            newTaxRate(tax1, null))
            );
        }

        private TaxRate newTaxRate(
                Tax tax,
                LocalDate startDate) {
            final TaxRate tr = new TaxRate();
            tr.setTax(tax);
            tr.setStartDate(startDate);
            return tr;
        }

    }

}