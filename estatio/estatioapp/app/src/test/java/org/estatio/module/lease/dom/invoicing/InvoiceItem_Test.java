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
package org.estatio.module.lease.dom.invoicing;

import java.math.BigDecimal;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import org.apache.isis.core.unittestsupport.comparable.ComparableContractTest_compareTo;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.unittestsupport.dom.bean.AbstractBeanPropertiesTest;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.dom.FixedAssetForTesting;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceForTesting;
import org.estatio.module.invoice.dom.InvoiceItem;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseForTesting;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForTesting;
import org.estatio.module.tax.dom.Tax;
import org.estatio.module.tax.dom.TaxRate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.anyOf;

public class InvoiceItem_Test {

    public static class BeanProperties extends AbstractBeanPropertiesTest {

        @Test
        public void test() {
            newPojoTester()
                    .withFixture(pojos(Charge.class))
                    .withFixture(pojos(Tax.class))
                    .withFixture(pojos(TaxRate.class))
                    .withFixture(pojos(Invoice.class, InvoiceForTesting.class))
                    .withFixture(pojos(Lease.class, LeaseForTesting.class))
                    .withFixture(pojos(LeaseTerm.class, LeaseTermForTesting.class))
                    .withFixture(pojos(FixedAsset.class, FixedAssetForTesting.class))
                    .withFixture(pojos(ApplicationTenancy.class))
                    .exercise(new InvoiceItemForLease());
        }
    }

    public static class CompareTo extends ComparableContractTest_compareTo<InvoiceItemForLease> {

        private Invoice inv1;
        private Invoice inv2;

        private Charge chg1;
        private Charge chg2;

        @Before
        public void setUpParentInvoices() throws Exception {
            inv1 = new InvoiceForLease();
            inv2 = new InvoiceForLease();

            inv1.setInvoiceNumber("000001");
            inv2.setInvoiceNumber("000002");

            chg1 = new Charge();
            chg2 = new Charge();

            chg1.setReference("ABC");
            chg2.setReference("DEF");
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<List<InvoiceItemForLease>> orderedTuples() {
            return listOf(
                    listOf(
                            newInvoiceItem(null, null, null, null),
                            newInvoiceItem(inv1, null, null, null),
                            newInvoiceItem(inv1, null, null, null),
                            newInvoiceItem(inv2, null, null, null)
                    ),
                    listOf(
                            newInvoiceItem(inv1, new LocalDate(2012, 4, 2), null, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), null, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), null, null),
                            newInvoiceItem(inv1, null, null, null)
                    ),
                    listOf(
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), null, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg2, null)
                    ),
                    listOf(
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, null),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, "ABC"),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, "ABC"),
                            newInvoiceItem(inv1, new LocalDate(2012, 3, 1), chg1, "DEF")
                    ));
        }

        private InvoiceItemForLease newInvoiceItem(
                Invoice invoice,
                LocalDate startDate,
                Charge charge,
                String description) {
            final InvoiceItemForLease ii = new InvoiceItemForLease();
            ii.setInvoice(invoice);
            ii.setStartDate(startDate);
            ii.setCharge(charge);
            ii.setDescription(description);
            return ii;
        }

    }

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Mock
    Tax mockTax;

    public static class Verify extends InvoiceItem_Test {

        private InvoiceItem invoiceItem;
        private LocalDate date = new LocalDate(2014,1,1);

        private TaxRate taxRate;

        @Before
        public void setup() {
            taxRate = new TaxRate();
            taxRate.setPercentage(new BigDecimal("17.5"));

            context.checking(new Expectations() {
                {
                    allowing(mockTax).taxRateFor(with(anyOf(aNull(LocalDate.class),any(LocalDate.class))));
                    will(returnValue(taxRate));
                }
            });
            invoiceItem = new InvoiceItem(){
                public ApplicationTenancy getApplicationTenancy() {
                    return null;
                }
            };
            invoiceItem.setTax(mockTax);
            invoiceItem.setInvoice(new InvoiceForLease());

        }

        @Test
        public void withLargeNetAmount() {
            invoiceItem.setNetAmount(new BigDecimal("100.00"));
            invoiceItem.verify();
            assertThat(invoiceItem.getVatAmount()).isEqualTo(new BigDecimal("17.50"));
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("117.50"));
        }

        @Test
        public void withSmallNetAmount() {
            invoiceItem.setNetAmount(new BigDecimal("1.50"));
            invoiceItem.verify();
            assertThat(invoiceItem.getVatAmount()).isEqualTo(new BigDecimal("0.26"));
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("1.76"));
        }

        @Test
        public void withNullTax() {
            invoiceItem.setTax(null);
            invoiceItem.setNetAmount(new BigDecimal("1.50"));
            invoiceItem.verify();
            assertThat(invoiceItem.getVatAmount()).isEqualTo(new BigDecimal("0"));
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("1.50"));
        }
    }

    public static class ChangeTax extends InvoiceItem_Test {

        private InvoiceItem invoiceItem;
        private TaxRate taxRate;

        @Before
        public void setup() {
            taxRate = new TaxRate();
            final BigDecimal oldPercentage = new BigDecimal("17.5");
            taxRate.setPercentage(oldPercentage);

            context.checking(new Expectations() {
                {
                    allowing(mockTax).taxRateFor(with(anyOf(aNull(LocalDate.class),any(LocalDate.class))));
                    will(returnValue(taxRate));
                }
            });
            invoiceItem = new InvoiceItem(){
                public ApplicationTenancy getApplicationTenancy() {
                    return null;
                }
            };
            invoiceItem.setTax(mockTax);
            invoiceItem.setInvoice(new InvoiceForLease());
            invoiceItem.setNetAmount(new BigDecimal("1000.00"));
            invoiceItem.setVatAmount(new BigDecimal("175.00"));
            invoiceItem.setGrossAmount(new BigDecimal("1175.00"));

        }

        @Test
        public void change_tax_resets_taxrate_and_recalculates() throws Exception {

            // given
            final BigDecimal percentageNewRate = new BigDecimal("10.0");
            final TaxRate newRate = new TaxRate();
            newRate.setPercentage(percentageNewRate);
            Tax newTax = new Tax(){
                @Override
                public TaxRate taxRateFor(final LocalDate date) {
                    return newRate;
                }

            };
            invoiceItem.setTaxRate(taxRate);
            assertThat(invoiceItem.getTaxRate()).isEqualTo(taxRate);
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("1175.00"));

            // when
            invoiceItem.changeTax(newTax);

            // then
            assertThat(invoiceItem.getTaxRate()).isEqualTo(newRate);
            assertThat(invoiceItem.getGrossAmount()).isEqualTo(new BigDecimal("1100.00"));
            assertThat(invoiceItem.getVatAmount()).isEqualTo(new BigDecimal("100.00"));

        }

    }

}