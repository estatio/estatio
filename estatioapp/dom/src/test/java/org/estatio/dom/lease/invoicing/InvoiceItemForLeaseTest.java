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
package org.estatio.dom.lease.invoicing;

import java.math.BigDecimal;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.asset.FixedAssetForTesting;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.LeaseTermForTesting;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.TaxRate;
import org.estatio.dom.tax.TaxRateRepository;

import static org.assertj.core.api.Assertions.assertThat;

public class InvoiceItemForLeaseTest {
    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Tax.class))
                    .withFixture(pojos(Charge.class))
                    .withFixture(pojos(Invoice.class))
                    .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
                    .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
                    .withFixture(pojos(Lease.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new InvoiceItemForLease());
        }

    }

    public static class Verify extends InvoiceItemForLeaseTest {

        private Charge charge;
        private Tax tax;
        private TaxRate rate;
        private InvoiceItemForLease item;

        @Mock
        TaxRateRepository mockTaxRateRepository;

        @Rule
        public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

        @Before
        public void setup() {
            charge = new Charge();
            tax = new Tax();
            tax.taxRateRepository = mockTaxRateRepository;

            rate = new TaxRate();
            rate.setPercentage(BigDecimal.valueOf(21));

            item = new InvoiceItemForLease();

            item.setCharge(charge);
            item.setTax(tax);
            item.setDueDate(new LocalDate(2012, 1, 1));
            item.setNetAmount(BigDecimal.ZERO);
            item.setVatAmount(BigDecimal.ZERO);
            item.setGrossAmount(BigDecimal.ZERO);
        }

        @Test
        public void happyCase() {
            context.checking(new Expectations() {
                {
                    allowing(mockTaxRateRepository).findTaxRateByTaxAndDate(with(tax), with(new LocalDate(2012, 1, 1)));
                    will(returnValue(rate));
                }
            });
            item.setNetAmount(BigDecimal.valueOf(12.34));
            item.verify();
            assertThat(item.getVatAmount()).isEqualTo(BigDecimal.valueOf(2.59));
            assertThat(item.getGrossAmount()).isEqualTo(BigDecimal.valueOf(14.93));
        }

    }

}