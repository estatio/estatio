/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
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

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoiceItemsForLease;
import org.estatio.dom.party.Parties;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class InvoiceFixture extends AbstractFixture {

    public static final LocalDate START_DATE = new LocalDate(2012, 1, 1);
    public static final String LEASE = "OXF-POISON-003";
    public static final String SELLER_PARTY = "ACME";
    public static final String BUYER_PARTY = "POISON";

    @Override
    public void install() {
        createInvoices();
    }

    private void createInvoices() {
        final Invoice invoice = invoices.newInvoice();
        invoice.setBuyer(parties.findPartyByReferenceOrName(BUYER_PARTY));
        invoice.setSeller(parties.findPartyByReferenceOrName(SELLER_PARTY));
        invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        invoice.setStatus(InvoiceStatus.NEW);
        
        final Lease lease = leases.findLeaseByReference(LEASE);
        invoice.setSource(lease);
        invoice.setDueDate(START_DATE);
        invoice.setInvoiceDate(START_DATE);

        final SortedSet<LeaseTerm> terms = lease.findFirstItemOfType(LeaseItemType.RENT).getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoiceItemsForLease.newInvoiceItem();
            item.modifyInvoice(invoice);
            item.setDueDate(START_DATE);
            item.setStartDate(START_DATE);
            item.modifyLeaseTerm(term);
            item.setSequence(invoice.nextItemSequence());
        }
    }

    // //////////////////////////////////////

    private Parties parties;

    public void injectParties(Parties parties) {
        this.parties = parties;
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
