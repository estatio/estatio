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
import org.estatio.dom.valuetypes.LocalDateInterval;
import org.joda.time.LocalDate;
import org.apache.isis.applib.fixturescripts.SimpleFixtureScript;

public class InvoiceAndInvoiceItemFixture extends SimpleFixtureScript {

    private final String sellerStr;
    private final String buyerStr;
    private final String leaseStr;
    private final String currencyStr;
    private final LocalDate startDate;
    private final LocalDateInterval interval;

    public InvoiceAndInvoiceItemFixture(String friendlyName, String localName, String sellerStr, String buyerStr, String leaseStr, String currencyStr, LocalDate startDate, LocalDateInterval interval) {
        super(friendlyName, localName);
        this.sellerStr = sellerStr;
        this.buyerStr = buyerStr;
        this.leaseStr = leaseStr;
        this.currencyStr = currencyStr;
        this.startDate = startDate;
        this.interval = interval;
    }

    @Override
    protected void execute(ExecutionContext executionContext) {

        final Party buyer = parties.findPartyByReference(buyerStr);
        final Party seller = parties.findPartyByReference(sellerStr);
        final Lease lease = leases.findLeaseByReference(leaseStr);
        final Currency currency = currencies.findCurrency(currencyStr);

        final Invoice invoice = invoices.newInvoice(seller, buyer, PaymentMethod.DIRECT_DEBIT, currency, startDate, lease, null);
        invoice.setInvoiceDate(startDate);

        executionContext.add(this, invoice);

        final SortedSet<LeaseTerm> terms = lease.findFirstItemOfType(LeaseItemType.RENT).getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemsForLease.newInvoiceItem(term, interval, startDate, null);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());

            executionContext.add(this, item);
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
