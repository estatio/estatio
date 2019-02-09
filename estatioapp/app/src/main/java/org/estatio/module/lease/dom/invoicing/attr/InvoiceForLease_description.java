package org.estatio.module.lease.dom.invoicing.attr;

import javax.inject.Inject;

import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.Mixin;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;

import org.estatio.module.invoice.dom.Invoice;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeName;
import org.estatio.module.invoice.dom.attr.InvoiceAttributeRepository;
import org.estatio.module.lease.dom.invoicing.InvoiceForLease;

@Mixin(method="prop")
public class InvoiceForLease_description {
    private final InvoiceForLease invoiceForLease;
    public InvoiceForLease_description(final InvoiceForLease invoiceForLease) {
        this.invoiceForLease = invoiceForLease;
    }
    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(contributed= Contributed.AS_ASSOCIATION)
    @PropertyLayout(multiLine = Invoice.DescriptionType.Meta.MULTI_LINE)
    public String prop() {
        return invoiceAttributeRepository.findValueByInvoiceAndName(InvoiceAttributeName.INVOICE_DESCRIPTION, invoiceForLease);
    }
    public boolean hideProp() {
        return false;
    }

    @Inject protected
    InvoiceAttributeRepository invoiceAttributeRepository;

}
