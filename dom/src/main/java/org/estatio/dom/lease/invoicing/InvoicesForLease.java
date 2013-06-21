package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceProvenance;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@Named("Invoices")
public class InvoicesForLease extends Invoices {

    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public InvoiceItemForLease newInvoiceItem() {
        InvoiceItemForLease invoiceItem = newTransientInstance(InvoiceItemForLease.class);
        persist(invoiceItem);
        return invoiceItem;
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        final List<Invoice> invoices = findMatchingInvoices(seller, buyer, paymentMethod, provenance, invoiceStatus, dueDate);
        return invoices.isEmpty() ? null : invoices.get(0);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus invoiceStatus, LocalDate dueDate) {
        return allMatches(queryForFindMatchingInvoices(seller, buyer, paymentMethod, provenance, invoiceStatus, dueDate));
    }

    private static QueryDefault<Invoice> queryForFindMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus status, LocalDate dueDate) {
        return new QueryDefault<Invoice>(Invoice.class, "invoice_findMatchingInvoices", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, "provenance", provenance, "status", status, "dueDate", dueDate);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence="2")
    public List<InvoiceItemForLease> findInvoiceItemsByLease(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        return allMatches(queryForFindItems(StringUtils.wildcardToRegex(leaseReference), startDate, dueDate));
    }

    private static QueryDefault<InvoiceItemForLease> queryForFindItems(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        return new QueryDefault<InvoiceItemForLease>(InvoiceItemForLease.class, "invoiceItem_findItems", "leaseReference", leaseReference, "startDate", startDate, "dueDate", dueDate);
    }

}
