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
package org.estatio.fixture.invoice;

import java.util.SortedSet;
import javax.inject.Inject;
import org.estatio.dom.currency.Currencies;
import org.estatio.dom.currency.Currency;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.AbstractInterval.IntervalEnding;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class InvoiceAndInvoiceItemFixture extends SimpleFixtureScript {

    public static final LocalDate START_DATE = new LocalDate(2012, 1, 1);
    public static final LocalDateInterval INTERVAL = new LocalDateInterval(new LocalDate(2012, 1, 1), new LocalDate(2012, 4, 1), IntervalEnding.EXCLUDING_END_DATE);

    public static final String LEASE = "OXF-POISON-003";
    public static final String SELLER_PARTY = "ACME";
    public static final String BUYER_PARTY = "POISON";

    @Override
    protected void execute(ExecutionContext fixtureResults) {
        createInvoices(fixtureResults);
    }

    private void createInvoices(ExecutionContext fixtureResults) {
        createInvoice(SELLER_PARTY, BUYER_PARTY, LEASE, "EUR", fixtureResults);
        createInvoice("ACME", "POISON", "KAL-POISON-001", "EUR", fixtureResults);
    }

    private void createInvoice(
            final String sellerStr,
            final String buyerStr,
            final String leaseStr,
            final String currencyStr,
            ExecutionContext fixtureResults) {

        final Party buyer = parties.findPartyByReference(buyerStr);
        final Party seller = parties.findPartyByReference(sellerStr);
        final Lease lease = leases.findLeaseByReference(leaseStr);
        final Currency currency = currencies.findCurrency(currencyStr);

        final Invoice invoice = invoices.newInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, currency, START_DATE, lease, null);
        invoice.setInvoiceDate(START_DATE);

        fixtureResults.add(this, invoice);

        final SortedSet<LeaseTerm> terms = lease.findFirstItemOfType(LeaseItemType.RENT).getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemsForLease.newInvoiceItem(term, INTERVAL, START_DATE, null);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());

            fixtureResults.add(this, item);
        }
    }

    // //////////////////////////////////////

    @Inject
    private Parties parties;

    @Inject
    private Currencies currencies;

    @Inject
    private Invoices invoices;

    @Inject
    private InvoiceItemsForLease invoiceItemsForLease;

    @Inject
    private Leases leases;

}
