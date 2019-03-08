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
package org.estatio.module.lease.integtests.lease;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.SortedSet;

import javax.inject.Inject;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.InvalidException;

import org.incode.module.base.integtests.VT;

import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.charge.fixtures.charges.enums.Charge_enum;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.InvoicingFrequency;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseAgreementRoleTypeEnum;
import org.estatio.module.lease.dom.LeaseItem;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseItem_autoSplit;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.LeaseTerm;
import org.estatio.module.lease.dom.LeaseTermForIndexable;
import org.estatio.module.lease.dom.LeaseTermForServiceCharge;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLeaseRepository;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForEntryFee_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForMarketing_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTax_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.incode.module.unittestsupport.dom.assertions.Asserting.assertType;

public class LeaseItem_IntegTest extends LeaseModuleIntegTestAbstract {

    @Before
    public void setupData() {
        runFixtureScript(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {

                executionContext.executeChild(this, Lease_enum.KalPoison001Nl.builder());
                executionContext.executeChild(this, LeaseItemForRent_enum.KalPoison001Nl.builder());
                executionContext.executeChild(this, InvoiceForLease_enum.KalPoison001Nl.builder());

                executionContext.executeChild(this, LeaseItemForRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfTopModel001Gb_TA.builder());
                executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDiscount_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForEntryFee_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForTax_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForDeposit_enum.OxfTopModel001Gb.builder());
                executionContext.executeChild(this, LeaseItemForMarketing_enum.OxfTopModel001Gb.builder());

            }
        });
    }

    @Inject
    LeaseMenu leaseMenu;

    @Inject
    LeaseRepository leaseRepository;

    Lease lease;

    @Before
    public void setUp() throws Exception {
        lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
    }

    public static class FindTerm extends LeaseItem_IntegTest {

        @Test
        public void whenExists_forRent() throws Exception {

            // this is mostly just asserting on the baseline fixture

            // given
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

            // and given
            final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();
            Assert.assertThat(terms.size(), is(1));
            final LeaseTerm term0 = terms.first();

            // when
            final LeaseTerm term = leaseTopModelRentItem.findTerm(VT.ld(2010, 7, 15));
            LeaseTermForIndexable leaseTopModelRentTerm = assertType(term, LeaseTermForIndexable.class);

            // then
            Assert.assertNotNull(leaseTopModelRentTerm);
            assertThat(leaseTopModelRentTerm).isEqualTo(term0);

            // and then
            Assert.assertNotNull(leaseTopModelRentTerm.getFrequency());
            Assert.assertNotNull(leaseTopModelRentTerm.getFrequency().nextDate(VT.ld(2012, 1, 1)));

            BigDecimal baseValue = leaseTopModelRentTerm.getBaseValue();
            Assert.assertEquals(VT.bd("20000.00"), baseValue);
        }

        @Test
        public void whenExists_forServiceCharge() throws Exception {

            // given
            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            // when
            LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = (LeaseTermForServiceCharge) leaseTopModelServiceChargeItem.findTerm(VT.ld(2010, 7, 15));

            // then
            assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue()).isEqualTo(VT.bd("6000.00"));
        }

    }

    public static class Copy extends LeaseItem_IntegTest {

        @Inject
        private ChargeRepository chargeRepository;

        @Test
        public void happyCase() throws Exception {

            // given
            LeaseItem leaseItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), LeaseAgreementRoleTypeEnum.LANDLORD);
            final Charge charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
            assertThat(leaseItem.getInvoicedBy()).isEqualTo(LeaseAgreementRoleTypeEnum.LANDLORD);

            // when
            final LocalDate startDate = VT.ld(2011, 7, 15);
            final LeaseItem newLeaseItem = wrap(leaseItem).copy(startDate, InvoicingFrequency.FIXED_IN_ADVANCE, PaymentMethod.DIRECT_DEBIT, charge);

            // then
            assertThat(newLeaseItem.getPaymentMethod()).isEqualTo(PaymentMethod.DIRECT_DEBIT);
            assertThat(newLeaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.FIXED_IN_ADVANCE);
            assertThat(newLeaseItem.getStartDate()).isEqualTo(startDate);
            assertThat(newLeaseItem.getCharge()).isEqualTo(charge);

            final BigInteger nextSequenceNumber = leaseItem.getSequence().add(VT.bi(1));
            assertThat(newLeaseItem.getSequence()).isEqualTo(nextSequenceNumber);

            final String atPath = leaseItem.getApplicationTenancyPath();
            assertThat(newLeaseItem.getApplicationTenancyPath()).isEqualTo(atPath);

            assertThat(newLeaseItem.getTerms().size()).isEqualTo(leaseItem.getTerms().size());
            assertThat(newLeaseItem.getEndDate()).isNull();
        }

    }

    public static class ChangeCharge extends LeaseItem_IntegTest {

        @Inject
        private ChargeRepository chargeRepository;

        @Test
        public void happyCase() throws Exception {

            // given
            LeaseItem leaseItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            final Charge charge = Charge_enum.GbServiceCharge.findUsing(serviceRegistry);
            assertThat(leaseItem.getCharge()).isEqualTo(charge);

            // when
            final Charge newCharge = Charge_enum.ItServiceCharge.findUsing(serviceRegistry);
            final LeaseItem leaseItemReturned = wrap(leaseItem).changeCharge(newCharge);

            // then
            assertThat(leaseItem.getCharge()).isEqualTo(newCharge);
            assertThat(leaseItemReturned).isSameAs(leaseItem);
        }
    }

    public static class ChangeInvoicingFrequency extends LeaseItem_IntegTest {

        @Rule
        public ExpectedException thrown = ExpectedException.none();

        @Inject
        private InvoiceItemForLeaseRepository invoiceItemForLeaseRepository;

        @Test
        public void happyCase() throws Exception {
            // given
            LeaseItem leaseItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseItem).isNotNull();
            assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.QUARTERLY_IN_ADVANCE);
            final SortedSet<LeaseTerm> terms = leaseItem.getTerms();
            terms.forEach(leaseTerm -> assertThat(leaseTerm.getInvoiceItems()).isEmpty());

            // when
            wrap(leaseItem).changeInvoicingFrequency(InvoicingFrequency.MONTHLY_IN_ADVANCE);

            // then
            assertThat(leaseItem.getInvoicingFrequency()).isEqualTo(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        }

        @Test
        public void whenStillHasInvoiceItems() throws Exception {
            // given
            Lease leaseWithInvoiceItem = Lease_enum.KalPoison001Nl.findUsing(serviceRegistry);
            LeaseItem leaseItem = leaseWithInvoiceItem.findFirstItemOfType(LeaseItemType.RENT);
            assertThat(leaseItem).isNotNull();
            final LeaseTerm term = leaseItem.getTerms().first();
            assertThat(term.getInvoiceItems()).isNotEmpty();

            // then
            thrown.expect(InvalidException.class);
            thrown.expectMessage("You cannot change the invoicing frequency of a lease item with invoice items on its terms. This lease item has invoice items on the following term(s):");

            // when
            wrap(leaseItem).changeInvoicingFrequency(InvoicingFrequency.MONTHLY_IN_ADVANCE);
        }
    }

    public static class GetTerms extends LeaseItem_IntegTest {

        @Test
        public void whenExists_andFirstIsIndexableRent() throws Exception {
            // this is just really asserting on the fixture

            // given
            LeaseItem leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));

            // when
            final SortedSet<LeaseTerm> terms = leaseTopModelRentItem.getTerms();

            // then
            Assert.assertThat(terms.size(), is(1));
            final LeaseTerm term0 = terms.first();

            LeaseTermForIndexable indexableRent = assertType(term0, LeaseTermForIndexable.class);

            Assert.assertNotNull(indexableRent.getFrequency());
            Assert.assertNotNull(indexableRent.getFrequency().nextDate(VT.ld(2012, 1, 1)));

            BigDecimal baseValue = indexableRent.getBaseValue();
            Assert.assertEquals(VT.bd("20000.00"), baseValue);
        }

        @Test
        public void whenExists_andFirstIsLeaseTermForServiceChargeBudgetAuditLineItem() throws Exception {
            // this is just really asserting on the fixture

            LeaseItem leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));

            final SortedSet<LeaseTerm> terms = leaseTopModelServiceChargeItem.getTerms();
            Assert.assertThat(terms.size(), Is.is(1));
            final LeaseTerm term0 = terms.first();

            LeaseTermForServiceCharge leaseTopModelServiceChargeTerm = assertType(term0, LeaseTermForServiceCharge.class);
            Assert.assertThat(leaseTopModelServiceChargeTerm.getBudgetedValue(), Is.is(VT.bd("6000.00")));
        }

    }

    public static class Verify extends LeaseItem_IntegTest {

        private LeaseItem leaseTopModelServiceChargeItem;
        private LeaseItem leaseTopModelRentItem;

        @Before
        public void setUp() throws Exception {
            super.setUp();

            leaseTopModelServiceChargeItem = lease.findItem(LeaseItemType.SERVICE_CHARGE, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelServiceChargeItem).isNotNull();

            leaseTopModelRentItem = lease.findItem(LeaseItemType.RENT, VT.ld(2010, 7, 15), VT.bi(1));
            assertThat(leaseTopModelRentItem).isNotNull();
        }

        @Test
        public void givenServiceChargeItem_thenCreatesTermsForThatItemOnly() throws Exception {

            // given
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNull();

            // when
            leaseTopModelServiceChargeItem.verify();

            // then
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNotNull();
        }

        @Test
        public void givenIndexableRentItem_thenCreatesTermsForThatItemOnly() throws Exception {

            // given
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNull();

            // when
            leaseTopModelRentItem.verify();

            // then
            assertThat(leaseTopModelRentItem.findTerm(VT.ld(2012, 7, 15))).isNotNull();
            assertThat(leaseTopModelServiceChargeItem.findTerm(VT.ld(2012, 7, 15))).isNull();
        }
    }

    public static class AutoSplit extends LeaseModuleIntegTestAbstract {

        private LeaseItem leaseTopModelServiceChargeItem;
        private LeaseItem leaseTopModelRentItem;

        @Before
        public void setUp() throws Exception {

            runFixtureScript(LeaseItemForTurnoverRent_enum.OxfTopModel001GbSplit.builder());

        }

        @Test
        public void auto_split_works() throws Exception {

            // given

            final LeaseItem leaseItem = LeaseItemForTurnoverRent_enum.OxfTopModel001GbSplit.findUsing(serviceRegistry);
            assertThat(leaseItem.getTerms()).hasSize(2);
            // when

            mixin(LeaseItem_autoSplit.class, leaseItem).autoSplit();

            transactionService.nextTransaction();

            // then
            assertThat(leaseItem.getTerms()).hasSize(5);

        }

    }

}