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

import java.util.List;

import javax.inject.Inject;

import org.hamcrest.core.Is;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.apache.isis.applib.fixturescripts.FixtureScript;

import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;

import org.incode.module.base.integtests.VT;

import org.estatio.module.asset.dom.FixedAsset;
import org.estatio.module.asset.fixtures.person.enums.Person_enum;
import org.estatio.module.asset.fixtures.property.enums.PropertyAndUnitsAndOwnerAndManager_enum;
import org.estatio.module.base.fixtures.security.apptenancy.enums.ApplicationTenancy_enum;
import org.estatio.module.charge.dom.Charge;
import org.estatio.module.charge.dom.ChargeRepository;
import org.estatio.module.currency.dom.Currency;
import org.estatio.module.currency.dom.CurrencyRepository;
import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.InvoiceRepository;
import org.estatio.module.invoice.dom.InvoiceStatus;
import org.estatio.module.invoice.dom.PaymentMethod;
import org.estatio.module.lease.app.LeaseMenu;
import org.estatio.module.lease.dom.Lease;
import org.estatio.module.lease.dom.LeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;
import org.estatio.module.lease.dom.invoicing.InvoiceForLeaseRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceItemForLease;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfMediax002Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.breakoptions.personas.LeaseBreakOptionsForOxfTopModel001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.module.lease.fixtures.invoicing.personas.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.module.lease.fixtures.lease.enums.Lease_enum;
import org.estatio.module.lease.fixtures.leaseitems.deposits.personas.LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.discount.personas.LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.percentage.personas.LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.rent.personas.LeaseItemAndLeaseTermForRentOf2ForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeForOxfPoison003Gb;
import org.estatio.module.lease.fixtures.leaseitems.servicecharge.personas.LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb;
import org.estatio.module.lease.fixtures.leaseitems.turnoverrent.personas.LeaseItemAndLeaseTermForTurnoverRentForOxfPoison003Gb;
import org.estatio.module.lease.integtests.LeaseModuleIntegTestAbstract;
import org.estatio.module.party.dom.Party;
import org.estatio.module.party.dom.PartyRepository;
import org.estatio.module.party.fixtures.organisation.enums.OrganisationAndComms_enum;

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
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentOf2ForOxfPoison003Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeForOxfPoison003Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfPoison003Gb());
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
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, Person_enum.LinusTorvaldsNl.builder());

                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.OxfGb.builder());
                    executionContext.executeChild(this, PropertyAndUnitsAndOwnerAndManager_enum.KalNl.builder());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());

                    executionContext.executeChild(this, Lease_enum.OxfPret004Gb.builder());

                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForRentOf2ForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForServiceChargeOf2ForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForTurnoverRentForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForDiscountForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForPercentageForOxfMiracl005Gb());
                    executionContext.executeChild(this, new LeaseItemAndLeaseTermForDepositForOxfMiracl005Gb());

                }
            });
        }

        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            seller = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_SELLER);
            buyer = partyRepository.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_BUYER);
            lease = leaseRepository.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
            invoiceStartDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);
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

}