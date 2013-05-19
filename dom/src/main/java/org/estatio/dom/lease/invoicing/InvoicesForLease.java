package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.party.Party;

@Named("Invoices")
public class InvoicesForLease extends Invoices {

    // {{ newInvoiceItem
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @Hidden
    public InvoiceItemForLease newInvoiceItem() {
        InvoiceItemForLease invoiceItem = newTransientInstance(InvoiceItemForLease.class);
        persist(invoiceItem);
        return invoiceItem;
    }
    // }}


    @ActionSemantics(Of.SAFE)
    @Hidden
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    public List<InvoiceItemForLease> findItems(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        throw new NotImplementedException();
    }
}
