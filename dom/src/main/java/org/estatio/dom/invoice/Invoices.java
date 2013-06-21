package org.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Prototype;

import org.estatio.dom.EstatioDomainService;

public class Invoices extends EstatioDomainService<Invoice> {

    public Invoices() {
        super(Invoices.class, Invoice.class);
    }

    // //////////////////////////////////////

    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoice() {
        Invoice invoice = newTransientInstance();
        persist(invoice);
        getContainer().flush();
        return invoice;
    }

    // //////////////////////////////////////

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "98")
    public List<Invoice> allInvoices() {
        return allInstances();
    }

    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "99")
    public List<InvoiceItem> allInvoiceItems() {
        return allInstances(InvoiceItem.class);
    }

}
