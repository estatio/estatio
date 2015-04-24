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
package org.estatio.integtests.invoice;

import java.util.List;
import javax.inject.Inject;
import org.hamcrest.core.Is;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancies;
import org.isisaddons.module.security.dom.tenancy.ApplicationTenancy;
import org.joda.time.LocalDate;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;
import org.estatio.dom.asset.FixedAsset;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.fixture.EstatioBaseLineFixture;
import org.estatio.fixture.asset.PropertyForKalNl;
import org.estatio.fixture.asset._PropertyForOxfGb;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001;
import org.estatio.fixture.invoice.InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003;
import org.estatio.fixture.lease.*;
import org.estatio.fixture.party.OrganisationForHelloWorldNl;
import org.estatio.fixture.party.OrganisationForPoisonNl;
import org.estatio.fixture.party.PersonForLinusTorvaldsNl;
import org.estatio.fixture.security.tenancy.ApplicationTenancyForGb;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoiceTest extends EstatioIntegrationTest {

    @Inject
    Invoices invoices;
    @Inject
    Parties parties;
    @Inject
    Leases leases;
    @Inject
    Currencies currencies;
    @Inject
    Charges charges;
    @Inject
    ApplicationTenancies applicationTenancies;


    Party seller;
    Party buyer;
    Lease lease;


    public static class NewItem extends InvoiceTest {

        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfPoison003Gb());
                }
            });
        }

        private ApplicationTenancy applicationTenancy;
        private Currency currency;
        private Charge charge;

        @Before
        public void setUp() throws Exception {
            applicationTenancy = applicationTenancies.findTenancyByPath(ApplicationTenancyForGb.PATH);

            seller = parties.findPartyByReference(OrganisationForHelloWorldNl.REF);
            buyer = parties.findPartyByReference(OrganisationForPoisonNl.REF);
            lease = leases.findLeaseByReference(_LeaseForOxfPoison003Gb.REF);

            charge = charges.allCharges().get(0);
            currency = currencies.allCurrencies().get(0);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            Invoice invoice = invoices.newInvoice(applicationTenancy, seller, buyer, PaymentMethod.BANK_TRANSFER, currency, VT.ld(2013, 1, 1), lease, null);

            // when
            invoice.newItem(charge, VT.bd(1), VT.bd("10000.123"), null, null);

            // then
            Invoice foundInvoice = invoices.findOrCreateMatchingInvoice(applicationTenancy, seller, buyer, PaymentMethod.BANK_TRANSFER, lease, InvoiceStatus.NEW, VT.ld(2013, 1, 1), null);
            assertThat(foundInvoice.getNetAmount(), is(VT.bd("10000.123")));

            // and also
            final InvoiceItemForLease invoiceItem = (InvoiceItemForLease) foundInvoice.getItems().first();
            assertThat(invoiceItem.getNetAmount(), is(VT.bd("10000.123")));
            assertThat(invoiceItem.getLease(), is(lease));
            assertThat(invoiceItem.getFixedAsset(), is((FixedAsset) lease.getOccupancies().first().getUnit()));

            // TODO: EST-290: netAmount has scale set to two but the example above
            // proves that it's possible to store with a higher precision
        }

    }

    public static class Remove extends InvoiceTest {


        @Before
        public void setupData() {
            runFixtureScript(new FixtureScript() {
                @Override
                protected void execute(ExecutionContext executionContext) {
                    executionContext.executeChild(this, new EstatioBaseLineFixture());

                    executionContext.executeChild(this, new PersonForLinusTorvaldsNl());

                    executionContext.executeChild(this, new _PropertyForOxfGb());
                    executionContext.executeChild(this, new PropertyForKalNl());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfTopModel001());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfMediax002Gb());

                    executionContext.executeChild(this, new LeaseBreakOptionsForOxfPoison003Gb());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003());
                    executionContext.executeChild(this, new InvoiceForLeaseItemTypeOfRentOneQuarterForKalPoison001());

                    executionContext.executeChild(this, new _LeaseForOxfPret004Gb());

                    executionContext.executeChild(this, new LeaseItemAndTermsForOxfMiracl005Gb());
                }
            });
        }

        private LocalDate invoiceStartDate;

        @Before
        public void setUp() throws Exception {
            seller = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_SELLER);
            buyer = parties.findPartyByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.PARTY_REF_BUYER);
            lease = leases.findLeaseByReference(InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.LEASE_REF);
            invoiceStartDate = InvoiceForLeaseItemTypeOfRentOneQuarterForOxfPoison003.startDateFor(lease);
        }

        @Test
        public void happyCase() throws Exception {
            // given
            List<Invoice> matchingInvoices = findMatchingInvoices(seller, buyer, lease);
            Assert.assertThat(matchingInvoices.size(), Is.is(1));
            Invoice invoice = matchingInvoices.get(0);
            // when
            invoice.remove();
            // then
            matchingInvoices = findMatchingInvoices(seller, buyer, lease);
            Assert.assertThat(matchingInvoices.size(), Is.is(0));
        }

        private List<Invoice> findMatchingInvoices(final Party seller, final Party buyer, final Lease lease) {
            return invoices.findMatchingInvoices(
                    seller, buyer, PaymentMethod.DIRECT_DEBIT,
                    lease, InvoiceStatus.NEW,
                    invoiceStartDate);
        }
    }

}