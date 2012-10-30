package com.eurocommercialproperties.estatio.dom.invoice;

import java.util.List;

import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Named;

@Named("Invoices")
public interface Invoices {

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Invoice newInvoice();

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> allInvoices();

    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<InvoiceItem> allInvoiceItems();

}
