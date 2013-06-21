package org.estatio.fixture.invoice;

import java.util.SortedSet;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.LeaseItemType;
import org.estatio.dom.lease.LeaseTerm;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
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
        invoice.setBuyer(parties.findPartyByReference(BUYER_PARTY));
        invoice.setSeller(parties.findPartyByReference(SELLER_PARTY));
        invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        invoice.setStatus(InvoiceStatus.NEW);
        
        final Lease lease = leases.findByReference(LEASE);
        invoice.setProvenance(lease);
        invoice.setDueDate(START_DATE);
        invoice.setInvoiceDate(START_DATE);

        final SortedSet<LeaseTerm> terms = lease.findFirstItemOfType(LeaseItemType.RENT).getTerms();
        for (final LeaseTerm term : terms) {
            InvoiceItemForLease item = invoices.newInvoiceItem();
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

    private InvoicesForLease invoices;

    public void injectInvoices(InvoicesForLease invoices) {
        this.invoices = invoices;
    }

    private Leases leases;

    public void injectLeases(Leases leases) {
        this.leases = leases;
    }

}
