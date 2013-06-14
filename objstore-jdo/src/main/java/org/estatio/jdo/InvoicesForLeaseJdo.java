package org.estatio.jdo;

import java.util.List;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceProvenance;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;
import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

@Named("Invoices")
public class InvoicesForLeaseJdo extends InvoicesForLease {

    @Override
    @ActionSemantics(Of.SAFE)
    public List<InvoiceItemForLease> findItems(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        return allMatches(queryForFindItems(StringUtils.wildcardToRegex(leaseReference), startDate, dueDate));
    }

    private static QueryDefault<InvoiceItemForLease> queryForFindItems(String leaseReference, LocalDate startDate, LocalDate dueDate) {
        return new QueryDefault<InvoiceItemForLease>(InvoiceItemForLease.class, "invoiceItem_findItems", "leaseReference", leaseReference, "startDate", startDate, "dueDate", dueDate);
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, final InvoiceProvenance provenance, InvoiceStatus status, LocalDate dueDate) {
        return allMatches(queryForFindMatchingInvoices(seller, buyer, paymentMethod, provenance, status, dueDate));
    }

    private static QueryDefault<Invoice> queryForFindMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus status, LocalDate dueDate) {
        return new QueryDefault<Invoice>(Invoice.class, "invoice_findMatchingInvoices", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, "provenance", provenance, "status", status, "dueDate", dueDate);
    }
}
