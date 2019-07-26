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
package org.estatio.module.lease.integtests.invoicing;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.assertj.core.api.Assertions;
import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.apache.isis.applib.services.wrapper.HiddenException;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.apptenancy.fixtures.enums.ApplicationTenancy_enum;
import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceRunType;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.contributions.Lease_calculate;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseItemType;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.fixtures.breakoptions.enums.BreakOption_enum;
import org.estatio.module.lease.fixtures.invoice.enums.InvoiceForLease_enum;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDeposit_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForDiscount_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForRent_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForServiceCharge_enum;
import org.estatio.module.lease.fixtures.leaseitems.enums.LeaseItemForTurnoverRent_enum;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.orgcomms.enums.OrganisationAndComms_enum;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class Invoice_IntegTest extends LeaseModuleIntegTestAbstract {

    @Inject
    InvoiceRepository invoiceRepository;
    @Inject
    InvoiceForLeaseRepository invoiceForLeaseRepository;
    @Inject
    PartyRepository partyRepository;
    @Inject
    LeaseMenu leaseMenu;
    @Inject
    LeaseRepository leaseRepository;
    @Inject
    CurrencyRepository currencyRepository;
    @Inject
    ChargeRepository chargeRepository;
    @Inject
    ApplicationTenancies applicationTenancies;

    Party seller;
    Party buyer;
    Lease lease;

    public static class NewItem extends Invoice_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, LeaseItemForRent_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForServiceCharge_enum.OxfPoison003Gb.builder());
                    executionContext.executeChild(this, LeaseItemForTurnoverRent_enum.OxfPoison003Gb.builder());
                }
            });
        }

        private ApplicationTenancy applicationTenancy;
        private Currency currency;
        private Charge charge;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancies.findTenancyByPath(ApplicationTenancy_enum.Gb.getPath());
            seller = OrganisationAndComms_enum.HelloWorldGb.findUsing(serviceRegistry);
            buyer = OrganisationAndComms_enum.PoisonGb.findUsing(serviceRegistry);
            lease = Lease_enum.OxfPoison003Gb.findUsing(serviceRegistry);

            charge = chargeRepository.listAll().get(0);
            currency = currencyRepository.allCurrencies().get(0);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            InvoiceForLease invoice = invoiceForLeaseRepository.newInvoice(applicationTenancy, seller, buyer, PaymentMethod.BANK_TRANSFER, currency, VT.ld(2013, 1, 1), lease, null);

            // when
            mixin(InvoiceForLease._newItem.class, invoice).$$(charge, VT.bd(1), VT.bd("10000.123"), null, null);

            // then
            InvoiceForLease foundInvoice = invoiceForLeaseRepository.findOrCreateMatchingInvoice(applicationTenancy, seller, buyer, PaymentMethod.BANK_TRANSFER, lease, InvoiceStatus.NEW, VT.ld(2013, 1, 1), null);
            assertThat(foundInvoice.getTotalNetAmount(), is(VT.bd("10000.123")));

            // and also
            final InvoiceItemForLease invoiceItem = (InvoiceItemForLease) foundInvoice.getItems().first();
            assertThat(invoiceItem.getNetAmount(), is(VT.bd("10000.123")));
            assertThat(invoiceItem.getLease(), is(lease));
            assertThat(invoiceItem.getFixedAsset(), is((FixedAsset) lease.primaryOccupancy().get().getUnit()));

            // TODO: EST-290: netAmount has scale set to two but the example above
            // proves that it's possible to store with a higher precision
        }

    }

    public static class Remove extends Invoice_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {

                    ec.executeChildren(this,
                            Person_enum.LinusTorvaldsNl,
                            PropertyAndUnitsAndOwnerAndManager_enum.OxfGb,
                            PropertyAndUnitsAndOwnerAndManager_enum.KalNl);

                    ec.executeChildren(this,
                            BreakOption_enum.OxfPoison003Gb_FIXED,
                            BreakOption_enum.OxfPoison003Gb_ROLLING,
                            BreakOption_enum.OxfPoison003Gb_FIXED,
                            BreakOption_enum.OxfPoison003Gb_ROLLING,
                            BreakOption_enum.OxfTopModel001Gb_FIXED,
                            BreakOption_enum.OxfTopModel001Gb_ROLLING);

                    ec.executeChildren(this,
                            InvoiceForLease_enum.OxfPoison003Gb,
                            InvoiceForLease_enum.KalPoison001Nl);

                    ec.executeChildren(this,
                            Lease_enum.OxfPret004Gb,

                            LeaseItemForRent_enum.OxfMiracl005Gb,
                            LeaseItemForServiceCharge_enum.OxfMiracl005Gb,
                            LeaseItemForTurnoverRent_enum.OxfMiracl005Gb,
                            LeaseItemForDiscount_enum.OxfMiracle005bGb,
                            LeaseItemForDeposit_enum.OxfMiracle005bGb
                    );

                }
            });
        }

        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            seller = InvoiceForLease_enum.OxfPoison003Gb.getSeller_d().findUsing(serviceRegistry);
            buyer = InvoiceForLease_enum.OxfPoison003Gb.getBuyer_d().findUsing(serviceRegistry);
            lease = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().findUsing(serviceRegistry);
            invoiceStartDate = InvoiceForLease_enum.OxfPoison003Gb.getLease_d().getStartDate().plusYears(1);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            List<InvoiceForLease> matchingInvoices = findMatchingInvoices(seller, buyer, lease);
            Assert.assertThat(matchingInvoices.size(), Is.is(1));
            Invoice invoice = matchingInvoices.get(0);
            // when
            mixin(Invoice._remove.class, invoice).exec();
            // then
            matchingInvoices = findMatchingInvoices(seller, buyer, lease);
            Assert.assertThat(matchingInvoices.size(), Is.is(0));
        }

        private List<InvoiceForLease> findMatchingInvoices(final Party seller, final Party buyer, final Lease lease) {
            return invoiceForLeaseRepository.findMatchingInvoices(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT,
                    lease, InvoiceStatus.NEW,
                    invoiceStartDate);
        }
    }

    public static class Reverse extends Invoice_IntegTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext ec) {

                    ec.executeChildren(this,
                            Lease_enum.OxfTopModel001Gb,
                            LeaseItemForRent_enum.OxfTopModel001Gb,
                            LeaseItemForServiceCharge_enum.OxfTopModel001Gb
                    );

                }
            });
        }

        @Before
        public void setUp() throws Exception {
            lease = Lease_enum.OxfTopModel001Gb.findUsing(serviceRegistry);
            mixin(Lease_calculate.class, lease).exec(
                    InvoiceRunType.NORMAL_RUN,
                    Arrays.asList(LeaseItemType.RENT, LeaseItemType.SERVICE_CHARGE),
                    new LocalDate(2019,01,01),
                    new LocalDate(2019, 01,01),
                    new LocalDate(2019,1,2)
            );
            transactionService.nextTransaction(); // otherwise terms are not flushed
        }

        @Rule
        public ExpectedException expectedExceptions = ExpectedException.none();

        @Test
        public void happyCase() throws Exception {
            // given
            List<InvoiceForLease> invoicesForPoisonLease = invoiceForLeaseRepository.findByLease(lease);
            Assertions.assertThat(invoicesForPoisonLease).hasSize(1);
            InvoiceForLease invoice = invoicesForPoisonLease.get(0);
            mixin(InvoiceForLease._approve.class, invoice).$$();
            mixin(InvoiceForLease._invoice.class, invoice).$$(new LocalDate(2018,12,31));

            InvoiceItemForLease firstItem = (InvoiceItemForLease) invoice.getItems().first();
            InvoiceItemForLease lastItem = (InvoiceItemForLease) invoice.getItems().last();

            // when
            Assertions.assertThat(invoice.getStatus()).isEqualTo(InvoiceStatus.INVOICED);
            LocalDate newDueDate = invoice.getDueDate().plusMonths(2);
            InvoiceForLease reversedInvoice = wrap(invoice).reverse(newDueDate);

            // then
            Assertions.assertThat(reversedInvoice.getItems()).hasSize(2);
            Assertions.assertThat(reversedInvoice.getLease()).isEqualTo(invoice.getLease());
            Assertions.assertThat(reversedInvoice.getSeller()).isEqualTo(invoice.getSeller());
            Assertions.assertThat(reversedInvoice.getBuyer()).isEqualTo(invoice.getBuyer());
            Assertions.assertThat(reversedInvoice.getAtPath()).isEqualTo(invoice.getAtPath());
            Assertions.assertThat(reversedInvoice.getPaymentMethod()).isEqualTo(invoice.getPaymentMethod());
            Assertions.assertThat(reversedInvoice.getCurrency()).isEqualTo(invoice.getCurrency());
            Assertions.assertThat(reversedInvoice.getDueDate()).isEqualTo(newDueDate);
            Assertions.assertThat(reversedInvoice.getStatus()).isEqualTo(InvoiceStatus.NEW);

            InvoiceItemForLease firstReversedItem = (InvoiceItemForLease) reversedInvoice.getItems().first();
            InvoiceItemForLease lastReversedItem = (InvoiceItemForLease) reversedInvoice.getItems().last();
            checkReversedInvoiceItems(firstItem, firstReversedItem);
            checkReversedInvoiceItems(lastItem, lastReversedItem);

        }

        private void checkReversedInvoiceItems(final InvoiceItemForLease original, final InvoiceItemForLease reversed){
            Assertions.assertThat(reversed.getNetAmount()).isEqualTo(original.getNetAmount().negate());
            Assertions.assertThat(reversed.getGrossAmount()).isEqualTo(original.getGrossAmount().negate());
            Assertions.assertThat(reversed.getVatAmount()).isEqualTo(original.getVatAmount().negate());
            Assertions.assertThat(reversed.getCharge()).isEqualTo(original.getCharge());
            Assertions.assertThat(reversed.getLeaseTerm()).isEqualTo(original.getLeaseTerm());
            Assertions.assertThat(reversed.getDescription()).isEqualTo(original.getDescription());
            Assertions.assertThat(reversed.getTax()).isEqualTo(original.getTax());
            Assertions.assertThat(reversed.getStartDate()).isEqualTo(original.getStartDate());
            Assertions.assertThat(reversed.getEndDate()).isEqualTo(original.getEndDate());
            Assertions.assertThat(reversed.getEffectiveStartDate()).isEqualTo(original.getEffectiveStartDate());
            Assertions.assertThat(reversed.getEffectiveEndDate()).isEqualTo(original.getEffectiveEndDate());
        }


        @Test
        public void when_not_invoiced() throws Exception {
            // given
            List<InvoiceForLease> invoicesForPoisonLease = invoiceForLeaseRepository.findByLease(lease);
            Assertions.assertThat(invoicesForPoisonLease).hasSize(1);
            InvoiceForLease invoice = invoicesForPoisonLease.get(0);
            mixin(InvoiceForLease._approve.class, invoice).$$();

            // expect
            expectedExceptions.expect(HiddenException.class);

            // when
            InvoiceForLease reversedInvoice = wrap(invoice).reverse();

        }

    }

}