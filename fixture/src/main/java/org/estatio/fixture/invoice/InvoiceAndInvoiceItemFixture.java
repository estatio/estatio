/*
 *
 *  Copyright 2012-2013 Eurocommercial Properties NV
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

import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

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

public class InvoiceAndInvoiceItemFixture extends AbstractFixture {

    public static final LocalDate START_DATE = new LocalDate(2012, 1, 1);
    public static final String LEASE = "OXF-POISON-003";
    public static final String SELLER_PARTY = "ACME";
    public static final String BUYER_PARTY = "POISON";

    @Override
    public void install() {
        createInvoices();
    }

    private void createInvoices() {

        createInvoice(BUYER_PARTY, SELLER_PARTY, LEASE, "EUR");
        createInvoice("ACME", "POISON", "KAL-POISON-001", "EUR");
    }

    private void createInvoice(
            final String buyerStr,
            final String sellerStr,
            final String leaseStr,
            final String currencyStr) {
        final Party buyer = parties.findPartyByReference(buyerStr);
        final Party seller = parties.findPartyByReference(sellerStr);
        final Lease lease = leases.findLeaseByReference(leaseStr);
        final Currency currency = currencies.findCurrency(currencyStr);
        final Invoice invoice = invoices.newInvoice(buyer, seller, PaymentMethod.DIRECT_DEBIT, currency, START_DATE, lease);
        invoice.setInvoiceDate(START_DATE);

        final SortedSet<LeaseTerm> terms = lease.findFirstItemOfType(LeaseItemType.RENT).getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemsForLease.newInvoiceItem(term, START_DATE, START_DATE);
            item.setInvoice(invoice);
            item.setSequence(invoice.nextItemSequence());
        }
    }

    // //////////////////////////////////////

    private Parties parties;

    public void injectParties(Parties parties) {
        this.parties = parties;
    }

    private Currencies currencies;

    public void injectCurrencies(Currencies currencies) {
        this.currencies = currencies;
    }

    private Invoices invoices;

    public void injectInvoices(Invoices invoices) {
        this.invoices = invoices;
    }

    private InvoiceItemsForLease invoiceItemsForLease;

    public void injectInvoiceItemsForLease(InvoiceItemsForLease invoices) {
        this.invoiceItemsForLease = invoices;
    }

    private Leases leases;

    public void injectLeases(Leases leases) {
        this.leases = leases;
    }

}
