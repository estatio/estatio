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

import javax.inject.Inject;
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
import org.estatio.fixture.lease.LeaseForOxfPoison003;
import org.estatio.fixture.lease.LeaseItemAndTermsForOxfPoison003;
import org.estatio.fixture.party.OrganisationForHelloWorld;
import org.estatio.fixture.party.OrganisationForPoison;
import org.estatio.integtests.EstatioIntegrationTest;
import org.estatio.integtests.VT;
import org.junit.Before;
import org.junit.Test;
import org.apache.isis.applib.fixturescripts.FixtureScript;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class InvoiceTest_newItem extends EstatioIntegrationTest {

    @Before
    public void setupData() {
        scenarioExecution().install(new FixtureScript() {
            @Override
            protected void execute(ExecutionContext executionContext) {
                execute(new LeaseItemAndTermsForOxfPoison003(), executionContext);
            }
        });
    }

    @Inject
    private Invoices invoices;
    @Inject
    private Parties parties;
    @Inject
    private Leases leases;
    @Inject
    private Currencies currencies;
    @Inject
    private Charges charges;

    private Party seller;
    private Party buyer;
    private Lease lease;
    private Currency currency;
    private Charge charge;

    @Before
    public void setUp() throws Exception {
        seller = parties.findPartyByReference(OrganisationForHelloWorld.PARTY_REFERENCE);
        buyer = parties.findPartyByReference(OrganisationForPoison.PARTY_REFERENCE);
        lease = leases.findLeaseByReference(LeaseForOxfPoison003.LEASE_REFERENCE);

        charge = charges.allCharges().get(0);
        currency = currencies.allCurrencies().get(0);
    }

    @Test
    public void happyCase() throws Exception {
        // given
        Invoice invoice = invoices.newInvoice(seller, buyer, PaymentMethod.BANK_TRANSFER, currency, VT.ld(2013, 1, 1), lease, null);

        // when
        invoice.newItem(charge, VT.bd(1), VT.bd("10000.123"), null, null);

        // then
        Invoice foundInvoice = invoices.findOrCreateMatchingInvoice(seller, buyer, PaymentMethod.BANK_TRANSFER, lease, InvoiceStatus.NEW, VT.ld(2013, 1, 1), null);
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
