package org.estatio.jdo;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.query.QueryDefault;

import org.estatio.dom.invoice.Invoice;
import org.estatio.dom.invoice.InvoiceProvenance;
import org.estatio.dom.invoice.InvoiceStatus;
import org.estatio.dom.invoice.PaymentMethod;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.invoicing.InvoiceItemForLease;
import org.estatio.dom.lease.invoicing.InvoicesForLease;
import org.estatio.dom.party.Party;
import org.estatio.dom.utils.StringUtils;

@Named("Invoices")
public class InvoicesForLeaseJdo extends InvoicesForLease {

    @Override
    @ActionSemantics(Of.SAFE)
    public Invoice findMatchingInvoice(Party seller, Party buyer, PaymentMethod paymentMethod, InvoiceProvenance provenance, InvoiceStatus status, LocalDate dueDate) {
        final List<Invoice> invoices = findMatchingInvoices(seller, buyer, paymentMethod, provenance, status, dueDate);
        return invoices.isEmpty()?null:invoices.get(0);
    }

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
//    @Override
//    @ActionSemantics(Of.SAFE)
//    public List<Invoice> findMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, final Lease lease, InvoiceStatus status, LocalDate dueDate) {
//        final List<Invoice> invoices = allMatches(queryForFindMatchingInvoices(seller, buyer, paymentMethod, /*lease,*/ status, dueDate));
//        return Lists.newArrayList(
//                Iterables.filter(invoices, 
//                        new Predicate<Invoice>() {
//                    public boolean apply(Invoice i) {
//                        return i.getProvenance() == lease;
//                    }
//                }
//                        ));
//    }
//    
//    private static QueryDefault<Invoice> queryForFindMatchingInvoices(Party seller, Party buyer, PaymentMethod paymentMethod, /*Lease lease,*/ InvoiceStatus status, LocalDate dueDate) {
//        return new QueryDefault<Invoice>(Invoice.class, "invoice_findMatchingInvoices", "seller", seller, "buyer", buyer, "paymentMethod", paymentMethod, /*"lease", lease,*/ "status", status, "dueDate", dueDate);
//    }
}
