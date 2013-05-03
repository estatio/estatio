package org.estatio.fixture.invoice;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceItem;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Parties;
import org.joda.time.LocalDate;

import org.apache.isis.applib.fixtures.AbstractFixture;

public class InvoiceFixture extends AbstractFixture {

    public static final LocalDate DATE = new LocalDate(2012, 1, 1);
    public static final String LEASE = "OXF-MEDIAX-002";
    public static final String SELLER_PARTY = "ACME";
    public static final String BUYER_PARTY = "MEDIAX";

    @Override
    public void install() {
        createInvoices();
    }

    private void createInvoices() {
        Invoice invoice = invoiceRepository.newInvoice();
        invoice.setBuyer(partyRepository.findPartyByReference(BUYER_PARTY));
        invoice.setSeller(partyRepository.findPartyByReference(SELLER_PARTY));
        invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        invoice.setStatus(InvoiceStatus.NEW);
        Lease lease = leaseRepository.findByReference(LEASE);
        invoice.setLease(lease);
        invoice.setDueDate(DATE);
        invoice.setInvoiceDate(DATE);

        InvoiceItem item = invoiceRepository.newInvoiceItem();
        item.modifyInvoice(invoice);
        item.setDueDate(DATE);
        item.setStartDate(DATE);
        // quick n dirty, just need some link
        item.modifyLeaseTerm(lease.getItems().first().getTerms().first());

    }

    private Parties partyRepository;

    public void setPartyRepository(Parties partyRepository) {
        this.partyRepository = partyRepository;
    }

    private Invoices invoiceRepository;

    public void setInvoiceRepository(Invoices invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    private Leases leaseRepository;

    public void setLeaseRepository(Leases leaseRepository) {
        this.leaseRepository = leaseRepository;
    }

}
