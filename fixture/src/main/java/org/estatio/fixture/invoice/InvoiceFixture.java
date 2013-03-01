package org.estatio.fixture.invoice;

import org.apache.isis.applib.fixtures.AbstractFixture;
import org.estatio.dom.invoice.Charge;
import org.estatio.dom.invoice.ChargeGroup;
import org.estatio.dom.invoice.Charges;
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
        createCharges();
        createInvoices();
    }

    private void createCharges() {
        ChargeGroup cg = chargeRepository.newChargeGroup();
        cg.setDescription("Rent");
        cg.setReference("RENT");
        createCharge("RENT", "Rent", "IT-VATSTD");
    }

    private void createCharge(String reference, String description, String taxReference) {
        Charge c = chargeRepository.newCharge(reference);
        c.setDescription(description);
        c.setTax(taxRepository.findTaxByReference(taxReference));
    }
    
    private void createInvoices() {
        Invoice invoice = invoiceRepository.newInvoice();
        invoice.setBuyer(partyRepository.findPartyByReference("TOPMODEL"));
        invoice.setSeller(partyRepository.findPartyByReference("ACME"));
        invoice.setPaymentMethod(PaymentMethod.DIRECT_DEBIT);
        invoice.setStatus(InvoiceStatus.CONCEPT);
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
    
    private Charges chargeRepository;
    
    public void setChargeRepository(Charges chargeRepository) {
        this.chargeRepository = chargeRepository;
    }
    
    private Taxes taxRepository;
    
    public void setTaxRepository(Taxes taxes) {
        this.taxRepository = taxes;
    }
}
