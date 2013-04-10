package org.estatio.dom.invoice;

import java.util.List;

import org.apache.commons.lang.NotImplementedException;
import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.Hidden;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;
import org.apache.isis.applib.annotation.Prototype;
import org.estatio.dom.lease.Lease;
import org.estatio.dom.lease.PaymentMethod;
import org.estatio.dom.party.Party;
import org.joda.time.LocalDate;


@Named("Invoices")
public class Invoices extends AbstractFactoryAndRepository {

    // {{ newInvoice
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    public Invoice newInvoice() {
        Invoice invoice = newTransientInstance(Invoice.class);
        persist(invoice);
        return invoice;
    }
    // }}

    // {{ newInvoiceItem
    @ActionSemantics(Of.NON_IDEMPOTENT)
    @MemberOrder(sequence = "1")
    @Hidden
    public InvoiceItem newInvoiceItem() {
        InvoiceItem invoiceItem = newTransientInstance(InvoiceItem.class);
        persist(invoiceItem);
        return invoiceItem;
    }
    // }}

    // {{ allInvoices
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> allInvoices() {
        return allInstances(Invoice.class);
    }
    // }}

    // {{ allInvoiceItems
    @Prototype
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<InvoiceItem> allInvoiceItems() {
        return allInstances(InvoiceItem.class);
    }
    // }}

    @ActionSemantics(Of.SAFE)
    @Hidden
    public Invoice findMatchingInvoice(
            Party seller,
            Party buyer,
            PaymentMethod paymentMethod,
            Lease lease,
            InvoiceStatus invoiceStatus,
            LocalDate dueDate
            ){
        throw new NotImplementedException();
    }

    @ActionSemantics(Of.SAFE)
    @Hidden
    public List<Invoice> findMatchingInvoices(
            Party seller,
            Party buyer,
            PaymentMethod paymentMethod,
            Lease lease,
            InvoiceStatus invoiceStatus,
            LocalDate dueDate
            ){
        throw new NotImplementedException();
    }
    
    @Prototype
    public void removeAll(){
        //TODO: For prototyping reasons we need a remove?
    }
}
