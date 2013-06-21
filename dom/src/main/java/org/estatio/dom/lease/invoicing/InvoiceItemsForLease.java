package org.estatio.dom.lease.invoicing;

import java.util.List;

import org.joda.time.LocalDate;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

import org.estatio.dom.EstatioDomainService;
import org.estatio.dom.utils.StringUtils;

public class InvoiceItemsForLease extends EstatioDomainService<InvoiceItemForLease> {

    public InvoiceItemsForLease() {
        super(InvoiceItemsForLease.class, InvoiceItemForLease.class);
    }
    
    // //////////////////////////////////////

    
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @Hidden
    public InvoiceItemForLease newInvoiceItem() {
        InvoiceItemForLease invoiceItem = newTransientInstance();
        persist(invoiceItem);
        return invoiceItem;
    }


    // //////////////////////////////////////

    @ActionSemantics(Of.SAFE)
    @MemberOrder(name="Invoices", sequence="2")
    public List<InvoiceItemForLease> findInvoiceItemsByLease(
            // TODO: should this not take a lease, rather than a leaseReference?
            final @Named("Lease") String leaseReference, 
            final @Named("Start Date") LocalDate startDate, 
            final @Named("Due Date") LocalDate dueDate) {
        return allMatches("invoiceItem_findItems", "leaseReference", StringUtils.wildcardToRegex(leaseReference), "startDate", startDate, "dueDate", dueDate);
    }

}
