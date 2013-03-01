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

public class InvoicesJdo extends Invoices {

    @Override
    @ActionSemantics(Of.SAFE)
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus) {
        return findMatchingInvoices(seller, buyer, paymentMethod, lease, invoiceStatus).get(0);
    }

    @Override
    @ActionSemantics(Of.SAFE)
    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus) {
        return allMatches(queryForFindMatchingInvoices(seller, buyer, paymentMethod, lease, invoiceStatus));

    }

    private static QueryDefault<Invoice> queryForFindMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, Lease lease, InvoiceStatus invoiceStatus) {
        return new QueryDefault<Invoice>(Invoice.class, "invoices_findMatchingInvoice", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, "lease", lease, "invoiceStatus", invoiceStatus);
    }
}
