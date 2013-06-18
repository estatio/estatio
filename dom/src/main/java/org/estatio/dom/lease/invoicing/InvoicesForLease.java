package org.estatio.dom.lease.invoicing;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.NotImplementedException;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceProvenance;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.Named;

@Named("Invoices")
public class InvoicesForLease extends Invoices {

    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public InvoiceItemForLease newInvoiceItem() {
        InvoiceItemForLease invoiceItem = newTransientInstance(InvoiceItemForLease.class);
        persist(invoiceItem);
        return invoiceItem;
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        final List<Invoice> invoices = findMatchingInvoices(seller, buyer, paymentMethod, provenance, invoiceStatus, dueDate);
        return invoices.isEmpty() ? null : invoices.get(0);
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    public List<InvoiceItemForLease> findItems(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        throw new NotImplementedException();
    }
    

}
