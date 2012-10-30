package com.eurocommercialproperties.estatio.objstore.dflt.invoice;

import java.util.List;

import org.apache.isis.applib.AbstractFactoryAndRepository;
import org.apache.isis.applib.annotation.ActionSemantics;
import org.apache.isis.applib.annotation.ActionSemantics.Of;
import org.apache.isis.applib.annotation.MemberOrder;

import com.eurocommercialproperties.estatio.dom.invoice.Invoice;
import com.eurocommercialproperties.estatio.dom.invoice.InvoiceItem;
import com.eurocommercialproperties.estatio.dom.invoice.Invoices;

public class InvoicesDefault extends AbstractFactoryAndRepository implements Invoices {

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "1")
    public Invoice newInvoice() {
        Invoice invoice = newTransientInstance(Invoice.class);
        persist(invoice);
        return invoice;
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "2")
    public List<Invoice> allInvoices() {
        return allInstances(Invoice.class);
    }

    @Override
    @ActionSemantics(Of.SAFE)
    @MemberOrder(sequence = "3")
    public List<InvoiceItem> allInvoiceItems() {
        return allInstances(InvoiceItem.class);
    }

}
