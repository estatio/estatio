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
import org.estatio.dom.lease.*;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.party.Parties;
import org.estatio.dom.party.Party;
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.estatio.fixture.EstatioFixtureScript;
import org.joda.time.LocalDate;

/**
 * Creates {@link org.estatio.dom.invoice.Invoice} and associated {@link org.estatio.dom.invoice.InvoiceItem}s.
 */
public abstract class InvoiceAbstract extends EstatioFixtureScript {

    protected InvoiceAbstract(String friendlyName, String localName) {
        super(friendlyName, localName);
    }

    protected Invoice createInvoice(Lease lease, String sellerStr, String buyerStr, PaymentMethod paymentMethod, String currencyStr, LocalDate startDate, ExecutionContext executionContext) {
        final Party buyer = parties.findPartyByReference(buyerStr);
        final Party seller = parties.findPartyByReference(sellerStr);
        final Currency currency = currencies.findCurrency(currencyStr);

        final String interactionId = null;

        final Invoice invoice = invoices.newInvoice(seller, buyer, paymentMethod, currency, startDate, lease, interactionId);
        invoice.setInvoiceDate(startDate);

        return executionContext.addResult(this, invoice);
    }

    protected void createInvoiceItemsForTermsOfFirstLeaseItemOfType(
            final Invoice invoice, final LeaseItemType leaseItemType,
            final LocalDate startDate, final LocalDateInterval interval,
            final ExecutionContext executionContext) {

        final Lease lease = invoice.getLease();
        final LeaseItem firstLeaseItem = lease.findFirstItemOfType(leaseItemType);
        final SortedSet<LeaseTerm> terms = firstLeaseItem.getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemsForLease.newInvoiceItem(term, interval, startDate, null);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());

            executionContext.addResult(this, item);
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
    protected Leases leases;

}
