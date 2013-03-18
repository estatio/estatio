package org.estatio.fixture.invoice;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.charge.Charge;
import org.estatio.dom.charge.ChargeGroup;
import org.estatio.dom.charge.Charges;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Leases;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Parties;
import org.estatio.dom.tax.Tax;
import org.estatio.dom.tax.Taxes;

public class InvoiceFixture extends AbstractFixture {

    @Override
    public void install() {
        createInvoices();
    }

    private void createInvoices() {
        Invoice invoice = invoiceRepository.newInvoice();
        invoice.setBuyer(partyRepository.findPartyByReference("TOPMODEL"));
        invoice.setSeller(partyRepository.findPartyByReference("ACME"));
        invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        invoice.setStatus(InvoiceStatus.NEW);
        invoice.setLease(leaseRepository.findByReference("OXF-TOPMODEL-001"));
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
