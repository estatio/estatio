package org.estatio.jdo;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.query.QueryDefault;
import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.Invoices;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;

public class InvoicesJdo extends Invoices {

    @Override
    @ActionSemantics(Of.SAFE)
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus status, LocalDate dueDate) {
        return firstMatch(queryForFindMatchingInvoices(seller, buyer, paymentMethod, lease, status, dueDate));
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus status, LocalDate dueDate) {
        return allMatches(queryForFindMatchingInvoices(seller, buyer, paymentMethod, lease, status, dueDate));
    }

    private static QueryDefault<Invoice> queryForFindMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus status, LocalDate dueDate) {
        return new QueryDefault<Invoice>(Invoice.class, "invoice_findMatchingInvoices", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, "lease", lease, "status", status, "dueDate", dueDate);
    }
}
